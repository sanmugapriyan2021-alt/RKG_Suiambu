package com.diagnostic.bluetoothtool.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diagnostic.bluetoothtool.model.ConnectionStatus
import com.diagnostic.bluetoothtool.model.DeviceType
import com.diagnostic.bluetoothtool.model.PreTestProfile
import com.diagnostic.bluetoothtool.model.ScannedDevice
import com.diagnostic.bluetoothtool.model.TestStatus
import com.diagnostic.bluetoothtool.ui.theme.AmberWarning
import com.diagnostic.bluetoothtool.ui.theme.CrimsonAlert
import com.diagnostic.bluetoothtool.ui.theme.CyberCyan
import com.diagnostic.bluetoothtool.ui.theme.DarkCardBorder
import com.diagnostic.bluetoothtool.ui.theme.DarkSurface
import com.diagnostic.bluetoothtool.ui.theme.DarkSurfaceVariant
import com.diagnostic.bluetoothtool.ui.theme.EmeraldGreen
import com.diagnostic.bluetoothtool.ui.theme.NeonBlue
import com.diagnostic.bluetoothtool.ui.theme.PurpleAccent
import com.diagnostic.bluetoothtool.ui.theme.TextPrimary
import com.diagnostic.bluetoothtool.ui.theme.TextSecondary
import com.diagnostic.bluetoothtool.ui.theme.TextTertiary

@Composable
fun StatusBadge(status: ConnectionStatus) {
    val (bgColor, textColor, text) = when (status) {
        ConnectionStatus.CONNECTED -> Triple(EmeraldGreen.copy(alpha = 0.2f), EmeraldGreen, "CONNECTED")
        ConnectionStatus.CONNECTING -> Triple(CyberCyan.copy(alpha = 0.2f), CyberCyan, "CONNECTING...")
        ConnectionStatus.BONDED -> Triple(PurpleAccent.copy(alpha = 0.2f), PurpleAccent, "PAIRED / BONDED")
        ConnectionStatus.FAILED -> Triple(CrimsonAlert.copy(alpha = 0.2f), CrimsonAlert, "ERROR")
        ConnectionStatus.DISCONNECTED -> Triple(DarkSurfaceVariant, TextTertiary, "DISCONNECTED")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun SignalStrengthBar(rssi: Int, level: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..4) {
            val barHeight = (4 + i * 3).dp
            val isFilled = i <= level
            val color = when {
                level >= 3 -> EmeraldGreen
                level == 2 -> AmberWarning
                else -> CrimsonAlert
            }
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isFilled) color else DarkCardBorder)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${rssi}dBm",
            fontSize = 10.sp,
            color = TextSecondary,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun DeviceItemCard(
    device: ScannedDevice,
    onClick: () -> Unit,
    onRunPreTest: () -> Unit
) {
    val isWifi = device.type == DeviceType.WIFI_AP || device.type == DeviceType.WIFI_P2P
    val icon: ImageVector = if (isWifi) Icons.Default.Wifi else Icons.Default.Bluetooth
    val accentColor = if (isWifi) NeonBlue else CyberCyan

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, if (device.status == ConnectionStatus.CONNECTED) EmeraldGreen else DarkCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
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
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = device.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = device.address,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                StatusBadge(status = device.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vendor: ${device.vendor}",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                    Text(
                        text = "Type: ${device.deviceClass}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                SignalStrengthBar(rssi = device.rssi, level = device.signalLevel)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onRunPreTest,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pre-Test",
                        fontSize = 12.sp,
                        color = CyberCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun PreTestSummaryCard(profile: PreTestProfile, onInspect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onInspect() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(CyberCyan, PurpleAccent))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SAVED PRE-TEST PROFILE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = profile.deviceName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "MAC: ${profile.macAddress} | IP: ${profile.ipAddress}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.15f))
                        .border(2.dp, CyberCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${profile.overallHealthScore}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                        Text(
                            text = "SCORE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                profile.tests.take(3).forEach { test ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val (icon, tint) = when (test.status) {
                                TestStatus.PASSED -> Icons.Default.CheckCircle to EmeraldGreen
                                TestStatus.WARNING -> Icons.Default.Warning to AmberWarning
                                else -> Icons.Default.Info to CrimsonAlert
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = test.name,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${test.latencyMs}ms",
                            fontSize = 11.sp,
                            color = TextTertiary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
