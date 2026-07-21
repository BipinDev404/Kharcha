#!/bin/bash
cat app/src/main/java/com/example/Screens.kt | sed -n '1,1402p' > temp.kt

cat << 'INNER_EOF' >> temp.kt
@Composable
fun CalendarScreen(viewModel: MainViewModel) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var expenseToEdit by remember { mutableStateOf<com.example.data.Expense?>(null) }
    
    val selectedDateExpenses = remember(expenses, selectedDateMillis) {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis
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
        
        CustomCalendarView(
            selectedDateMillis = selectedDateMillis,
            onDateSelected = { selectedDateMillis = it },
            expenses = expenses
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
INNER_EOF

cat app/src/main/java/com/example/Screens.kt | sed -n '1488,$p' >> temp.kt
mv temp.kt app/src/main/java/com/example/Screens.kt
