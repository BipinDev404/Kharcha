import os

widgets_dir = "app/src/main/java/com/example/widgets"
os.makedirs(widgets_dir, exist_ok=True)

# 1. SummaryWidget
with open(f"{widgets_dir}/SummaryWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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
""")

# 2. QuickAddWidget
with open(f"{widgets_dir}/QuickAddWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity

class QuickAddWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(Color(0xFF6200EA)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.Center
            ) {
                Text("+ Add Expense", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontWeight = FontWeight.Bold, fontSize = 18.sp))
            }
        }
    }
}

class QuickAddWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickAddWidget()
}
""")

# 3. RecentTransactionsWidget
with open(f"{widgets_dir}/RecentTransactionsWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.first

class RecentTransactionsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val expenses = database.expenseDao().getAllExpenses().first()
        val recent = expenses.sortedByDescending { it.date }.take(2)
        
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(Color(0xFF1E1E1E)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.TopStart
            ) {
                Column(modifier = GlanceModifier.fillMaxWidth()) {
                    Text("Recent", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color(0xFF81C784)), fontWeight = FontWeight.Bold))
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    recent.forEach { exp ->
                        Row(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp)) {
                            Text(exp.category, style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White)), modifier = GlanceModifier.defaultWeight())
                            Text("₹${exp.amount}", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.LightGray)))
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

# 4. LimitWidget
with open(f"{widgets_dir}/LimitWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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
""")

# 5. CategoryWidget
with open(f"{widgets_dir}/CategoryWidget.kt", "w") as f:
    f.write("""package com.example.widgets

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

class CategoryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val expenses = database.expenseDao().getAllExpenses().first()
        val category = expenses.filter { it.category != "Received" && it.category != "Borrowed" && it.category != "Lent" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { exp -> exp.amount } }
            .maxByOrNull { it.value }?.key ?: "None"
            
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize().background(Color(0xFF282828)).padding(16.dp).clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text("Top Category", style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.Gray)))
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(category, style = TextStyle(color = androidx.glance.unit.ColorProvider(Color(0xFF29B6F6)), fontWeight = FontWeight.Bold, fontSize = 18.sp))
                }
            }
        }
    }
}

class CategoryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CategoryWidget()
}
""")

print("Done fixing widgets")
