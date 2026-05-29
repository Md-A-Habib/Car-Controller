package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.ui.CarViewModel

@Composable
fun ConfigScreen(
    viewModel: CarViewModel,
    modifier: Modifier = Modifier
) {
    val isAutoConnect by viewModel.isAutoConnectEnabled.collectAsState()
    val isHaptic by viewModel.isHapticEnabled.collectAsState()
    val isStayAwake by viewModel.isStayAwakeEnabled.collectAsState()
    val isNotifications by viewModel.isNotificationsEnabled.collectAsState()
    val isDemo by viewModel.isDemoMode.collectAsState()

    // Color definitions
    val surfaceColor = Color(0xFFF0F3F8)
    val cardBg = Color.White
    val mainBorder = Color(0xFFE4EBF5)
    val connectBlue = Color(0xFF00A8FF)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // 1. APPLICATION SETTINGS CARD (Grouped Toggle switches)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, mainBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "APPLICATION SETTINGS",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D6D7E),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Mock Mode (Very useful for emulator test debugging!)
                SettingToggleRow(
                    title = "Simulation Mode",
                    subtext = "Simulate motors and mock telemetry inputs",
                    icon = Icons.Default.BugReport,
                    checked = isDemo,
                    onCheckedChange = { viewModel.setDemoMode(it) },
                    testTag = "config_toggle_demo"
                )

                Divider(color = Color(0xFFF1F3F6), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                SettingToggleRow(
                    title = "Auto Connect",
                    subtext = "Connect on app startup",
                    icon = Icons.Default.Autorenew,
                    checked = isAutoConnect,
                    onCheckedChange = { viewModel.isAutoConnectEnabled.value = it },
                    testTag = "config_toggle_autoconnect"
                )

                Divider(color = Color(0xFFF1F3F6), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                SettingToggleRow(
                    title = "Haptic Feedback",
                    subtext = "Vibrate on button press",
                    icon = Icons.Default.Vibration,
                    checked = isHaptic,
                    onCheckedChange = { viewModel.isHapticEnabled.value = it },
                    testTag = "config_toggle_haptics"
                )

                Divider(color = Color(0xFFF1F3F6), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                SettingToggleRow(
                    title = "Stay Awake",
                    subtext = "Prevent screen timeout",
                    icon = Icons.Default.Power,
                    checked = isStayAwake,
                    onCheckedChange = { viewModel.isStayAwakeEnabled.value = it },
                    testTag = "config_toggle_stayawake"
                )

                Divider(color = Color(0xFFF1F3F6), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                SettingToggleRow(
                    title = "Notifications",
                    subtext = "Connection status alerts",
                    icon = Icons.Default.NotificationsActive,
                    checked = isNotifications,
                    onCheckedChange = { viewModel.isNotificationsEnabled.value = it },
                    testTag = "config_toggle_notifications"
                )
            }
        }

        // 2. DEVICE INFORMATION CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, mainBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DEVICE INFORMATION",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D6D7E),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DeviceStaticRow(label = "Device", value = "ESP32-WROOM-32", icon = Icons.Default.DeveloperBoard)
                DeviceStaticRow(label = "Firmware", value = "v2.1.4-stable", icon = Icons.Default.Dns)
                DeviceStaticRow(label = "MAC Address", value = "A4:CF:12:9E:31:B0", icon = Icons.Default.Fingerprint, isLast = true)
            }
        }

        // 3. TELEMETRY PACKET TRACE PANEL (Additional high-fidelity console window to show real physical commands working)
        val logs by viewModel.telemetryLogs.collectAsState()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E272C)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3E50))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WS TRANSACTION TRACE LOG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00A8FF),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = Color(0xFF00A8FF).copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF151C1F), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs) { log ->
                            Text(
                                text = log,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (log.contains("SENT") || log.contains("Connected")) Color(0xFF2ECC71) else if (log.contains("FAIL") || log.contains("Error")) Color(0xFFE74C3C) else Color(0xFFBDC3C7)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. DISCRETE FOOTER BRANDING VERSION
        Text(
            text = "ESP Car Controller · v2.1.0",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7F8C8D),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .testTag("app_version_footer")
        )
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0xFFF0F3F8), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00A8FF),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = Color(0xFF7F8C8D)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF00A8FF),
                uncheckedThumbColor = Color(0xFFBDC3C7),
                uncheckedTrackColor = Color(0xFFE4EBF5)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
fun DeviceStaticRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00A8FF),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5D6D7E)
                )
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50),
                fontFamily = FontFamily.Monospace
            )
        )
    }

    if (!isLast) {
        Divider(color = Color(0xFFF1F3F6), thickness = 1.dp)
    }
}
