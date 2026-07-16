import re

with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if i == 237: # Row start
        skip = True
        new_lines.append("""        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SPENT THIS MONTH", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${String.format(Locale.US, "%.0f", currentMonthSpent)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
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
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            Text("View All", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onNavigateToCalendar() })
        }
        Spacer(modifier = Modifier.height(16.dp))
""")
    if i == 319: # Spacer before if(expenses.isEmpty())
        skip = False
        continue
    
    if not skip:
        new_lines.append(line)

with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.writelines(new_lines)
