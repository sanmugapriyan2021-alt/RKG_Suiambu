package com.diagnostic.bluetoothtool.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.diagnostic.bluetoothtool.model.ConnectionStatus
import com.diagnostic.bluetoothtool.model.DeviceType
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HardwareTestScreen(viewModel: MainViewModel) {
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val isPlayingSpeaker by viewModel.isPlayingSpeaker.collectAsState()
    val isRecordingMic by viewModel.isRecordingMic.collectAsState()
    val micDb by viewModel.micDecibels.collectAsState()
    val isPreTestRunning by viewModel.isPreTestRunning.collectAsState()
    val dnsInfo by viewModel.deviceDnsInfo.collectAsState()
    val isDnsTesting by viewModel.isDnsTesting.collectAsState()
    val authInfo by viewModel.peripheralAuthInfo.collectAsState()

    var selectedFreq by remember { mutableIntStateOf(1000) }
    var customDomainQuery by remember { mutableStateOf("gemini.google.com") }

    // Fallback device target (e.g. Fire-Lens Smartglass) if none is explicitly selected
    val activeGadget = selectedDevice ?: ScannedDevice(
        id = "DEFAULT_GADGET",
        name = "Fire-Lens Smartglass",
        address = "00:1A:7D:22:33:44",
        rssi = -62,
        type = DeviceType.BLUETOOTH_CLASSIC,
        status = ConnectionStatus.CONNECTED,
        deviceClass = "Wearable Headset",
        vendor = "Qualcomm Technologies",
        isBonded = true,
        signalLevel = 4
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GADGET HARDWARE & DNS",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Diagnostic & DNS controls for ${activeGadget.name}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { viewModel.runPreTest(activeGadget) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(10.dp),
                enabled = !isPreTestRunning
            ) {
                if (isPreTestRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = TextPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Pre-Test All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. ACTIVE TARGET GADGET STATUS CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (activeGadget.type == DeviceType.WIFI_AP) Icons.Default.Wifi else Icons.Default.Bluetooth,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = activeGadget.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${activeGadget.address} • ${activeGadget.deviceClass}",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "CONNECTED",
                                    color = EmeraldGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow("Vendor Chipset", activeGadget.vendor)
                        DetailRow("RF Link Type", activeGadget.type.name)
                        DetailRow("Signal Strength", "${activeGadget.rssi} dBm (Level ${activeGadget.signalLevel}/4)")
                        DetailRow("Bonding State", if (activeGadget.isBonded) "BONDED & AUTHENTICATED" else "STANDARD PAIRING")
                    }
                }
            }

            // 2. GADGET DNS & NETWORK ROUTING CONTROLLER CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CyberCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Dns,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gadget DNS & IP Routing",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = dnsInfo.activeDnsProvider,
                                        fontSize = 11.sp,
                                        color = CyberCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PurpleAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${dnsInfo.queryLatencyMs}ms",
                                    color = PurpleAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow("Primary DNS Server", dnsInfo.primaryDns)
                        DetailRow("Secondary DNS Server", dnsInfo.secondaryDns)
                        DetailRow("DNS Security Standard", dnsInfo.dnsSecurityStatus)
                        DetailRow("Domain Query Result", "${dnsInfo.resolvedDomain} -> ${dnsInfo.resolvedIp}")

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "SELECT DNS PROVIDER PRESET:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple("Cloudflare DoT", "1.1.1.1", "1.0.0.1"),
                                Triple("Google Public", "8.8.8.8", "8.8.4.4"),
                                Triple("AdGuard Privacy", "94.140.14.14", "94.140.15.15"),
                                Triple("Quad9 Threat", "9.9.9.9", "149.112.112.112")
                            ).forEach { (label, prim, sec) ->
                                val isSelected = dnsInfo.primaryDns == prim
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setCustomDns(prim, sec) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                                        selectedLabelColor = CyberCyan,
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Test Domain Resolution Latency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customDomainQuery,
                                onValueChange = { customDomainQuery = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Test Domain Resolution", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberCyan,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.testDnsResolution(customDomainQuery) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isDnsTesting
                            ) {
                                if (isDnsTesting) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = DarkBackground, strokeWidth = 2.dp)
                                } else {
                                    Text("Test DNS", color = DarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. GADGET SPEAKER / ACOUSTIC TRANSDUCER TEST
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NeonBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = NeonBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gadget Acoustic Transducer",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Dispatches test tones to ${activeGadget.name}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Frequency Selection
                        Text(text = "SELECT FREQUENCY:", fontSize = 10.sp, color = TextTertiary, fontFamily = FontFamily.Monospace)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(440 to "440Hz", 1000 to "1kHz", 2500 to "2.5kHz", 5000 to "5kHz").forEach { (f, label) ->
                                FilterChip(
                                    selected = selectedFreq == f,
                                    onClick = { selectedFreq = f },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonBlue.copy(alpha = 0.25f),
                                        selectedLabelColor = NeonBlue,
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.playSpeakerTest(selectedFreq) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingSpeaker) EmeraldGreen else NeonBlue
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isPlayingSpeaker
                        ) {
                            Icon(
                                imageVector = if (isPlayingSpeaker) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlayingSpeaker) "Emitting Tone (${selectedFreq}Hz)..." else "Play Transducer Test Tone",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // 4. GADGET MICROPHONE ARRAY & NOISE FLOOR TEST
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CyberCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gadget Mic Array & Sampling",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Samples peripheral voice channel & SPL dB",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Decibel Meter
                        val animatedDb by animateFloatAsState(targetValue = (micDb / 90f).coerceIn(0f, 1f), label = "db")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "LIVE NOISE FLOOR LEVEL:", fontSize = 10.sp, color = TextTertiary, fontFamily = FontFamily.Monospace)
                            Text(
                                text = "%.1f dB SPL".format(micDb),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { animatedDb },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyberCyan,
                            trackColor = DarkSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.recordMicTest(3) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecordingMic) PurpleAccent else CyberCyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isRecordingMic
                        ) {
                            Icon(
                                imageVector = if (isRecordingMic) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRecordingMic) "Sampling Gadget Audio (3s)..." else "Sample & Record Mic Audio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkBackground
                            )
                        }
                    }
                }
            }

            // 5. GADGET PROFILE & GATT SECURITY INTEGRITY
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PurpleAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = PurpleAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Peripheral RF & GATT Security",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Link-layer authentication and profile isolation",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow("Pairing Architecture", authInfo?.pairingMode ?: "Secure Simple Pairing (Numeric Comparison)")
                        DetailRow("Link Encryption", authInfo?.encryptionStandard ?: "AES-CCM 128-bit Link Layer")
                        DetailRow("Key Size", authInfo?.keySize ?: "128-bit E0 / AES")
                        DetailRow("MITM Protection", authInfo?.mitmProtection ?: "Enforced / Authenticated")

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "SUPPORTED GADGET PROFILES:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val profiles = authInfo?.supportedProfiles ?: listOf(
                                "A2DP Sink (Audio)",
                                "HFP 1.7 (Voice)",
                                "AVRCP 1.6 (Controls)",
                                "GATT Battery (0x180F)"
                            )
                            profiles.forEach { prof ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DarkSurfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = prof, fontSize = 10.sp, color = CyberCyan, fontFamily = FontFamily.Monospace)
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextTertiary)
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextSecondary,
            fontFamily = FontFamily.Monospace
        )
    }
}
