package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ConnectionStatus
import com.example.ui.CarViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var viewModel: CarViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        viewModel = CarViewModel(context as Application)
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("ESP Car Controller", appName)
    }

    @Test
    fun `viewModel initializes with connection in simulation mode`() {
        viewModel.setDemoMode(true) // Force immediate synchronous CONNECTED state flow
        assertTrue(viewModel.isDemoMode.value)
        assertEquals(ConnectionStatus.CONNECTED, viewModel.connectionStatus.value)
        assertEquals("ESP_CAR_01", viewModel.currentSsid.value)
        assertEquals("192.168.4.1", viewModel.currentIp.value)
    }

    @Test
    fun `sending direction updates live state`() {
        viewModel.sendDirection("FORWARD")
        assertEquals("FORWARD", viewModel.currentDirection.value)
        
        viewModel.triggerStop()
        assertEquals("IDLE", viewModel.currentDirection.value)
    }

    @Test
    fun `turbo boost toggle updates speed parameters`() {
        viewModel.toggleTurboBoost()
        assertTrue(viewModel.isTurboActive.value)
        assertEquals(100f, viewModel.currentSpeed.value)
    }
}
