# Blootuth & Wi-Fi Diagnostic Suite (Android)

A modern, native Android application built with **Kotlin 2.0**, **Jetpack Compose**, and **Material 3** for scanning, inspecting, and diagnosing Bluetooth (BLE / Classic) and Wi-Fi devices, conducting hardware integrity checks (Speaker DAC, Mic SPL recording, Touch display matrix, Battery PMU telemetry), and executing interactive diagnostic commands.

---

## 📱 Features

1. **Bluetooth & Wi-Fi RF Scanner (`HomeScreen.kt`)**:
   - Real-time BLE advertisement scanning & Classic Bluetooth discovery.
   - 2.4 GHz and 5 GHz Wi-Fi network scanner with SSID, BSSID, RSSI, and signal level gauges.
   - Device vendor recognition (Qualcomm, Apple, Samsung, Sony, Espressif ESP32, Raspberry Pi, Cisco, TP-Link, etc.).
   - Connection status badges (`CONNECTED`, `CONNECTING`, `PAIRED / BONDED`, `DISCONNECTED`).

2. **Device Inspector & Pre-Test (`DeviceDetailScreen.kt`)**:
   - Detailed hardware and networking parameters: MAC address, IPv4 address, signal strength, frequency, GATT service tree.
   - **Connect Test** & **Run Pre-Test** action buttons.
   - Automated Pre-Test execution saving a structured health profile directly to the Home dashboard.

3. **Hardware Diagnostics Suite (`HardwareTestScreen.kt`)**:
   - **Speaker & DAC Transducer**: Pure sine wave audio generator at configurable frequencies (440Hz, 1kHz, 2.5kHz, 5kHz) to verify transducer resonance.
   - **Microphone Array**: Live audio decibel (SPL) meter visualizer and 3-second voice recording diagnostic that saves raw PCM/WAV cache data.
   - **Touch Screen Digitizer**: Interactive touch canvas for multi-point digitizer testing.

4. **Live Battery & PMU Telemetry (`BatteryMonitorScreen.kt`)**:
   - Real-time battery percentage, voltage (mV), temperature (°C), and charging technology (AC/USB/Wireless).
   - Live connected duration counter (`HH:MM:SS`) and estimated battery drain rate (`%/hr`).

5. **Diagnostic Terminal Console (`DiagnosticConsoleScreen.kt`)**:
   - Interactive shell supporting commands: `help`, `scan_bt`, `stop_bt`, `scan_wifi`, `test_speaker`, `test_mic`, `get_battery`, `net_info`, `ping [host]`, `inspect_gatt`, `run_pretest`, `clear`.
   - Formatted output with structured JSON telemetry payload inspection.

---

## 🛠️ Project Structure

```
Blootuth/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── res/
            │   ├── values/ (strings.xml, themes.xml, colors.xml)
            │   └── xml/ (backup_rules.xml, data_extraction_rules.xml)
            └── java/com/diagnostic/bluetoothtool/
                ├── MainActivity.kt
                ├── model/
                │   └── DeviceModels.kt
                ├── service/
                │   ├── BluetoothScannerManager.kt
                │   ├── WifiNetworkManager.kt
                │   ├── HardwareDiagnosticManager.kt
                │   └── DiagnosticCommandEngine.kt
                ├── viewmodel/
                │   └── MainViewModel.kt
                └── ui/
                    ├── theme/ (Theme.kt, Color.kt, Type.kt)
                    ├── components/ (CommonComponents.kt)
                    └── screens/
                        ├── HomeScreen.kt
                        ├── DeviceDetailScreen.kt
                        ├── HardwareTestScreen.kt
                        ├── BatteryMonitorScreen.kt
                        └── DiagnosticConsoleScreen.kt
```

---

## 🚀 Building & Running

### Requirements
- **Android Studio Ladybug (2024.2+)** or **IntelliJ IDEA with Android plugin**
- **JDK 17+**
- **Android SDK (API 26 to 34)**

### Build with Gradle
To build the Debug APK via Gradle:
```bash
./gradlew assembleDebug
```
The output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
