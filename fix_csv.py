with open('app/src/main/java/com/example/Screens.kt', 'r') as f:
    text = f.read()

bad_str = 'val safeMerchant = exp.merchant.replace(",", " ")\\n                                            val safeCategory = exp.category.replace(",", " ")\\n                                            val safePayment = exp.paymentMethod.replace(",", " ")\\n                                            writer.write("${exp.id},${exp.amount},${safeCategory},${safeMerchant},${safePayment},${exp.date},\"${exp.notes.replace("\\"", "\\"\\"")}\"\\n")'

good_str = """val safeMerchant = exp.merchant.replace(",", " ")
                                            val safeCategory = exp.category.replace(",", " ")
                                            val safePayment = exp.paymentMethod.replace(",", " ")
                                            writer.write("${exp.id},${exp.amount},${safeCategory},${safeMerchant},${safePayment},${exp.date},\\"${exp.notes.replace("\\"", "\\"\\"")}\\"\\n")"""

text = text.replace(bad_str, good_str)

with open('app/src/main/java/com/example/Screens.kt', 'w') as f:
    f.write(text)
