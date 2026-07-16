import os

drawable_dir = "app/src/main/res/drawable"

icons = {
    "ic_wallet.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="#FFFFFF">
    <path android:fillColor="@android:color/white" android:pathData="M21,7.5C21,6.12 19.88,5 18.5,5H5.5C4.12,5 3,6.12 3,7.5v9C3,17.88 4.12,19 5.5,19h13C19.88,19 21,17.88 21,16.5V7.5zM19,16.5c0,0.28 -0.22,0.5 -0.5,0.5H5.5c-0.28,0 -0.5,-0.22 -0.5,-0.5v-9C5,7.22 5.22,7 5.5,7h13C18.78,7 19,7.22 19,7.5V16.5z"/>
    <path android:fillColor="@android:color/white" android:pathData="M16,12m-1.5,0a1.5,1.5 0,1 1,3 0a1.5,1.5 0,1 1,-3 0"/>
</vector>""",
    "ic_add.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="#FFFFFF">
    <path android:fillColor="@android:color/white" android:pathData="M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
</vector>""",
    "ic_history.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="#FFFFFF">
    <path android:fillColor="@android:color/white" android:pathData="M13,3c-4.97,0 -9,4.03 -9,9H1l3.89,3.89l0.07,0.14L9,12H6c0,-3.87 3.13,-7 7,-7s7,3.13 7,7s-3.13,7 -7,7c-1.93,0 -3.68,-0.79 -4.94,-2.06l-1.42,1.42C8.27,19.99 10.51,21 13,21c4.97,0 9,-4.03 9,-9S17.97,3 13,3zM12,8v5l4.28,2.54l0.72,-1.21l-3.5,-2.08V8H12z"/>
</vector>""",
    "ic_limit.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="#FFFFFF">
    <path android:fillColor="@android:color/white" android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10s10,-4.48 10,-10S17.52,2 12,2zM12,20c-4.41,0 -8,-3.59 -8,-8s3.59,-8 8,-8s8,3.59 8,8S16.41,20 12,20zM12.5,7H11v6l5.25,3.15l0.75,-1.23l-4.5,-2.67z"/>
</vector>""",
    "ic_category.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="#FFFFFF">
    <path android:fillColor="@android:color/white" android:pathData="M12,2l-5.5,9h11zM11.14,20.31L12.55,18.9L16.08,22.44L17.5,21.03L13.96,17.49L15.38,16.08L11.14,16.08zM4,16h5v5H4z"/>
</vector>"""
}

for name, content in icons.items():
    with open(f"{drawable_dir}/{name}", "w") as f:
        f.write(content)

print("Icons created.")
