import os

layout_dir = "app/src/main/res/layout"
os.makedirs(layout_dir, exist_ok=True)

with open(f"{layout_dir}/widget_initial_layout.xml", "w") as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/widget_bg_dark"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="16dp">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Expense Tracker"
        android:textColor="#FFFFFF"
        android:textStyle="bold"
        android:textSize="16sp" />
</LinearLayout>
""")

drawable_dir = "app/src/main/res/drawable"
os.makedirs(drawable_dir, exist_ok=True)
with open(f"{drawable_dir}/widget_bg_dark.xml", "w") as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#212121" />
    <corners android:radius="16dp" />
</shape>
""")

xml_dir = "app/src/main/res/xml"
widgets = ["summarywidget", "quickaddwidget", "recenttransactionswidget", "limitwidget", "categorywidget"]

for w in widgets:
    path = f"{xml_dir}/{w}_info.xml"
    with open(path, "r") as f:
        content = f.read()
    
    content = content.replace('android:initialLayout="@layout/glance_default_loading_layout"', 'android:initialLayout="@layout/widget_initial_layout"')
    
    if "android:previewLayout" not in content:
        content = content.replace('android:targetCellHeight="2"', 'android:targetCellHeight="2"\n    android:previewLayout="@layout/widget_initial_layout"')
    
    with open(path, "w") as f:
        f.write(content)

print("Fixed previews")
