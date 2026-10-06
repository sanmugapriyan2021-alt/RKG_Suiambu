package com.diagnostic.bluetoothtool.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diagnostic.bluetoothtool.model.DevicePortInfo
import com.diagnostic.bluetoothtool.model.ScannedDevice
import com.diagnostic.bluetoothtool.ui.theme.AmberWarning
import com.diagnostic.bluetoothtool.ui.theme.CrimsonAlert
import com.diagnostic.bluetoothtool.ui.theme.CyberCyan
import com.diagnostic.bluetoothtool.ui.theme.DarkBackground
import com.diagnostic.bluetoothtool.ui.theme.DarkCardBorder
import com.diagnostic.bluetoothtool.ui.theme.DarkSurface
import com.diagnostic.bluetoothtool.ui.theme.DarkSurfaceVariant
import com.diagnostic.bluetoothtool.ui.theme.EmeraldGreen
import com.diagnostic.bluetoothtool.ui.theme.NeonBlue
import com.diagnostic.bluetoothtool.ui.theme.PurpleAccent
import com.diagnostic.bluetoothtool.ui.theme.TextPrimary
import com.diagnostic.bluetoothtool.ui.theme.TextSecondary
import com.diagnostic.bluetoothtool.ui.theme.TextTertiary
import com.diagnostic.bluetoothtool.viewmodel.MainViewModel

@Composable
fun DevicePortConfigScreen(
    device: ScannedDevice,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var micGain by remember { mutableFloatStateOf(12.0f) }
    var hudBrightness by remember { mutableFloatStateOf(80f) }
    var isNoiseCancelEnabled by remember { mutableStateOf(true) }
    var isHudOverlayActive by remember { mutableStateOf(true) }

    val ports = remember {
        listOf(
            DevicePortInfo("P1", "Dual MEMS Microphones", "AUDIO_MIC", "STREAMING ACTIVE", "24-bit 48kHz HD Audio", true, true, "RECORD_AUDIO", "I2S Ch 0/1"),
            DevicePortInfo("P2", "Micro-OLED Waveguide HUD", "DISPLAY_HUD", "ONLINE (60Hz)", "640x400 Monochromatic AR", true, true, "SYSTEM_ALERT_WINDOW", "MIPI-DSI Bus"),
            DevicePortInfo("P3", "Qualcomm High-Speed UART", "SERIAL_PORT", "READY / UNLOCKED", "921600 baud, CTS/RTS Flow", true, true, "BLUETOOTH_PRIVILEGED", "TTY_BT0 Port"),
            DevicePortInfo("P4", "RFCOMM Serial Port Profile", "SERIAL_PORT", "CONNECTED", "SPP Channel 1 (Encrypted)", true, true, "BLUETOOTH_CONNECT", "UUID 0x1101"),
            DevicePortInfo("P5", "GATT Battery Service", "BLE_GATT", "NOTIFY ACTIVE", "0x180F (Char 0x2A19)", true, false, "BLUETOOTH_CONNECT", "Handle 0x0014"),
            DevicePortInfo("P6", "Google Gemini AI Hotword Port", "BLE_GATT", "LISTENING", "UUID 0xFEF5 / Voice Trigger", true, true, "RECORD_AUDIO", "Handle 0x002A"),
            DevicePortInfo("P7", "Capacitive Touch & 6-Axis IMU", "INPUT_BUTTON", "READY", "HID Touchpad + Gyro (200Hz)", true, true, "BLUETOOTH_CONNECT", "I2C Sensor Bus")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "PORT & I/O OPERATORS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hardware interface & permissions for ${device.name}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(EmeraldGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "SYNCED",
                    color = EmeraldGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Hardware Specs & Identification Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Router, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "DEVICE IDENTIFIERS & PROTOCOLS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonBlue, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Bluetooth Core", fontSize = 12.sp, color = TextSecondary)
                            Text(text = device.bluetoothVersion, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "MAC Address", fontSize = 12.sp, color = TextSecondary)
                            Text(text = device.address, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Assigned IP Address", fontSize = 12.sp, color = TextSecondary)
                            Text(text = device.ipAddress ?: "192.168.1.142 (DoT Link)", fontSize = 12.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Encryption Standard", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "AES-CCM 128-bit Link Layer", fontSize = 12.sp, color = EmeraldGreen, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Interactive Microphone Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "MIC INPUT OPERATOR (I2S)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PurpleAccent, fontFamily = FontFamily.Monospace)
                            }
                            Text(text = "${"%.1f".format(micGain)} dB Gain", fontSize = 11.sp, color = PurpleAccent, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = micGain,
                            onValueChange = { micGain = it },
                            valueRange = 0f..24f,
                            colors = SliderDefaults.colors(
                                thumbColor = PurpleAccent,
                                activeTrackColor = PurpleAccent,
                                inactiveTrackColor = DarkSurfaceVariant
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Active Beamforming & Noise Reduction", fontSize = 12.sp, color = TextPrimary)
                            Switch(
                                checked = isNoiseCancelEnabled,
                                onCheckedChange = { isNoiseCancelEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldGreen,
                                    checkedTrackColor = EmeraldGreen.copy(alpha = 0.3f),
                                    uncheckedThumbColor = TextTertiary,
                                    uncheckedTrackColor = DarkSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Interactive Display HUD Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.DisplaySettings, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "DISPLAY HUD (WAVEGUIDE AR)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                            }
                            Text(text = "${hudBrightness.toInt()}%", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = hudBrightness,
                            onValueChange = { hudBrightness = it },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = CyberCyan,
                                activeTrackColor = CyberCyan,
                                inactiveTrackColor = DarkSurfaceVariant
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Real-Time Telemetry AR Overlay", fontSize = 12.sp, color = TextPrimary)
                            Switch(
                                checked = isHudOverlayActive,
                                onCheckedChange = { isHudOverlayActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberCyan,
                                    checkedTrackColor = CyberCyan.copy(alpha = 0.3f),
                                    uncheckedThumbColor = TextTertiary,
                                    uncheckedTrackColor = DarkSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Port Matrix Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SYNCHRONIZED PORTS & I/O OPERATORS (${ports.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextTertiary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // List of Synchronized Ports
            items(ports) { port ->
                var isPortEnabled by remember { mutableStateOf(port.isAuthorized) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, if (isPortEnabled) DarkCardBorder else CrimsonAlert.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (port.portType) {
                                                "AUDIO_MIC" -> PurpleAccent.copy(alpha = 0.2f)
                                                "DISPLAY_HUD" -> CyberCyan.copy(alpha = 0.2f)
                                                "SERIAL_PORT" -> NeonBlue.copy(alpha = 0.2f)
                                                "BLE_GATT" -> EmeraldGreen.copy(alpha = 0.2f)
                                                else -> AmberWarning.copy(alpha = 0.2f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SettingsInputComponent,
                                        contentDescription = null,
                                        tint = when (port.portType) {
                                            "AUDIO_MIC" -> PurpleAccent
                                            "DISPLAY_HUD" -> CyberCyan
                                            "SERIAL_PORT" -> NeonBlue
                                            "BLE_GATT" -> EmeraldGreen
                                            else -> AmberWarning
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = port.portName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = "${port.channelOrAddress} • ${port.version}", fontSize = 10.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                                }
                            }

                            Switch(
                                checked = isPortEnabled,
                                onCheckedChange = { isPortEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldGreen,
                                    checkedTrackColor = EmeraldGreen.copy(alpha = 0.3f),
                                    uncheckedThumbColor = CrimsonAlert,
                                    uncheckedTrackColor = DarkSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isPortEnabled) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isPortEnabled) EmeraldGreen else CrimsonAlert,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPortEnabled) port.status else "BLOCKED / REVOKED",
                                    fontSize = 10.sp,
                                    color = if (isPortEnabled) EmeraldGreen else CrimsonAlert,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "Perm: ${port.permission}",
                                fontSize = 9.sp,
                                color = TextTertiary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Save & Apply Changes Button
            item {
                Button(
                    onClick = {
                        viewModel.addCommandLog("port_sync", "Applied port configurations: Mic Gain ${micGain.toInt()}dB, HUD Brightness ${hudBrightness.toInt()}%, Synchronized 7 I/O channels.")
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text(
                        text = "SYNCHRONIZE & APPLY TO GADGET",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkBackground,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
