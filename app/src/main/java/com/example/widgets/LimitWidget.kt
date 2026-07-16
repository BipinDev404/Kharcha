package com.example.widgets

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.first

class LimitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val expenses = database.expenseDao().getAllExpenses().first()
        val spent = expenses.filter { it.category != "Received" && it.category != "Borrowed" && it.category != "Lent" }.sumOf { it.amount }
        val limit = 5000.0 // Hardcoded for widget demo
        
        val statusColor = if (spent > limit) Color(0xFFEF5350) else Color(0xFF66BB6A)
        
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(Color(0xFF282828)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text("Monthly Limit", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.Gray)))
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text("₹${spent} / ₹${limit}", style = TextStyle(color = androidx.glance.unit.ColorProvider(statusColor), fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

class LimitWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LimitWidget()
}
