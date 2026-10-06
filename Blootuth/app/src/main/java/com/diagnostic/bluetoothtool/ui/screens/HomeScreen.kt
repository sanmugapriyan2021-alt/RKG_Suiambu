package com.diagnostic.bluetoothtool.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.diagnostic.bluetoothtool.viewmodel.ScanFilterTab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.diagnostic.bluetoothtool.model.PeripheralCategory

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToDeviceDetail: (ScannedDevice) -> Unit,
    onNavigateToGemini: () -> Unit = {}
) {
    val devices by viewModel.displayedDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val peripheralBattery by viewModel.peripheralBatteryLevel.collectAsState()
    val audioState by viewModel.classroomAudioState.collectAsState()

    val activeGadget = selectedDevice ?: ScannedDevice(
        id = "DEFAULT_GADGET",
        name = "Fire-Lens Smartglass",
        address = "41:42:FF:B0:2B:29",
        rssi = -62,
        type = DeviceType.BLUETOOTH_CLASSIC,
        category = PeripheralCategory.SMART_GLASS,
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
        // Top Header with Project Name "ScanWeb" and Top-Right Scan Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SCANWEB",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Bluetooth Malicious Activity & RF Diagnostic Suite",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Top Right Scan Button
            Button(
                onClick = {
                    if (isScanning) viewModel.stopScanning() else viewModel.startScanning()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning) CrimsonAlert else CyberCyan
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STOPPING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SCAN RF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkBackground,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Linked Gadget Hero Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToDeviceDetail(activeGadget) },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
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
                            imageVector = when (activeGadget.category) {
                                PeripheralCategory.TWS_EARBUDS -> Icons.Default.Headphones
                                PeripheralCategory.BT_SPEAKER -> Icons.Default.Speaker
                                PeripheralCategory.SMART_GLASS -> Icons.Default.Visibility
                                PeripheralCategory.SMART_LIGHT -> Icons.Default.Lightbulb
                                PeripheralCategory.WIFI_NETWORK -> Icons.Default.Wifi
                                else -> Icons.Default.Bluetooth
                            },
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ACTIVE TARGET: ",
                                fontSize = 10.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = activeGadget.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${activeGadget.address} • ${activeGadget.category.name}",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${if (peripheralBattery > 0) peripheralBattery else 100}% PMU",
                            color = EmeraldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Direct Gemini AI Classroom & Network Engineer Assistant Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToGemini() },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PurpleAccent.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PurpleAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GEMINI AI HARDWARE & SECURITY HUB",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = PurpleAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = if (audioState.isPlaying) "🔊 Streaming audio to device..." else "Multi-device test cases, blue/red light & token telemetry",
                            fontSize = 10.sp,
                            color = if (audioState.isPlaying) EmeraldGreen else TextSecondary
                        )
                    }
                }

                Text(
                    text = "OPEN ->",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleAccent,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Multi-Peripheral Category Filter Switcher (Scrollable Row)
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == ScanFilterTab.ALL,
                onClick = { viewModel.setFilterTab(ScanFilterTab.ALL) },
                label = { Text("All (${devices.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DarkSurfaceVariant,
                    selectedLabelColor = TextPrimary,
                    containerColor = DarkSurface,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedTab == ScanFilterTab.BLUETOOTH,
                onClick = { viewModel.setFilterTab(ScanFilterTab.BLUETOOTH) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Bluetooth,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (selectedTab == ScanFilterTab.BLUETOOTH) CyberCyan else TextTertiary
                    )
                },
                label = { Text("Bluetooth", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                    selectedLabelColor = CyberCyan,
                    containerColor = DarkSurface,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedTab == ScanFilterTab.AIRPODS_TWS,
                onClick = { viewModel.setFilterTab(ScanFilterTab.AIRPODS_TWS) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (selectedTab == ScanFilterTab.AIRPODS_TWS) CyberCyan else TextTertiary
                    )
                },
                label = { Text("AirPods / TWS", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                    selectedLabelColor = CyberCyan,
                    containerColor = DarkSurface,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedTab == ScanFilterTab.SPEAKERS,
                onClick = { viewModel.setFilterTab(ScanFilterTab.SPEAKERS) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Speaker,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (selectedTab == ScanFilterTab.SPEAKERS) EmeraldGreen else TextTertiary
                    )
                },
                label = { Text("Speakers", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldGreen.copy(alpha = 0.2f),
                    selectedLabelColor = EmeraldGreen,
                    containerColor = DarkSurface,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedTab == ScanFilterTab.SMART_LIGHT,
                onClick = { viewModel.setFilterTab(ScanFilterTab.SMART_LIGHT) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (selectedTab == ScanFilterTab.SMART_LIGHT) AmberWarning else TextTertiary
                    )
                },
                label = { Text("Smart Lights", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AmberWarning.copy(alpha = 0.2f),
                    selectedLabelColor = AmberWarning,
                    containerColor = DarkSurface,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedTab == ScanFilterTab.WIFI,
                onClick = { viewModel.setFilterTab(ScanFilterTab.WIFI) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (selectedTab == ScanFilterTab.WIFI) NeonBlue else TextTertiary
                    )
                },
                label = { Text("Wi-Fi Networks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonBlue.copy(alpha = 0.2f),
                    selectedLabelColor = NeonBlue,
                    containerColor = DarkSurface,
                    labelColor = TextSecondary
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Devices List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (devices.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = CyberCyan.copy(alpha = 0.6f),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "NO SIGNALS DETECTED YET",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'SCAN RF' above to discover Bluetooth peripherals & Wi-Fi networks.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(devices, key = { it.id }) { device ->
                DeviceScanRowCard(
                    device = device,
                    onClick = {
                        viewModel.selectDevice(device)
                        onNavigateToDeviceDetail(device)
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceScanRowCard(
    device: ScannedDevice,
    onClick: () -> Unit
) {
    val isWifi = device.type == DeviceType.WIFI_AP || device.type == DeviceType.WIFI_P2P
    val iconColor = when (device.category) {
        PeripheralCategory.TWS_EARBUDS -> CyberCyan
        PeripheralCategory.BT_SPEAKER -> EmeraldGreen
        PeripheralCategory.SMART_GLASS -> PurpleAccent
        PeripheralCategory.SMART_LIGHT -> AmberWarning
        PeripheralCategory.WIFI_NETWORK -> NeonBlue
        else -> if (isWifi) NeonBlue else CyberCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (device.category) {
                                PeripheralCategory.TWS_EARBUDS -> Icons.Default.Headphones
                                PeripheralCategory.BT_SPEAKER -> Icons.Default.Speaker
                                PeripheralCategory.SMART_GLASS -> Icons.Default.Visibility
                                PeripheralCategory.SMART_LIGHT -> Icons.Default.Lightbulb
                                PeripheralCategory.WIFI_NETWORK -> Icons.Default.Wifi
                                else -> if (isWifi) Icons.Default.Wifi else Icons.Default.Bluetooth
                            },
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = device.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${device.address} • ${device.category.name.replace("_", " ")}",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }

                // Status or Security Badge
                if (isWifi) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (device.isPasswordProtected) EmeraldGreen.copy(alpha = 0.15f)
                                else AmberWarning.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (device.isPasswordProtected) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (device.isPasswordProtected) EmeraldGreen else AmberWarning,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (device.isPasswordProtected) "SECURED" else "OPEN",
                                color = if (device.isPasswordProtected) EmeraldGreen else AmberWarning,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (device.isBonded) PurpleAccent.copy(alpha = 0.2f)
                                else DarkSurfaceVariant
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (device.isBonded) "PAIRED / BONDED" else "DISCONNECTED",
                            color = if (device.isBonded) PurpleAccent else TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hardware Capability Indicators Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (device.category == PeripheralCategory.BT_SPEAKER || device.category == PeripheralCategory.TWS_EARBUDS) {
                    CapabilityPill(text = "🔊 Sound Amp", color = EmeraldGreen)
                }
                if (device.category == PeripheralCategory.SMART_LIGHT || device.category == PeripheralCategory.SMART_GLASS) {
                    CapabilityPill(text = "💡 LED (Blue/Red)", color = AmberWarning)
                }
                if (device.category == PeripheralCategory.TWS_EARBUDS) {
                    CapabilityPill(text = "🎧 Dual L/R Pods", color = CyberCyan)
                }
                if (device.category == PeripheralCategory.SMART_GLASS) {
                    CapabilityPill(text = "👓 AR HUD", color = PurpleAccent)
                }
                CapabilityPill(text = "🔒 AES-CCM", color = NeonBlue)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Signal Quality & Security Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SignalQualityBars(level = device.signalLevel)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${device.rssi} dBm",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = if (isWifi) device.securityProtocol else "Vendor: ${device.vendor}",
                    fontSize = 10.sp,
                    color = TextTertiary,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CapabilityPill(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun SignalQualityBars(level: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
        for (i in 1..4) {
            val isActive = i <= level
            val barHeight = (i * 3 + 3).dp
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isActive) CyberCyan else DarkSurfaceVariant)
            )
        }
    }
}
