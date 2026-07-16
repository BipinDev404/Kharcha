import os

widgets = [
    ("SummaryWidget", "Monthly Summary"),
    ("QuickAddWidget", "Quick Add"),
    ("RecentTransactionsWidget", "Recent Transactions"),
    ("LimitWidget", "Monthly Limit"),
    ("CategoryWidget", "Top Category")
]

xml_dir = "app/src/main/res/xml"
os.makedirs(xml_dir, exist_ok=True)

for class_name, label in widgets:
    xml_code = f"""<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="110dp"
    android:minHeight="110dp"
    android:updatePeriodMillis="86400000"
    android:initialLayout="@layout/glance_default_loading_layout"
    android:widgetCategory="home_screen"
    android:previewImage="@drawable/ic_launcher_foreground"
    android:targetCellWidth="2"
    android:targetCellHeight="2" />
"""
    with open(f"{xml_dir}/{class_name.lower()}_info.xml", "w") as f:
        f.write(xml_code)

with open("app/src/main/AndroidManifest.xml", "r") as f:
    manifest = f.read()

receivers = ""
for class_name, label in widgets:
    receivers += f"""
        <receiver
            android:name=".widgets.{class_name}Receiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/{class_name.lower()}_info" />
        </receiver>"""

if "SummaryWidgetReceiver" not in manifest:
    manifest = manifest.replace("</application>", receivers + "\n    </application>")
    with open("app/src/main/AndroidManifest.xml", "w") as f:
        f.write(manifest)
    print("Updated Manifest")
else:
    print("Manifest already updated")

