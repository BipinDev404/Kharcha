with open("app/build.gradle.kts", "r") as f:
    text = f.read()

text = text.replace('implementation(libs.androidx.datastore.preferences)', 'implementation(libs.androidx.datastore.preferences)\n    implementation(libs.androidx.glance.appwidget)\n    implementation(libs.androidx.glance.material3)')

with open("app/build.gradle.kts", "w") as f:
    f.write(text)
print("Finished Gradle patch.")
