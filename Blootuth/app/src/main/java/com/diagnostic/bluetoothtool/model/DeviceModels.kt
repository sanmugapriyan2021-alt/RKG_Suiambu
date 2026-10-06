package com.diagnostic.bluetoothtool.model

enum class DeviceType {
    BLUETOOTH_BLE,
    BLUETOOTH_CLASSIC,
    BLUETOOTH_DUAL,
    WIFI_AP,
    WIFI_P2P,
    LOCAL_DEVICE
}

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    BONDED,
    FAILED
}

enum class TestStatus {
    IDLE,
    RUNNING,
    PASSED,
    WARNING,
    FAILED
}

data class GattCharacteristicInfo(
    val uuid: String,
    val properties: List<String>,
    val permissions: List<String>,
    val valueHex: String? = null
)

data class GattServiceInfo(
    val uuid: String,
    val name: String,
    val characteristics: List<GattCharacteristicInfo>
)

enum class PeripheralCategory {
    TWS_EARBUDS,      // AirPods, Galaxy Buds, TWS Earphones
    BT_SPEAKER,       // Bluetooth Speakers, Soundbars, Audio Amplifiers
    SMART_GLASS,      // AR Glasses (Fire-Lens, Ray-Ban Meta)
    SMART_LIGHT,      // BLE LED Bulbs, RGB Lightbars, Indicator Peripherals
    GENERIC_IOT,      // Generic BLE / RF Sensors / Network Targets
    WIFI_NETWORK      // Wi-Fi Access Point / P2P Direct
}

data class HardwareCapabilityItem(
    val id: String,
    val name: String,
    val category: String, // Sound, Light, Battery, Security, Display, Sensor
    val isAvailable: Boolean,
    val statusDetail: String,
    val testActionCommand: String,
    val iconType: String = "Bolt"
)

data class TwsEarbudsMetrics(
    val leftBattery: Int = 92,
    val rightBattery: Int = 88,
    val caseBattery: Int = 96,
    val isLeftConnected: Boolean = true,
    val isRightConnected: Boolean = true,
    val isLeftInEar: Boolean = true,
    val isRightInEar: Boolean = true,
    val ancMode: String = "TRANSPARENCY", // "ANC_ON", "TRANSPARENCY", "OFF"
    val audioCodec: String = "AAC-LC (256 kbps)",
    val spatialAudioEnabled: Boolean = true,
    val modelGeneration: String = "AirPods Pro Gen 2 (H2 Silicon)"
)

data class SpeakerAmplificationMetrics(
    val currentVolume: Int = 75,
    val maxVolume: Int = 100,
    val amplificationGainDb: Float = 6.0f, // 0.0 to +18.0 dB
    val equalizerPreset: String = "Bass Boost (+8dB)",
    val isBassBoostEnabled: Boolean = true,
    val isDspLimiterActive: Boolean = true,
    val audioLatencyMs: Int = 24,
    val outputPowerWatts: Float = 40.0f
)

data class SmartLightMetrics(
    val isAvailable: Boolean = true,
    val isLightOn: Boolean = true,
    val activeColor: String = "BLUE", // "BLUE", "RED", "WHITE", "RGB", "AMBER"
    val brightnessPercent: Int = 85,
    val isStrobeActive: Boolean = false,
    val colorHex: String = "#0070F3",
    val geminiControlEnabled: Boolean = true
)

data class SecurityVulnerabilityScanItem(
    val id: String,
    val title: String,
    val testType: String, // BLE_FUZZING, MITM_SNIFFING, GATT_PERMS, PRIVACY_LEAK, RF_JAMMING
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW, SAFE
    val status: String,   // VULNERABLE, SECURED, AUDITED, TESTING
    val details: String,
    val remediation: String
)

data class WirelessStandardInfo(
    val ieeeStandard: String = "IEEE 802.15.1 (Bluetooth 5.3 Core)",
    val phyLayer: String = "LE 2M PHY (2 Msym/s GFSK)",
    val linkLayerMtu: Int = 247,
    val connectionIntervalMs: Float = 45.0f,
    val slaveLatency: Int = 0,
    val supervisionTimeoutMs: Int = 5000,
    val packetErrorRatePct: Float = 0.12f,
    val pathLossDb: Float = 64.2f,
    val estimatedDistanceMeters: Float = 2.4f,
    val linkBudgetDb: Float = 95.0f,
    val channelHoppingMode: String = "FHSS 79 Channels (AFH Active)"
)

data class RfRegulatoryCompliance(
    val regulatoryDomain: String = "FCC / ETSI / CE Multi-Domain",
    val maxEirpDbM: Float = 20.0f,
    val measuredTxPowerDbM: Float = 4.0f,
    val isEirpCompliant: Boolean = true,
    val outOfBandEmissionsDbM: Float = -48.5f,
    val emissionMaskStatus: String = "PASS (Within ETSI EN 300 328 v2.2.2 Mask)",
    val sarRfExposureLimit: String = "0.042 W/kg (Limit: 1.6 W/kg SAR FCC 1g)",
    val channelOccupancyDutyCyclePct: Float = 3.2f,
    val frequencyRange: String = "2402 MHz – 2480 MHz (ISM Band)"
)

data class ScannedDevice(
    val id: String,
    val name: String,
    val address: String, // MAC or BSSID or Local
    val rssi: Int = 0,
    val type: DeviceType,
    val category: PeripheralCategory = PeripheralCategory.GENERIC_IOT,
    val status: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val deviceClass: String = "Generic Peripheral",
    val vendor: String = "Unknown Vendor",
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val isBonded: Boolean = false,
    val ipAddress: String? = null,
    val frequencyMhz: Int? = null,
    val signalLevel: Int = 3, // 0-4 scale
    val isPasswordProtected: Boolean = true,
    val securityProtocol: String = "WPA2-PSK (AES)",
    val wifiBand: String = "2.4 GHz",
    val bluetoothVersion: String = "Bluetooth 5.3 Core",
    val capabilities: List<HardwareCapabilityItem> = emptyList(),
    val twsMetrics: TwsEarbudsMetrics = TwsEarbudsMetrics(),
    val speakerMetrics: SpeakerAmplificationMetrics = SpeakerAmplificationMetrics(),
    val lightMetrics: SmartLightMetrics = SmartLightMetrics()
)

data class BatteryMetrics(
    val levelPercentage: Int = 0,
    val voltageMv: Int = 0,
    val temperatureCelsius: Float = 0f,
    val isCharging: Boolean = false,
    val chargingSource: String = "None",
    val health: String = "Good",
    val technology: String = "Li-ion",
    val connectionDurationSeconds: Long = 0,
    val estimatedDrainRatePerHour: Float = 0f,
    val history: List<BatteryHistoryPoint> = emptyList()
)

data class BatteryHistoryPoint(
    val timestamp: Long,
    val level: Int,
    val temperature: Float
)

data class HardwareComponentInfo(
    val componentName: String,
    val status: String,
    val specifications: Map<String, String>,
    val isOperational: Boolean = true
)

data class OsAndBiosInfo(
    val osVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val kernelVersion: String,
    val bootloader: String,
    val basebandVersion: String,
    val buildId: String,
    val hardwareBoard: String,
    val socChipset: String,
    val deviceFingerprint: String,
    val systemUptime: String,
    val selinuxStatus: String,
    val supportedAbis: List<String>
)

data class AppInstallInfo(
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val minSdk: Int,
    val targetSdk: Int,
    val firstInstallTime: String,
    val lastUpdateTime: String,
    val apkDataSize: String,
    val apkSignerDigest: String,
    val requestedPermissions: List<String>,
    val grantedPermissions: List<String>
)

data class VulnerabilityFinding(
    val id: String,
    val title: String,
    val severity: String, // INFO, LOW, MEDIUM, HIGH
    val category: String, // RF Security, OS Patch, Permissions, GATT Permissions, Network
    val description: String,
    val remediation: String
)

data class SecurityAuditReport(
    val deviceName: String,
    val targetAddress: String,
    val timestamp: Long = System.currentTimeMillis(),
    val overallPostureScore: Int, // 0 - 100
    val isEncrypted: Boolean,
    val isBonded: Boolean,
    val openWritableCharacteristics: Int,
    val osSecurityPatchLevel: String,
    val findings: List<VulnerabilityFinding>,
    val aiSecuritySummary: String
)

data class AiCommandSuggestion(
    val command: String,
    val description: String,
    val category: String
)

data class HardwareDiagnosticTest(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    var status: TestStatus = TestStatus.IDLE,
    var resultMessage: String = "Not executed yet",
    var details: Map<String, String> = emptyMap(),
    var latencyMs: Long = 0
)

data class PreTestProfile(
    val profileId: String = "DEFAULT_PROFILE",
    val timestamp: Long = System.currentTimeMillis(),
    val deviceName: String,
    val macAddress: String,
    val ipAddress: String,
    val tests: List<HardwareDiagnosticTest>,
    val overallHealthScore: Int, // 0 - 100
    val aiDiagnosticSummary: String,
    val structuredJsonData: String
)

data class CommandConsoleEntry(
    val id: String,
    val command: String,
    val output: String,
    val isSuccess: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val rawJson: String? = null
)

data class SmartGlassServiceItem(
    val id: String,
    val name: String,
    val uuid: String,
    val category: String, // Audio, Call, Remote Control, Sensor, AI Assistant, Custom
    val isEnabled: Boolean = true,
    val description: String
)

data class PeripheralAuthenticationInfo(
    val pairingMode: String, // e.g., "Secure Simple Pairing (SSP - Numeric Comparison)"
    val encryptionStandard: String, // "AES-CCM 128-bit Link Layer"
    val keySize: String, // "128-bit E0 / AES"
    val isBonded: Boolean,
    val supportedProfiles: List<String>, // ["A2DP Sink (Audio)", "HFP 1.7 (Voice)", "AVRCP 1.6 (Controls)", "GATT Battery"]
    val mitmProtection: String // "Enforced / Authenticated"
)

data class ActiveMediaAppInfo(
    val appName: String, // e.g., "Spotify", "YouTube Music", "Default Media Player"
    val isPlaying: Boolean,
    val volumeLevel: Int,
    val maxVolume: Int,
    val audioRoute: String // "Bluetooth A2DP (Fire-Lens)"
)

data class DeviceDnsInfo(
    val primaryDns: String = "1.1.1.1",
    val secondaryDns: String = "1.0.0.1",
    val activeDnsProvider: String = "Cloudflare Secure DNS",
    val queryLatencyMs: Long = 18,
    val resolvedDomain: String = "gemini.google.com",
    val resolvedIp: String = "142.250.190.46",
    val isCustomDnsEnabled: Boolean = false,
    val dnsSecurityStatus: String = "DNS-over-TLS (DoT) Encrypted",
    val lastTestTimestamp: Long = System.currentTimeMillis()
)

enum class BlePowerMode {
    LOW_LATENCY,  // 15ms interval - High responsiveness (Gaming/Voice)
    BALANCED,     // 45ms interval - Standard music/audio streaming
    POWER_SAVE    // 100ms interval - Extended battery endurance
}

data class DevicePortInfo(
    val id: String,
    val portName: String,
    val portType: String, // AUDIO_MIC, DISPLAY_HUD, SERIAL_PORT, BLE_GATT, INPUT_BUTTON
    val status: String,
    val version: String,
    val isAuthorized: Boolean = true,
    val isConfigurable: Boolean = true,
    val permission: String,
    val channelOrAddress: String
)

data class EligibleAppInfo(
    val id: String,
    val packageName: String,
    val appName: String,
    val category: String,
    val isRouted: Boolean,
    val description: String
)

data class GenerativeWidget(
    val id: String,
    val title: String,
    val category: String,
    val controlType: String, // BUTTON, SLIDER, SWITCH, VALUE_CARD
    val currentValue: String,
    val commandAction: String,
    val iconName: String = "Bolt"
)

data class GeminiGenerativeUiConfig(
    val aiInsight: String,
    val overrideThemeName: String = "Cyber-AR Matrix HUD",
    val promptUsed: String = "Override UI for Firebolt Smartglass with live audio, HUD brightness, and DoT DNS bypass",
    val widgets: List<GenerativeWidget> = emptyList(),
    val isAiGenerated: Boolean = true,
    val generationTimestamp: Long = System.currentTimeMillis()
)

data class GeminiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "User" or "Gemini AI"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tokenMetrics: TokenUsageMetrics? = null,
    val isPlayingAudio: Boolean = false,
    val isClassroomNote: Boolean = false
)

data class TokenUsageMetrics(
    val promptTokens: Int = 0,
    val candidateTokens: Int = 0,
    val totalTokens: Int = 0,
    val latencyMs: Long = 0,
    val generationSpeedTokPerSec: Float = 0f,
    val modelName: String = "gemini-1.5-flash-002",
    val finishReason: String = "STOP"
)

enum class AudioPresetType {
    CLASSROOM_VOCAL,
    STEALTH_WHISPER,
    LECTURE_SPEED_2X,
    HIGH_CLARITY_BOOST
}

data class ClassroomAudioProfile(
    val type: AudioPresetType,
    val title: String,
    val description: String,
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val volumeGain: Float = 1.0f,
    val isWhisperMode: Boolean = false
)

data class ClassroomAudioState(
    val isPlaying: Boolean = false,
    val activeText: String = "",
    val speechRate: Float = 1.15f,
    val pitch: Float = 1.0f,
    val activeProfile: ClassroomAudioProfile = ClassroomAudioProfile(
        type = AudioPresetType.CLASSROOM_VOCAL,
        title = "Classroom Vocal Clarity",
        description = "Balanced speech synthesis optimized for classroom environments and lecture notes",
        speechRate = 1.15f,
        pitch = 1.0f,
        volumeGain = 1.0f
    ),
    val isBluetoothScoActive: Boolean = true,
    val volumeLevel: Int = 85
)



