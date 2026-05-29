package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CarConnectionManager
import com.example.data.ConnectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WifiNetwork(
    val ssid: String,
    val signalStrengthDbm: Int,
    val isSecure: Boolean = true
)

class CarViewModel(application: Application) : AndroidViewModel(application) {
    val connectionManager = CarConnectionManager()

    // Expose flow endpoints of connection manager
    val isConnected: StateFlow<Boolean> = connectionManager.isConnected
    val connectionStatus: StateFlow<ConnectionStatus> = connectionManager.connectionStatus
    val currentSsid: StateFlow<String> = connectionManager.currentSsid
    val currentIp: StateFlow<String> = connectionManager.currentIp
    val signalStrength: StateFlow<Int> = connectionManager.signalStrength
    val isDemoMode: StateFlow<Boolean> = connectionManager.isDemoMode
    val telemetryLogs: StateFlow<List<String>> = connectionManager.telemetryLogs

    // Active driving state indicators
    private val _currentDirection = MutableStateFlow("IDLE")
    val currentDirection: StateFlow<String> = _currentDirection

    private val _currentSpeed = MutableStateFlow(65f) // Default speed: 65%
    val currentSpeed: StateFlow<Float> = _currentSpeed

    private val _isTurboActive = MutableStateFlow(false)
    val isTurboActive: StateFlow<Boolean> = _isTurboActive

    private val _isHornActive = MutableStateFlow(false)
    val isHornActive: StateFlow<Boolean> = _isHornActive

    private val _isLightsActive = MutableStateFlow(false)
    val isLightsActive: StateFlow<Boolean> = _isLightsActive

    // Settings Toggles (Config Screen)
    val isAutoConnectEnabled = MutableStateFlow(true)
    val isHapticEnabled = MutableStateFlow(true)
    val isStayAwakeEnabled = MutableStateFlow(false)
    val isNotificationsEnabled = MutableStateFlow(false)

    // Scanning and Wi-Fi networks (Network Screen)
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _availableNetworks = MutableStateFlow<List<WifiNetwork>>(
        listOf(
            WifiNetwork("ESP_CAR_01", -42),
            WifiNetwork("HomeNetwork_5G", -72),
            WifiNetwork("AndroidHotspot", -65),
            WifiNetwork("ESP_CAR_Backup_02", -85),
            WifiNetwork("Lab_WiFi_Router", -58)
        )
    )
    val availableNetworks: StateFlow<List<WifiNetwork>> = _availableNetworks

    // Vibrator for haptics
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        // Auto connect initially if configured and in demo mode
        if (isAutoConnectEnabled.value) {
            connectionManager.connect()
        }
    }

    fun triggerHapticFeedback() {
        if (!isHapticEnabled.value) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(45)
            }
        } catch (e: Exception) {
            // Ignore if vibration fails or permission not declared
        }
    }

    fun connectToCar(ssid: String = "ESP_CAR_01", ip: String = "192.168.4.1") {
        triggerHapticFeedback()
        connectionManager.connect(ssid, ip)
    }

    fun disconnectFromCar() {
        triggerHapticFeedback()
        connectionManager.disconnect()
        _currentDirection.value = "IDLE"
    }

    fun setDemoMode(enabled: Boolean) {
        triggerHapticFeedback()
        connectionManager.setDemoMode(enabled)
    }

    // Direction controls
    fun sendDirection(direction: String) {
        triggerHapticFeedback()
        _currentDirection.value = direction
        // Send command to ESP
        connectionManager.sendCommand(direction)
    }

    fun triggerStop() {
        triggerHapticFeedback()
        _currentDirection.value = "IDLE"
        connectionManager.sendCommand("STOP")
    }

    fun triggerEmergencyStop() {
        triggerHapticFeedback()
        _currentDirection.value = "IDLE"
        connectionManager.sendCommand("ESTOP")
    }

    // Speed controls
    fun updateSpeed(speed: Float) {
        _currentSpeed.value = speed
        _isTurboActive.value = false // speed adjust breaks turbo standby/active
        val speedInt = speed.toInt()
        connectionManager.sendCommand("SPEED:$speedInt")
    }

    fun toggleTurboBoost() {
        triggerHapticFeedback()
        _isTurboActive.value = !_isTurboActive.value
        if (_isTurboActive.value) {
            _currentSpeed.value = 100f
            connectionManager.sendCommand("SPEED:100")
            connectionManager.sendCommand("TURBO")
        } else {
            _currentSpeed.value = 65f
            connectionManager.sendCommand("SPEED:65")
            connectionManager.sendCommand("TURBO_STANDBY")
        }
    }

    // Auxiliary controls
    fun toggleHorn() {
        triggerHapticFeedback()
        val newState = !_isHornActive.value
        _isHornActive.value = newState
        connectionManager.sendCommand(if (newState) "HORN" else "HORN_OFF")
    }

    fun toggleLights() {
        triggerHapticFeedback()
        val newState = !_isLightsActive.value
        _isLightsActive.value = newState
        connectionManager.sendCommand(if (newState) "LIGHTS" else "LIGHTS_OFF")
    }

    // Network search lists updates
    fun scanNetworks() {
        triggerHapticFeedback()
        viewModelScope.launch {
            _isScanning.value = true
            // Introduce some simulated delay, then mix network orders
            connectionManager.sendCommand("SCAN_START")
            kotlinx.coroutines.delay(1500)
            
            val updated = listOf(
                WifiNetwork("ESP_CAR_01", (-50..-30).random()),
                WifiNetwork("HomeNetwork_5G", (-80..-60).random()),
                WifiNetwork("AndroidHotspot", (-75..-55).random()),
                WifiNetwork("ESP_CAR_Backup_02", (-90..-70).random()),
                WifiNetwork("Lab_WiFi_Router", (-65..-45).random()),
                WifiNetwork("Cafe_Guest_Secure", (-80..-75).random(), isSecure = true),
                WifiNetwork("ESP_TEST_HOTSPOT", (-48..-40).random(), isSecure = false)
            ).sortedByDescending { it.signalStrengthDbm }

            _availableNetworks.value = updated
            _isScanning.value = false
            connectionManager.sendCommand("SCAN_COMPLETE")
        }
    }
}
