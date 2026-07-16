package com.example.widgets

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.first

class RecentTransactionsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val expenses = database.expenseDao().getAllExpenses().first()
        val recent = expenses.sortedByDescending { it.date }.take(2)
        
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_bg_dark)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.TopStart
            ) {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_history),
                            contentDescription = "History",
                            modifier = GlanceModifier.size(24.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Text("Recent Activity", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color(0xFF81C784)), fontWeight = FontWeight.Bold, fontSize = 14.sp))
                    }
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    recent.forEach { exp ->
                        Row(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 6.dp)) {
                            Text(exp.category, style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontSize = 14.sp), modifier = GlanceModifier.defaultWeight())
                            val isIncome = exp.category == "Received" || exp.category == "Borrowed"
                            val amountColor = if (isIncome) Color(0xFF66BB6A) else Color.LightGray
                            val prefix = if (isIncome) "+" else "-"
                            Text("${prefix}₹${exp.amount}", style = TextStyle(color = androidx.glance.unit.ColorProvider(amountColor), fontWeight = FontWeight.Medium, fontSize = 14.sp))
                        }
                    }
                }
            }
        }
    }
}

class RecentTransactionsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RecentTransactionsWidget()
}
