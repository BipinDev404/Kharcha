package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.data.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: MainViewModel) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val monthlyLimit by viewModel.monthlyLimit.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var filterTitle by remember { mutableStateOf("") }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    
    val chartColors = listOf(
        Color(0xFF6C63FF), Color(0xFF00C4B5), Color(0xFFFF6584),
        Color(0xFFFF9F43), Color(0xFF4D96FF), Color(0xFF2ED573)
    )
    
    if (selectedFilter != null) {
        val filteredExpenses = expenses.filter {
            when {
                selectedFilter == "All" -> true
                selectedFilter == "AllCategories" -> it.category != "Lent" && it.category != "Received" && it.category != "Borrowed"
                selectedFilter == "CurrentMonth" -> {
                    val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
                    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                    val cal = java.util.Calendar.getInstance()
                    cal.timeInMillis = it.date
                    it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" &&
                    cal.get(java.util.Calendar.MONTH) == currentMonth && cal.get(java.util.Calendar.YEAR) == currentYear
                }
                selectedFilter == "Spent" -> it.category != "Lent" && it.category != "Received" && it.category != "Borrowed"
                selectedFilter == "Lent" -> it.category == "Lent"
                selectedFilter == "Received" -> it.category == "Received" || it.category == "Borrowed"
                selectedFilter?.startsWith("Month:") == true -> {
                    val targetMonth = selectedFilter!!.substringAfter("Month:")
                    val cal = java.util.Calendar.getInstance()
                    cal.timeInMillis = it.date
                    val month = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(cal.time)
                    month == targetMonth
                }
                selectedFilter?.startsWith("Category:") == true -> {
                    val targetCat = selectedFilter!!.substringAfter("Category:")
                    it.category == targetCat
                }
                else -> true
            }
        }.sortedByDescending { it.date }

        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            CenterAlignedTopAppBar(
                title = { Text(filterTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { selectedFilter = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
            
            val totalFilteredAmount = filteredExpenses.sumOf { it.amount }
            
            if (filteredExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No transactions found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Text("Total in this period", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("₹${String.format(Locale.US, "%.0f", totalFilteredAmount)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onEdit = { expenseToEdit = it },
                            onDelete = { viewModel.deleteExpense(it) }
                        )
                    }
                }
            }
        }
        
        if (expenseToEdit != null) {
            EditExpenseDialog(
                expense = expenseToEdit!!,
                onDismiss = { expenseToEdit = null },
                onConfirm = { updatedExpense ->
                    viewModel.updateExpense(updatedExpense)
                    expenseToEdit = null
                }
            )
        }
        return
    }

    // Main Dashboard
    val totalSpent = expenses.filter { it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" }.sumOf { it.amount }
    val totalLent = expenses.filter { it.category == "Lent" }.sumOf { it.amount }
    val totalReceived = expenses.filter { it.category == "Received" || it.category == "Borrowed" }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 100.dp)
    ) {
        item {
            Text("Statistics", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (expenses.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No transactions to analyze.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            // 1. Overview Card
            item {
                DashboardCard(
                    title = "Total Spending",
                    subtitle = "All time spending",
                    onViewReport = { filterTitle = "All Spent"; selectedFilter = "Spent" }
                ) {
                    Text(
                        text = "₹${String.format(Locale.US, "%.0f", totalSpent)}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val last7Days = (0..6).map { i ->
                        val dayCal = java.util.Calendar.getInstance()
                        dayCal.add(java.util.Calendar.DAY_OF_YEAR, -(6 - i))
                        dayCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                        dayCal.set(java.util.Calendar.MINUTE, 0)
                        dayCal.set(java.util.Calendar.SECOND, 0)
                        dayCal.set(java.util.Calendar.MILLISECOND, 0)
                        val start = dayCal.timeInMillis
                        dayCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                        val end = dayCal.timeInMillis
                        val daySpend = expenses.filter { 
                            it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" &&
                            it.date in start until end
                        }.sumOf { it.amount }
                        val label = SimpleDateFormat("dd", Locale.getDefault()).format(start)
                        Pair(label, daySpend)
                    }
                    val maxSpend = last7Days.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1.0
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        last7Days.forEach { (label, daySpend) ->
                            val heightFraction = (daySpend / maxSpend).toFloat().coerceIn(0f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Spacer(modifier = Modifier.weight(1f - heightFraction + 0.01f))
                                Box(
                                    modifier = Modifier
                                        .weight(heightFraction + 0.01f)
                                        .fillMaxWidth(0.5f)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(chartColors[0])
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // 2. Weekly/Monthly Expense Card (Categories)
            item {
                DashboardCard(
                    title = "Top Categories",
                    subtitle = "Spend by category",
                    onViewReport = { filterTitle = "All Categories"; selectedFilter = "AllCategories" }
                ) {
                    val catGroups = expenses
                        .filter { it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" }
                        .groupBy { it.category }
                        .mapValues { it.value.sumOf { exp -> exp.amount } }
                        .toList()
                        .sortedByDescending { it.second }
                        .take(5)
                        
                    if (catGroups.isEmpty()) {
                        Text("No category data.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            DonutChart(
                                values = catGroups.map { it.second.toFloat() },
                                colors = chartColors,
                                modifier = Modifier.size(110.dp).padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(24.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                catGroups.forEachIndexed { index, (cat, amt) ->
                                    val color = chartColors[index % chartColors.size]
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            filterTitle = "Spent on $cat"
                                            selectedFilter = "Category:$cat"
                                        }.padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(cat, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), maxLines = 1)
                                        Text("₹${String.format(Locale.US, "%.0f", amt)}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Spending Limit Card
            item {
                DashboardCard(
                    title = "Spending Limit",
                    subtitle = "Current month",
                    onViewReport = { filterTitle = "Current Month Spend"; selectedFilter = "CurrentMonth" }
                ) {
                    val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
                    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                    val currentMonthSpent = expenses.filter { 
                        val cal = java.util.Calendar.getInstance()
                        cal.timeInMillis = it.date
                        it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" &&
                        cal.get(java.util.Calendar.MONTH) == currentMonth && cal.get(java.util.Calendar.YEAR) == currentYear
                    }.sumOf { it.amount }
                    
                    val ratio = if (monthlyLimit > 0) (currentMonthSpent / monthlyLimit).toFloat().coerceIn(0f, 1f) else 0f
                    
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.BottomCenter) {
                        SemiCircleProgress(
                            progress = ratio,
                            color = if (ratio > 0.9f) Color(0xFFFF6584) else Color(0xFF00C4B5),
                            modifier = Modifier.fillMaxWidth(0.8f).aspectRatio(2f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                text = "₹${String.format(Locale.US, "%.0f", currentMonthSpent)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "of ₹${String.format(Locale.US, "%.0f", monthlyLimit)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 4. Monthly Breakdown Card
            item {
                DashboardCard(
                    title = "Monthly Breakdown",
                    subtitle = "Spend in last 6 months",
                    onViewReport = { filterTitle = "Monthly View"; selectedFilter = "AllCategories" }
                ) {
                    val monthlyGroups = expenses
                        .filter { it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" }
                        .groupBy { 
                            val cal = java.util.Calendar.getInstance()
                            cal.timeInMillis = it.date
                            val label = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)
                            val fullLabel = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(cal.time)
                            val sortKey = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(cal.time)
                            Triple(label, fullLabel, sortKey)
                        }
                        .toList()
                        .sortedBy { it.first.third }
                        .takeLast(7)
                        
                    if (monthlyGroups.isEmpty()) {
                        Text("No monthly data.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val maxAmount = monthlyGroups.maxOfOrNull { it.second.sumOf { exp -> exp.amount } } ?: 1.0
                        val allCatsInMonths = monthlyGroups.flatMap { it.second }.map { it.category }.distinct().sorted()
                        val catColorMap = allCatsInMonths.withIndex().associate { it.value to chartColors[it.index % chartColors.size] }
                        
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                monthlyGroups.forEach { (monthInfo, monthExpenses) ->
                                    val monthLabel = monthInfo.first
                                    val totalMonthSpend = monthExpenses.sumOf { it.amount }
                                    val heightFraction = (totalMonthSpend / maxAmount).toFloat().coerceIn(0f, 1f)
                                    
                                    val catSpends = monthExpenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }.toList().sortedByDescending { it.second }
                                    
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f).clickable {
                                            filterTitle = "Transactions in ${monthInfo.second}"
                                            selectedFilter = "Month:${monthInfo.second}"
                                        }
                                    ) {
                                        Spacer(modifier = Modifier.weight(1f - heightFraction + 0.01f))
                                        Column(
                                            modifier = Modifier
                                                .weight(heightFraction + 0.01f)
                                                .fillMaxWidth(0.5f)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        ) {
                                            catSpends.forEach { (cat, amt) ->
                                                val catFraction = (amt / totalMonthSpend).toFloat()
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(catFraction.coerceAtLeast(0.01f))
                                                        .background(catColorMap[cat] ?: Color.Gray)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(monthLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val topCats = monthlyGroups.flatMap { it.second }.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }.toList().sortedByDescending { it.second }.take(4)
                                topCats.forEach { (cat, _) ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(catColorMap[cat] ?: Color.Gray))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(cat, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    onViewReport: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onViewReport)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "View Report",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, value: Double, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.clickable { onClick() }.padding(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("₹${String.format(Locale.US, "%.0f", value)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SemiCircleProgress(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 24.dp.toPx()
        val radius = (size.width - strokeWidth) / 2
        val arcSize = Size(radius * 2, radius * 2)
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
        
        drawArc(
            color = color.copy(alpha = 0.15f),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f * progress,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun DonutChart(values: List<Float>, colors: List<Color>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val total = values.sum().coerceAtLeast(1f)
        var startAngle = -90f
        val strokeWidth = 32.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
        
        for (i in values.indices) {
            val sweepAngle = (values[i] / total) * 360f
            drawArc(
                color = colors[i % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngle
        }
    }
}
