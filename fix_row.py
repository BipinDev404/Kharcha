with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

bad = """        Spacer(modifier = Modifier.height(16.dp))
        Row(
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {"""

good = """        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {"""

if bad in text:
    print("Found the bad row!")
    text = text.replace(bad, good)
    with open("app/src/main/java/com/example/Screens.kt", "w") as f:
        f.write(text)
else:
    print("Bad row not found. Let's see what is there...")
    
