package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.google.firebase.FirebaseApp

import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun DashboardScreen(viewModel: MainViewModel, onNavigateToCalendar: () -> Unit = {}) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    
    val score by viewModel.score.collectAsStateWithLifecycle()
    val shortInsight by viewModel.shortInsight.collectAsStateWithLifecycle()
    val monthlyLimit by viewModel.monthlyLimit.collectAsStateWithLifecycle()
    
    LaunchedEffect(expenses, monthlyLimit) {
        viewModel.updateDashboardStats(expenses)
    }
    
    val totalSpent = expenses.filter { it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" }.sumOf { it.amount }
    val totalLent = expenses.filter { it.category == "Lent" }.sumOf { it.amount }
    val totalReceived = expenses.filter { it.category == "Received" || it.category == "Borrowed" }.sumOf { it.amount }
    
    val colorLent = Color(0xFFFFC107) // Yellow
    val colorReceived = Color(0xFF4CAF50) // Green
    
    val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val currentMonthSpent = expenses.filter { 
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = it.date
        it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" &&
        cal.get(java.util.Calendar.MONTH) == currentMonth && cal.get(java.util.Calendar.YEAR) == currentYear
    }.sumOf { it.amount }
    
    val remaining = (monthlyLimit - currentMonthSpent).coerceAtLeast(0.0)
    val remainingRatio = if (monthlyLimit > 0) (remaining / monthlyLimit).toFloat() else 0f
    
    val dailySpends = remember(expenses) {
        val calendar = java.util.Calendar.getInstance()
        (6 downTo 0).map { daysAgo ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis
            
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            val endOfDay = calendar.timeInMillis - 1
            
            expenses.filter {
                it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" &&
                it.date in startOfDay..endOfDay
            }.sumOf { it.amount }
        }
    }
    
    var selectedDayIndex by remember { mutableIntStateOf(6) }
    val maxSpend = dailySpends.maxOrNull()?.takeIf { it > 0 } ?: 1.0
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        AnimatedVisibility(visible = monthlyLimit > 0 && currentMonthSpent > monthlyLimit) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Filled.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Budget Exceeded", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                        Text("You've spent ₹${String.format(java.util.Locale.US, "%.0f", currentMonthSpent - monthlyLimit)} over your monthly limit.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(Date()).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Dashboard", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)
            }
            IconButton(onClick = onNavigateToCalendar) {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Calendar"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        val displayDateText = if (selectedDayIndex == 6) {
                            "Today's Spending"
                        } else {
                            val cal = java.util.Calendar.getInstance()
                            cal.add(java.util.Calendar.DAY_OF_YEAR, -(6 - selectedDayIndex))
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(cal.time) + " Spending"
                        }
                        Text(displayDateText, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("₹${String.format(Locale.US, "%.2f", dailySpends[selectedDayIndex])}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Score: $score", color = MaterialTheme.colorScheme.onPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailySpends.forEachIndexed { index, spend ->
                        val heightRatio = (spend / maxSpend).toFloat().coerceAtLeast(0.05f)
                        val isSelected = index == selectedDayIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightRatio)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.15f))
                                .clickable { selectedDayIndex = index }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorReceived.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, colorReceived.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("RECEIVED", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, letterSpacing = 1.sp, color = colorReceived)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${String.format(Locale.US, "%.0f", totalReceived)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorLent.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, colorLent.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LENT", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, letterSpacing = 1.sp, color = colorLent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${String.format(Locale.US, "%.0f", totalLent)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val remainingColor = when {
                remainingRatio <= 0.0 -> MaterialTheme.colorScheme.error
                remainingRatio <= 0.2f -> Color(0xFFFFC107) // Yellow
                else -> MaterialTheme.colorScheme.primary
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = remainingColor.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, remainingColor.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("REMAINING", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, letterSpacing = 1.sp, color = remainingColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${String.format(Locale.US, "%.0f", remaining)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                        Box(modifier = Modifier.fillMaxWidth(remainingRatio).fillMaxHeight().background(remainingColor, CircleShape))
                    }
                }
            }
            
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.GradientStart.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, com.example.ui.theme.GradientStart.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI INSIGHT", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(shortInsight ?: "Analyzing...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent AI Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            Text("View All", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (expenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No expenses yet. Add one!", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(expenses.take(5)) { expense ->
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: MainViewModel) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("Date (Newest)") }
    var sortExpanded by remember { mutableStateOf(false) }
    
    val sortOptions = listOf("Date (Newest)", "Date (Oldest)", "Amount (High to Low)", "Amount (Low to High)")
    
    val filteredExpenses = expenses.filter { 
        it.merchant.contains(searchQuery, ignoreCase = true) || 
        it.category.contains(searchQuery, ignoreCase = true) ||
        it.amount.toString().contains(searchQuery)
    }.let { list ->
        when (sortBy) {
            "Date (Newest)" -> list.sortedByDescending { it.date }
            "Date (Oldest)" -> list.sortedBy { it.date }
            "Amount (High to Low)" -> list.sortedByDescending { it.amount }
            "Amount (Low to High)" -> list.sortedBy { it.amount }
            else -> list
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("All Transactions", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search merchant, category, or amount") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExposedDropdownMenuBox(
            expanded = sortExpanded,
            onExpandedChange = { sortExpanded = !sortExpanded }
        ) {
            OutlinedTextField(
                value = sortBy,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sort By") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(16.dp)
            )
            ExposedDropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false }
            ) {
                sortOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sortBy = option
                            sortExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (filteredExpenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No transactions found.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredExpenses) { expense ->
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Add Expense") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        
        ManualExpenseEntry(viewModel, onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualExpenseEntry(viewModel: MainViewModel, onBack: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("Expense") }
    var category by remember { mutableStateOf("Other") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var dateExpanded by remember { mutableStateOf(false) }
    var selectedDaysAgo by remember { mutableIntStateOf(0) }
    
    val dateOptions = (0..5).map { daysAgo ->
        when (daysAgo) {
            0 -> "Today"
            1 -> "Yesterday"
            else -> {
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(cal.time)
            }
        }
    }
    
    val expenseCategories = listOf("Food", "Travel", "Fuel", "Shopping", "Bills", "Education", "Medical", "Rent", "Salary", "Investment", "Recharge", "Subscription", "Electronics", "Entertainment", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Expense", "Lent", "Received").forEach { type ->
                if (transactionType == type) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(type, fontSize = 12.sp)
                    }
                } else {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { 
                            transactionType = type
                            if (type != "Expense") category = type else category = "Other"
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(type, fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = dateExpanded,
            onExpandedChange = { dateExpanded = !dateExpanded }
        ) {
            OutlinedTextField(
                value = dateOptions[selectedDaysAgo],
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(16.dp)
            )
            ExposedDropdownMenu(
                expanded = dateExpanded,
                onDismissRequest = { dateExpanded = false }
            ) {
                dateOptions.forEachIndexed { index, selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedDaysAgo = index
                            dateExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            label = { Text(if (transactionType == "Expense") "Merchant / Description" else "Person's Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (transactionType == "Expense") {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    expenseCategories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                category = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        AnimatedButton(
            onClick = {
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, -selectedDaysAgo)
                
                val newExpense = com.example.data.Expense(
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    merchant = merchant.ifBlank { "Unknown" },
                    category = category,
                    paymentMethod = "Manual",
                    date = cal.timeInMillis,
                    notes = notes
                )
                viewModel.addManualExpense(newExpense)
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = amount.isNotBlank()
        ) {
            Text("Save ${if (transactionType == "Expense") "Manual Expense" else transactionType}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(viewModel: MainViewModel) {
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val isChatOpen by viewModel.isChatOpen.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        if (insights == null) {
            viewModel.generateInsights()
        }
    }
    
    if (isChatOpen) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { viewModel.closeChat() },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .imePadding()
            ) {
                Text("Chat with AI", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false).fillMaxWidth(),
                    reverseLayout = true
                ) {
                    items(chatMessages.reversed()) { (role, msg) ->
                        val isUser = role == "User"
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(12.dp)
                            ) {
                                Text(msg, color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
                
                if (isProcessing) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                var messageInput by remember { mutableStateOf("") }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask about your expenses...") },
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(targetValue = if (isPressed) 0.8f else 1f, animationSpec = tween(150))
                    
                    IconButton(
                        onClick = {
                            viewModel.sendChatMessage(messageInput)
                            messageInput = ""
                        },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .size(48.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("AI Insights", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isProcessing && !isChatOpen) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    MarkdownText(insights ?: "No insights generated.")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedButton(
                onClick = { viewModel.generateInsights() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Refresh Insights")
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, onEdit: (Expense) -> Unit, onDelete: (Expense) -> Unit) {
    var showActions by remember { mutableStateOf(false) }
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, animationSpec = tween(150))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = androidx.compose.foundation.LocalIndication.current) { showActions = !showActions },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(expense.category.take(1).uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(if (expense.merchant.isNotBlank()) expense.merchant else expense.category, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        val dateString = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(expense.date))
                        Text("$dateString • ${expense.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
                val amountColor = when (expense.category) {
                    "Received" -> Color(0xFF4CAF50)
                    "Lent" -> Color(0xFFFFC107)
                    else -> MaterialTheme.colorScheme.error
                }
                val prefix = when (expense.category) {
                    "Received" -> "+"
                    else -> "-"
                }
                Text("$prefix₹${String.format(Locale.US, "%.2f", expense.amount)}", fontWeight = FontWeight.Bold, color = amountColor)
            }
            
            AnimatedVisibility(visible = showActions) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .clickable { onEdit(expense) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            .clickable { onDelete(expense) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditExpenseDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onConfirm: (Expense) -> Unit
) {
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var merchant by remember { mutableStateOf(expense.merchant) }
    var category by remember { mutableStateOf(expense.category) }
    var notes by remember { mutableStateOf(expense.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newAmount = amount.toDoubleOrNull() ?: expense.amount
                onConfirm(expense.copy(amount = newAmount, merchant = merchant, category = category, notes = notes))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Text("Settings", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("App Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                
                val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
                SettingsRow(
                    icon = Icons.Filled.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Toggle dark theme across the app",
                    trailing = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode(it) }
                        )
                    }
                )
                
                var showLimitDialog by remember { mutableStateOf(false) }
                val monthlyLimit by viewModel.monthlyLimit.collectAsStateWithLifecycle()
                
                SettingsRow(
                    icon = Icons.Filled.AttachMoney,
                    title = "Monthly Limit",
                    subtitle = "₹${String.format(Locale.US, "%.0f", monthlyLimit)}",
                    onClick = { showLimitDialog = true }
                )
                
                if (showLimitDialog) {
                    var limitInput by remember { mutableStateOf(monthlyLimit.toString()) }
                    AlertDialog(
                        onDismissRequest = { showLimitDialog = false },
                        title = { Text("Set Monthly Limit") },
                        text = {
                            OutlinedTextField(
                                value = limitInput,
                                onValueChange = { limitInput = it },
                                label = { Text("Amount (₹)") },
                                shape = RoundedCornerShape(16.dp)
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { 
                                limitInput.toDoubleOrNull()?.let { viewModel.setMonthlyLimit(it) }
                                showLimitDialog = false 
                            }) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLimitDialog = false }) { Text("Cancel") }
                        }
                    )
                }

                var showThemeDialog by remember { mutableStateOf(false) }
                val themeColorIndex by viewModel.themeColorIndex.collectAsStateWithLifecycle()
                
                SettingsRow(
                    icon = Icons.Filled.ColorLens,
                    title = "Theme Colors",
                    subtitle = "Customize app accent colors",
                    onClick = { showThemeDialog = true }
                )
                
                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        title = { Text("Select Theme Color") },
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val colors = if (isDarkMode) com.example.ui.theme.ThemeColorsDark else com.example.ui.theme.ThemeColorsLight
                                colors.forEachIndexed { index, color ->
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .clickable { 
                                                viewModel.setThemeColorIndex(index)
                                                showThemeDialog = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (index == themeColorIndex) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showThemeDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                SettingsRow(
                    icon = Icons.Filled.Notifications,
                    title = "Notifications",
                    subtitle = "Manage expense alerts"
                )
                
                val context = LocalContext.current
                val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
                val coroutineScope = rememberCoroutineScope()
                
                val exportLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument("application/json"),
                    onResult = { uri ->
                        uri?.let {
                            coroutineScope.launch {
                                try {
                                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                                        val jsonString = Json.encodeToString(expenses)
                                        outputStream.write(jsonString.toByteArray())
                                    }
                                    Toast.makeText(context, "Data exported successfully", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to export data", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )

                val importLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                    onResult = { uri ->
                        uri?.let {
                            coroutineScope.launch {
                                try {
                                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                                        val jsonString = inputStream.bufferedReader().use { reader -> reader.readText() }
                                        val importedExpenses = Json.decodeFromString<List<com.example.data.Expense>>(jsonString)
                                        importedExpenses.forEach { exp ->
                                            viewModel.addManualExpense(exp)
                                        }
                                    }
                                    Toast.makeText(context, "Data imported successfully", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to import data", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
                
                SettingsRow(
                    icon = Icons.Filled.Share,
                    title = "Export Data",
                    subtitle = "Export transactions to a JSON file",
                    onClick = {
                        exportLauncher.launch("expenses.json")
                    }
                )
                
                SettingsRow(
                    icon = Icons.Filled.Download,
                    title = "Import Data",
                    subtitle = "Import transactions from a JSON file",
                    onClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    }
                )
                
                var showClearDialog by remember { mutableStateOf(false) }
                SettingsRow(
                    icon = Icons.Filled.Delete,
                    title = "Clear All Data",
                    subtitle = "Delete all transactions permanently",
                    onClick = {
                        showClearDialog = true
                    }
                )

                if (showClearDialog) {
                    AlertDialog(
                        onDismissRequest = { showClearDialog = false },
                        title = { Text("Clear All Data") },
                        text = { Text("Are you sure you want to delete all transactions? This action cannot be undone.") },
                        confirmButton = {
                            TextButton(onClick = { 
                                viewModel.deleteAllExpenses()
                                showClearDialog = false 
                                Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = iconColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.6f))
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(16.dp))
            trailing()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: MainViewModel) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    
    var expenseToEdit by remember { mutableStateOf<com.example.data.Expense?>(null) }
    
    val selectedDateExpenses = remember(expenses, datePickerState.selectedDateMillis) {
        val selectedMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = selectedMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val endOfDay = calendar.timeInMillis - 1
        
        expenses.filter { it.date in startOfDay..endOfDay }
    }
    
    val totalSpent = selectedDateExpenses.filter { it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Calendar", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 16.dp))
        
        androidx.compose.material3.DatePicker(
            state = datePickerState,
            modifier = Modifier.fillMaxWidth().height(400.dp),
            showModeToggle = false,
            title = null,
            headline = null
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daily Spending", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("₹${String.format(java.util.Locale.US, "%.0f", totalSpent)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (selectedDateExpenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No transactions on this date.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(selectedDateExpenses) { expense ->
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
}
