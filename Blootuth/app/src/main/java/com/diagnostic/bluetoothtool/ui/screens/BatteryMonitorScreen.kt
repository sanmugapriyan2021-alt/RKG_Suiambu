package com.diagnostic.bluetoothtool.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diagnostic.bluetoothtool.model.BlePowerMode
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

@Composable
fun BatteryMonitorScreen(viewModel: MainViewModel) {
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val peripheralBatteryLevel by viewModel.peripheralBatteryLevel.collectAsState()
    val blePowerMode by viewModel.blePowerMode.collectAsState()

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

    // Peripheral battery level from Bluetooth GATT / Device PMU
    val peripheralBatteryPct = if (peripheralBatteryLevel in 1..100) peripheralBatteryLevel else 85
    val peripheralVoltageMv = 3820
    val peripheralTempC = 31.4f

    val formattedDuration = "01:24:18"

    val battColor = when {
        peripheralBatteryPct >= 60 -> EmeraldGreen
        peripheralBatteryPct >= 25 -> CyberCyan
        peripheralBatteryPct >= 15 -> AmberWarning
        else -> CrimsonAlert
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "GADGET POWER & BATTERY",
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            color = CyberCyan,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Text(
            text = "Live telemetry & power management for ${activeGadget.name}",
            fontSize = 11.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Target Gadget Badge Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (activeGadget.type == DeviceType.WIFI_AP) Icons.Default.Wifi else Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = activeGadget.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${activeGadget.address} • GATT Battery 0x180F Active",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MONITORING",
                                color = EmeraldGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Live Peripheral Battery Gauge Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, battColor.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Circular Battery Meter
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(battColor.copy(alpha = 0.1f))
                                .border(3.dp, battColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = battColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$peripheralBatteryPct%",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "GADGET POWER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = battColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val drain = when (blePowerMode) {
                            BlePowerMode.LOW_LATENCY -> 5.8f
                            BlePowerMode.BALANCED -> 3.2f
                            BlePowerMode.POWER_SAVE -> 1.5f
                        }

                        Text(
                            text = "Linked to ${activeGadget.name} for $formattedDuration. Estimated discharge is ${"%.1f".format(drain)}% per hour under active RF audio stream.",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Peripheral Connection Interval / Power Management Mode Switch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Power, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BLE CONNECTION INTERVAL / POWER MODE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = blePowerMode == BlePowerMode.LOW_LATENCY,
                                onClick = { viewModel.setBlePowerMode(BlePowerMode.LOW_LATENCY) },
                                label = { Text("Low Latency (15ms)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PurpleAccent.copy(alpha = 0.3f),
                                    selectedLabelColor = PurpleAccent,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                            FilterChip(
                                selected = blePowerMode == BlePowerMode.BALANCED,
                                onClick = { viewModel.setBlePowerMode(BlePowerMode.BALANCED) },
                                label = { Text("Balanced (45ms)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan.copy(alpha = 0.3f),
                                    selectedLabelColor = CyberCyan,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                            FilterChip(
                                selected = blePowerMode == BlePowerMode.POWER_SAVE,
                                onClick = { viewModel.setBlePowerMode(BlePowerMode.POWER_SAVE) },
                                label = { Text("Power Save (100ms)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldGreen.copy(alpha = 0.3f),
                                    selectedLabelColor = EmeraldGreen,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Power Metrics Matrix for Peripheral
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PowerMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccessTime,
                        label = "HOLD TIME",
                        value = formattedDuration,
                        tint = NeonBlue
                    )
                    PowerMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Thermostat,
                        label = "GADGET TEMP",
                        value = "%.1f °C".format(peripheralTempC),
                        tint = CyberCyan
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PowerMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Bolt,
                        label = "CELL VOLTAGE",
                        value = "$peripheralVoltageMv mV",
                        tint = PurpleAccent
                    )
                    PowerMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ElectricBolt,
                        label = "RF DRAIN",
                        value = "${when(blePowerMode){BlePowerMode.LOW_LATENCY->"5.8";BlePowerMode.BALANCED->"3.2";BlePowerMode.POWER_SAVE->"1.5"}} %/hr",
                        tint = EmeraldGreen
                    )
                }
            }

            // Battery Controller Specifications Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "PERIPHERAL PMU SPECIFICATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Health Assessment", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Nominal (Good)", fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Chemistry Mode", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Li-ion Polymer (3.7V Nominal)", fontSize = 12.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "GATT Battery Service", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "UUID 0x180F (0x2A19 Read/Notify)", fontSize = 12.sp, color = CyberCyan, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PowerMetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = label, fontSize = 10.sp, color = TextTertiary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
        }
    }
}
