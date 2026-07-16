package com.example.widgets

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

class SummaryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val expenses = database.expenseDao().getAllExpenses().first()
        val spent = expenses.filter { it.category != "Received" && it.category != "Borrowed" && it.category != "Lent" }.sumOf { it.amount }
        
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(Color(0xFF282828)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text("Total Spent", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color(0xFFB0BEC5))))
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text("₹${spent}", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontWeight = FontWeight.Bold, fontSize = 24.sp))
                }
            }
        }
    }
}

class SummaryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SummaryWidget()
}
