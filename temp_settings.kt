                Text("Cancel")
            }
        }
    )
}

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .blur(if (showDeveloperDialog || showClearDialog) 16.dp else 0.dp)
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
                    contract = ActivityResultContracts.CreateDocument("text/csv"),
                    onResult = { uri ->
                        uri?.let {
                            coroutineScope.launch {
                                try {
                                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                                        val writer = outputStream.bufferedWriter()
                                        writer.write("Transaction ID,Type,Amount,Category,Merchant,Payment Method,Date,Time,Timestamp_MS,Notes\n")
                                        expenses.forEach { exp ->
                                            val type = when (exp.category) {
                                                "Received", "Borrowed" -> "Income"
                                                "Lent" -> "Lent"
                                                else -> "Expense"
                                            }
                                            val safeMerchant = exp.merchant.replace(",", " ").replace("\"", "")
                                            val safeCategory = exp.category.replace(",", " ").replace("\"", "")
                                            val safePayment = exp.paymentMethod.replace(",", " ").replace("\"", "")
                                            
                                            val cal = java.util.Calendar.getInstance().apply { timeInMillis = exp.date }
                                            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(cal.time)
                                            val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(cal.time)
                                            
                                            writer.write("${exp.id},${type},${exp.amount},${safeCategory},${safeMerchant},${safePayment},${dateStr},${timeStr},${exp.date},\"${exp.notes.replace("\"", "\"\"")}\"\n")
                                        }
                                        writer.flush()
                                    }
                                    Toast.makeText(context, "Data exported to CSV successfully", Toast.LENGTH_SHORT).show()
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
                                        val reader = inputStream.bufferedReader()
                                        val lines = reader.readLines()
                                        if (lines.isNotEmpty()) {
                                            lines.drop(1).forEach { line ->
                                                val parts = line.split(",", limit = 10)
                                                if (parts.size >= 9) {
                                                    val amount = parts[2].toDoubleOrNull() ?: 0.0
                                                    val category = parts[3]
                                                    val merchant = parts[4]
                                                    val paymentMethod = parts[5]
                                                    val date = parts[8].toLongOrNull() ?: 0L
                                                    val notes = if (parts.size >= 10) {
                                                        val rawNotes = parts[9]
                                                        if (rawNotes.startsWith("\"") && rawNotes.endsWith("\"")) {
                                                            rawNotes.substring(1, rawNotes.length - 1).replace("\"\"", "\"")
                                                        } else rawNotes
                                                    } else ""
                                                    
                                                    viewModel.addManualExpense(com.example.data.Expense(amount = amount, category = category, merchant = merchant, paymentMethod = paymentMethod, date = date, notes = notes))
                                                }
                                            }
                                        }
                                    }
                                    Toast.makeText(context, "Data imported from CSV successfully", Toast.LENGTH_SHORT).show()
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
                    subtitle = "Export transactions to a CSV file",
                    onClick = {
                        exportLauncher.launch("expenses.csv")
                    }
                )
                
                SettingsRow(
                    icon = Icons.Filled.Download,
                    title = "Import Data",
                    subtitle = "Import transactions from a CSV file",
                    onClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    }
                )
                
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
                
                androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                
                SettingsRow(
                    icon = Icons.Filled.Person,
                    title = "Developer Info",
                    subtitle = "About the developer",
                    onClick = { showDeveloperDialog = true }
                )
                
                androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                if (showDeveloperDialog) {
                    var isFullScreenImage by remember { mutableStateOf(false) }
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    
                    if (isFullScreenImage) {
                        androidx.compose.ui.window.Dialog(
                            onDismissRequest = { isFullScreenImage = false },
                            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)).clickable { isFullScreenImage = false }) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = R.drawable.developer_pic),
                                    contentDescription = "Full Screen Profile",
                                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                                    contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
                                )
                                IconButton(
                                    onClick = { isFullScreenImage = false },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha=0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = "Close", tint = Color.White)
                                }
                            }
                        }
                    }

                    AlertDialog(
                        onDismissRequest = { showDeveloperDialog = false },
                        shape = RoundedCornerShape(24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = R.drawable.developer_pic),
                                    contentDescription = "Developer",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        .clickable { isFullScreenImage = true }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Bipin Yadav", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("BCA Student & Developer", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://instagram.com/yadavv_bipin") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(Color(0xFFE1306C).copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(painterResource(id = R.drawable.ic_instagram), contentDescription = "Instagram", tint = Color(0xFFE1306C), modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("Instagram", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text("Follow on Instagram", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://github.com/BipinDev404") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(painterResource(id = R.drawable.ic_github), contentDescription = "GitHub", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("GitHub", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text("View my projects", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://example.com/donate") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Favorite, contentDescription = "Donate", tint = MaterialTheme.colorScheme.error)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("Donate", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                                            Text("Support the developer", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f))
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDeveloperDialog = false }) {
                                Text("Close")
                            }
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
