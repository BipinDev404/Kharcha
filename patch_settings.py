with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

bad = """                if (showClearDialog) {
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
}"""

good = """                if (showClearDialog) {
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
                
                Text("Developer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingsRow(
                    icon = Icons.Filled.Person,
                    title = "Bipin Yadav",
                    subtitle = "BCA Student"
                )
                
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                
                SettingsRow(
                    icon = Icons.Filled.Link,
                    title = "Instagram",
                    subtitle = "@yadavv_bipin",
                    onClick = { uriHandler.openUri("https://instagram.com/yadavv_bipin") }
                )
                SettingsRow(
                    icon = Icons.Filled.Code,
                    title = "GitHub",
                    subtitle = "BipinDev404",
                    onClick = { uriHandler.openUri("https://github.com/BipinDev404") }
                )
                SettingsRow(
                    icon = Icons.Filled.Favorite,
                    title = "Donate",
                    subtitle = "Support the developer",
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = { uriHandler.openUri("https://example.com/donate") }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}"""

if bad in text:
    print("Found Settings block!")
    text = text.replace(bad, good)
    with open("app/src/main/java/com/example/Screens.kt", "w") as f:
        f.write(text)
else:
    print("Settings block NOT found")
