package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CustomCalendarView(
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    expenses: List<com.example.data.Expense>
) {
    var currentMonthMillis by remember { mutableStateOf(selectedDateMillis) }
    
    val displayCalendar = java.util.Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
    displayCalendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
    
    val currentMonth = displayCalendar.get(java.util.Calendar.MONTH)
    val currentYear = displayCalendar.get(java.util.Calendar.YEAR)
    
    val daysInMonth = displayCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = displayCalendar.get(java.util.Calendar.DAY_OF_WEEK)
    
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(displayCalendar.time)
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
                cal.add(java.util.Calendar.MONTH, -1)
                currentMonthMillis = cal.timeInMillis
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(monthName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { 
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
                cal.add(java.util.Calendar.MONTH, 1)
                currentMonthMillis = cal.timeInMillis
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
        
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        val numRows = kotlin.math.ceil((daysInMonth + firstDayOfWeek - 1) / 7.0).toInt()
        
        var dayCounter = 1
        for (row in 0 until numRows) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                for (col in 0 until 7) {
                    if (row == 0 && col < firstDayOfWeek - 1 || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val day = dayCounter
                        val cal = java.util.Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
                        cal.set(java.util.Calendar.DAY_OF_MONTH, day)
                        val startOfDay = cal.timeInMillis.let {
                            val c = java.util.Calendar.getInstance().apply { timeInMillis = it }
                            c.set(java.util.Calendar.HOUR_OF_DAY, 0)
                            c.set(java.util.Calendar.MINUTE, 0)
                            c.set(java.util.Calendar.SECOND, 0)
                            c.set(java.util.Calendar.MILLISECOND, 0)
                            c.timeInMillis
                        }
                        val endOfDay = startOfDay + 24 * 60 * 60 * 1000L - 1
                        
                        val dayExpenses = expenses.filter { it.date in startOfDay..endOfDay }
                        val isSelected = run {
                            val selCal = java.util.Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                            selCal.get(java.util.Calendar.YEAR) == currentYear &&
                            selCal.get(java.util.Calendar.MONTH) == currentMonth &&
                            selCal.get(java.util.Calendar.DAY_OF_MONTH) == day
                        }
                        
                        val circleColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            dayExpenses.isNotEmpty() -> {
                                val spent = dayExpenses.filter { it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" }.sumOf { it.amount }
                                val received = dayExpenses.filter { it.category == "Received" || it.category == "Borrowed" }.sumOf { it.amount }
                                if (spent > 0) MaterialTheme.colorScheme.errorContainer
                                else if (received > 0) Color(0xFF4CAF50).copy(alpha = 0.2f)
                                else Color(0xFFFFC107).copy(alpha = 0.2f)
                            }
                            else -> Color.Transparent
                        }
                        
                        val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(circleColor)
                                .clickable { onDateSelected(startOfDay) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = textColor,
                                fontWeight = if (isSelected || dayExpenses.isNotEmpty()) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
