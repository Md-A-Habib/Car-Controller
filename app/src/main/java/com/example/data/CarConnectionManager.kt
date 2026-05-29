package com.example.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

enum class ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    CONNECTING
}

class CarConnectionManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var webSocket: WebSocket? = null
    
    // OkHttp Client setup
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    // State flows
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _currentSsid = MutableStateFlow("ESP_CAR_01")
    val currentSsid: StateFlow<String> = _currentSsid

    private val _currentIp = MutableStateFlow("192.168.4.1")
    val currentIp: StateFlow<String> = _currentIp

    private val _signalStrength = MutableStateFlow(-42) // in dBm
    val signalStrength: StateFlow<Int> = _signalStrength

    private val _isDemoMode = MutableStateFlow(true) // Start in demo mode for emulator testing
    val isDemoMode: StateFlow<Boolean> = _isDemoMode

    // Real-time telemetry log to show in-app what commands are transmitted
    private val _telemetryLogs = MutableStateFlow<List<String>>(listOf("System initialized in Simulation Mode."))
    val telemetryLogs: StateFlow<List<String>> = _telemetryLogs

    init {
        // Simple periodic signal strength variation to simulate realistic telemetry
        scope.launch {
            while (true) {
                delay(4000)
                if (_isConnected.value) {
                    val variation = (-2..2).random()
                    _signalStrength.value = (_signalStrength.value + variation).coerceIn(-90, -30)
                }
            }
        }
    }

    fun setDemoMode(enabled: Boolean) {
        _isDemoMode.value = enabled
        addLog("Mode switched to: ${if (enabled) "Simulation (Demo)" else "Physical Device"}")
        if (enabled) {
            _connectionStatus.value = ConnectionStatus.CONNECTED
            _isConnected.value = true
        } else {
            disconnect()
        }
    }

    fun connect(ssid: String = "ESP_CAR_01", ip: String = "192.168.4.1") {
        if (_connectionStatus.value == ConnectionStatus.CONNECTING || _connectionStatus.value == ConnectionStatus.CONNECTED) {
            return
        }

        _currentSsid.value = ssid
        _currentIp.value = ip
        _connectionStatus.value = ConnectionStatus.CONNECTING
        addLog("Connecting to $ssid ($ip)...")

        if (_isDemoMode.value) {
            scope.launch {
                delay(1200) // Simulate connection delay
                _connectionStatus.value = ConnectionStatus.CONNECTED
                _isConnected.value = true
                _signalStrength.value = -42
                addLog("Connected to simulated $ssid successfully!")
            }
            return
        }

        // Real WebSocket Connection
        val wsUrl = "ws://$ip:81"
        val request = Request.Builder().url(wsUrl).build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _connectionStatus.value = ConnectionStatus.CONNECTED
                _isConnected.value = true
                addLog("WS Connection established to $wsUrl")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                addLog("WS RECEIVED: $text")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
                _isConnected.value = false
                addLog("WS Connection closed (Code: $code): $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
                _isConnected.value = false
                addLog("WS Error: ${t.localizedMessage ?: "Unknown failure"}")
                Log.e("CarConnectionManager", "WebSocket failure", t)
            }
        })
    }

    fun disconnect() {
        addLog("Disconnecting...")
        if (_isDemoMode.value) {
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
            _isConnected.value = false
            addLog("Simulated disconnection completed.")
            return
        }

        webSocket?.close(1000, "User disconnected")
        webSocket = null
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        _isConnected.value = false
        addLog("Physical connection disconnected.")
    }

    /**
     * Sends a command over WebSocket (or prints to telemetry log if in demo mode).
     * Commands are string-based as per instructions:
     * - `FORWARD`, `BACKWARD`, `LEFT`, `RIGHT`, `STOP`
     * - `SPEED:xx`
     * - `HORN` / `HORN_OFF`
     * - `LIGHTS` / `LIGHTS_OFF`
     */
    fun sendCommand(command: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date())
        if (_isConnected.value) {
            if (_isDemoMode.value) {
                addLog("[$timestamp] [WS SENT] $command")
            } else {
                val sent = webSocket?.send(command) ?: false
                if (sent) {
                    addLog("[$timestamp] [WS SENT] $command")
                } else {
                    addLog("[$timestamp] [WS FAIL] $command")
                }
            }
        } else {
            addLog("[$timestamp] [NOT CONNECTED] Blocked: $command")
        }
    }

    private fun addLog(message: String) {
        val currentList = _telemetryLogs.value.toMutableList()
        currentList.add(0, message) // Insert newest message at position 0
        if (currentList.size > 50) {
            currentList.removeAt(currentList.lastIndex)
        }
        _telemetryLogs.value = currentList
        Log.d("CarConnectionManager", message)
    }
}
