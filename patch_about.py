import re

with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

bad_state = """    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }"""
good_state = """    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }"""

text = text.replace(bad_state, good_state)

bad_blur = ".blur(if (showDeveloperDialog || showClearDialog) 16.dp else 0.dp)"
good_blur = ".blur(if (showDeveloperDialog || showClearDialog || showAboutDialog) 16.dp else 0.dp)"
text = text.replace(bad_blur, good_blur)

pattern = re.compile(r'                Spacer\(modifier = Modifier.height\(32\.dp\)\)\s*androidx.compose.material3.HorizontalDivider\(modifier = Modifier.padding\(vertical = 16\.dp\), color = MaterialTheme.colorScheme.onSurface.copy\(alpha = 0\.1f\)\)\s*SettingsRow\(\s*icon = Icons\.Filled\.Person,\s*title = "Developer Info",\s*subtitle = "About the developer",\s*onClick = \{ showDeveloperDialog = true \}\s*\)\s*androidx.compose.material3.HorizontalDivider\(modifier = Modifier.padding\(vertical = 16\.dp\), color = MaterialTheme.colorScheme.onSurface.copy\(alpha = 0\.1f\)\)')

good_section = """                Spacer(modifier = Modifier.height(32.dp))
                
                Text("More About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsRow(
                    icon = Icons.Filled.AutoAwesome,
                    title = "About",
                    subtitle = "App details and how to use",
                    onClick = { showAboutDialog = true }
                )
                
                SettingsRow(
                    icon = Icons.Filled.Person,
                    title = "Developer Info",
                    subtitle = "About the developer",
                    onClick = { showDeveloperDialog = true }
                )
                
                SettingsRow(
                    icon = Icons.Filled.Info,
                    title = "Version",
                    subtitle = "1.0.0"
                )
                
                if (showAboutDialog) {
                    AlertDialog(
                        onDismissRequest = { showAboutDialog = false },
                        shape = RoundedCornerShape(24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = {
                            Text("About Expense Tracker", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        },
                        text = {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text("This app helps you track your daily expenses, set monthly limits, and visualize your spending habits over time.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                item {
                                    Text("How to use:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                                item {
                                    Text("1. Use the '+' button to add an expense, income, or lent amount.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("2. View your transactions in the 'History' tab.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("3. Analyze your spending by categories in the 'Statistics' tab.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("4. Set a monthly limit and customize themes in 'Settings'.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showAboutDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }"""

if pattern.search(text):
    text = pattern.sub(good_section, text)
    with open("app/src/main/java/com/example/Screens.kt", "w") as f:
        f.write(text)
    print("Success!")
else:
    print("Pattern not found!")
