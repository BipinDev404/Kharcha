with open("app/src/main/java/com/example/Screens.kt", "r") as f:
    text = f.read()

imports = """
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
"""

text = text.replace("import androidx.compose.material.icons.filled.Person\n", "import androidx.compose.material.icons.filled.Person\n" + imports)

with open("app/src/main/java/com/example/Screens.kt", "w") as f:
    f.write(text)
