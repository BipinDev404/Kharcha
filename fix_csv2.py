import re

with open('app/src/main/java/com/example/Screens.kt', 'r') as f:
    text = f.read()

bad_export = """                                        writer.write("id,amount,category,merchant,paymentMethod,date,notes\\n")
                                        expenses.forEach { exp ->
                                            val safeMerchant = exp.merchant.replace(",", " ")
                                            val safeCategory = exp.category.replace(",", " ")
                                            val safePayment = exp.paymentMethod.replace(",", " ")
                                            writer.write("${exp.id},${exp.amount},${safeCategory},${safeMerchant},${safePayment},${exp.date},\\"${exp.notes.replace("\\"", "\\"\\"")}\\"\\n")
                                        }"""

good_export = """                                        writer.write("Transaction ID,Type,Amount,Category,Merchant,Payment Method,Date,Time,Timestamp_MS,Notes\\n")
                                        expenses.forEach { exp ->
                                            val type = when (exp.category) {
                                                "Received", "Borrowed" -> "Income"
                                                "Lent" -> "Lent"
                                                else -> "Expense"
                                            }
                                            val safeMerchant = exp.merchant.replace(",", " ").replace("\\"", "")
                                            val safeCategory = exp.category.replace(",", " ").replace("\\"", "")
                                            val safePayment = exp.paymentMethod.replace(",", " ").replace("\\"", "")
                                            
                                            val cal = java.util.Calendar.getInstance().apply { timeInMillis = exp.date }
                                            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(cal.time)
                                            val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(cal.time)
                                            
                                            writer.write("${exp.id},${type},${exp.amount},${safeCategory},${safeMerchant},${safePayment},${dateStr},${timeStr},${exp.date},\\"${exp.notes.replace("\\"", "\\"\\"")}\\"\\n")
                                        }"""

bad_import = """                                                val parts = line.split(",")
                                                if (parts.size >= 6) {
                                                    val amount = parts[1].toDoubleOrNull() ?: 0.0
                                                    val category = parts[2]
                                                    val merchant = parts[3]
                                                    val paymentMethod = parts[4]
                                                    val date = parts[5].toLongOrNull() ?: 0L
                                                    val notes = if (parts.size >= 7) parts.subList(6, parts.size).joinToString(",").replace("^\\"|\\"$".toRegex(), "").replace("\\"\\"", "\\"") else ""
                                                    
                                                    viewModel.addManualExpense(com.example.data.Expense(amount = amount, category = category, merchant = merchant, paymentMethod = paymentMethod, date = date, notes = notes))
                                                }"""

good_import = """                                                val parts = line.split(",", limit = 10)
                                                if (parts.size >= 9) {
                                                    val amount = parts[2].toDoubleOrNull() ?: 0.0
                                                    val category = parts[3]
                                                    val merchant = parts[4]
                                                    val paymentMethod = parts[5]
                                                    val date = parts[8].toLongOrNull() ?: 0L
                                                    val notes = if (parts.size >= 10) {
                                                        val rawNotes = parts[9]
                                                        if (rawNotes.startsWith("\\"") && rawNotes.endsWith("\\"")) {
                                                            rawNotes.substring(1, rawNotes.length - 1).replace("\\"\\"", "\\"")
                                                        } else rawNotes
                                                    } else ""
                                                    
                                                    viewModel.addManualExpense(com.example.data.Expense(amount = amount, category = category, merchant = merchant, paymentMethod = paymentMethod, date = date, notes = notes))
                                                }"""

if bad_export in text:
    print("Found export block")
else:
    print("Export block not found")

if bad_import in text:
    print("Found import block")
else:
    print("Import block not found")

text = text.replace(bad_export, good_export)
text = text.replace(bad_import, good_import)

with open('app/src/main/java/com/example/Screens.kt', 'w') as f:
    f.write(text)

