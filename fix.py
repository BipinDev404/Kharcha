with open('app/src/main/java/com/example/Screens.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    if 'TextAlignpackage com.example' in line or 'package com.example' in line or 'import androidx.compose.ui.text.style.TextAlign' in line:
        continue
    new_lines.append(line)

with open('app/src/main/java/com/example/Screens.kt', 'w') as f:
    f.write('package com.example\n')
    f.write('import androidx.compose.ui.text.style.TextAlign\n')
    f.writelines(new_lines)
