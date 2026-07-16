import os
widgets_dir = "app/src/main/java/com/example/widgets"
drawable_dir = "app/src/main/res/drawable"

with open(f"{drawable_dir}/widget_bg_primary.xml", "w") as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#6200EA" />
    <corners android:radius="16dp" />
</shape>
""")

with open(f"{widgets_dir}/QuickAddWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.R

class QuickAddWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize()
                    .background(ImageProvider(R.drawable.widget_bg_dark))
                    .padding(16.dp)
                    .clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = GlanceModifier.size(56.dp)
                            .background(ImageProvider(R.drawable.widget_bg_primary)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_add),
                            contentDescription = "Add",
                            modifier = GlanceModifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    Text("Add Expense", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontWeight = FontWeight.Medium, fontSize = 16.sp))
                }
            }
        }
    }
}

class QuickAddWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickAddWidget()
}
""")

with open(f"{widgets_dir}/RecentTransactionsWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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
""")

with open(f"{widgets_dir}/LimitWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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

class LimitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val expenses = database.expenseDao().getAllExpenses().first()
        val spent = expenses.filter { it.category != "Received" && it.category != "Borrowed" && it.category != "Lent" }.sumOf { it.amount }
        val limit = 5000.0 // Hardcoded for widget demo
        
        val statusColor = if (spent > limit) Color(0xFFEF5350) else Color(0xFF66BB6A)
        
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_bg_dark)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_limit),
                            contentDescription = "Limit",
                            modifier = GlanceModifier.size(24.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Text("Monthly Limit", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.Gray), fontSize = 14.sp))
                    }
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    Text("Spent: ₹${spent}", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontSize = 16.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text("Limit: ₹${limit}", style = TextStyle(color = androidx.glance.unit.ColorProvider(statusColor), fontWeight = FontWeight.Medium, fontSize = 14.sp))
                }
            }
        }
    }
}

class LimitWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LimitWidget()
}
""")

with open(f"{widgets_dir}/CategoryWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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
""")

print("Done fixing all widgets")
