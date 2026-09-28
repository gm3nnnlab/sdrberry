package org.chornobyl.hamdash.ui.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatUtcClock(epochMillis: Long): String {
    val fmt = SimpleDateFormat("HH:mm:ss", Locale.US)
    fmt.timeZone = TimeZone.getTimeZone("UTC")
    return fmt.format(Date(epochMillis))
}

fun formatLocalClock(epochMillis: Long): String {
    val fmt = SimpleDateFormat("HH:mm:ss", Locale.US)
    fmt.timeZone = TimeZone.getDefault()
    return fmt.format(Date(epochMillis))
}

fun formatDate(epochMillis: Long): String {
    val fmt = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
    return fmt.format(Date(epochMillis))
}

fun formatClockPair(epochMillis: Long, zone: TimeZone): String {
    val fmt = SimpleDateFormat("HH:mm:ss", Locale.US)
    fmt.timeZone = zone
    return fmt.format(Date(epochMillis))
}
