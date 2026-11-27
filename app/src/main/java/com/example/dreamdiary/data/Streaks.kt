package com.example.dreamdiary.data

/** Серии дней подряд, в которые записан хотя бы один сон. */
object Streaks {

    /** [sortedDates] - уникальные даты yyyy-MM-dd по возрастанию. */
    fun longest(sortedDates: List<String>): Int {
        if (sortedDates.isEmpty()) return 0
        val days = sortedDates.map { DateUtils.epochDay(it) }
        var best = 1
        var run = 1
        for (i in 1 until days.size) {
            run = if (days[i] - days[i - 1] == 1L) run + 1 else 1
            if (run > best) best = run
        }
        return best
    }

    /** Текущая серия: считается, если последний сон записан сегодня или вчера. */
    fun current(sortedDates: List<String>, todayIso: String = DateUtils.todayIso()): Int {
        if (sortedDates.isEmpty()) return 0
        val days = sortedDates.map { DateUtils.epochDay(it) }
        val gapToToday = DateUtils.epochDay(todayIso) - days.last()
        if (gapToToday > 1) return 0
        var run = 1
        for (i in days.size - 1 downTo 1) {
            if (days[i] - days[i - 1] == 1L) run++ else break
        }
        return run
    }
}
