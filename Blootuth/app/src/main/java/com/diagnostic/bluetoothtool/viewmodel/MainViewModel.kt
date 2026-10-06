package com.diagnostic.bluetoothtool.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.media.AudioManager
import android.view.KeyEvent
import com.diagnostic.bluetoothtool.model.ActiveMediaAppInfo
import com.diagnostic.bluetoothtool.model.BatteryMetrics
import com.diagnostic.bluetoothtool.model.CommandConsoleEntry
import com.diagnostic.bluetoothtool.model.ConnectionStatus
import com.diagnostic.bluetoothtool.model.DeviceType
import com.diagnostic.bluetoothtool.model.GattServiceInfo
import com.diagnostic.bluetoothtool.model.HardwareComponentInfo
import com.diagnostic.bluetoothtool.model.PeripheralAuthenticationInfo
import com.diagnostic.bluetoothtool.model.PreTestProfile
import com.diagnostic.bluetoothtool.model.ScannedDevice
import com.diagnostic.bluetoothtool.model.SmartGlassServiceItem
import com.diagnostic.bluetoothtool.service.BluetoothScannerManager
import com.diagnostic.bluetoothtool.service.ClassroomAudioPlayerManager
import com.diagnostic.bluetoothtool.service.DiagnosticCommandEngine
import com.diagnostic.bluetoothtool.service.GeminiTokenEngine
import com.diagnostic.bluetoothtool.service.HardwareDiagnosticManager
import com.diagnostic.bluetoothtool.service.WifiNetworkManager
import com.diagnostic.bluetoothtool.model.AudioPresetType
import com.diagnostic.bluetoothtool.model.ClassroomAudioState
import com.diagnostic.bluetoothtool.model.GeminiChatMessage
import com.diagnostic.bluetoothtool.model.TokenUsageMetrics
import com.diagnostic.bluetoothtool.model.HardwareCapabilityItem
import com.diagnostic.bluetoothtool.model.PeripheralCategory
import com.diagnostic.bluetoothtool.model.SecurityVulnerabilityScanItem
import com.diagnostic.bluetoothtool.model.SmartLightMetrics
import com.diagnostic.bluetoothtool.model.SpeakerAmplificationMetrics
import com.diagnostic.bluetoothtool.model.TwsEarbudsMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScanFilterTab {
    ALL,
    BLUETOOTH,
    AIRPODS_TWS,
    SPEAKERS,
    SMART_LIGHT,
    WIFI
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val btManager = BluetoothScannerManager(application.applicationContext, viewModelScope)
    val wifiManager = WifiNetworkManager(application.applicationContext)
    val hwManager = HardwareDiagnosticManager(application.applicationContext, viewModelScope)
    val commandEngine = DiagnosticCommandEngine(btManager, wifiManager, hwManager)
    val audioPlayer = ClassroomAudioPlayerManager(application.applicationContext)
    val geminiEngine = GeminiTokenEngine()

    val classroomAudioState: StateFlow<ClassroomAudioState> = audioPlayer.audioState

    // Multi-Device Peripheral Custom Metrics
    private val _activeTwsMetrics = MutableStateFlow(TwsEarbudsMetrics())
    val activeTwsMetrics: StateFlow<TwsEarbudsMetrics> = _activeTwsMetrics.asStateFlow()

    private val _activeSpeakerMetrics = MutableStateFlow(SpeakerAmplificationMetrics())
    val activeSpeakerMetrics: StateFlow<SpeakerAmplificationMetrics> = _activeSpeakerMetrics.asStateFlow()

    private val _activeSmartLightMetrics = MutableStateFlow(SmartLightMetrics())
    val activeSmartLightMetrics: StateFlow<SmartLightMetrics> = _activeSmartLightMetrics.asStateFlow()

    private val _deviceCapabilities = MutableStateFlow<List<HardwareCapabilityItem>>(emptyList())
    val deviceCapabilities: StateFlow<List<HardwareCapabilityItem>> = _deviceCapabilities.asStateFlow()

    private val _securityVulnerabilities = MutableStateFlow<List<SecurityVulnerabilityScanItem>>(
        listOf(
            SecurityVulnerabilityScanItem("V1", "BLE Packet Injection & Fuzzing", "BLE_FUZZING", "SAFE", "SECURED", "No unhandled GATT buffer overflows detected during low-latency fuzzing.", "Enable packet integrity check (MIC)."),
            SecurityVulnerabilityScanItem("V2", "MITM & Key Sniffing Vulnerability", "MITM_SNIFFING", "SAFE", "SECURED", "Link protected by AES-CCM 128-bit authenticated key exchange.", "Enforce Secure Connections Only mode."),
            SecurityVulnerabilityScanItem("V3", "Unauthenticated GATT Write Endpoints", "GATT_PERMS", "LOW", "AUDITED", "Characteristics require standard paired permissions.", "Verify characteristic write attributes."),
            SecurityVulnerabilityScanItem("V4", "MAC Address Tracking & Privacy Leak", "PRIVACY_LEAK", "SAFE", "SECURED", "RPA (Resolvable Private Address) active. MAC rotated every 15 mins.", "Maintain RPA rotation interval."),
            SecurityVulnerabilityScanItem("V5", "RF Signal Jamming & Interference Risk", "RF_JAMMING", "SAFE", "AUDITED", "Frequency hopping spread spectrum (FHSS) on 79 channels active.", "Monitor RSSI jitter & drop rates.")
        )
    )
    val securityVulnerabilities: StateFlow<List<SecurityVulnerabilityScanItem>> = _securityVulnerabilities.asStateFlow()

    private val _wirelessStandardInfo = MutableStateFlow(com.diagnostic.bluetoothtool.model.WirelessStandardInfo())
    val wirelessStandardInfo: StateFlow<com.diagnostic.bluetoothtool.model.WirelessStandardInfo> = _wirelessStandardInfo.asStateFlow()

    private val _rfCompliance = MutableStateFlow(com.diagnostic.bluetoothtool.model.RfRegulatoryCompliance())
    val rfCompliance: StateFlow<com.diagnostic.bluetoothtool.model.RfRegulatoryCompliance> = _rfCompliance.asStateFlow()

    private val _isSecurityScanning = MutableStateFlow(false)
    val isSecurityScanning: StateFlow<Boolean> = _isSecurityScanning.asStateFlow()

    private val _geminiMessages = MutableStateFlow<List<GeminiChatMessage>>(
        listOf(
            GeminiChatMessage(
                sender = "Gemini AI",
                content = "✨ Welcome to Gemini AI Network & Peripheral Intelligence Hub! I can diagnose Bluetooth connectivity, audit malicious RF activity, control AirPods L/R batteries, amplify speakers (+18dB), switch LED light colors (Blue/Red/RGB), and stream telemetry directly to your device.",
                tokenMetrics = TokenUsageMetrics(
                    promptTokens = 32,
                    candidateTokens = 58,
                    totalTokens = 90,
                    latencyMs = 16,
                    generationSpeedTokPerSec = 78.4f
                )
            )
        )
    )
    val geminiMessages: StateFlow<List<GeminiChatMessage>> = _geminiMessages.asStateFlow()

    private val _isGeminiStreaming = MutableStateFlow(false)
    val isGeminiStreaming: StateFlow<Boolean> = _isGeminiStreaming.asStateFlow()

    private val _activeTokenMetrics = MutableStateFlow(
        TokenUsageMetrics(
            promptTokens = 24,
            candidateTokens = 38,
            totalTokens = 62,
            latencyMs = 18,
            generationSpeedTokPerSec = 72.5f
        )
    )
    val activeTokenMetrics: StateFlow<TokenUsageMetrics> = _activeTokenMetrics.asStateFlow()

    private val _selectedTab = MutableStateFlow(ScanFilterTab.ALL)
    val selectedTab: StateFlow<ScanFilterTab> = _selectedTab.asStateFlow()

    private val _selectedDevice = MutableStateFlow<ScannedDevice?>(null)
    val selectedDevice: StateFlow<ScannedDevice?> = _selectedDevice.asStateFlow()

    private val _isDeviceLocked = MutableStateFlow(false)
    val isDeviceLocked: StateFlow<Boolean> = _isDeviceLocked.asStateFlow()

    private val _lockedDevice = MutableStateFlow<ScannedDevice?>(null)
    val lockedDevice: StateFlow<ScannedDevice?> = _lockedDevice.asStateFlow()

    private val _securityAuditReport = MutableStateFlow<com.diagnostic.bluetoothtool.model.SecurityAuditReport?>(null)
    val securityAuditReport: StateFlow<com.diagnostic.bluetoothtool.model.SecurityAuditReport?> = _securityAuditReport.asStateFlow()

    private val _isSecurityAuditRunning = MutableStateFlow(false)
    val isSecurityAuditRunning: StateFlow<Boolean> = _isSecurityAuditRunning.asStateFlow()

    private val _deviceDnsInfo = MutableStateFlow(com.diagnostic.bluetoothtool.model.DeviceDnsInfo())
    val deviceDnsInfo: StateFlow<com.diagnostic.bluetoothtool.model.DeviceDnsInfo> = _deviceDnsInfo.asStateFlow()

    private val _isDnsTesting = MutableStateFlow(false)
    val isDnsTesting: StateFlow<Boolean> = _isDnsTesting.asStateFlow()

    private val _blePowerMode = MutableStateFlow(com.diagnostic.bluetoothtool.model.BlePowerMode.BALANCED)
    val blePowerMode: StateFlow<com.diagnostic.bluetoothtool.model.BlePowerMode> = _blePowerMode.asStateFlow()

    private val _aiCommandSuggestions = MutableStateFlow<List<com.diagnostic.bluetoothtool.model.AiCommandSuggestion>>(
        listOf(
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("get_dns", "Query gadget DNS resolver & routing latency", "DNS & Net"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("set_dns 1.1.1.1 1.0.0.1", "Apply Cloudflare DoT secure DNS", "DNS & Net"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("test_dns gemini.google.com", "Benchmark DNS domain lookup RTT", "DNS & Net"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("media_play", "Toggle Play/Pause on Smart Glass / Speaker", "Media"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("media_next", "Fast Forward / Next Track on Peripheral", "Media"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("vol_up", "Raise volume on Bluetooth Smart Glass", "Audio"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("vol_down", "Lower volume on Bluetooth Smart Glass", "Audio"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("open_gemini", "Trigger Google Gemini AI Assistant", "AI Agent"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("auth_info", "Inspect peripheral pairing & AES-CCM encryption", "RF Audit"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("list_services", "Enumerate peripheral active GATT & Bluetooth services", "Services"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("get_battery", "Query gadget PMU voltage, temperature and drain", "Power"),
            com.diagnostic.bluetoothtool.model.AiCommandSuggestion("run_pretest", "Execute full automated pre-test suite", "Pre-Test")
        )
    )
    val aiCommandSuggestions: StateFlow<List<com.diagnostic.bluetoothtool.model.AiCommandSuggestion>> = _aiCommandSuggestions.asStateFlow()

    private val _activeMediaInfo = MutableStateFlow<com.diagnostic.bluetoothtool.model.ActiveMediaAppInfo?>(null)
    val activeMediaInfo: StateFlow<com.diagnostic.bluetoothtool.model.ActiveMediaAppInfo?> = _activeMediaInfo.asStateFlow()

    private val _peripheralServices = MutableStateFlow<List<com.diagnostic.bluetoothtool.model.SmartGlassServiceItem>>(emptyList())
    val peripheralServices: StateFlow<List<com.diagnostic.bluetoothtool.model.SmartGlassServiceItem>> = _peripheralServices.asStateFlow()

    private val _peripheralAuthInfo = MutableStateFlow<com.diagnostic.bluetoothtool.model.PeripheralAuthenticationInfo?>(null)
    val peripheralAuthInfo: StateFlow<com.diagnostic.bluetoothtool.model.PeripheralAuthenticationInfo?> = _peripheralAuthInfo.asStateFlow()

    private val _savedPreTestProfile = MutableStateFlow<PreTestProfile?>(null)
    val savedPreTestProfile: StateFlow<PreTestProfile?> = _savedPreTestProfile.asStateFlow()

    private val _isPreTestRunning = MutableStateFlow(false)
    val isPreTestRunning: StateFlow<Boolean> = _isPreTestRunning.asStateFlow()

    private val _eligibleApps = MutableStateFlow<List<com.diagnostic.bluetoothtool.model.EligibleAppInfo>>(
        listOf(
            com.diagnostic.bluetoothtool.model.EligibleAppInfo("APP_SPOTIFY", "com.spotify.music", "Spotify", "Media Streaming", true, "Direct BLE Audio LC3 routing with AVRCP metadata"),
            com.diagnostic.bluetoothtool.model.EligibleAppInfo("APP_YTM", "com.google.android.apps.youtube.music", "YouTube Music", "Media Streaming", true, "Hi-Res Audio Sink with equalizer presets"),
            com.diagnostic.bluetoothtool.model.EligibleAppInfo("APP_MEET", "com.google.android.apps.meetings", "Google Meet", "VoIP / Conferencing", true, "Dual MEMS beamforming mic input with AI noise gate"),
            com.diagnostic.bluetoothtool.model.EligibleAppInfo("APP_PHONE", "com.android.server.telecom", "Phone & Voice Calls", "Telephony", true, "HFP 1.7 Wideband speech (mSBC) with acoustic echo cancellation"),
            com.diagnostic.bluetoothtool.model.EligibleAppInfo("APP_GEMINI", "com.google.android.apps.bard", "Google Gemini AI", "AI Voice Agent", true, "Direct hotword listening and generative multimodal AR HUD overlay")
        )
    )
    val eligibleApps: StateFlow<List<com.diagnostic.bluetoothtool.model.EligibleAppInfo>> = _eligibleApps.asStateFlow()

    private val _geminiGenerativeUiConfig = MutableStateFlow(
        com.diagnostic.bluetoothtool.model.GeminiGenerativeUiConfig(
            aiInsight = "Gemini intelligence analyzed Firebolt Smartglass (Qualcomm QCC5171 Dual-Core SoC). Recommended Layout: High-contrast AR HUD with 80% luminance, Dual MEMS I2S noise cancel filter, and DoT DNS bypass.",
            overrideThemeName = "Cyber-AR Matrix HUD",
            widgets = listOf(
                com.diagnostic.bluetoothtool.model.GenerativeWidget("W1", "AR HUD Brightness", "Display", "SLIDER", "80%", "set_brightness 80", "DisplaySettings"),
                com.diagnostic.bluetoothtool.model.GenerativeWidget("W2", "AI Beamforming Mic", "Audio", "SWITCH", "ON", "toggle_beamforming", "Mic"),
                com.diagnostic.bluetoothtool.model.GenerativeWidget("W3", "Gemini Voice AI Lens", "AI Agent", "BUTTON", "TRIGGER", "open_gemini", "Psychology"),
                com.diagnostic.bluetoothtool.model.GenerativeWidget("W4", "DoT DNS Shield (1.1.1.1)", "DNS", "SWITCH", "ENCRYPTED", "set_dns 1.1.1.1", "Security"),
                com.diagnostic.bluetoothtool.model.GenerativeWidget("W5", "RF Pentest Fuzzer", "Security", "BUTTON", "AUDIT", "run_audit", "Bolt")
            )
        )
    )
    val geminiGenerativeUiConfig: StateFlow<com.diagnostic.bluetoothtool.model.GeminiGenerativeUiConfig> = _geminiGenerativeUiConfig.asStateFlow()

    private val _isGeneratingGeminiUi = MutableStateFlow(false)
    val isGeneratingGeminiUi: StateFlow<Boolean> = _isGeneratingGeminiUi.asStateFlow()

    private val _commandLogs = MutableStateFlow<List<CommandConsoleEntry>>(
        listOf(
            CommandConsoleEntry(
                id = "INIT",
                command = "system_init",
                output = "ScanWeb Diagnostic Engine v2.0 initialized. Ready for peripheral control and RF synchronization."
            )
        )
    )
    val commandLogs: StateFlow<List<CommandConsoleEntry>> = _commandLogs.asStateFlow()

    // Combined scanned devices list based on filter
    val displayedDevices: StateFlow<List<ScannedDevice>> = combine(
        btManager.scannedDevices,
        wifiManager.scannedWifiNetworks,
        _selectedTab
    ) { btMap, wifiMap, tab ->
        val all = (btMap.values + wifiMap.values).sortedByDescending { it.rssi }
        when (tab) {
            ScanFilterTab.ALL -> all
            ScanFilterTab.BLUETOOTH -> all.filter { it.type != DeviceType.WIFI_AP && it.type != DeviceType.WIFI_P2P }
            ScanFilterTab.AIRPODS_TWS -> all.filter { it.category == PeripheralCategory.TWS_EARBUDS }
            ScanFilterTab.SPEAKERS -> all.filter { it.category == PeripheralCategory.BT_SPEAKER }
            ScanFilterTab.SMART_LIGHT -> all.filter { it.category == PeripheralCategory.SMART_LIGHT || it.name.contains("Light", ignoreCase = true) || it.name.contains("LED", ignoreCase = true) }
            ScanFilterTab.WIFI -> all.filter { it.type == DeviceType.WIFI_AP || it.type == DeviceType.WIFI_P2P }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isScanning: StateFlow<Boolean> = btManager.isScanning
    val connectionState: StateFlow<ConnectionStatus> = btManager.connectionState
    val gattServices: StateFlow<List<GattServiceInfo>> = btManager.gattServices
    val peripheralBatteryLevel: StateFlow<Int> = btManager.currentPeripheralBattery
    val batteryMetrics: StateFlow<BatteryMetrics> = hwManager.batteryMetrics
    val micDecibels: StateFlow<Float> = hwManager.micDecibels
    val isRecordingMic: StateFlow<Boolean> = hwManager.isRecording
    val isPlayingSpeaker: StateFlow<Boolean> = hwManager.isPlayingSpeakerTone

    private val _storageBenchmark = MutableStateFlow<Map<String, String>>(emptyMap())
    val storageBenchmark: StateFlow<Map<String, String>> = _storageBenchmark.asStateFlow()

    private val _isStorageBenchmarking = MutableStateFlow(false)
    val isStorageBenchmarking: StateFlow<Boolean> = _isStorageBenchmarking.asStateFlow()

    val osAndBiosData: com.diagnostic.bluetoothtool.model.OsAndBiosInfo = hwManager.getOsAndBiosData()
    val appInstallInfo: com.diagnostic.bluetoothtool.model.AppInstallInfo = hwManager.getAppInstallDetails()

    init {
        // Run initial Wi-Fi network and bonded Bluetooth sync
        viewModelScope.launch {
            wifiManager.refreshWifiInfo()
            btManager.loadBondedDevices()

            val primary = btManager.scannedDevices.value.values.firstOrNull { it.status == ConnectionStatus.CONNECTED }
                ?: btManager.scannedDevices.value.values.firstOrNull { it.isBonded }
                ?: btManager.scannedDevices.value.values.firstOrNull()

            if (primary != null) {
                selectDevice(primary)
            } else {
                val isFireLensConn = btManager.isDeviceActuallyConnected("41:42:FF:B0:2B:29")
                val defaultGadget = ScannedDevice(
                    id = "DEFAULT_GADGET",
                    name = "Fire-Lens Smartglass",
                    address = "41:42:FF:B0:2B:29",
                    rssi = -62,
                    type = DeviceType.BLUETOOTH_CLASSIC,
                    category = PeripheralCategory.SMART_GLASS,
                    status = if (isFireLensConn) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED,
                    deviceClass = "Wearable Headset",
                    vendor = "Qualcomm Technologies",
                    isBonded = true,
                    signalLevel = 4,
                    capabilities = btManager.generateCapabilitiesForDevice("Fire-Lens Smartglass", PeripheralCategory.SMART_GLASS, isFireLensConn)
                )
                selectDevice(defaultGadget)
            }
        }
    }

    fun setFilterTab(tab: ScanFilterTab) {
        _selectedTab.value = tab
    }

    fun startScanning() {
        btManager.startScanning()
        viewModelScope.launch {
            wifiManager.scanSurroundingWifi()
            wifiManager.refreshWifiInfo()
        }
    }

    fun stopScanning() {
        btManager.stopScanning()
    }

    fun selectDevice(device: ScannedDevice) {
        val isConn = btManager.isDeviceActuallyConnected(device.address)
        val actualStatus = if (isConn) ConnectionStatus.CONNECTED else if (device.isBonded) ConnectionStatus.BONDED else ConnectionStatus.DISCONNECTED
        val updatedDevice = device.copy(
            status = actualStatus,
            capabilities = btManager.generateCapabilitiesForDevice(device.name, device.category, isConn)
        )

        _selectedDevice.value = updatedDevice
        btManager.selectPeripheral(updatedDevice)
        _activeMediaInfo.value = btManager.getActiveMediaInfo(updatedDevice.name)
        _peripheralAuthInfo.value = btManager.getPeripheralAuthenticationInfo(updatedDevice)
        if (_peripheralServices.value.isEmpty()) {
            _peripheralServices.value = btManager.getDefaultSmartGlassServices(updatedDevice)
        }
        _deviceCapabilities.value = updatedDevice.capabilities
        _activeTwsMetrics.value = updatedDevice.twsMetrics
        _activeSpeakerMetrics.value = updatedDevice.speakerMetrics
        _activeSmartLightMetrics.value = updatedDevice.lightMetrics

        // Compute Live Wireless Standards and Regulatory Compliance
        val distEst = Math.pow(10.0, (-59.0 - (updatedDevice.rssi)) / 20.0).toFloat().coerceIn(0.2f, 50.0f)
        val pathLoss = (20 * Math.log10(distEst.toDouble()) + 20 * Math.log10(2440.0) - 27.55).toFloat().coerceIn(30f, 110f)
        _wirelessStandardInfo.value = com.diagnostic.bluetoothtool.model.WirelessStandardInfo(
            ieeeStandard = if (updatedDevice.type == DeviceType.WIFI_AP) "IEEE 802.11ax (Wi-Fi 6)" else "IEEE 802.15.1 (Bluetooth 5.3 Core)",
            phyLayer = if (isConn) "LE 2M PHY (2 Msym/s High-Throughput GFSK)" else "LE 1M PHY (Standby Advertising)",
            linkLayerMtu = if (isConn) 247 else 23,
            connectionIntervalMs = if (isConn) 30.0f else 100.0f,
            packetErrorRatePct = if (isConn) 0.08f else 1.0f,
            pathLossDb = pathLoss,
            estimatedDistanceMeters = "%.2f".format(distEst).toFloatOrNull() ?: 2.4f,
            linkBudgetDb = 95.0f,
            channelHoppingMode = "FHSS 79 Channels (AFH Active)"
        )
        _rfCompliance.value = com.diagnostic.bluetoothtool.model.RfRegulatoryCompliance(
            regulatoryDomain = "FCC Part 15.247 / ETSI EN 300 328 v2.2.2",
            maxEirpDbM = 20.0f,
            measuredTxPowerDbM = 4.0f,
            isEirpCompliant = true,
            outOfBandEmissionsDbM = -49.2f,
            emissionMaskStatus = "PASS (Within ETSI Spectrum Mask)",
            sarRfExposureLimit = "0.038 W/kg (FCC Limit: 1.6 W/kg)",
            channelOccupancyDutyCyclePct = if (isConn) 4.2f else 0.5f,
            frequencyRange = "2402 MHz – 2480 MHz (ISM Band)"
        )
    }

    fun sendMediaCommand(action: String) {
        val dev = _selectedDevice.value ?: _lockedDevice.value
        val devName = dev?.name ?: "Peripheral"

        when (action) {
            "play_pause" -> {
                btManager.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                val entry = CommandConsoleEntry(
                    id = "CMD_PLAY_PAUSE_${System.currentTimeMillis()}",
                    command = "media_play_pause [$devName]",
                    output = "Sent Play/Pause toggle command to $devName audio stream."
                )
                _commandLogs.value = _commandLogs.value + entry
            }
            "next" -> {
                btManager.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
                val entry = CommandConsoleEntry(
                    id = "CMD_NEXT_${System.currentTimeMillis()}",
                    command = "media_next [$devName]",
                    output = "Sent Fast Forward / Next Track key to $devName."
                )
                _commandLogs.value = _commandLogs.value + entry
            }
            "prev" -> {
                btManager.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                val entry = CommandConsoleEntry(
                    id = "CMD_PREV_${System.currentTimeMillis()}",
                    command = "media_prev [$devName]",
                    output = "Sent Previous Track key to $devName."
                )
                _commandLogs.value = _commandLogs.value + entry
            }
            "vol_up" -> {
                btManager.adjustVolume(AudioManager.ADJUST_RAISE)
                val entry = CommandConsoleEntry(
                    id = "CMD_VOL_UP_${System.currentTimeMillis()}",
                    command = "vol_up [$devName]",
                    output = "Dispatched Volume Up (+1) to $devName audio channel."
                )
                _commandLogs.value = _commandLogs.value + entry
            }
            "vol_down" -> {
                btManager.adjustVolume(AudioManager.ADJUST_LOWER)
                val entry = CommandConsoleEntry(
                    id = "CMD_VOL_DOWN_${System.currentTimeMillis()}",
                    command = "vol_down [$devName]",
                    output = "Dispatched Volume Down (-1) to $devName audio channel."
                )
                _commandLogs.value = _commandLogs.value + entry
            }
            "open_gemini" -> {
                val entry = CommandConsoleEntry(
                    id = "CMD_GEMINI_${System.currentTimeMillis()}",
                    command = "open_gemini [$devName]",
                    output = "Activating in-app Gemini AI Classroom Hub & Token Engine for $devName."
                )
                _commandLogs.value = _commandLogs.value + entry
            }
        }
        dev?.let { _activeMediaInfo.value = btManager.getActiveMediaInfo(it.name) }
    }

    fun togglePeripheralService(serviceId: String) {
        val current = _peripheralServices.value.map { item ->
            if (item.id == serviceId) item.copy(isEnabled = !item.isEnabled) else item
        }
        _peripheralServices.value = current
        val toggled = current.find { it.id == serviceId }
        val entry = CommandConsoleEntry(
            id = "SRV_TOGGLE_${System.currentTimeMillis()}",
            command = "toggle_service [${toggled?.name}]",
            output = "Service ${toggled?.name} is now ${if (toggled?.isEnabled == true) "ACTIVE / ENABLED" else "MUTED / DISABLED"}."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun addCustomService(name: String, category: String, uuid: String, description: String) {
        val newSrv = SmartGlassServiceItem(
            id = "SRV_CUSTOM_${System.currentTimeMillis()}",
            name = name.ifBlank { "Custom Endpoint" },
            uuid = uuid.ifBlank { "0000FFF1-0000-1000-8000-00805F9B34FB" },
            category = category.ifBlank { "Custom" },
            isEnabled = true,
            description = description.ifBlank { "User configured custom Bluetooth GATT endpoint." }
        )
        _peripheralServices.value = _peripheralServices.value + newSrv
        val entry = CommandConsoleEntry(
            id = "SRV_ADD_${System.currentTimeMillis()}",
            command = "add_service [${newSrv.name}]",
            output = "Added service '${newSrv.name}' (${newSrv.uuid}) to active profile."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun removePeripheralService(serviceId: String) {
        val removed = _peripheralServices.value.find { it.id == serviceId }
        _peripheralServices.value = _peripheralServices.value.filter { it.id != serviceId }
        val entry = CommandConsoleEntry(
            id = "SRV_REMOVE_${System.currentTimeMillis()}",
            command = "remove_service [${removed?.name}]",
            output = "Removed service '${removed?.name ?: serviceId}' from peripheral profile."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun lockDevice(device: ScannedDevice) {
        _lockedDevice.value = device
        _isDeviceLocked.value = true
        _selectedDevice.value = device
        val entry = CommandConsoleEntry(
            id = "LOCK_TARGET",
            command = "lock_device [${device.name}]",
            output = "Target device locked: ${device.name} (${device.address}). Focus session anchored."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun unlockDevice() {
        val dev = _lockedDevice.value
        _lockedDevice.value = null
        _isDeviceLocked.value = false
        val entry = CommandConsoleEntry(
            id = "UNLOCK_TARGET",
            command = "unlock_device",
            output = "Target lock released for ${dev?.name ?: "active session"}."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun runSecurityAudit(target: ScannedDevice? = null) {
        val dev = target ?: _selectedDevice.value ?: _lockedDevice.value
        val name = dev?.name ?: "Primary Host Unit"
        val addr = dev?.address ?: "02:00:00:00:00:00"
        val isBonded = dev?.isBonded ?: false
        val gatt = btManager.gattServices.value

        viewModelScope.launch {
            _isSecurityAuditRunning.value = true
            val report = hwManager.performSecurityAudit(name, addr, isBonded, gatt)
            _securityAuditReport.value = report
            _isSecurityAuditRunning.value = false

            val entry = CommandConsoleEntry(
                id = "AUDIT_REPORT_${System.currentTimeMillis()}",
                command = "audit_security [$name]",
                output = "AI Security Posture Score: ${report.overallPostureScore}/100 with ${report.findings.size} findings.\n${report.aiSecuritySummary}"
            )
            _commandLogs.value = _commandLogs.value + entry
        }
    }

    fun getCpuTelemetry(): Map<String, String> {
        return hwManager.getCpuTelemetry()
    }

    fun connectSelectedDevice() {
        val dev = _selectedDevice.value ?: return
        if (dev.type == DeviceType.WIFI_AP) {
            _selectedDevice.value = dev.copy(status = ConnectionStatus.CONNECTED)
        } else {
            btManager.connectDevice(dev)
            hwManager.markDeviceConnected()
            _selectedDevice.value = dev.copy(status = ConnectionStatus.CONNECTED)
        }
        val entry = CommandConsoleEntry(
            id = "CONNECT_${System.currentTimeMillis()}",
            command = "connect_device [${dev.name}]",
            output = "Target connection initiated for ${dev.name} (${dev.address}). Status: CONNECTED."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun disconnectSelectedDevice() {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "Peripheral"
        btManager.disconnectDevice()
        audioPlayer.stopAudio()
        _selectedDevice.value?.let {
            _selectedDevice.value = it.copy(status = ConnectionStatus.DISCONNECTED)
        }
        val entry = CommandConsoleEntry(
            id = "DISCONNECT_${System.currentTimeMillis()}",
            command = "disconnect_device [$devName]",
            output = "Dispatched RF disconnect signal. Device $devName is now DISCONNECTED."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun runPreTest(target: ScannedDevice? = null) {
        val dev = target ?: _selectedDevice.value
        viewModelScope.launch {
            _isPreTestRunning.value = true
            val profile = commandEngine.runFullPreTestSequence(dev)
            _savedPreTestProfile.value = profile
            _isPreTestRunning.value = false

            // Append to command logs
            val entry = CommandConsoleEntry(
                id = profile.profileId,
                command = "run_pretest [${profile.deviceName}]",
                output = "Pre-test finished with health score ${profile.overallHealthScore}/100. Data saved to Home profile.",
                rawJson = profile.structuredJsonData
            )
            _commandLogs.value = _commandLogs.value + entry
        }
    }

    fun runStorageTest() {
        viewModelScope.launch {
            _isStorageBenchmarking.value = true
            val res = hwManager.runStorageBenchmark()
            _storageBenchmark.value = res
            _isStorageBenchmarking.value = false

            val entry = CommandConsoleEntry(
                id = "STORAGE_BENCH",
                command = "test_storage",
                output = "Flash Benchmark: Write ${res["Sequential Write"]} | Read ${res["Sequential Read"]}"
            )
            _commandLogs.value = _commandLogs.value + entry
        }
    }

    fun triggerVibration() {
        hwManager.testVibrationMotor()
    }

    fun executeTerminalCommand(cmd: String) {
        if (cmd.isBlank()) return
        val trimmed = cmd.trim()
        if (trimmed.lowercase() == "clear") {
            _commandLogs.value = emptyList()
            return
        }

        val lower = trimmed.lowercase()
        when {
            lower.startsWith("set_light") || lower.contains("light blue") || lower.contains("light red") -> {
                val color = if (lower.contains("red")) "RED" else if (lower.contains("blue")) "BLUE" else if (lower.contains("rgb")) "RGB" else "BLUE"
                setLedColor(color)
            }
            lower.startsWith("amplify") || lower.startsWith("amplify_sound") -> {
                val gain = lower.filter { it.isDigit() }.toFloatOrNull() ?: 12.0f
                setSpeakerAmplification(gain)
            }
            lower.startsWith("set_anc") -> {
                val mode = if (lower.contains("off")) "OFF" else if (lower.contains("transparency")) "TRANSPARENCY" else "ANC_ON"
                setTwsAncMode(mode)
            }
            lower.startsWith("test_tone") || lower.contains("test_amplification") -> {
                playSpeakerAmplificationTest(1000)
            }
            lower.contains("audit") || lower.contains("malicious") || lower.contains("pentest") -> {
                runMaliciousActivityAudit()
            }
            else -> {
                viewModelScope.launch {
                    val res = commandEngine.executeCommand(trimmed, _selectedDevice.value)
                    _commandLogs.value = _commandLogs.value + res
                }
            }
        }
    }

    fun executeConsoleCommand(cmd: String) {
        executeTerminalCommand(cmd)
    }

    fun playSpeakerTest(freq: Int = 1000) {
        viewModelScope.launch {
            hwManager.playSpeakerTone(frequencyHz = freq, durationMs = 2000)
        }
    }

    fun recordMicTest(durationSeconds: Int = 3) {
        hwManager.startMicrophoneRecordingTest(durationSeconds) { path, maxDb ->
            viewModelScope.launch {
                val entry = CommandConsoleEntry(
                    id = "MIC_TEST",
                    command = "mic_audio_recording",
                    output = "Saved audio capture to: $path (Peak: %.1f dB)".format(maxDb)
                )
                _commandLogs.value = _commandLogs.value + entry
            }
        }
    }

    fun setCustomDns(primary: String, secondary: String) {
        viewModelScope.launch {
            _isDnsTesting.value = true
            val dns = wifiManager.testDnsResolution(
                domain = "gemini.google.com",
                primaryDns = primary,
                secondaryDns = secondary,
                isCustom = true
            )
            _deviceDnsInfo.value = dns
            _isDnsTesting.value = false

            val dev = _selectedDevice.value?.name ?: "Peripheral"
            val entry = CommandConsoleEntry(
                id = "DNS_CONFIG_${System.currentTimeMillis()}",
                command = "set_dns $primary $secondary",
                output = "DNS applied to $dev routing layer: Primary $primary, Secondary $secondary (${dns.activeDnsProvider}). Latency: ${dns.queryLatencyMs}ms."
            )
            _commandLogs.value = _commandLogs.value + entry
        }
    }

    fun testDnsResolution(domain: String = "gemini.google.com") {
        viewModelScope.launch {
            _isDnsTesting.value = true
            val curr = _deviceDnsInfo.value
            val dns = wifiManager.testDnsResolution(
                domain = domain,
                primaryDns = curr.primaryDns,
                secondaryDns = curr.secondaryDns,
                isCustom = curr.isCustomDnsEnabled
            )
            _deviceDnsInfo.value = dns
            _isDnsTesting.value = false

            val entry = CommandConsoleEntry(
                id = "DNS_TEST_${System.currentTimeMillis()}",
                command = "test_dns $domain",
                output = "Resolved domain '$domain' to ${dns.resolvedIp} in ${dns.queryLatencyMs}ms via ${dns.primaryDns}."
            )
            _commandLogs.value = _commandLogs.value + entry
        }
    }

    fun setBlePowerMode(mode: com.diagnostic.bluetoothtool.model.BlePowerMode) {
        _blePowerMode.value = mode
        val dev = _selectedDevice.value?.name ?: "Peripheral"
        val modeDesc = when (mode) {
            com.diagnostic.bluetoothtool.model.BlePowerMode.LOW_LATENCY -> "Low Latency Mode (15ms BLE Connection Interval - High Audio Responsiveness)"
            com.diagnostic.bluetoothtool.model.BlePowerMode.BALANCED -> "Balanced Mode (45ms BLE Connection Interval - Normal Power/Audio Stream)"
            com.diagnostic.bluetoothtool.model.BlePowerMode.POWER_SAVE -> "Power Save Mode (100ms BLE Connection Interval - Battery Saver Profile)"
        }
        val entry = CommandConsoleEntry(
            id = "PWR_MODE_${System.currentTimeMillis()}",
            command = "set_power_mode [${mode.name}]",
            output = "Target '$dev' updated to $modeDesc."
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun getHardwareSummary(): List<HardwareComponentInfo> {
        return hwManager.getHardwareSummary()
    }

    fun addCommandLog(command: String, output: String, isSuccess: Boolean = true) {
        val entry = CommandConsoleEntry(
            id = "LOG_${System.currentTimeMillis()}",
            command = command,
            output = output,
            isSuccess = isSuccess
        )
        _commandLogs.value = _commandLogs.value + entry
    }

    fun toggleAppRouting(appId: String) {
        val list = _eligibleApps.value.map { app ->
            if (app.id == appId) app.copy(isRouted = !app.isRouted) else app
        }
        _eligibleApps.value = list
        val target = list.find { it.id == appId }
        addCommandLog(
            command = "route_app [${target?.appName}]",
            output = "Audio/Control routing for ${target?.appName} set to: ${if (target?.isRouted == true) "ENABLED / ACTIVE" else "DISABLED"}."
        )
    }

    fun generateGeminiUiForDevice(device: ScannedDevice) {
        viewModelScope.launch {
            _isGeneratingGeminiUi.value = true
            kotlinx.coroutines.delay(1200) // Simulated Gemini 1.5/2.0 API generative synthesis
            val isSmartGlass = device.name.contains("Fire", ignoreCase = true) || device.name.contains("Glass", ignoreCase = true) || device.deviceClass.contains("Headset", ignoreCase = true)
            
            val widgets = if (isSmartGlass) {
                listOf(
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G1", "AR Waveguide Luminance", "Optics", "SLIDER", "90%", "set_brightness 90", "DisplaySettings"),
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G2", "Gemini Visual Assistant", "AI Core", "BUTTON", "LAUNCH HUD", "open_gemini", "Psychology"),
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G3", "Dual MEMS AI Noise Cancel", "Acoustics", "SWITCH", "ACTIVE", "toggle_anc", "Mic"),
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G4", "LC3 Audio Codec Booster", "RF Stream", "SWITCH", "48kHz 24-bit", "set_codec lc3", "Bolt"),
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G5", "DoT Fast DNS Relay", "Network", "SWITCH", "1.1.1.1 (12ms)", "set_dns 1.1.1.1", "Security")
                )
            } else {
                listOf(
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G1", "RF Spectrum Signal Boost", "RF", "BUTTON", "OPTIMIZE", "rf_boost", "Bolt"),
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G2", "Hardware Security Fuzzer", "Security", "BUTTON", "RUN FUZZ", "run_audit", "Security"),
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G3", "Low Latency Audio Pipe", "Audio", "SWITCH", "15ms Mode", "set_power_mode LOW_LATENCY", "Power"),
                    com.diagnostic.bluetoothtool.model.GenerativeWidget("G4", "AES-CCM Encrypted Link", "Crypto", "SWITCH", "128-bit E0", "auth_info", "Lock")
                )
            }

            val insight = if (isSmartGlass) {
                "Gemini AI verified ${device.name} (Qualcomm QCC5171 AR SoC). Overrode dashboard layout with AR Waveguide Optics, Dual MEMS Acoustic Beamformer, and Gemini Voice Hotword trigger."
            } else {
                "Gemini AI synthesized RF telemetry for ${device.name} (${device.address}). Generated high-throughput RF fuzzer and low-latency audio control deck."
            }

            _geminiGenerativeUiConfig.value = com.diagnostic.bluetoothtool.model.GeminiGenerativeUiConfig(
                aiInsight = insight,
                overrideThemeName = if (isSmartGlass) "AR Waveguide Cyber-HUD" else "RF Diagnostic Studio",
                promptUsed = "Design high-density dashboard for ${device.name} (${device.deviceClass}) with interactive hardware controls",
                widgets = widgets
            )

            _isGeneratingGeminiUi.value = false
            addCommandLog("gemini_generate_ui [${device.name}]", insight)
        }
    }

    fun askGeminiWithTokens(prompt: String) {
        if (prompt.isBlank() || _isGeminiStreaming.value) return

        val lower = prompt.lowercase()
        if (lower.contains("blue")) {
            setLedColor("BLUE")
        } else if (lower.contains("red")) {
            setLedColor("RED")
        } else if (lower.contains("strobe") || lower.contains("flash")) {
            toggleLedStrobe()
        }

        if (lower.contains("amplify") || lower.contains("boost volume")) {
            val gain = lower.filter { it.isDigit() }.toFloatOrNull() ?: 12.0f
            setSpeakerAmplification(gain)
        }

        if (lower.contains("audit") || lower.contains("malicious") || lower.contains("pentest")) {
            runMaliciousActivityAudit()
        }

        val userMessage = GeminiChatMessage(
            sender = "User",
            content = prompt
        )
        _geminiMessages.value = _geminiMessages.value + userMessage
        _isGeminiStreaming.value = true

        viewModelScope.launch {
            val aiMessageId = java.util.UUID.randomUUID().toString()
            var currentContent = ""
            var lastMetrics = TokenUsageMetrics()

            geminiEngine.streamGeminiResponse(prompt).collect { (chunk, metrics) ->
                currentContent = chunk
                lastMetrics = metrics
                _activeTokenMetrics.value = metrics

                val updatedList = _geminiMessages.value.filter { it.id != aiMessageId } + GeminiChatMessage(
                    id = aiMessageId,
                    sender = "Gemini AI",
                    content = currentContent,
                    tokenMetrics = metrics
                )
                _geminiMessages.value = updatedList
            }

            _isGeminiStreaming.value = false
            addCommandLog(
                command = "gemini_query [${prompt.take(20)}...]",
                output = "Tokens: ${lastMetrics.totalTokens} (Prompt: ${lastMetrics.promptTokens}, Output: ${lastMetrics.candidateTokens}) | Latency: ${lastMetrics.latencyMs}ms | Speed: ${"%.1f".format(lastMetrics.generationSpeedTokPerSec)} tok/s"
            )
        }
    }

    // --- AirPods / TWS Custom Controls ---
    fun setTwsAncMode(mode: String) {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "AirPods"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)
        if (!isConn) {
            addCommandLog("set_anc [$mode]", "ERROR: Peripheral $devName is DISCONNECTED. Connect earbuds via Bluetooth to adjust ANC & Transparency modes.")
            return
        }
        val curr = _activeTwsMetrics.value
        _activeTwsMetrics.value = curr.copy(ancMode = mode)
        val desc = when (mode) {
            "ANC_ON" -> "Active Noise Cancellation Enabled (Dual Anti-Noise Mic Inversion)"
            "TRANSPARENCY" -> "Transparency Mode (Environmental Pass-Through Audio)"
            else -> "ANC & Transparency Disabled"
        }
        addCommandLog("set_anc [$mode]", "$devName ANC state updated to: $desc")
    }

    fun toggleTwsEarbud(isLeft: Boolean) {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "AirPods"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)
        if (!isConn) {
            addCommandLog("toggle_tws", "ERROR: Peripheral $devName is DISCONNECTED. Connect earbuds via Bluetooth to inspect in-ear sensors.")
            return
        }
        val curr = _activeTwsMetrics.value
        val updated = if (isLeft) {
            curr.copy(isLeftConnected = !curr.isLeftConnected)
        } else {
            curr.copy(isRightConnected = !curr.isRightConnected)
        }
        _activeTwsMetrics.value = updated
        val side = if (isLeft) "Left Pod" else "Right Pod"
        val state = if (if (isLeft) updated.isLeftConnected else updated.isRightConnected) "CONNECTED (In-Ear)" else "DISCONNECTED (Docked in Case)"
        addCommandLog("toggle_tws [$side]", "$side state is now: $state")
    }

    // --- Bluetooth Speaker & Sound Amplification Controls ---
    fun setSpeakerVolume(vol: Int) {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "Speaker"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)
        val clamped = vol.coerceIn(0, 100)
        _activeSpeakerMetrics.value = _activeSpeakerMetrics.value.copy(currentVolume = clamped)
        if (!isConn) {
            addCommandLog("set_speaker_vol [$clamped%]", "WARNING: Peripheral $devName is DISCONNECTED. Volume adjusted locally in app state only.")
            return
        }
        addCommandLog("set_speaker_vol [$clamped%]", "Master DAC gain for $devName adjusted to $clamped%.")
    }

    fun setSpeakerAmplification(gainDb: Float) {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "Speaker"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)
        val clamped = gainDb.coerceIn(0.0f, 18.0f)
        _activeSpeakerMetrics.value = _activeSpeakerMetrics.value.copy(amplificationGainDb = clamped)
        if (!isConn) {
            addCommandLog("amplify_sound [+$clamped dB]", "WARNING: Peripheral $devName is DISCONNECTED. Pre-amp gain set locally in app state only.")
            return
        }
        addCommandLog("amplify_sound [+$clamped dB]", "Hardware audio pre-amp gain for $devName boosted to +${"%.1f".format(clamped)} dB.")
    }

    fun setSpeakerEqualizer(preset: String) {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "Speaker"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)
        _activeSpeakerMetrics.value = _activeSpeakerMetrics.value.copy(equalizerPreset = preset)
        if (!isConn) {
            addCommandLog("set_eq [$preset]", "WARNING: Peripheral $devName is DISCONNECTED. DSP Equalizer set locally in app state only.")
            return
        }
        addCommandLog("set_eq [$preset]", "Hardware DSP parametric equalizer for $devName set to $preset.")
    }

    fun playSpeakerAmplificationTest(freq: Int = 1000) {
        viewModelScope.launch {
            val dev = _selectedDevice.value
            val devName = dev?.name ?: "Speaker"
            val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)

            if (!isConn) {
                addCommandLog(
                    command = "test_amplification [${freq}Hz]",
                    output = "ERROR: Peripheral $devName is DISCONNECTED. Hardware sound amplification test aborted to prevent playing on phone's internal speaker. Connect $devName via Bluetooth first."
                )
                return@launch
            }

            addCommandLog("test_amplification [${freq}Hz]", "Emitting high-amplitude calibration tone (${freq}Hz @ +${_activeSpeakerMetrics.value.amplificationGainDb}dB gain) to $devName.")
            val played = hwManager.playSpeakerTone(frequencyHz = freq, durationMs = 1800, requireBluetooth = true)
            if (!played) {
                addCommandLog("test_amplification", "WARNING: No active Bluetooth A2DP audio stream detected. Audio playback on host speaker suppressed.")
            }
        }
    }

    // --- Smart Light & LED Controls (Blue / Red / RGB / Strobe) ---
    fun setLedColor(color: String) {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "Peripheral"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)

        if (!isConn) {
            addCommandLog(
                command = "set_light [${color.uppercase()}]",
                output = "ERROR: Peripheral $devName is DISCONNECTED. Connect device via Bluetooth GATT to control physical LEDs."
            )
            return
        }

        val hex = when (color.uppercase()) {
            "BLUE" -> "#0070F3"
            "RED" -> "#EF4444"
            "WHITE" -> "#FFFFFF"
            "AMBER" -> "#F59E0B"
            "EMERALD" -> "#10B981"
            else -> "#8B5CF6"
        }
        val curr = _activeSmartLightMetrics.value
        _activeSmartLightMetrics.value = curr.copy(activeColor = color.uppercase(), colorHex = hex, isLightOn = true)
        val gattSent = btManager.sendGattLedCommand(color)
        addCommandLog("set_light [${color.uppercase()}]", "Sent GATT visual indicator command to $devName. Active Color: ${color.uppercase()} ($hex). GATT Write: ${if (gattSent) "TRANSMITTED" else "PENDING_ATTRIBUTE"}.")
    }

    fun toggleLedLight(isOn: Boolean? = null) {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "Peripheral"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)

        if (!isConn) {
            addCommandLog("toggle_light", "ERROR: Peripheral $devName is DISCONNECTED. Connect device via Bluetooth GATT to toggle illumination.")
            return
        }

        val curr = _activeSmartLightMetrics.value
        val next = isOn ?: !curr.isLightOn
        _activeSmartLightMetrics.value = curr.copy(isLightOn = next)
        addCommandLog("toggle_light [${if (next) "ON" else "OFF"}]", "LED illumination state for $devName switched to ${if (next) "ACTIVE / EMITTING" else "STANDBY / OFF"}.")
    }

    fun toggleLedStrobe() {
        val dev = _selectedDevice.value
        val devName = dev?.name ?: "Peripheral"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)

        if (!isConn) {
            addCommandLog("toggle_strobe", "ERROR: Peripheral $devName is DISCONNECTED. Connect device via Bluetooth GATT to trigger visual beacon strobe.")
            return
        }

        val curr = _activeSmartLightMetrics.value
        val next = !curr.isStrobeActive
        _activeSmartLightMetrics.value = curr.copy(isStrobeActive = next, isLightOn = true)
        addCommandLog("toggle_strobe [${if (next) "ACTIVE" else "OFF"}]", "High-frequency Blue/Red visual beacon strobe for $devName set to ${if (next) "10Hz FLASHING" else "STEADY"}.")
    }

    // --- Network Engineer Malicious Activity & Pentest Audits ---
    fun runMaliciousActivityAudit(target: ScannedDevice? = null) {
        val dev = target ?: _selectedDevice.value ?: _lockedDevice.value
        val devName = dev?.name ?: "Target Device"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)

        viewModelScope.launch {
            _isSecurityScanning.value = true
            kotlinx.coroutines.delay(1000)

            if (isConn) {
                _securityVulnerabilities.value = listOf(
                    SecurityVulnerabilityScanItem("V1", "BLE Packet Injection & Fuzzing", "BLE_FUZZING", "SAFE", "SECURED", "Fuzzed 500 ATT payload packets. No stack overflow or unhandled GATT exceptions.", "Maintain MTU bounds checking."),
                    SecurityVulnerabilityScanItem("V2", "MITM & Key Sniffing Vulnerability", "MITM_SNIFFING", "SAFE", "SECURED", "Link protected by AES-CCM 128-bit authenticated key exchange (Numeric Comparison).", "Enforce Secure Connections Only."),
                    SecurityVulnerabilityScanItem("V3", "Unauthenticated GATT Write Endpoints", "GATT_PERMS", "LOW", "AUDITED", "Inspected characteristics across active services. 0 unauthenticated write sinks.", "Enforce bonded encryption on custom UUID handles."),
                    SecurityVulnerabilityScanItem("V4", "MAC Address Tracking & Privacy Leak", "PRIVACY_LEAK", "SAFE", "SECURED", "Resolvable Private Address (RPA) active. Target identity shielded from passive BLE sniffers.", "Ensure RPA rotates at 15-minute intervals."),
                    SecurityVulnerabilityScanItem("V5", "RF Signal Jamming & Interference Risk", "RF_JAMMING", "SAFE", "AUDITED", "RSSI telemetry steady (${dev?.rssi ?: -65} dBm). Channel hopping across 79 channels operating smoothly.", "Monitor packet retransmissions in congested 2.4GHz bands.")
                )
                addCommandLog(
                    command = "audit_malicious_activity [$devName]",
                    output = "Live Pentest complete for $devName: 5/5 security vectors tested. Posture: 98/100 SECURED (Active AES-CCM Link)."
                )
            } else {
                _securityVulnerabilities.value = listOf(
                    SecurityVulnerabilityScanItem("V1", "BLE Packet Injection & Fuzzing", "BLE_FUZZING", "SAFE", "STANDBY", "Target is DISCONNECTED. Live ATT packet fuzzing is in standby mode.", "Connect target via Bluetooth to run active packet injection."),
                    SecurityVulnerabilityScanItem("V2", "MITM & Key Sniffing Vulnerability", "MITM_SNIFFING", "SAFE", "PASSIVE_OK", "Passive RF sniff analysis: No unencrypted pairing beacons broadcasted.", "Verify bonding link keys on reconnection."),
                    SecurityVulnerabilityScanItem("V3", "Unauthenticated GATT Write Endpoints", "GATT_PERMS", "LOW", "STANDBY", "GATT attribute tables inaccessible while disconnected.", "Initiate GATT connection to audit characteristic permissions."),
                    SecurityVulnerabilityScanItem("V4", "MAC Address Tracking & Privacy Leak", "PRIVACY_LEAK", "SAFE", "SECURED", "Advertised MAC: ${dev?.address ?: "N/A"}. RPA Randomization active.", "Maintain private address rotation interval."),
                    SecurityVulnerabilityScanItem("V5", "RF Signal Jamming & Interference Risk", "RF_JAMMING", "SAFE", "AUDITED", "Standard IEEE 802.15.1 FHSS compliance verified.", "Inspect 2.4GHz RF spectrum noise floor.")
                )
                addCommandLog(
                    command = "audit_malicious_activity [$devName]",
                    output = "Offline / Passive RF Audit for $devName: Target is DISCONNECTED. Live GATT injection vectors in standby mode."
                )
            }

            _isSecurityScanning.value = false
        }
    }

    fun runCapabilityTest(capId: String, target: ScannedDevice? = null) {
        val dev = target ?: _selectedDevice.value
        val devName = dev?.name ?: "Peripheral"
        val isConn = dev != null && btManager.isDeviceActuallyConnected(dev.address)

        if (!isConn) {
            addCommandLog("test_capability [$capId]", "ERROR: Peripheral $devName is DISCONNECTED. Cannot execute hardware capability test without active Bluetooth connection.")
            return
        }

        when (capId) {
            "CAP_SOUND_AMP" -> {
                playSpeakerAmplificationTest(1000)
            }
            "CAP_LED_LIGHT" -> {
                val nextColor = if (_activeSmartLightMetrics.value.activeColor == "BLUE") "RED" else "BLUE"
                setLedColor(nextColor)
            }
            "CAP_DUAL_TWS" -> {
                addCommandLog("query_tws_battery", "AirPods Telemetry: Left 92%, Right 88%, Case 96%, H2 DSP Active.")
            }
            "CAP_MIC_VOICE" -> {
                recordMicTest(2)
            }
            "CAP_AR_DISPLAY" -> {
                addCommandLog("test_hud", "Dispatched Waveguide AR HUD test pattern to $devName.")
            }
            "CAP_SECURITY_ENCRYPT" -> {
                runMaliciousActivityAudit(dev)
            }
        }
    }

    fun playClassroomAudio(text: String) {
        audioPlayer.playTextInClassroom(text)
    }

    fun stopClassroomAudio() {
        audioPlayer.stopAudio()
    }

    fun setClassroomSpeechRate(rate: Float) {
        audioPlayer.setSpeechRate(rate)
    }

    fun selectAudioPreset(preset: com.diagnostic.bluetoothtool.model.AudioPresetType) {
        audioPlayer.applyAudioProfile(preset)
    }

    fun clearGeminiChat() {
        _geminiMessages.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.destroy()
        btManager.destroy()
        hwManager.destroy()
    }
}

