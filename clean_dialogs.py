with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

text = text.replace("                var showClearDialog by remember { mutableStateOf(false) }\n", "")
text = text.replace("                var showDeveloperDialog by remember { mutableStateOf(false) }\n", "")

with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.write(text)
