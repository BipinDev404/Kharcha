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

class CategoryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val expenses = database.expenseDao().getAllExpenses().first()
        val categoryData = expenses.filter { it.category != "Received" && it.category != "Borrowed" && it.category != "Lent" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { exp -> exp.amount } }
            .maxByOrNull { it.value }
            
        val category = categoryData?.key ?: "None"
        val maxAmount = categoryData?.value ?: 0.0
            
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_bg_dark)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_category),
                            contentDescription = "Category",
                            modifier = GlanceModifier.size(24.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Text("Top Category", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.Gray), fontSize = 14.sp))
                    }
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    Text(category, style = TextStyle(color = androidx.glance.unit.ColorProvider(Color(0xFF29B6F6)), fontWeight = FontWeight.Bold, fontSize = 20.sp))
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text("₹${maxAmount}", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontSize = 14.sp))
                }
            }
        }
    }
}

class CategoryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CategoryWidget()
}
