package com.example

import java.util.Calendar

fun isCurrentMonth(timeInMillis: Long): Boolean {
    val current = Calendar.getInstance()
    val date = Calendar.getInstance()
    date.timeInMillis = timeInMillis
    return current.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
           current.get(Calendar.MONTH) == date.get(Calendar.MONTH)
}
