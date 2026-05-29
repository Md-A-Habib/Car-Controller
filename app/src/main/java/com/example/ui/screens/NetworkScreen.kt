package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CarViewModel
import com.example.ui.WifiNetwork

@Composable
fun NetworkScreen(
    viewModel: CarViewModel,
    modifier: Modifier = Modifier
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val ssid by viewModel.currentSsid.collectAsState()
    val ip by viewModel.currentIp.collectAsState()
    val signalStrength by viewModel.signalStrength.collectAsState()
    val availableNetworks by viewModel.availableNetworks.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    // Color definitions
    val surfaceColor = Color(0xFFF0F3F8)
    val cardBg = Color.White
    val mainBorder = Color(0xFFE4EBF5)
    val connectBlue = Color(0xFF00A8FF)
    val activeBlueSoft = Color(0xFFE3F2FD)
    val disconnectRed = Color(0xFFFF3B30)
    val disconnectRedSoft = Color(0xFFFFEBEE)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // 1. CONNECTED NETWORK CARD (ONLY displays if connected)
        if (isConnected) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("connected_network_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, mainBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONNECTED AP STATUS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D6D7E),
                                letterSpacing = 1.2.sp
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF00C853), CircleShape)
                            )
                            Text(
                                text = "ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00C853)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ssid,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2C3E50)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Static Gateway IP: $ip",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF7F8C8D),
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        // Custom 4-Bar Signal Visualizer + metric
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            WifiSignalBars(dbm = signalStrength, color = connectBlue)
                            Text(
                                text = "Strong $signalStrength dBm",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = connectBlue,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Red Styled Disconnect button
                    Button(
                        onClick = { viewModel.disconnectFromCar() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("disconnect_network_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = disconnectRedSoft,
                            contentColor = disconnectRed
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, disconnectRed.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Disconnect icon",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DISCONNECT TERMINAL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        } else {
            // Disconnected fallback helper header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, mainBorder)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(disconnectRedSoft, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = disconnectRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "No Controller Link Active",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Text(
                            text = "Select an ESP AP below of the format ESP_CAR_xx to establish connection",
                            fontSize = 12.sp,
                            color = Color(0xFF7F8C8D)
                        )
                    }
                }
            }
        }

        // 2. CONSOLE SCAN/REFRESH ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AVAILABLE AP SCAN LIST",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7A90),
                    letterSpacing = 1.2.sp
                )
            )
            
            IconButton(
                onClick = { viewModel.scanNetworks() },
                enabled = !isScanning,
                modifier = Modifier.testTag("scan_networks_button")
            ) {
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan networks",
                        tint = connectBlue
                    )
                }
            }
        }

        // 3. AVAILABLE NETWORKS LIST (Scrollable)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, mainBorder)
        ) {
            if (availableNetworks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = Color(0xFFBDC3C7)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Hotspots Discovered",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7F8C8D),
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Tap refresh to scan for nearby signals",
                        color = Color(0xFFBDC3C7),
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(availableNetworks) { network ->
                        val isCurrentNetworkConnected = isConnected && network.ssid == ssid
                        val itemRowBg = if (isCurrentNetworkConnected) activeBlueSoft else Color.Transparent
                        val itemBorderColor = if (isCurrentNetworkConnected) connectBlue.copy(alpha = 0.4f) else Color.Transparent

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(itemRowBg)
                                .border(1.dp, itemBorderColor, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isCurrentNetworkConnected) {
                                        // clicking connected disconnects
                                        viewModel.disconnectFromCar()
                                    } else {
                                        viewModel.connectToCar(network.ssid, "192.168.4.1")
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("network_row_${network.ssid}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Wi-Fi symbol
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFFF0F3F8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (network.isSecure) Icons.Default.WifiLock else Icons.Default.Wifi,
                                    contentDescription = "Wifi SSID Icon",
                                    tint = if (isCurrentNetworkConnected) connectBlue else Color(0xFF7F8C8D),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Network name
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = network.ssid,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C3E50),
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (isCurrentNetworkConnected) "Connected" else "Tap to link",
                                    fontSize = 11.sp,
                                    color = if (isCurrentNetworkConnected) connectBlue else Color(0xFF95A5A6)
                                )
                            }

                            // Custom Signal Bars representation
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${network.signalStrengthDbm} dBm",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = if (isCurrentNetworkConnected) connectBlue else Color(0xFF7F8C8D),
                                    fontWeight = FontWeight.Bold
                                )
                                WifiSignalBars(
                                    dbm = network.signalStrengthDbm,
                                    color = if (isCurrentNetworkConnected) connectBlue else Color(0xFFBDC3C7)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom elegant signal strength bars (4 bars of increasing dimensions based on dbm level)
 */
@Composable
fun WifiSignalBars(
    dbm: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Determine quality bars count:
    // 1 bar: -85 dbm and lower
    // 2 bars: -85 dbm to -70 dbm
    // 3 bars: -70 dbm to -55 dbm
    // 4 bars: -55 dbm and higher
    val activeBars = when {
        dbm >= -55 -> 4
        dbm >= -70 -> 3
        dbm >= -85 -> 2
        else -> 1
    }

    Row(
        modifier = modifier.wrapContentSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..4) {
            val barHeight = (i * 4).dp
            val isActive = i <= activeBars
            val barBgColor = if (isActive) color else color.copy(alpha = 0.2f)

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                    .background(barBgColor)
            )
        }
    }
}
