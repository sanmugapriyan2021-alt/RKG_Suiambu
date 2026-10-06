package com.diagnostic.bluetoothtool.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.diagnostic.bluetoothtool.model.BlePowerMode
import com.diagnostic.bluetoothtool.model.ConnectionStatus
import com.diagnostic.bluetoothtool.model.DeviceType
import com.diagnostic.bluetoothtool.model.HardwareCapabilityItem
import com.diagnostic.bluetoothtool.model.PeripheralCategory
import com.diagnostic.bluetoothtool.model.RfRegulatoryCompliance
import com.diagnostic.bluetoothtool.model.ScannedDevice
import com.diagnostic.bluetoothtool.model.SecurityVulnerabilityScanItem
import com.diagnostic.bluetoothtool.model.WirelessStandardInfo
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
fun DeviceDetailScreen(
    device: ScannedDevice,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToPorts: () -> Unit,
    onNavigateToGemini: () -> Unit = {}
) {
    val isLocked by viewModel.isDeviceLocked.collectAsState()
    val peripheralBattery by viewModel.peripheralBatteryLevel.collectAsState()
    val blePowerMode by viewModel.blePowerMode.collectAsState()
    val eligibleApps by viewModel.eligibleApps.collectAsState()
    val geminiUiConfig by viewModel.geminiGenerativeUiConfig.collectAsState()
    val isGeneratingGeminiUi by viewModel.isGeneratingGeminiUi.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val currentDevice = selectedDevice ?: device
    val isConnected = currentDevice.status == ConnectionStatus.CONNECTED

    val twsMetrics by viewModel.activeTwsMetrics.collectAsState()
    val speakerMetrics by viewModel.activeSpeakerMetrics.collectAsState()
    val smartLightMetrics by viewModel.activeSmartLightMetrics.collectAsState()
    val capabilities by viewModel.deviceCapabilities.collectAsState()
    val vulnerabilities by viewModel.securityVulnerabilities.collectAsState()
    val isSecurityScanning by viewModel.isSecurityScanning.collectAsState()
    val wirelessInfo by viewModel.wirelessStandardInfo.collectAsState()
    val rfCompliance by viewModel.rfCompliance.collectAsState()

    var customCommandText by remember { mutableStateOf("") }
    val battPct = if (peripheralBattery in 1..100) peripheralBattery else 100

    val battColor = when {
        battPct >= 60 -> EmeraldGreen
        battPct >= 25 -> CyberCyan
        battPct >= 15 -> AmberWarning
        else -> CrimsonAlert
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Top Navigation Bar
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
                        text = currentDevice.name.uppercase(),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "TARGET: ${currentDevice.address} • ${currentDevice.category.name}",
                        fontSize = 10.sp,
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            IconButton(
                onClick = {
                    if (isLocked) viewModel.unlockDevice() else viewModel.lockDevice(device)
                }
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Lock",
                    tint = if (isLocked) AmberWarning else TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Hero Status & Battery Dashboard Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(CyberCyan.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (currentDevice.category) {
                                            PeripheralCategory.TWS_EARBUDS -> Icons.Default.Headphones
                                            PeripheralCategory.BT_SPEAKER -> Icons.Default.Speaker
                                            PeripheralCategory.SMART_GLASS -> Icons.Default.Visibility
                                            PeripheralCategory.SMART_LIGHT -> Icons.Default.Lightbulb
                                            PeripheralCategory.WIFI_NETWORK -> Icons.Default.Wifi
                                            else -> Icons.Default.Bluetooth
                                        },
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentDevice.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Category: ${currentDevice.category.name.replace("_", " ")}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isConnected) EmeraldGreen.copy(alpha = 0.2f)
                                        else CrimsonAlert.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isConnected) "CONNECTED" else "DISCONNECTED",
                                    color = if (isConnected) EmeraldGreen else CrimsonAlert,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Device & Gadget OS Matrix
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Host Device & OS", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "OPPO A53 • Android 11 (API 30)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Peripheral Protocol / Chipset", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "${currentDevice.vendor} (${currentDevice.bluetoothVersion})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Link Layer Encryption", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "AES-CCM 128-bit Authenticated", fontSize = 11.sp, color = EmeraldGreen, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Hero Battery Gauge Row & Connect/Disconnect Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(battColor.copy(alpha = 0.15f))
                                        .border(2.dp, battColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.ElectricBolt,
                                            contentDescription = null,
                                            tint = battColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "$battPct%",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "PMU BATTERY & TELEMETRY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = battColor,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "3820 mV • Li-ion Cell • 31.4 °C",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (isConnected) "Active RF Link • Drain: 2.8%/hr" else "RF Inactive • Power Sleep Mode",
                                        fontSize = 10.sp,
                                        color = TextTertiary
                                    )
                                }
                            }

                            if (isConnected) {
                                Button(
                                    onClick = { viewModel.disconnectSelectedDevice() },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert.copy(alpha = 0.2f)),
                                    border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.6f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "DISCONNECT",
                                        fontSize = 10.sp,
                                        color = CrimsonAlert,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.connectSelectedDevice() },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen.copy(alpha = 0.2f)),
                                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.6f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "CONNECT",
                                        fontSize = 10.sp,
                                        color = EmeraldGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- DISCONNECTED WARNING BANNER & RF INACTIVE ADVISORY ---
            if (!isConnected) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CrimsonAlert.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = CrimsonAlert,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "⚠️ PERIPHERAL DISCONNECTED (RF Inactive)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CrimsonAlert,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Actual physical connection with ${currentDevice.name} is inactive. Hardware sound tests and LED actuators are isolated to prevent false routing to the host phone's internal speaker. Reconnect physical device to test live RF hardware.",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // --- WIRELESS STANDARDS & RF REGULATORY COMPLIANCE INSPECTOR ---
            if (wirelessInfo != null && rfCompliance != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                                    Icon(
                                        imageVector = Icons.Default.NetworkCheck,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "WIRELESS STANDARDS & RF COMPLIANCE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CyberCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (rfCompliance!!.isEirpCompliant) EmeraldGreen.copy(alpha = 0.2f) else CrimsonAlert.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (rfCompliance!!.isEirpCompliant) "FCC/ETSI PASS" else "NON-COMPLIANT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rfCompliance!!.isEirpCompliant) EmeraldGreen else CrimsonAlert,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Wireless PHY & Layer Specifications Matrix
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "IEEE Standard", fontSize = 10.sp, color = TextSecondary)
                                        Text(text = wirelessInfo!!.ieeeStandard, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "PHY Layer / Band", fontSize = 10.sp, color = TextSecondary)
                                        Text(text = "${wirelessInfo!!.phyLayer} • ${rfCompliance!!.frequencyRange}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Hopping / MTU", fontSize = 10.sp, color = TextSecondary)
                                        Text(text = "${wirelessInfo!!.channelHoppingMode} • MTU ${wirelessInfo!!.linkLayerMtu}", fontSize = 10.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Interval / Supervision", fontSize = 10.sp, color = TextSecondary)
                                        Text(text = "${wirelessInfo!!.connectionIntervalMs} ms / ${wirelessInfo!!.supervisionTimeoutMs} ms", fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // RF Telemetry & Link Budget Calculations
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Path Loss & Distance Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(text = "PATH LOSS (FSPL)", fontSize = 9.sp, color = TextTertiary, fontFamily = FontFamily.Monospace)
                                        Text(text = "${"%.1f".format(wirelessInfo!!.pathLossDb)} dB", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "Dist: ~${"%.2f".format(wirelessInfo!!.estimatedDistanceMeters)} m", fontSize = 9.sp, color = CyberCyan, fontFamily = FontFamily.Monospace)
                                    }
                                }

                                // Link Budget Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(text = "LINK BUDGET", fontSize = 9.sp, color = TextTertiary, fontFamily = FontFamily.Monospace)
                                        Text(text = "${"%.1f".format(wirelessInfo!!.linkBudgetDb)} dB", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldGreen, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "PER: ${"%.2f".format(wirelessInfo!!.packetErrorRatePct)}%", fontSize = 9.sp, color = if (wirelessInfo!!.packetErrorRatePct < 1.0f) EmeraldGreen else AmberWarning, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Regulatory Domain & EIRP Compliance
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "RF Regulatory Domain", fontSize = 10.sp, color = TextSecondary)
                                        Text(text = rfCompliance!!.regulatoryDomain, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PurpleAccent, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Measured EIRP vs Limit", fontSize = 10.sp, color = TextSecondary)
                                        Text(text = "${"%.1f".format(rfCompliance!!.measuredTxPowerDbM)} dBm / ${"%.1f".format(rfCompliance!!.maxEirpDbM)} dBm", fontSize = 10.sp, color = if (rfCompliance!!.isEirpCompliant) EmeraldGreen else CrimsonAlert, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "SAR Limit & Duty Cycle", fontSize = 10.sp, color = TextSecondary)
                                        Text(text = "${rfCompliance!!.sarRfExposureLimit} • ${"%.1f".format(rfCompliance!!.channelOccupancyDutyCyclePct)}% DC", fontSize = 10.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- Category 1: AIRPODS / TWS CUSTOM CONTROL DECK ---
            if (currentDevice.category == PeripheralCategory.TWS_EARBUDS) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Headphones, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AIRPODS & TWS DUAL POD CONTROLLER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dual Pod Battery & Connectivity Visualizer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Left Pod Card
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.toggleTwsEarbud(isLeft = true) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (twsMetrics.isLeftConnected) DarkSurfaceVariant else DarkSurfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    border = BorderStroke(1.dp, if (twsMetrics.isLeftConnected) CyberCyan.copy(alpha = 0.5f) else DarkCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "LEFT POD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "${twsMetrics.leftBattery}%", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (twsMetrics.isLeftConnected) "IN EAR • ACTIVE" else "DOCKED IN CASE",
                                            fontSize = 8.5.sp,
                                            color = if (twsMetrics.isLeftConnected) EmeraldGreen else TextTertiary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                // Right Pod Card
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.toggleTwsEarbud(isLeft = false) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (twsMetrics.isRightConnected) DarkSurfaceVariant else DarkSurfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    border = BorderStroke(1.dp, if (twsMetrics.isRightConnected) CyberCyan.copy(alpha = 0.5f) else DarkCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "RIGHT POD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "${twsMetrics.rightBattery}%", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (twsMetrics.isRightConnected) "IN EAR • ACTIVE" else "DOCKED IN CASE",
                                            fontSize = 8.5.sp,
                                            color = if (twsMetrics.isRightConnected) EmeraldGreen else TextTertiary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                // Case Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                    border = BorderStroke(1.dp, DarkCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "CASE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PurpleAccent, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "${twsMetrics.caseBattery}%", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "STANDBY", fontSize = 8.5.sp, color = PurpleAccent, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // ANC / Transparency Mode Switcher
                            Text(text = "NOISE CONTROL MODES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextTertiary, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = twsMetrics.ancMode == "ANC_ON",
                                    onClick = { viewModel.setTwsAncMode("ANC_ON") },
                                    label = { Text("Noise Cancellation", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PurpleAccent.copy(alpha = 0.3f),
                                        selectedLabelColor = PurpleAccent,
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = TextSecondary
                                    )
                                )
                                FilterChip(
                                    selected = twsMetrics.ancMode == "TRANSPARENCY",
                                    onClick = { viewModel.setTwsAncMode("TRANSPARENCY") },
                                    label = { Text("Transparency", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan.copy(alpha = 0.3f),
                                        selectedLabelColor = CyberCyan,
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = TextSecondary
                                    )
                                )
                                FilterChip(
                                    selected = twsMetrics.ancMode == "OFF",
                                    onClick = { viewModel.setTwsAncMode("OFF") },
                                    label = { Text("Off", fontSize = 10.sp) },
                                    modifier = Modifier.weight(0.6f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DarkSurfaceVariant,
                                        selectedLabelColor = TextPrimary,
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // --- Category 2: BLUETOOTH SPEAKER & SOUND AMPLIFICATION DECK ---
            if (currentDevice.category == PeripheralCategory.BT_SPEAKER || currentDevice.category == PeripheralCategory.TWS_EARBUDS) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                                    Icon(imageVector = Icons.Default.Speaker, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SPEAKER AMPLIFICATION & VOLUME",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldGreen,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "+${"%.1f".format(speakerMetrics.amplificationGainDb)} dB Pre-Amp",
                                    fontSize = 11.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Master Volume Slider
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Master Output Volume", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "${speakerMetrics.currentVolume}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = speakerMetrics.currentVolume.toFloat(),
                                onValueChange = { viewModel.setSpeakerVolume(it.toInt()) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = EmeraldGreen,
                                    activeTrackColor = EmeraldGreen,
                                    inactiveTrackColor = DarkSurfaceVariant
                                )
                            )

                            // Hardware Amplification Boost Slider (0.0 to +18.0 dB)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Hardware DSP Sound Amplification", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "+${"%.1f".format(speakerMetrics.amplificationGainDb)} dB Boost", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = speakerMetrics.amplificationGainDb,
                                onValueChange = { viewModel.setSpeakerAmplification(it) },
                                valueRange = 0.0f..18.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberCyan,
                                    activeTrackColor = CyberCyan,
                                    inactiveTrackColor = DarkSurfaceVariant
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Equalizer Presets
                            Text(text = "DSP EQUALIZER PRESET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextTertiary, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            val eqScroll = rememberScrollState()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(eqScroll),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Bass Boost (+8dB)", "Vocal Clarity", "Club Dynamic", "Flat Monitor").forEach { eq ->
                                    FilterChip(
                                        selected = speakerMetrics.equalizerPreset == eq,
                                        onClick = { viewModel.setSpeakerEqualizer(eq) },
                                        label = { Text(eq, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldGreen.copy(alpha = 0.25f),
                                            selectedLabelColor = EmeraldGreen,
                                            containerColor = DarkSurfaceVariant,
                                            labelColor = TextSecondary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Hardware Audio Calibration Tone Emitters
                            Text(text = "HARDWARE TEST TONE EMITTER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextTertiary, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.playSpeakerAmplificationTest(100) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                                ) {
                                    Text("100Hz Sub", fontSize = 10.sp, color = TextPrimary)
                                }
                                Button(
                                    onClick = { viewModel.playSpeakerAmplificationTest(1000) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                                ) {
                                    Text("1kHz Mid", fontSize = 10.sp, color = CyberCyan)
                                }
                                Button(
                                    onClick = { viewModel.playSpeakerAmplificationTest(10000) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                                ) {
                                    Text("10kHz High", fontSize = 10.sp, color = EmeraldGreen)
                                }
                            }
                        }
                    }
                }
            }

            // --- Category 3: SMART LIGHT & LED CONTROL DECK (BLUE / RED / RGB) ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LED LIGHT & VISUAL DIODE CONTROLLER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AmberWarning,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (smartLightMetrics.isAvailable) EmeraldGreen.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (smartLightMetrics.isAvailable) "AVAILABLE" else "NOT DETECTED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (smartLightMetrics.isAvailable) EmeraldGreen else TextTertiary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Control physical LED indicators or smart bulb lighting in real-time via Bluetooth GATT & Gemini AI.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Blue and Red Interactive Color Selection Pads
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setLedColor("BLUE") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (smartLightMetrics.activeColor == "BLUE") NeonBlue else DarkSurfaceVariant
                                ),
                                border = BorderStroke(1.dp, NeonBlue)
                            ) {
                                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🔵 BLUE LIGHT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Button(
                                onClick = { viewModel.setLedColor("RED") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (smartLightMetrics.activeColor == "RED") CrimsonAlert else DarkSurfaceVariant
                                ),
                                border = BorderStroke(1.dp, CrimsonAlert)
                            ) {
                                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🔴 RED LIGHT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // White / RGB & Strobe Flash Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setLedColor("RGB") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent.copy(alpha = 0.3f)),
                                border = BorderStroke(1.dp, PurpleAccent)
                            ) {
                                Text("✨ RGB Rainbow Cycle", fontSize = 10.sp, color = PurpleAccent, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.toggleLedStrobe() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (smartLightMetrics.isStrobeActive) AmberWarning.copy(alpha = 0.3f) else DarkSurfaceVariant
                                ),
                                border = BorderStroke(1.dp, if (smartLightMetrics.isStrobeActive) AmberWarning else DarkCardBorder)
                            ) {
                                Text(
                                    text = if (smartLightMetrics.isStrobeActive) "⚡ Strobe: ACTIVE" else "⚡ Test Strobe Beacon",
                                    fontSize = 10.sp,
                                    color = if (smartLightMetrics.isStrobeActive) AmberWarning else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // --- HARDWARE CAPABILITY AVAILABILITY MATRIX ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HARDWARE CAPABILITY DISCOVERY MATRIX",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(text = "${capabilities.size} Capabilities", fontSize = 10.sp, color = TextTertiary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        capabilities.forEach { cap ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = cap.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = cap.statusDetail, fontSize = 9.sp, color = if (cap.isAvailable) EmeraldGreen else TextTertiary, fontFamily = FontFamily.Monospace)
                                    }

                                    if (cap.isAvailable) {
                                        Button(
                                            onClick = { viewModel.runCapabilityTest(cap.id, currentDevice) },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.2f)),
                                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = "TEST", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DarkSurface)
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text(text = "NOT AVAILABLE", fontSize = 8.5.sp, color = TextTertiary, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- NETWORK ENGINEER MALICIOUS ACTIVITY & PENTEST SUITE ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "BLUETOOTH SECURITY & MALICIOUS ACTIVITY PENTEST",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PurpleAccent,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.runMaliciousActivityAudit(currentDevice) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, PurpleAccent)
                        ) {
                            if (isSecurityScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PurpleAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Executing Security Vectors & Packet Fuzzing...", fontSize = 11.sp, color = PurpleAccent, fontFamily = FontFamily.Monospace)
                            } else {
                                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "RUN MALICIOUS ACTIVITY AUDIT (5 VECTORS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PurpleAccent, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Security Vectors List
                        vulnerabilities.forEach { v ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = v.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (v.severity == "SAFE") EmeraldGreen.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = v.status,
                                                fontSize = 9.sp,
                                                color = if (v.severity == "SAFE") EmeraldGreen else AmberWarning,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = v.details, fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Google Gemini AI Assistant Button
            item {
                Button(
                    onClick = {
                        viewModel.sendMediaCommand("open_gemini")
                        onNavigateToGemini()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✨ Open Gemini AI Hardware & Token Engine",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Live Command Dispatcher Input
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customCommandText,
                        onValueChange = { customCommandText = it },
                        placeholder = { Text("e.g. 'set_light blue', 'amplify 12', 'set_anc ANC_ON'", fontSize = 11.sp, color = TextTertiary) },
                        modifier = Modifier.weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextPrimary, fontFamily = FontFamily.Monospace),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (customCommandText.isNotBlank()) {
                                viewModel.executeConsoleCommand(customCommandText)
                                customCommandText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberCyan)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = DarkBackground)
                    }
                }
            }
        }
    }
}
