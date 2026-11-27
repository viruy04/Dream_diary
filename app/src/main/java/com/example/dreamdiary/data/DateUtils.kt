package com.example.dreamdiary.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

/**
 * Даты в БД хранятся строкой yyyy-MM-dd: она сортируется как текст,
 * поэтому по ней удобно делать диапазоны (календарь, неделя).
 * java.time не используется: minSdk = 24.
 */
object DateUtils {

    private val ru = Locale("ru")

    fun isoOf(year: Int, month: Int, day: Int): String =
        String.format(Locale.US, "%04d-%02d-%02d", year, month, day)

    private fun isoOf(c: Calendar): String =
        isoOf(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))

    fun todayIso(): String = isoOf(Calendar.getInstance())

    fun isoDaysAgo(days: Int): String =
        isoOf(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -days) })

    /** Диапазон дат месяца: month - от 0 до 11, как в Calendar. */
    fun monthRange(year: Int, month: Int): Pair<String, String> =
        isoOf(year, month + 1, 1) to isoOf(year, month + 1, 31)

    /**
     * Разбор даты рождения «ДД.ММ.ГГГГ» -> yyyy-MM-dd.
     * Возвращает null, если дата несуществующая, раньше 1900 года или в будущем.
     */
    fun parseBirthDate(text: String): String? {
        val match = Regex("""^(\d{1,2})\.(\d{1,2})\.(\d{4})$""").matchEntire(text.trim()) ?: return null
        val (d, m, y) = match.destructured
        val day = d.toInt()
        val month = m.toInt()
        val year = y.toInt()
        if (year < 1900) return null

        val calendar = Calendar.getInstance().apply {
            isLenient = false
            clear()
            set(year, month - 1, day)
        }
        try {
            calendar.timeInMillis // при несуществующей дате (31.02) бросает исключение
        } catch (e: IllegalArgumentException) {
            return null
        }

        val iso = isoOf(year, month, day)
        return if (iso > todayIso()) null else iso
    }

    /** yyyy-MM-dd -> ДД.ММ.ГГГГ */
    fun isoToDisplay(iso: String): String {
        val p = iso.split("-")
        return "${p[2]}.${p[1]}.${p[0]}"
    }

    /** Номер дня от 1970 года, нужен для серий подряд. */
    fun epochDay(iso: String): Long {
        val p = iso.split("-").map { it.toInt() }
        val calendar = GregorianCalendar(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(p[0], p[1] - 1, p[2])
        }
        return Math.floorDiv(calendar.timeInMillis, 86_400_000L)
    }

    fun ageYears(birthIso: String, todayIso: String = todayIso()): Int {
        val b = birthIso.split("-").map { it.toInt() }
        val t = todayIso.split("-").map { it.toInt() }
        var age = t[0] - b[0]
        if (t[1] * 100 + t[2] < b[1] * 100 + b[2]) age--
        return age
    }

    /** «2 ноября 2026, 08:30» */
    fun formatDateTime(millis: Long): String =
        SimpleDateFormat("d MMMM yyyy, HH:mm", ru).format(Date(millis))

    fun formatTime(millis: Long): String =
        SimpleDateFormat("HH:mm", ru).format(Date(millis))
}

/** Склонения для русского языка. */
object RuText {
    fun plural(n: Int, one: String, few: String, many: String): String {
        val mod100 = n % 100
        val mod10 = n % 10
        return when {
            mod100 in 11..14 -> many
            mod10 == 1 -> one
            mod10 in 2..4 -> few
            else -> many
        }
    }

    fun dreams(n: Int) = "$n " + plural(n, "сон", "сна", "снов")
    fun years(n: Int) = "$n " + plural(n, "год", "года", "лет")
    fun days(n: Int) = "$n " + plural(n, "день", "дня", "дней")
}
