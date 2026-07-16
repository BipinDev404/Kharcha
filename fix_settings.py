import re

with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

bad = """@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {"""

good = """@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .blur(if (showDeveloperDialog || showClearDialog) 16.dp else 0.dp)
    ) {"""

if bad in text:
    print("Found SettingsScreen")
    text = text.replace(bad, good)
    with open("app/src/main/java/com/example/Screens.kt", "w") as f:
        f.write(text)
else:
    print("SettingsScreen not found")
