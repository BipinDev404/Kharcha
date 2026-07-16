with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

import_R = "import com.example.R\nimport androidx.compose.ui.res.painterResource\n"
if "import com.example.R" not in text:
    text = text.replace("import androidx.compose.ui.draw.blur\n", "import androidx.compose.ui.draw.blur\n" + import_R)

# We will just replace between "Spacer(modifier = Modifier.height(32.dp))" at line 1171 
# and the end of Developer dialog

import re

pattern = re.compile(r'                SettingsRow\(\s*icon = Icons\.Filled\.Person,\s*title = "Developer Info",.*?(?=                            TextButton\(onClick = \{ showDeveloperDialog = false \}\) \{\s*Text\("Close"\)\s*\}\s*\}\s*\)\s*\}\s*Spacer\(modifier = Modifier.height\(32\.dp\)\))', re.DOTALL)

good = """                androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                
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
"""

# check if pattern matches
match = pattern.search(text)
if match:
    print("Match found!")
    new_text = text[:match.start()] + good + text[match.end():]
    with open("app/src/main/java/com/example/Screens.kt", "w") as f:
        f.write(new_text)
else:
    print("Match NOT found!")
