with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

text = text.replace('Card(\n                                    onClick = { uriHandler.openUri("https://instagram.com/yadavv_bipin") },\n', 'Card(\n                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://instagram.com/yadavv_bipin") },\n')
text = text.replace('Card(\n                                    onClick = { uriHandler.openUri("https://github.com/BipinDev404") },\n', 'Card(\n                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://github.com/BipinDev404") },\n')
text = text.replace('Card(\n                                    onClick = { uriHandler.openUri("https://example.com/donate") },\n', 'Card(\n                                    modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri("https://example.com/donate") },\n')

# and we should remove the trailing `modifier = Modifier.fillMaxWidth()` from those cards since we just moved it up
text = text.replace('                                    modifier = Modifier.fillMaxWidth()\n                                ) {', '                                ) {')

with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.write(text)
