import re

with open("gradle/libs.versions.toml", "r") as f:
    text = f.read()

# Add version
text = text.replace('datastorePreferences = "1.1.7"', 'datastorePreferences = "1.1.7"\nglance = "1.1.1"')

# Add library
text = text.replace('androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }', 'androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }\nandroidx-glance-appwidget = { group = "androidx.glance", name = "glance-appwidget", version.ref = "glance" }\nandroidx-glance-material3 = { group = "androidx.glance", name = "glance-material3", version.ref = "glance" }')

with open("gradle/libs.versions.toml", "w") as f:
    f.write(text)
print("Finished TOML patch.")
