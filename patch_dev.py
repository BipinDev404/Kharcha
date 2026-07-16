import re

with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

bad = """                if (showDeveloperDialog) {
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    AlertDialog(
                        onDismissRequest = { showDeveloperDialog = false },
                        title = { Text("Developer", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                Text("Bipin Yadav", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("BCA Student", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://instagram.com/yadavv_bipin") }.padding(vertical = 12.dp)
                                ) {
                                    Icon(Icons.Filled.Info, contentDescription = "Instagram", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Instagram: @yadavv_bipin", style = MaterialTheme.typography.bodyLarge)
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://github.com/BipinDev404") }.padding(vertical = 12.dp)
                                ) {
                                    Icon(Icons.Filled.Build, contentDescription = "GitHub", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("GitHub: BipinDev404", style = MaterialTheme.typography.bodyLarge)
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://example.com/donate") }.padding(vertical = 12.dp)
                                ) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Donate", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Donate", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDeveloperDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }"""

good = """                if (showDeveloperDialog) {
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    AlertDialog(
                        onDismissRequest = { showDeveloperDialog = false },
                        shape = RoundedCornerShape(24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = "Developer",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
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
                                    onClick = { uriHandler.openUri("https://instagram.com/yadavv_bipin") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(Color(0xFFE1306C).copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Info, contentDescription = "Instagram", tint = Color(0xFFE1306C))
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("Instagram", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text("@yadavv_bipin", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Card(
                                    onClick = { uriHandler.openUri("https://github.com/BipinDev404") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Build, contentDescription = "GitHub", tint = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("GitHub", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text("BipinDev404", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Card(
                                    onClick = { uriHandler.openUri("https://example.com/donate") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                                    modifier = Modifier.fillMaxWidth()
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
                }"""

if bad in text:
    print("Found Developer section!")
    text = text.replace(bad, good)
    with open("app/src/main/java/com/example/Screens.kt", "w") as f:
        f.write(text)
else:
    print("Developer section NOT found")

