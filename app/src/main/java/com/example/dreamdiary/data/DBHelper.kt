package com.example.dreamdiary.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Класс для работы с БД (лабораторная работа №6): наследник SQLiteOpenHelper.
 *
 * Таблицы:
 *  - profile       - единственный профиль пользователя (id всегда 1);
 *  - dreams        - сны;
 *  - tags          - справочник тегов;
 *  - dream_tags    - связь «сон - тег» (многие ко многим);
 *  - achievements  - открытые достижения (код + дата).
 */
class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val LOG_TAG: String = "myLogs"

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "dreamDiary.db"

        const val COLUMN_ID = "id"

        const val TABLE_PROFILE = "profile"
        const val COLUMN_PROFILE_NAME = "name"
        const val COLUMN_BIRTH_DATE = "birth_date"

        const val TABLE_DREAMS = "dreams"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DREAM_DATE = "dream_date"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_MOOD = "mood"

        const val TABLE_TAGS = "tags"
        const val COLUMN_TAG_NAME = "name"

        const val TABLE_DREAM_TAGS = "dream_tags"
        const val COLUMN_DREAM_ID = "dream_id"
        const val COLUMN_TAG_ID = "tag_id"

        const val TABLE_ACHIEVEMENTS = "achievements"
        const val COLUMN_CODE = "code"
        const val COLUMN_UNLOCKED_AT = "unlocked_at"
    }

    // Без этого SQLite не проверяет внешние ключи и не удаляет связи тегов при удалении сна
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(LOG_TAG, "--- onCreate database ---")

        db.execSQL(
            "create table $TABLE_PROFILE (" +
                    "$COLUMN_ID integer primary key check ($COLUMN_ID = 1), " +
                    "$COLUMN_PROFILE_NAME text not null, " +
                    "$COLUMN_BIRTH_DATE text not null);"
        )

        db.execSQL(
            "create table $TABLE_DREAMS (" +
                    "$COLUMN_ID integer primary key autoincrement, " +
                    "$COLUMN_TITLE text not null, " +
                    "$COLUMN_DESCRIPTION text not null default '', " +
                    "$COLUMN_DREAM_DATE text not null, " +
                    "$COLUMN_CREATED_AT integer not null, " +
                    "$COLUMN_MOOD text);"
        )

        db.execSQL(
            "create table $TABLE_TAGS (" +
                    "$COLUMN_ID integer primary key autoincrement, " +
                    "$COLUMN_TAG_NAME text not null unique);"
        )

        db.execSQL(
            "create table $TABLE_DREAM_TAGS (" +
                    "$COLUMN_DREAM_ID integer not null, " +
                    "$COLUMN_TAG_ID integer not null, " +
                    "primary key ($COLUMN_DREAM_ID, $COLUMN_TAG_ID), " +
                    "foreign key ($COLUMN_DREAM_ID) references $TABLE_DREAMS($COLUMN_ID) on delete cascade, " +
                    "foreign key ($COLUMN_TAG_ID) references $TABLE_TAGS($COLUMN_ID) on delete cascade);"
        )

        db.execSQL(
            "create table $TABLE_ACHIEVEMENTS (" +
                    "$COLUMN_CODE text primary key, " +
                    "$COLUMN_UNLOCKED_AT text not null);"
        )

        db.execSQL("create index idx_dreams_date on $TABLE_DREAMS($COLUMN_DREAM_DATE);")
        db.execSQL("create index idx_dreams_mood on $TABLE_DREAMS($COLUMN_MOOD);")
        db.execSQL("create index idx_dream_tags_tag on $TABLE_DREAM_TAGS($COLUMN_TAG_ID);")
    }

    // Данные пользователя нельзя удалять при обновлении: вместо drop table
    // здесь добавляются шаги миграции, например:
    //   if (oldVersion < 2) { db.execSQL("alter table dreams add column ...") }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(LOG_TAG, "--- onUpgrade database: $oldVersion -> $newVersion ---")
    }
}
