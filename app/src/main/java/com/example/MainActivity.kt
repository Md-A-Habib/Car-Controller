package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.ConnectionStatus
import com.example.ui.CarViewModel
import com.example.ui.screens.ConfigScreen
import com.example.ui.screens.DriveScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NetworkScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CarViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val isConnected by viewModel.isConnected.collectAsState()
                val connectionStatus by viewModel.connectionStatus.collectAsState()
                
                // Color configuration
                val headerBg = Color.White
                val accentBlue = Color(0xFF00A8FF)
                val darkGrey = Color(0xFF2C3E50)
                val dotGreen = Color(0xFF00C853)
                val dotRed = Color(0xFFFF3B30)

                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                    topBar = {
                        // Persistent Header
                        Surface(
                            modifier = Modifier.fillMaxWidth().testTag("persistent_header"),
                            tonalElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE4EBF5))
                        ) {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = headerBg),
                                title = {
                                    Column(verticalArrangement = Arrangement.Center) {
                                        // Header text split
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "ESP CAR",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 20.sp,
                                                color = darkGrey
                                            )
                                            Text(
                                                text = "CONTROLLER",
                                                fontWeight = FontWeight.Light,
                                                fontSize = 20.sp,
                                                color = accentBlue
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        // Real-time Status dot and label
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when (connectionStatus) {
                                                            ConnectionStatus.CONNECTED -> dotGreen
                                                            ConnectionStatus.CONNECTING -> Color(0xFFFF9100)
                                                            ConnectionStatus.DISCONNECTED -> dotRed
                                                        }
                                                    )
                                                    .testTag("connection_status_dot")
                                            )
                                            Text(
                                                text = when (connectionStatus) {
                                                    ConnectionStatus.CONNECTED -> "Connected"
                                                    ConnectionStatus.CONNECTING -> "Connecting..."
                                                    ConnectionStatus.DISCONNECTED -> "Disconnected"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF7F8C8D),
                                                modifier = Modifier.testTag("connection_status_label")
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    // Wi-Fi signal status icon
                                    IconButton(
                                        onClick = { navController.navigate("network") },
                                        modifier = Modifier.testTag("header_wifi_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                                            contentDescription = "Wi-Fi Status",
                                            tint = if (isConnected) accentBlue else Color(0xFFBDC3C7)
                                        )
                                    }
                                    
                                    // Circular Power/Standby button on top-right
                                    IconButton(
                                        onClick = {
                                            if (isConnected) {
                                                viewModel.disconnectFromCar()
                                            } else {
                                                viewModel.connectToCar()
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(40.dp)
                                            .border(1.5.dp, if (isConnected) accentBlue else Color(0xFFBDC3C7), CircleShape)
                                            .clip(CircleShape)
                                            .background(if (isConnected) accentBlue.copy(alpha = 0.15f) else Color.Transparent)
                                            .testTag("header_power_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PowerSettingsNew,
                                            contentDescription = "Standby toggle",
                                            tint = if (isConnected) accentBlue else Color(0xFFBDC3C7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        // Persistent Bottom Navigation
                        val currentBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = currentBackStackEntry?.destination?.route ?: "home"
                        
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_nav_bar").windowInsetsPadding(WindowInsets.navigationBars),
                            containerColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            // Home Tab
                            NavigationBarItem(
                                selected = currentRoute == "home",
                                onClick = {
                                    viewModel.triggerHapticFeedback()
                                    navController.navigate("home") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = accentBlue,
                                    selectedTextColor = accentBlue,
                                    indicatorColor = accentBlue.copy(alpha = 0.15f),
                                    unselectedIconColor = Color(0xFF7F8C8D),
                                    unselectedTextColor = Color(0xFF7F8C8D)
                                ),
                                modifier = Modifier.testTag("nav_item_home")
                            )

                            // Drive Tab
                            NavigationBarItem(
                                selected = currentRoute == "drive",
                                onClick = {
                                    viewModel.triggerHapticFeedback()
                                    navController.navigate("drive") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Gamepad, contentDescription = "Drive") },
                                label = { Text("Drive", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = accentBlue,
                                    selectedTextColor = accentBlue,
                                    indicatorColor = accentBlue.copy(alpha = 0.15f),
                                    unselectedIconColor = Color(0xFF7F8C8D),
                                    unselectedTextColor = Color(0xFF7F8C8D)
                                ),
                                modifier = Modifier.testTag("nav_item_drive")
                            )

                            // Network Tab
                            NavigationBarItem(
                                selected = currentRoute == "network",
                                onClick = {
                                    viewModel.triggerHapticFeedback()
                                    navController.navigate("network") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Wifi, contentDescription = "Network") },
                                label = { Text("Network", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = accentBlue,
                                    selectedTextColor = accentBlue,
                                    indicatorColor = accentBlue.copy(alpha = 0.15f),
                                    unselectedIconColor = Color(0xFF7F8C8D),
                                    unselectedTextColor = Color(0xFF7F8C8D)
                                ),
                                modifier = Modifier.testTag("nav_item_network")
                            )

                            // Config Tab
                            NavigationBarItem(
                                selected = currentRoute == "config",
                                onClick = {
                                    viewModel.triggerHapticFeedback()
                                    navController.navigate("config") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
                                label = { Text("Config", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = accentBlue,
                                    selectedTextColor = accentBlue,
                                    indicatorColor = accentBlue.copy(alpha = 0.15f),
                                    unselectedIconColor = Color(0xFF7F8C8D),
                                    unselectedTextColor = Color(0xFF7F8C8D)
                                ),
                                modifier = Modifier.testTag("nav_item_config")
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToDrive = {
                                    navController.navigate("drive") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        
                        composable("drive") {
                            DriveScreen(viewModel = viewModel)
                        }
                        
                        composable("network") {
                            NetworkScreen(viewModel = viewModel)
                        }
                        
                        composable("config") {
                            ConfigScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
