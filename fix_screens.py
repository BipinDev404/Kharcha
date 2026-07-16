import re

with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

# 1. State and Blur
bad_state = """    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }"""
good_state = """    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDonateDialog by remember { mutableStateOf(false) }"""
if good_state not in text:
    text = text.replace(bad_state, good_state)

bad_blur = ".blur(if (showDeveloperDialog || showClearDialog || showAboutDialog) 16.dp else 0.dp)"
good_blur = ".blur(if (showDeveloperDialog || showClearDialog || showAboutDialog || showDonateDialog) 16.dp else 0.dp)"
if good_blur not in text:
    text = text.replace(bad_blur, good_blur)

# 2. Add Donate SettingsRow
bad_settings_row = """                SettingsRow(
                    icon = Icons.Filled.Info,
                    title = "Version",
                    subtitle = "1.0.0"
                )"""
good_settings_row = """                SettingsRow(
                    icon = Icons.Filled.Info,
                    title = "Version",
                    subtitle = "1.0.0"
                )
                
                SettingsRow(
                    icon = Icons.Filled.Favorite,
                    title = "Donate",
                    subtitle = "Support future updates",
                    onClick = { showDonateDialog = true }
                )"""
if good_settings_row not in text:
    text = text.replace(bad_settings_row, good_settings_row)

# 3. Remove donate from developer dialog
pattern = re.compile(r'                                Spacer\(modifier = Modifier\.height\(16\.dp\)\)\s*Text\("If you like this app, consider supporting future updates.", style = MaterialTheme\.typography\.bodyMedium, color = MaterialTheme\.colorScheme\.onSurfaceVariant\)\s*val upiId = "placeholder@upi" // Add your UPI ID here\s*val payeeName = "Bipin Yadav"\s*Row\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*horizontalArrangement = Arrangement\.spacedBy\(8\.dp\)\s*\) \{\s*Button\(\s*onClick = \{ uriHandler\.openUri\("upi://pay\?pa=\$upiId&pn=\$payeeName&am=50&cu=INR"\) \},\s*modifier = Modifier\.weight\(1f\)\s*\) \{\s*Text\("Donate ₹50"\)\s*\}\s*Button\(\s*onClick = \{ uriHandler\.openUri\("upi://pay\?pa=\$upiId&pn=\$payeeName&am=100&cu=INR"\) \},\s*modifier = Modifier\.weight\(1f\)\s*\) \{\s*Text\("Donate ₹100"\)\s*\}\s*\}\s*Row\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*horizontalArrangement = Arrangement\.spacedBy\(8\.dp\)\s*\) \{\s*Button\(\s*onClick = \{ uriHandler\.openUri\("upi://pay\?pa=\$upiId&pn=\$payeeName&am=200&cu=INR"\) \},\s*modifier = Modifier\.weight\(1f\)\s*\) \{\s*Text\("Donate ₹200"\)\s*\}\s*OutlinedButton\(\s*onClick = \{ uriHandler\.openUri\("upi://pay\?pa=\$upiId&pn=\$payeeName&cu=INR"\) \},\s*modifier = Modifier\.weight\(1f\)\s*\) \{\s*Text\("Custom Amount"\)\s*\}\s*\}')
text = pattern.sub("", text)

donate_dialog = """                if (showDonateDialog) {
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    AlertDialog(
                        onDismissRequest = { showDonateDialog = false },
                        shape = RoundedCornerShape(24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = {
                            Text("Donate", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("If you like this app, consider supporting future updates.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                val upiId = "bipinforpersonal@okhdfcbank"
                                val payeeName = "Bipin Yadav"
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { uriHandler.openUri("upi://pay?pa=$upiId&pn=$payeeName&am=50&cu=INR") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("₹50")
                                    }
                                    Button(
                                        onClick = { uriHandler.openUri("upi://pay?pa=$upiId&pn=$payeeName&am=100&cu=INR") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("₹100")
                                    }
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { uriHandler.openUri("upi://pay?pa=$upiId&pn=$payeeName&am=200&cu=INR") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("₹200")
                                    }
                                    OutlinedButton(
                                        onClick = { uriHandler.openUri("upi://pay?pa=$upiId&pn=$payeeName&cu=INR") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Custom")
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDonateDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }"""

# Remove existing donate_dialog if there's any
pattern2 = re.compile(r'\s*if \(showDonateDialog\) \{.*?\s*Spacer\(modifier = Modifier\.height\(32\.dp\)\)', re.DOTALL)
text = pattern2.sub("\n                Spacer(modifier = Modifier.height(32.dp))", text)

# Now inject donate_dialog right before the final `Spacer(modifier = Modifier.height(32.dp))` that is at the end of SettingsScreen
parts = text.rsplit("Spacer(modifier = Modifier.height(32.dp))", 2)
# We want to replace the SECOND to last one (since the very last one might be outside or at the end of SettingsScreen lazycolumn)
# Actually, let's just find the end of `SettingsScreen` and insert it there.
# Let's find:
end_pattern = """                        confirmButton = {
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
}"""
good_end_pattern = """                        confirmButton = {
                            TextButton(onClick = { showDeveloperDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
                
""" + donate_dialog + """
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}"""

if end_pattern in text:
    text = text.replace(end_pattern, good_end_pattern)
else:
    print("Could not find the end pattern!")

with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.write(text)
print("Finished patching.")
