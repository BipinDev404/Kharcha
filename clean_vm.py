import re

with open("app/src/main/java/com/example/MainViewModel.kt", "r") as f:
    text = f.read()

# We need to remove imports, variables and methods.
# For simplicity, we can do a quick regex replacement or just re-write the file entirely since it's not that big.

print("Length of file:", len(text))
