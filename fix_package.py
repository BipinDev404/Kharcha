with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

bad_prefix = "import androidx.compose.ui.draw.blur\nimport com.example.R\nimport androidx.compose.ui.res.painterResource\npackage com.example\n"
good_prefix = "package com.example\nimport androidx.compose.ui.draw.blur\nimport com.example.R\nimport androidx.compose.ui.res.painterResource\n"

text = text.replace(bad_prefix, good_prefix)
with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.write(text)
