import re

with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

pattern = re.compile(r'                                Card\(\s*modifier = Modifier\.fillMaxWidth\(\)\.clickable \{ uriHandler\.openUri\("https://example\.com/donate"\) \},\s*colors = CardDefaults\.cardColors\(containerColor = MaterialTheme\.colorScheme\.errorContainer\.copy\(alpha = 0\.5f\)\),\s*shape = RoundedCornerShape\(16\.dp\),\s*border = BorderStroke\(1\.dp, MaterialTheme\.colorScheme\.error\.copy\(alpha = 0\.2f\)\),\s*\) \{\s*Row\(\s*modifier = Modifier\.padding\(16\.dp\),\s*verticalAlignment = Alignment\.CenterVertically\s*\) \{\s*Box\(\s*modifier = Modifier\.size\(40\.dp\)\.background\(MaterialTheme\.colorScheme\.error\.copy\(alpha = 0\.1f\), CircleShape\),\s*contentAlignment = Alignment\.Center\s*\) \{\s*Icon\(Icons\.Filled\.Favorite, contentDescription = "Donate", tint = MaterialTheme\.colorScheme\.error\)\s*\}\s*Spacer\(modifier = Modifier\.width\(16\.dp\)\)\s*Column \{\s*Text\("Donate", style = MaterialTheme\.typography\.bodyLarge, fontWeight = FontWeight\.Bold, color = MaterialTheme\.colorScheme\.onErrorContainer\)\s*Text\("Support the developer", style = MaterialTheme\.typography\.bodyMedium, color = MaterialTheme\.colorScheme\.onErrorContainer\.copy\(alpha = 0\.7f\)\)\s*\}\s*\}\s*\}')

good_section = """                                Spacer(modifier = Modifier.height(16.dp))
                                Text("If you like this app, consider supporting future updates.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                val upiId = "placeholder@upi" // Add your UPI ID here
                                val payeeName = "Bipin Yadav"
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { uriHandler.openUri("upi://pay?pa=$upiId&pn=$payeeName&am=50&cu=INR") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Donate ₹50")
                                    }
                                    Button(
                                        onClick = { uriHandler.openUri("upi://pay?pa=$upiId&pn=$payeeName&am=100&cu=INR") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Donate ₹100")
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
                                        Text("Donate ₹200")
                                    }
                                    OutlinedButton(
                                        onClick = { uriHandler.openUri("upi://pay?pa=$upiId&pn=$payeeName&cu=INR") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Custom Amount")
                                    }
                                }"""

if pattern.search(text):
    text = pattern.sub(good_section, text)
    with open("app/src/main/java/com/example/Screens.kt", "w") as f:
        f.write(text)
    print("Success!")
else:
    print("Pattern not found!")
