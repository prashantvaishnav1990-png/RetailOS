# RetailOS Android Project

This wraps the RetailOS working MVP in a native Android WebView.

IMPORTANT:
- This folder is Android source code, not an APK.
- A real APK must be compiled with Android SDK/Gradle.
- Camera permission is declared.
- Bluetooth thermal printing is NOT implemented in this build.
- WhatsApp sharing opens wa.me.
- Data remains local to the app WebView.

Phone-only build options:
1. Use an Android cloud IDE/build service that supports Gradle projects.
2. Open this project in Android Studio on a computer.

The generated APK cannot be truthfully produced by this Python environment because it does not have Android SDK/Gradle tooling available.
