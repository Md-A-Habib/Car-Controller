package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConnectionStatus
import com.example.ui.CarViewModel

@Composable
fun HomeScreen(
    viewModel: CarViewModel,
    onNavigateToDrive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val ssid by viewModel.currentSsid.collectAsState()
    val ip by viewModel.currentIp.collectAsState()
    val isDemo by viewModel.isDemoMode.collectAsState()

    // Soft neumorphic background colors: Light silver-gray
    val surfaceColor = Color(0xFFF0F3F8)
    val cardBg = Color.White
    val accentBlue = Color(0xFF00A8FF)
    val accentBlueSoft = Color(0xFFE3F2FD)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. CAR STATUS CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("car_status_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4EBF5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(cardBg, accentBlueSoft.copy(alpha = 0.3f))
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statusTextColor = when (connectionStatus) {
                        ConnectionStatus.CONNECTED -> Color(0xFF00C853)
                        ConnectionStatus.CONNECTING -> Color(0xFFFF9100)
                        ConnectionStatus.DISCONNECTED -> Color(0xFFFF3B30)
                    }
                    
                    Icon(
                        imageVector = when (connectionStatus) {
                            ConnectionStatus.CONNECTED -> Icons.Default.CheckCircle
                            ConnectionStatus.CONNECTING -> Icons.Default.RadioButtonChecked
                            ConnectionStatus.DISCONNECTED -> Icons.Default.OfflineBolt
                        },
                        contentDescription = "Status symbol",
                        tint = statusTextColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = "VEHICLE TELEMETRY",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7A90),
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bold futuristic READY or UNCONNECTED text
                val mainStatusText = when (connectionStatus) {
                    ConnectionStatus.CONNECTED -> "READY"
                    ConnectionStatus.CONNECTING -> "LINKING..."
                    ConnectionStatus.DISCONNECTED -> "OFFLINE"
                }
                
                val mainStatusColor = when (connectionStatus) {
                    ConnectionStatus.CONNECTED -> Color(0xFF00A8FF)
                    ConnectionStatus.CONNECTING -> Color(0xFFFF9100)
                    ConnectionStatus.DISCONNECTED -> Color(0xFF7F8C8D)
                }

                Text(
                    text = mainStatusText,
                    fontSize = 44.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = mainStatusColor,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Divider(color = Color(0xFFE4EBF5), thickness = 1.dp)

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SSID",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8D9CAE))
                        )
                        Text(
                            text = ssid,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2C3E50)
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "IP ADDRESS",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8D9CAE))
                        )
                        Text(
                            text = ip,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2C3E50)
                            )
                        )
                    }
                }
            }
        }

        // 2. DETAILS TABLE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4EBF5))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "SPECIFICATIONS",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DetailRow(label = "Network", value = ssid, icon = Icons.Default.Wifi)
                DetailRow(label = "IP Address", value = ip, icon = Icons.Default.Info)
                DetailRow(label = "Protocol", value = "HTTP / WebSocket", icon = Icons.Default.Cast)
                
                val modeDesc = if (ssid.contains("ESP", ignoreCase = true)) "Access Point" else "Station Mode"
                DetailRow(label = "WiFi Mode", value = modeDesc, icon = Icons.Default.CheckCircle, isLast = true)
            }
        }

        // 3. CALL-TO-ACTION CONTAINER (Dashed border button to Drive Screen)
        val strokeColor = if (isConnected) accentBlue else Color(0xFFBDC3C7)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .drawBehind {
                    drawRoundRect(
                        color = strokeColor,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = dashEffect
                        ),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    )
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    viewModel.triggerHapticFeedback()
                    onNavigateToDrive()
                }
                .testTag("drive_cta_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎮 TAP DRIVE TO CONTROL",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isConnected) accentBlue else Color(0xFF7F8C8D),
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (isConnected) "Console online. Direct remote link ready." else "Connect to car wifi to unlock dashboard controls.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF6B7A90),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        
        // Simulation alert
        AnimatedVisibility(
            visible = isDemo,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF3CD),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info icon",
                        tint = Color(0xFF856404),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Running in Simulated Mode. Commands print to screen telemetry.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF856404),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
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
        Divider(color = Color(0xFFFFFBFE).copy(alpha = 0.1f), thickness = 0.5.dp)
        Divider(color = Color(0xFFF1F3F6), thickness = 1.dp)
    }
}
