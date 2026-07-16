with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

text = text.replace("Color.White.copy", "MaterialTheme.colorScheme.onSurface.copy")
# Also need to make sure Color.White text etc. are handled, but let's check what else uses Color.White

with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.write(text)
