package hntech.hntechserver.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun isNewCheck(today: String): String {
    val dateInterval = 3L
    val startDateStr: String = LocalDateTime.now().minusDays(dateInterval)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val endDateStr: String = LocalDateTime.now().plusDays(dateInterval)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val targetDate: Date = sdf.parse(today.split(" ")[0])
    val startDate: Date = sdf.parse(startDateStr)
    val endDate: Date = sdf.parse(endDateStr)

    return if (targetDate in startDate..endDate) "true" else "false"
}