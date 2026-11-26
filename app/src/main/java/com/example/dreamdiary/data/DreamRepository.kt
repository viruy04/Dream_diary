package com.example.dreamdiary.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/**
 * Вся работа с БД (insert / query / delete из лабораторной) собрана здесь,
 * экраны к SQLite напрямую не обращаются.
 *
 * Один экземпляр на приложение. В отличие от лабораторной, dbHelper.close() после каждой
 * операции не вызывается: открытое соединение переиспользуется.
 *
 * [version] увеличивается после каждой записи. Экраны читают его внутри remember(...),
 * поэтому списки и статистика обновляются сами. Запросы выполняются в основном потоке:
 * для локального дневника объёмы данных небольшие.
 */
class DreamRepository private constructor(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)
    private val logTag = dbHelper.LOG_TAG

    var version by mutableIntStateOf(0)
        private set

    companion object {
        @Volatile
        private var instance: DreamRepository? = null

        fun get(context: Context): DreamRepository =
            instance ?: synchronized(this) {
                instance ?: DreamRepository(context).also { instance = it }
            }
    }

    // ---------- Профиль ----------

    fun hasProfile(): Boolean =
        DatabaseUtils.queryNumEntries(dbHelper.readableDatabase, DBHelper.TABLE_PROFILE) > 0

    fun getProfile(): Profile? {
        val db = dbHelper.readableDatabase
        return db.query(DBHelper.TABLE_PROFILE, null, null, null, null, null, null).use { c ->
            if (c.moveToFirst()) {
                Profile(
                    name = c.getString(c.getColumnIndexOrThrow(DBHelper.COLUMN_PROFILE_NAME)),
                    birthDate = c.getString(c.getColumnIndexOrThrow(DBHelper.COLUMN_BIRTH_DATE))
                )
            } else {
                null
            }
        }
    }

    /** Профиль один: при повторном сохранении строка с id = 1 заменяется. */
    fun saveProfile(name: String, birthIso: String) {
        val cv = ContentValues().apply {
            put(DBHelper.COLUMN_ID, 1)
            put(DBHelper.COLUMN_PROFILE_NAME, name.trim())
            put(DBHelper.COLUMN_BIRTH_DATE, birthIso)
        }
        dbHelper.writableDatabase.insertWithOnConflict(
            DBHelper.TABLE_PROFILE, null, cv, SQLiteDatabase.CONFLICT_REPLACE
        )
        Log.d(logTag, "profile saved")
        version++
    }

    // ---------- Сны ----------

    /**
     * Добавляет сон с тегами и проверяет достижения.
     * Возвращает достижения, открытые именно этим сном.
     */
    fun addDream(
        title: String,
        description: String,
        mood: Mood?,
        tags: List<String>
    ): List<AchievementType> {
        val db = dbHelper.writableDatabase
        var unlocked: List<AchievementType> = emptyList()

        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put(DBHelper.COLUMN_TITLE, title.trim())
                put(DBHelper.COLUMN_DESCRIPTION, description.trim())
                put(DBHelper.COLUMN_DREAM_DATE, DateUtils.todayIso())
                put(DBHelper.COLUMN_CREATED_AT, System.currentTimeMillis())
                if (mood != null) put(DBHelper.COLUMN_MOOD, mood.code) else putNull(DBHelper.COLUMN_MOOD)
            }
            val dreamId = db.insertOrThrow(DBHelper.TABLE_DREAMS, null, cv)
            Log.d(logTag, "row inserted, ID = $dreamId")

            tags.map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { tag ->
                val link = ContentValues().apply {
                    put(DBHelper.COLUMN_DREAM_ID, dreamId)
                    put(DBHelper.COLUMN_TAG_ID, getOrCreateTagId(db, tag))
                }
                db.insertWithOnConflict(
                    DBHelper.TABLE_DREAM_TAGS, null, link, SQLiteDatabase.CONFLICT_IGNORE
                )
            }

            unlocked = unlockEarnedAchievements(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        version++
        return unlocked
    }

    fun deleteDream(id: Long) {
        val db = dbHelper.writableDatabase
        // связи в dream_tags удаляются каскадом
        val count = db.delete(DBHelper.TABLE_DREAMS, "${DBHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
        // теги, которых больше нет ни у одного сна, убираем из справочника
        db.execSQL(
            "delete from ${DBHelper.TABLE_TAGS} where ${DBHelper.COLUMN_ID} not in " +
                    "(select ${DBHelper.COLUMN_TAG_ID} from ${DBHelper.TABLE_DREAM_TAGS})"
        )
        Log.d(logTag, "deleted rows count = $count")
        version++
    }

    /**
     * Сны от новых к старым. Фильтр по настроению выполняет SQLite, а поиск - Kotlin:
     * LIKE в SQLite не различает регистр только для латиницы, для кириллицы не работает.
     */
    fun getDreams(query: String = "", moods: Set<Mood> = emptySet()): List<Dream> {
        val selection = if (moods.isEmpty()) null
        else "${DBHelper.COLUMN_MOOD} in (${moods.joinToString(",") { "?" }})"
        val args = if (moods.isEmpty()) null else moods.map { it.code }.toTypedArray()
        return loadDreams(selection, args, "${DBHelper.COLUMN_DREAM_DATE} desc, ${DBHelper.COLUMN_CREATED_AT} desc")
            .filter { it.matches(query) }
    }

    /** Сны за период (включительно), по времени записи - для календаря. */
    fun getDreamsInRange(startIso: String, endIso: String): List<Dream> =
        loadDreams(
            "${DBHelper.COLUMN_DREAM_DATE} between ? and ?",
            arrayOf(startIso, endIso),
            "${DBHelper.COLUMN_CREATED_AT} asc"
        )

    /** Сны в порядке записи (старые первыми): так звёзды на карте не «прыгают» при добавлении новых. */
    fun getDreamsOldestFirst(): List<Dream> =
        loadDreams(null, null, "${DBHelper.COLUMN_CREATED_AT} asc, ${DBHelper.COLUMN_ID} asc")

    private fun loadDreams(selection: String?, args: Array<String>?, orderBy: String): List<Dream> {
        val db = dbHelper.readableDatabase
        val tagsByDream = loadTagsByDream(db)
        val result = mutableListOf<Dream>()
        db.query(DBHelper.TABLE_DREAMS, null, selection, args, null, null, orderBy).use { c ->
            val idCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_ID)
            val titleCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE)
            val descCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)
            val dateCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_DREAM_DATE)
            val createdCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_CREATED_AT)
            val moodCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_MOOD)
            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                result += Dream(
                    id = id,
                    title = c.getString(titleCol),
                    description = c.getString(descCol),
                    dreamDate = c.getString(dateCol),
                    createdAt = c.getLong(createdCol),
                    mood = if (c.isNull(moodCol)) null else Mood.fromCode(c.getString(moodCol)),
                    tags = tagsByDream[id] ?: emptyList()
                )
            }
        }
        return result
    }

    private fun loadTagsByDream(db: SQLiteDatabase): Map<Long, List<String>> {
        val map = HashMap<Long, MutableList<String>>()
        db.rawQuery(
            "select dt.${DBHelper.COLUMN_DREAM_ID}, t.${DBHelper.COLUMN_TAG_NAME} " +
                    "from ${DBHelper.TABLE_DREAM_TAGS} dt " +
                    "join ${DBHelper.TABLE_TAGS} t on t.${DBHelper.COLUMN_ID} = dt.${DBHelper.COLUMN_TAG_ID} " +
                    "order by t.${DBHelper.COLUMN_TAG_NAME}",
            null
        ).use { c ->
            while (c.moveToNext()) {
                map.getOrPut(c.getLong(0)) { mutableListOf() }.add(c.getString(1))
            }
        }
        return map
    }

    private fun getOrCreateTagId(db: SQLiteDatabase, name: String): Long {
        val cv = ContentValues().apply { put(DBHelper.COLUMN_TAG_NAME, name) }
        db.insertWithOnConflict(DBHelper.TABLE_TAGS, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
        return db.query(
            DBHelper.TABLE_TAGS, arrayOf(DBHelper.COLUMN_ID),
            "${DBHelper.COLUMN_TAG_NAME} = ?", arrayOf(name), null, null, null
        ).use { c ->
            c.moveToFirst()
            c.getLong(0)
        }
    }

    // ---------- Статистика ----------

    fun getStatistics(): DreamStatistics {
        val db = dbHelper.readableDatabase

        val total = DatabaseUtils.queryNumEntries(db, DBHelper.TABLE_DREAMS).toInt()
        val lastWeek = DatabaseUtils.queryNumEntries(
            db, DBHelper.TABLE_DREAMS,
            "${DBHelper.COLUMN_DREAM_DATE} >= ?", arrayOf(DateUtils.isoDaysAgo(6))
        ).toInt()

        val byMood = mutableListOf<MoodCount>()
        db.rawQuery(
            "select ${DBHelper.COLUMN_MOOD}, count(*) as c from ${DBHelper.TABLE_DREAMS} " +
                    "where ${DBHelper.COLUMN_MOOD} is not null " +
                    "group by ${DBHelper.COLUMN_MOOD} order by c desc, ${DBHelper.COLUMN_MOOD}",
            null
        ).use { c ->
            while (c.moveToNext()) {
                Mood.fromCode(c.getString(0))?.let { byMood += MoodCount(it, c.getInt(1)) }
            }
        }

        val topTags = mutableListOf<TagCount>()
        db.rawQuery(
            "select t.${DBHelper.COLUMN_TAG_NAME}, count(*) as c from ${DBHelper.TABLE_DREAM_TAGS} dt " +
                    "join ${DBHelper.TABLE_TAGS} t on t.${DBHelper.COLUMN_ID} = dt.${DBHelper.COLUMN_TAG_ID} " +
                    "group by t.${DBHelper.COLUMN_ID} order by c desc, t.${DBHelper.COLUMN_TAG_NAME} limit 5",
            null
        ).use { c ->
            while (c.moveToNext()) topTags += TagCount(c.getString(0), c.getInt(1))
        }

        val dates = loadDistinctDates(db)

        return DreamStatistics(
            total = total,
            lastWeek = lastWeek,
            byMood = byMood,
            topTags = topTags,
            currentStreak = Streaks.current(dates),
            longestStreak = Streaks.longest(dates)
        )
    }

    private fun loadDistinctDates(db: SQLiteDatabase): List<String> {
        val dates = mutableListOf<String>()
        db.rawQuery(
            "select distinct ${DBHelper.COLUMN_DREAM_DATE} from ${DBHelper.TABLE_DREAMS} " +
                    "order by ${DBHelper.COLUMN_DREAM_DATE}",
            null
        ).use { c ->
            while (c.moveToNext()) dates += c.getString(0)
        }
        return dates
    }

    /** Число созвездий: групп из 2+ снов, связанных общими тегами. */
    fun getConstellationCount(): Int =
        ConstellationLogic.countGroups(getDreamsOldestFirst().map { it.tags })

    // ---------- Достижения ----------

    fun getAchievements(): List<AchievementState> {
        val unlocked = loadUnlocked(dbHelper.readableDatabase)
        return AchievementType.entries.map { AchievementState(it, unlocked[it.code]) }
    }

    fun getUnlockedAchievementsCount(): Int = getAchievements().count { it.unlockedAt != null }

    /** Открытые достижения не отзываются, даже если сон потом удалили. */
    private fun unlockEarnedAchievements(db: SQLiteDatabase): List<AchievementType> {
        val already = loadUnlocked(db).keys
        val progress = measureProgress(db)
        val today = DateUtils.todayIso()

        val newly = AchievementType.entries.filter { it.code !in already && it.isEarned(progress) }
        newly.forEach { type ->
            val cv = ContentValues().apply {
                put(DBHelper.COLUMN_CODE, type.code)
                put(DBHelper.COLUMN_UNLOCKED_AT, today)
            }
            db.insertWithOnConflict(DBHelper.TABLE_ACHIEVEMENTS, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
            Log.d(logTag, "achievement unlocked: ${type.code}")
        }
        return newly
    }

    private fun loadUnlocked(db: SQLiteDatabase): Map<String, String> {
        val map = HashMap<String, String>()
        db.query(DBHelper.TABLE_ACHIEVEMENTS, null, null, null, null, null, null).use { c: Cursor ->
            val codeCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_CODE)
            val dateCol = c.getColumnIndexOrThrow(DBHelper.COLUMN_UNLOCKED_AT)
            while (c.moveToNext()) map[c.getString(codeCol)] = c.getString(dateCol)
        }
        return map
    }

    private fun measureProgress(db: SQLiteDatabase): AchievementProgress {
        val dreams = DatabaseUtils.queryNumEntries(db, DBHelper.TABLE_DREAMS).toInt()
        val withMood = DatabaseUtils.queryNumEntries(
            db, DBHelper.TABLE_DREAMS, "${DBHelper.COLUMN_MOOD} is not null", null
        ).toInt()
        val withTags = DatabaseUtils.longForQuery(
            db, "select count(distinct ${DBHelper.COLUMN_DREAM_ID}) from ${DBHelper.TABLE_DREAM_TAGS}", null
        ).toInt()
        val maxTagUses = DatabaseUtils.longForQuery(
            db,
            "select coalesce(max(c), 0) from " +
                    "(select count(*) as c from ${DBHelper.TABLE_DREAM_TAGS} group by ${DBHelper.COLUMN_TAG_ID})",
            null
        ).toInt()
        return AchievementProgress(
            dreams = dreams,
            dreamsWithMood = withMood,
            dreamsWithTags = withTags,
            maxTagUses = maxTagUses,
            longestStreak = Streaks.longest(loadDistinctDates(db))
        )
    }
}
