package com.example.dreamdiary.data

/**
 * Знак зодиака. В БД не хранится: вычисляется по дате рождения.
 */
enum class ZodiacSign(val title: String) {
    ARIES("Овен"),
    TAURUS("Телец"),
    GEMINI("Близнецы"),
    CANCER("Рак"),
    LEO("Лев"),
    VIRGO("Дева"),
    LIBRA("Весы"),
    SCORPIO("Скорпион"),
    SAGITTARIUS("Стрелец"),
    CAPRICORN("Козерог"),
    AQUARIUS("Водолей"),
    PISCES("Рыбы");

    companion object {
        fun of(month: Int, day: Int): ZodiacSign {
            val md = month * 100 + day // 21 марта -> 321
            return when {
                md >= 1222 || md <= 119 -> CAPRICORN // знак переходит через границу года
                md <= 218 -> AQUARIUS
                md <= 320 -> PISCES
                md <= 419 -> ARIES
                md <= 520 -> TAURUS
                md <= 620 -> GEMINI
                md <= 722 -> CANCER
                md <= 822 -> LEO
                md <= 922 -> VIRGO
                md <= 1022 -> LIBRA
                md <= 1121 -> SCORPIO
                else -> SAGITTARIUS
            }
        }

        /** [iso] - дата в формате yyyy-MM-dd. */
        fun fromIso(iso: String): ZodiacSign {
            val parts = iso.split("-")
            return of(parts[1].toInt(), parts[2].toInt())
        }
    }
}
