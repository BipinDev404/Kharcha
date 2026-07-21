cat app/src/main/java/com/example/Screens.kt | sed 's/import androidx.compose.material.icons.Icons/import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.*\nimport androidx.compose.material.icons.automirrored.filled.*/' > temp.kt
mv temp.kt app/src/main/java/com/example/Screens.kt

cat app/src/main/java/com/example/Screens.kt | sed 's/androidx.compose.material.icons.filled./Icons.Filled./g' | sed 's/androidx.compose.material.icons.automirrored.filled./Icons.AutoMirrored.Filled./g' > temp.kt
mv temp.kt app/src/main/java/com/example/Screens.kt
