with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

text = text.replace("Icons.Filled.Link", "Icons.Filled.Info")
text = text.replace("Icons.Filled.Code", "Icons.Filled.Build")

with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.write(text)
