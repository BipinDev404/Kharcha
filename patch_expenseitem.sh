cat app/src/main/java/com/example/Screens.kt | sed -n '1,691p' > temp.kt
cat << 'INNER_EOF' >> temp.kt
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (expense.category.lowercase()) {
                            "food" -> androidx.compose.material.icons.filled.Restaurant
                            "travel" -> androidx.compose.material.icons.filled.Flight
                            "fuel" -> androidx.compose.material.icons.filled.LocalGasStation
                            "shopping" -> androidx.compose.material.icons.filled.ShoppingCart
                            "bills" -> androidx.compose.material.icons.filled.Receipt
                            "education" -> androidx.compose.material.icons.filled.School
                            "medical" -> androidx.compose.material.icons.filled.LocalHospital
                            "rent", "house" -> androidx.compose.material.icons.filled.Home
                            "salary" -> androidx.compose.material.icons.filled.AttachMoney
                            "investment" -> androidx.compose.material.icons.filled.TrendingUp
                            "recharge" -> androidx.compose.material.icons.filled.PhoneAndroid
                            "subscription" -> androidx.compose.material.icons.filled.Subscriptions
                            "electronics" -> androidx.compose.material.icons.filled.Computer
                            "entertainment" -> androidx.compose.material.icons.filled.Movie
                            "lent" -> androidx.compose.material.icons.automirrored.filled.ArrowForward
                            "received", "borrowed" -> androidx.compose.material.icons.automirrored.filled.ArrowBack
                            else -> androidx.compose.material.icons.filled.AttachMoney
                        }
                        Icon(icon, contentDescription = expense.category, tint = MaterialTheme.colorScheme.primary)
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
INNER_EOF
# Let's get the rest of the lines from 720 onwards
# Wait, I need to check where the Row ends in the new version. The original line 717 was the Text for amount, followed by } of Row and then the Expandable part. Let's see what is after 717 originally.
