with open("app/src/main/java/com/example/AppNavigation.kt", "r") as f:
    text = f.read()

bad = """                if (currentDestination?.route == Statistics::class.qualifiedName) {
                    FloatingActionButton(
                        onClick = { viewModel.toggleChat() },
                        interactionSource = interactionSource,
                        modifier = Modifier.scale(scale),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(32.dp))
                    }
                } else {
                    FloatingActionButton(
                        onClick = { navController.navigate(AddExpense) },
                        interactionSource = interactionSource,
                        modifier = Modifier.scale(scale),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Expense", modifier = Modifier.size(32.dp))
                    }
                }"""

good = """                FloatingActionButton(
                    onClick = { navController.navigate(AddExpense) },
                    interactionSource = interactionSource,
                    modifier = Modifier.scale(scale),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Expense", modifier = Modifier.size(32.dp))
                }"""

if bad in text:
    print("Found navigation fab block!")
    text = text.replace(bad, good)
    with open("app/src/main/java/com/example/AppNavigation.kt", "w") as f:
        f.write(text)
else:
    print("Navigation fab block NOT found")
