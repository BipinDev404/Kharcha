import re

with open("app/src/main/java/com/example/ui/theme/Theme.kt", "r") as f:
    text = f.read()

bad_light = """      else -> lightColorScheme(
          primary = primaryColor,
          background = LightBackground,
          surface = LightSurface,
          onPrimary = TextPrimaryLight,
          onBackground = TextPrimaryLight,
          onSurface = TextPrimaryLight,
          error = ExpenseRed
      )"""

good_light = """      else -> lightColorScheme(
          primary = primaryColor,
          background = LightBackground,
          surface = LightSurface,
          onPrimary = androidx.compose.ui.graphics.Color.White,
          onBackground = TextPrimaryLight,
          onSurface = TextPrimaryLight,
          error = ExpenseRed
      )"""

if bad_light in text:
    print("Found lightColorScheme!")
    text = text.replace(bad_light, good_light)
    with open("app/src/main/java/com/example/ui/theme/Theme.kt", "w") as f:
        f.write(text)
else:
    print("lightColorScheme not found.")
