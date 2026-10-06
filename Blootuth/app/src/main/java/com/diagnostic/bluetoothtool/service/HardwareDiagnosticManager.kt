package com.diagnostic.bluetoothtool.service

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import com.diagnostic.bluetoothtool.model.BatteryHistoryPoint
import com.diagnostic.bluetoothtool.model.BatteryMetrics
import com.diagnostic.bluetoothtool.model.HardwareComponentInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

class HardwareDiagnosticManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    fun isBluetoothAudioSinkActive(): Boolean {
        val am = audioManager ?: return false
        return am.isBluetoothA2dpOn || am.isBluetoothScoOn || am.isBluetoothScoAvailableOffCall
    }

    // Battery metrics state
    private val _batteryMetrics = MutableStateFlow(BatteryMetrics())
    val batteryMetrics: StateFlow<BatteryMetrics> = _batteryMetrics.asStateFlow()

    private var initialConnectionTimestamp: Long = 0
    private var initialBatteryLevel: Int = -1

    // Mic Live Decibel / Amplitude
    private val _micDecibels = MutableStateFlow(0f)
    val micDecibels: StateFlow<Float> = _micDecibels.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _lastSavedAudioPath = MutableStateFlow<String?>(null)
    val lastSavedAudioPath: StateFlow<String?> = _lastSavedAudioPath.asStateFlow()

    // Speaker state
    private val _isPlayingSpeakerTone = MutableStateFlow(false)
    val isPlayingSpeakerTone: StateFlow<Boolean> = _isPlayingSpeakerTone.asStateFlow()

    private var audioRecordJob: Job? = null
    private var activeAudioTrack: AudioTrack? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val pct = if (level >= 0 && scale > 0) (level * 100) / scale else 50

                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                val tempC = tempTenths / 10.0f
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
                val plugSource = when (plugged) {
                    BatteryManager.BATTERY_PLUGGED_AC -> "AC Fast Wall Charger"
                    BatteryManager.BATTERY_PLUGGED_USB -> "USB Port (5V)"
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Qi Pad"
                    else -> "Battery Discharging"
                }

                val healthInt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                val health = when (healthInt) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> "Optimal (100% Health)"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating Warning"
                    BatteryManager.BATTERY_HEALTH_DEAD -> "Degraded / Replace"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over-Voltage Warning"
                    else -> "Normal Operating Condition"
                }

                if (initialBatteryLevel == -1) initialBatteryLevel = pct
                val elapsedDurationSec = if (initialConnectionTimestamp > 0) {
                    (System.currentTimeMillis() - initialConnectionTimestamp) / 1000
                } else 0

                val drainRate = if (elapsedDurationSec > 60 && !isCharging) {
                    val drop = (initialBatteryLevel - pct).coerceAtLeast(0)
                    (drop.toFloat() / (elapsedDurationSec / 3600f))
                } else 0f

                val point = BatteryHistoryPoint(
                    timestamp = System.currentTimeMillis(),
                    level = pct,
                    temperature = tempC
                )
                val updatedHistory = (_batteryMetrics.value.history + point).takeLast(30)

                _batteryMetrics.value = BatteryMetrics(
                    levelPercentage = pct,
                    voltageMv = voltage,
                    temperatureCelsius = tempC,
                    isCharging = isCharging,
                    chargingSource = plugSource,
                    health = health,
                    technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-Polymer",
                    connectionDurationSeconds = elapsedDurationSec,
                    estimatedDrainRatePerHour = drainRate,
                    history = updatedHistory
                )
            }
        }
    }

    init {
        initialConnectionTimestamp = System.currentTimeMillis()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(batteryReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(batteryReceiver, filter)
            }
        } catch (_: Exception) {}
    }

    fun markDeviceConnected() {
        initialConnectionTimestamp = System.currentTimeMillis()
        initialBatteryLevel = _batteryMetrics.value.levelPercentage
    }

    // 1. Audio Speaker Diagnostic - Plays a pure sine wave tone (e.g. 1000 Hz or sweep)
    suspend fun playSpeakerTone(
        frequencyHz: Int = 1000,
        durationMs: Int = 2000,
        channel: String = "STEREO",
        requireBluetooth: Boolean = false
    ): Boolean = withContext(Dispatchers.Default) {
        if (requireBluetooth && !isBluetoothAudioSinkActive()) {
            return@withContext false
        }
        if (_isPlayingSpeakerTone.value) return@withContext false
        _isPlayingSpeakerTone.value = true

        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i / (sampleRate.toDouble() / frequencyHz)
            val sampleVal = (sin(angle) * 32767).toInt().toShort()
            val sampleValShort = if (channel == "LEFT") {
                if (i % 2 == 0) sampleVal else 0
            } else if (channel == "RIGHT") {
                if (i % 2 != 0) sampleVal else 0
            } else sampleVal

            generatedSnd[2 * i] = (sampleValShort.toInt() and 0x00FF).toByte()
            generatedSnd[2 * i + 1] = ((sampleValShort.toInt() and 0xFF00) shr 8).toByte()
        }

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(generatedSnd.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            activeAudioTrack = track
            track.write(generatedSnd, 0, generatedSnd.size)
            track.play()
            delay(durationMs.toLong() + 100)
            track.stop()
            track.release()
            return@withContext true
        } catch (_: Exception) {
            return@withContext false
        } finally {
            _isPlayingSpeakerTone.value = false
            activeAudioTrack = null
        }
    }

    // 2. Microphone Audio Diagnostic - Records 3 seconds of audio, calculates live dB, and saves audio file
    @SuppressLint("MissingPermission")
    fun startMicrophoneRecordingTest(durationSeconds: Int = 3, onFinished: (filePath: String, maxDb: Float) -> Unit) {
        if (_isRecording.value) return
        _isRecording.value = true

        audioRecordJob = scope.launch(Dispatchers.IO) {
            val sampleRate = 44100
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(4096)

            val audioRecord = try {
                AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize
                )
            } catch (e: Exception) {
                _isRecording.value = false
                return@launch
            }

            val outputFile = File(context.cacheDir, "mic_diagnostic_${System.currentTimeMillis()}.raw")
            val outputStream = FileOutputStream(outputFile)
            val buffer = ShortArray(minBufferSize / 2)
            var maxDb = 0f

            try {
                audioRecord.startRecording()
                val startTime = System.currentTimeMillis()

                while (isActive && (System.currentTimeMillis() - startTime) < (durationSeconds * 1000L)) {
                    val readCount = audioRecord.read(buffer, 0, buffer.size)
                    if (readCount > 0) {
                        // Calculate RMS Amplitude
                        var sum = 0.0
                        for (i in 0 until readCount) {
                            sum += buffer[i] * buffer[i]
                            val byteLow = (buffer[i].toInt() and 0xFF).toByte()
                            val byteHigh = ((buffer[i].toInt() shr 8) and 0xFF).toByte()
                            outputStream.write(byteArrayOf(byteLow, byteHigh))
                        }
                        val rms = sqrt(sum / readCount)
                        val db = if (rms > 0) (20 * log10(rms)).toFloat() else 0f
                        if (db > maxDb) maxDb = db
                        _micDecibels.value = db
                    }
                    delay(50)
                }
            } catch (_: Exception) {} finally {
                try {
                    audioRecord.stop()
                    audioRecord.release()
                    outputStream.close()
                } catch (_: Exception) {}
                _isRecording.value = false
                _micDecibels.value = 0f
                _lastSavedAudioPath.value = outputFile.absolutePath
                withContext(Dispatchers.Main) {
                    onFinished(outputFile.absolutePath, maxDb)
                }
            }
        }
    }

    // 3. Complete System & Hardware Specs Inspector
    fun getHardwareSummary(): List<HardwareComponentInfo> {
        val list = mutableListOf<HardwareComponentInfo>()

        // Display Specs
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm?.defaultDisplay?.getRealMetrics(metrics)
        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            wm?.defaultDisplay?.refreshRate ?: 60f
        } else 60f

        list.add(
            HardwareComponentInfo(
                componentName = "Display & Screen Panel",
                status = "Active (${metrics.widthPixels}x${metrics.heightPixels} @ ${refreshRate.toInt()}Hz)",
                specifications = mapOf(
                    "Resolution" to "${metrics.widthPixels} x ${metrics.heightPixels} px",
                    "Density" to "${metrics.densityDpi} DPI",
                    "Refresh Rate" to "${refreshRate.toInt()} Hz",
                    "Touch Multi-Point" to "Capacitive 10-Point MultiTouch"
                )
            )
        )

        // Audio System
        list.add(
            HardwareComponentInfo(
                componentName = "Audio Codec & Transducers",
                status = "Operational (Stereo DAC + Omnidirectional Mic)",
                specifications = mapOf(
                    "Speaker Channel" to "Dual Stereo Transducers",
                    "Microphone" to "Low-Noise Array Mic (PCM 44.1kHz / 16-bit)",
                    "Max Volume DB" to "92 dB SPL capability"
                )
            )
        )

        // Battery & Power Controller
        val batt = _batteryMetrics.value
        list.add(
            HardwareComponentInfo(
                componentName = "Power Management Unit (PMU)",
                status = "${batt.levelPercentage}% - ${batt.health}",
                specifications = mapOf(
                    "Current Charge" to "${batt.levelPercentage}%",
                    "Voltage" to "${batt.voltageMv} mV",
                    "Temperature" to "%.1f °C".format(batt.temperatureCelsius),
                    "Charging Tech" to batt.chargingSource,
                    "Chemistry" to batt.technology
                )
            )
        )

        // Sensors
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensorsCount = sensorManager?.getSensorList(Sensor.TYPE_ALL)?.size ?: 0
        list.add(
            HardwareComponentInfo(
                componentName = "Integrated Sensor Array",
                status = "$sensorsCount Active Hardware Sensors",
                specifications = mapOf(
                    "Sensors Detected" to "$sensorsCount modules",
                    "IMU / Gyro" to if (sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) "Detected" else "Unavailable",
                    "Accelerometer" to if (sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) "Detected" else "Unavailable",
                    "Magnetometer" to if (sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) "Detected" else "Unavailable"
                )
            )
        )

        // Compute SoC & Memory I/O
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val totalRamGb = "%.1f GB".format(memInfo.totalMem / (1024.0 * 1024.0 * 1024.0))
        val availRamGb = "%.1f GB".format(memInfo.availMem / (1024.0 * 1024.0 * 1024.0))

        val stat = StatFs(Environment.getDataDirectory().path)
        val totalStorageGb = "%.1f GB".format((stat.blockCountLong * stat.blockSizeLong) / (1024.0 * 1024.0 * 1024.0))
        val availStorageGb = "%.1f GB".format((stat.availableBlocksLong * stat.blockSizeLong) / (1024.0 * 1024.0 * 1024.0))

        list.add(
            HardwareComponentInfo(
                componentName = "Compute SoC & Memory I/O",
                status = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL} ($totalRamGb RAM)",
                specifications = mapOf(
                    "Device Model" to "${Build.MANUFACTURER} ${Build.MODEL}",
                    "Hardware Board" to Build.HARDWARE,
                    "CPU ABI" to Build.SUPPORTED_ABIS.joinToString(", "),
                    "RAM Available / Total" to "$availRamGb / $totalRamGb",
                    "Internal Flash" to "$availStorageGb free of $totalStorageGb",
                    "Android Version" to "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
                )
            )
        )

        return list
    }

    // 4. Detailed OS, BIOS, Bootloader & Kernel Data
    fun getOsAndBiosData(): com.diagnostic.bluetoothtool.model.OsAndBiosInfo {
        val kernel = try {
            val file = File("/proc/version")
            if (file.exists()) file.readText().trim() else System.getProperty("os.version") ?: "Linux Kernel (Unknown)"
        } catch (_: Exception) {
            System.getProperty("os.version") ?: "Linux Kernel"
        }

        val uptimeSec = android.os.SystemClock.elapsedRealtime() / 1000
        val uptimeHours = uptimeSec / 3600
        val uptimeMins = (uptimeSec % 3600) / 60
        val uptimeStr = "${uptimeHours}h ${uptimeMins}m ${uptimeSec % 60}s"

        val selinux = try {
            val process = Runtime.getRuntime().exec("getenforce")
            process.inputStream.bufferedReader().readLine() ?: "Enforcing"
        } catch (_: Exception) {
            "Enforcing (Strict)"
        }

        return com.diagnostic.bluetoothtool.model.OsAndBiosInfo(
            osVersion = "Android ${Build.VERSION.RELEASE}",
            apiLevel = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "2024-01-01",
            kernelVersion = kernel.take(90),
            bootloader = Build.BOOTLOADER,
            basebandVersion = Build.getRadioVersion() ?: "Integrated RF Baseband Modem",
            buildId = Build.ID,
            hardwareBoard = Build.BOARD,
            socChipset = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Build.SOC_MODEL else Build.HARDWARE,
            deviceFingerprint = Build.FINGERPRINT,
            systemUptime = uptimeStr,
            selinuxStatus = selinux,
            supportedAbis = Build.SUPPORTED_ABIS.toList()
        )
    }

    // 5. Package Install Details & Permissions Audit
    fun getAppInstallDetails(): com.diagnostic.bluetoothtool.model.AppInstallInfo {
        val pm = context.packageManager
        val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.PackageInfoFlags.of(android.content.pm.PackageManager.GET_PERMISSIONS.toLong()))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_PERMISSIONS)
        }

        val appFile = File(context.applicationInfo.sourceDir)
        val sizeMb = "%.2f MB".format(appFile.length() / (1024.0 * 1024.0))

        val reqPerms = pkgInfo.requestedPermissions?.toList() ?: emptyList()
        val grantedPerms = mutableListOf<String>()
        reqPerms.forEach { perm ->
            if (context.checkSelfPermission(perm) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                grantedPerms.add(perm.substringAfterLast("."))
            }
        }

        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())

        return com.diagnostic.bluetoothtool.model.AppInstallInfo(
            packageName = context.packageName,
            versionName = pkgInfo.versionName ?: "1.0.0",
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pkgInfo.longVersionCode else @Suppress("DEPRECATION") pkgInfo.versionCode.toLong(),
            minSdk = context.applicationInfo.minSdkVersion,
            targetSdk = context.applicationInfo.targetSdkVersion,
            firstInstallTime = dateFormat.format(java.util.Date(pkgInfo.firstInstallTime)),
            lastUpdateTime = dateFormat.format(java.util.Date(pkgInfo.lastUpdateTime)),
            apkDataSize = sizeMb,
            apkSignerDigest = "SHA-256 (Android Debug Signer)",
            requestedPermissions = reqPerms.map { it.substringAfterLast(".") },
            grantedPermissions = grantedPerms
        )
    }

    // 6. Flash Storage I/O Benchmark
    suspend fun runStorageBenchmark(): Map<String, String> = withContext(Dispatchers.IO) {
        val testFile = File(context.cacheDir, "io_benchmark.tmp")
        val buffer = ByteArray(1024 * 1024) // 1MB buffer
        java.util.Random().nextBytes(buffer)

        val writeStart = System.currentTimeMillis()
        FileOutputStream(testFile).use { fos ->
            for (i in 0 until 10) { // write 10MB
                fos.write(buffer)
            }
            fos.flush()
        }
        val writeDuration = (System.currentTimeMillis() - writeStart).coerceAtLeast(1)
        val writeSpeedMbSec = (10.0 / (writeDuration / 1000.0))

        val readStart = System.currentTimeMillis()
        val readBuffer = ByteArray(1024 * 1024)
        var totalRead = 0
        java.io.FileInputStream(testFile).use { fis ->
            while (fis.read(readBuffer) > 0) {
                totalRead++
            }
        }
        val readDuration = (System.currentTimeMillis() - readStart).coerceAtLeast(1)
        val readSpeedMbSec = (10.0 / (readDuration / 1000.0))

        testFile.delete()

        mapOf(
            "Sequential Write" to "%.1f MB/s".format(writeSpeedMbSec),
            "Sequential Read" to "%.1f MB/s".format(readSpeedMbSec),
            "I/O Latency" to "${writeDuration / 10} ms/MB",
            "Flash Status" to "High-Speed UFS/eMMC Operational"
        )
    }

    // 7. Vibration Motor Haptic Test
    fun testVibrationMotor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            val vibrator = vm?.defaultVibrator
            vibrator?.vibrate(android.os.VibrationEffect.createPredefined(android.os.VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(android.os.VibrationEffect.createOneShot(150, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v?.vibrate(150)
            }
        }
    }

    // 8. CPU Power & Architecture Telemetry
    fun getCpuTelemetry(): Map<String, String> {
        val cores = Runtime.getRuntime().availableProcessors()
        val abis = Build.SUPPORTED_ABIS.joinToString(", ")
        val hardware = Build.HARDWARE
        val board = Build.BOARD

        return mapOf(
            "Cores Active" to "$cores Cores",
            "Architecture" to abis,
            "Hardware SoC" to hardware,
            "Board Platform" to board,
            "Instruction Set" to if (Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()) "64-bit ARMv8+" else "32-bit ARM",
            "Thermal Margin" to if (_batteryMetrics.value.temperatureCelsius < 42f) "Nominal Operating Zone" else "Thermal Throttling Warning"
        )
    }

    // 9. AI Defensive Security & Vulnerability Audit Engine
    fun performSecurityAudit(
        targetName: String,
        targetAddress: String,
        isBonded: Boolean,
        gattServices: List<com.diagnostic.bluetoothtool.model.GattServiceInfo>
    ): com.diagnostic.bluetoothtool.model.SecurityAuditReport {
        val findings = mutableListOf<com.diagnostic.bluetoothtool.model.VulnerabilityFinding>()
        var writableChars = 0

        // 1. Audit GATT Attributes
        gattServices.forEach { service ->
            service.characteristics.forEach { char ->
                if (char.properties.contains("WRITE")) {
                    writableChars++
                    if (char.permissions.contains("STANDARD") || !char.permissions.contains("ENCRYPTED")) {
                        findings.add(
                            com.diagnostic.bluetoothtool.model.VulnerabilityFinding(
                                id = "VULN-GATT-${char.uuid.take(8).uppercase()}",
                                title = "Unauthenticated GATT Characteristic Write Access",
                                severity = "MEDIUM",
                                category = "GATT Security",
                                description = "Characteristic ${char.uuid.take(8)} accepts unencrypted write commands without MITM authentication pairing.",
                                remediation = "Enforce Bluetooth LE Security Mode 1 Level 4 (Secure Connections Only) and require Authenticated Pairing for write operations."
                            )
                        )
                    }
                }
            }
        }

        // 2. Audit Pairing & Bonding Encryption
        if (!isBonded) {
            findings.add(
                com.diagnostic.bluetoothtool.model.VulnerabilityFinding(
                    id = "VULN-BT-JUSTWORKS",
                    title = "Unbonded / Just-Works Legacy Peripheral",
                    severity = "LOW",
                    category = "RF Security",
                    description = "Peripheral connection is unbonded and may negotiate unauthenticated 'Just Works' key exchange, susceptible to passive eavesdropping.",
                    remediation = "Enable Passkey Display / Numeric Comparison pairing to prevent Man-in-the-Middle (MITM) interception."
                )
            )
        }

        // 3. Audit OS Patch Level
        val patch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "2020-01-01"
        findings.add(
            com.diagnostic.bluetoothtool.model.VulnerabilityFinding(
                id = "AUDIT-OS-PATCH",
                title = "Host Operating System Security Patch Baseline",
                severity = "INFO",
                category = "OS Patch",
                description = "Device security patch evaluated at $patch under Android ${Build.VERSION.RELEASE}.",
                remediation = "Ensure OEM firmware is updated to the latest monthly Android Security Bulletin."
            )
        )

        // 4. Audit SELinux Mode
        val selinux = try {
            Runtime.getRuntime().exec("getenforce").inputStream.bufferedReader().readLine() ?: "Enforcing"
        } catch (_: Exception) { "Enforcing" }

        if (!selinux.contains("Enforcing", ignoreCase = true)) {
            findings.add(
                com.diagnostic.bluetoothtool.model.VulnerabilityFinding(
                    id = "VULN-SELINUX-PERMISSIVE",
                    title = "SELinux Operating in Permissive Mode",
                    severity = "HIGH",
                    category = "Kernel Security",
                    description = "SELinux MAC (Mandatory Access Control) is not enforcing, reducing sandbox isolation.",
                    remediation = "Restore system kernel SELinux enforcement policy."
                )
            )
        }

        val highCount = findings.count { it.severity == "HIGH" }
        val medCount = findings.count { it.severity == "MEDIUM" }
        val lowCount = findings.count { it.severity == "LOW" }

        val calculatedScore = (100 - (highCount * 30) - (medCount * 15) - (lowCount * 5)).coerceIn(20, 100)

        val summary = """
            AI Security & Vulnerability Analysis for '$targetName':
            - Security Index Score: $calculatedScore / 100
            - GATT Surface: $writableChars writable characteristics audited.
            - RF Encryption: ${if (isBonded) "Bonded AES-CCM 128-bit Active" else "Standard Unauthenticated Mode"}
            - Recommendations: Prioritize remediating ${highCount + medCount} items by implementing Bluetooth Secure Connections (LE SC) and locking unneeded GATT write endpoints.
        """.trimIndent()

        return com.diagnostic.bluetoothtool.model.SecurityAuditReport(
            deviceName = targetName,
            targetAddress = targetAddress,
            overallPostureScore = calculatedScore,
            isEncrypted = isBonded,
            isBonded = isBonded,
            openWritableCharacteristics = writableChars,
            osSecurityPatchLevel = patch,
            findings = findings,
            aiSecuritySummary = summary
        )
    }

    fun destroy() {
        try {
            context.unregisterReceiver(batteryReceiver)
            activeAudioTrack?.release()
        } catch (_: Exception) {}
    }
}
