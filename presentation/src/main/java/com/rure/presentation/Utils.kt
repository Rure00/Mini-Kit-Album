package com.rure.presentation

import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun formatDuration(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%d:%02d".format(m, s)
}

fun parseYear(date: String): Int {
    return runCatching {
        LocalDate.parse(date).year
    }.getOrDefault(0)
}

fun formatDate(date: String): String {
    return runCatching {
        LocalDate.parse(date).format(DateTimeFormatter.ISO_LOCAL_DATE)
    }.getOrDefault(date)
}