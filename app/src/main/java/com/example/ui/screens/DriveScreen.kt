package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CarViewModel

@Composable
fun DriveScreen(
    viewModel: CarViewModel,
    modifier: Modifier = Modifier
) {
    val currentDirection by viewModel.currentDirection.collectAsState()
    val speed by viewModel.currentSpeed.collectAsState()
    val isTurbo by viewModel.isTurboActive.collectAsState()
    val isHorn by viewModel.isHornActive.collectAsState()
    val isLights by viewModel.isLightsActive.collectAsState()

    // Color definitions
    val bgColor = Color(0xFFF0F3F8)
    val cardBg = Color.White
    val activeBlue = Color(0xFF00A8FF)
    val activeBlueSoft = Color(0xFFE3F2FD)
    val buttonNormalBg = Color(0xFFFFFFFF)
    val buttonBorderColor = Color(0xFFE4EBF5)
    val stopRed = Color(0xFFFF3B30)
    val stopRedSoft = Color(0xFFFFEBEE)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // 1. DIRECTIONAL D-PAD SECTION
        Card(
            modifier = Modifier.fillMaxWidth().weight(1.2f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, buttonBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large D-pad cluster container
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .background(Color(0xFFE9F0FA), CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // D-PAD BACKING RING
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(6.dp, Color(0xFFF0F3F8), CircleShape)
                            .clip(CircleShape)
                    )

                    // UP arrow
                    DPadArrowButton(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        isActive = (currentDirection == "FORWARD"),
                        testTag = "dpad_forward",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp),
                        onClick = {
                            if (currentDirection == "FORWARD") viewModel.triggerStop()
                            else viewModel.sendDirection("FORWARD")
                        }
                    )

                    // DOWN arrow
                    DPadArrowButton(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        isActive = (currentDirection == "BACKWARD"),
                        testTag = "dpad_backward",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp),
                        onClick = {
                            if (currentDirection == "BACKWARD") viewModel.triggerStop()
                            else viewModel.sendDirection("BACKWARD")
                        }
                    )

                    // LEFT arrow
                    DPadArrowButton(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        isActive = (currentDirection == "LEFT"),
                        testTag = "dpad_left",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 4.dp),
                        onClick = {
                            if (currentDirection == "LEFT") viewModel.triggerStop()
                            else viewModel.sendDirection("LEFT")
                        }
                    )

                    // RIGHT arrow
                    DPadArrowButton(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        isActive = (currentDirection == "RIGHT"),
                        testTag = "dpad_right",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp),
                        onClick = {
                            if (currentDirection == "RIGHT") viewModel.triggerStop()
                            else viewModel.sendDirection("RIGHT")
                        }
                    )

                    // CENTRAL RED STOP BUTTON
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .shadow(2.dp, CircleShape)
                            .background(stopRed, CircleShape)
                            .clip(CircleShape)
                            .clickable { viewModel.triggerStop() }
                            .testTag("dpad_stop"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "STOP",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Direction state indicator text below pad
                val directionText = when (currentDirection) {
                    "FORWARD" -> "— FORWARD —"
                    "BACKWARD" -> "— REVERSE —"
                    "LEFT" -> "— LEFT TURN —"
                    "RIGHT" -> "— RIGHT TURN —"
                    else -> "— IDLE —"
                }
                
                val directionColor = when (currentDirection) {
                    "FORWARD" -> activeBlue
                    "BACKWARD" -> activeBlue
                    "LEFT" -> Color(0xFF9B59B6)
                    "RIGHT" -> Color(0xFF9B59B6)
                    else -> Color(0xFF7F8C8D)
                }

                Text(
                    text = directionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = directionColor,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("current_direction_indicator")
                )
            }
        }

        // 2. SPEED CONTROL CARD
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, buttonBorderColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header displaying speed %
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROPULSION VELOCITY",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D6D7E),
                            letterSpacing = 1.sp
                        )
                    )

                    // Technical monospace speed percentage display
                    Text(
                        text = "${speed.toInt()}%",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (isTurbo) stopRed else activeBlue,
                        modifier = Modifier.testTag("speed_percentage_display")
                    )
                }

                // Slider
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                    Slider(
                        value = speed,
                        onValueChange = { viewModel.updateSpeed(it) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isTurbo) stopRed else activeBlue,
                            activeTrackColor = if (isTurbo) stopRed else activeBlue,
                            inactiveTrackColor = Color(0xFFE4EBF5)
                        ),
                        modifier = Modifier.testTag("speed_slider")
                    )
                }

                // Slider calibration ticks markings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("0", "25", "50", "75", "MAX").forEach { tick ->
                        Text(
                            text = tick,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8D9CAE),
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Turbo button
                val turboBtnColor = if (isTurbo) stopRed else Color(0xFF2C3E50)
                val turboBg = if (isTurbo) stopRedSoft else Color(0xFFF0F3F8)
                val turboBorder = if (isTurbo) stopRed.copy(alpha = 0.5f) else buttonBorderColor

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(turboBg, RoundedCornerShape(12.dp))
                        .border(1.dp, turboBorder, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleTurboBoost() }
                        .padding(horizontal = 16.dp)
                        .testTag("turbo_boost_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Turbo",
                        tint = turboBtnColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTurbo) "TURBO BOOST — ACTIVE" else "TURBO BOOST — STANDBY",
                        fontWeight = FontWeight.Bold,
                        color = turboBtnColor,
                        fontSize = 13.sp,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }

        // 3. AUXILIARY CONTROLS (Bottom Row, equal widths, rounded squares)
        Row(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Horn button
            AuxButton(
                icon = Icons.Default.Campaign,
                label = "Horn",
                isActive = isHorn,
                activeColor = activeBlue,
                activeBg = activeBlueSoft,
                testTag = "aux_horn",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleHorn() }
            )

            // Lights button
            AuxButton(
                icon = Icons.Default.Lightbulb,
                label = "Lights",
                isActive = isLights,
                activeColor = Color(0xFFF1C40F),
                activeBg = Color(0xFFFEF9E7),
                testTag = "aux_lights",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleLights() }
            )

            // E-Stop button
            AuxButton(
                icon = Icons.Default.Warning,
                label = "E-STOP",
                isActive = false, // E-Stop triggers instantaneous burst, no state
                activeColor = stopRed,
                activeBg = stopRedSoft,
                isUrgent = true,
                testTag = "aux_estop",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.triggerEmergencyStop() }
            )
        }
    }
}

@Composable
fun DPadArrowButton(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val size = 52.dp
    val buttonBg = if (isActive) Color(0xFF00A8FF) else Color.White
    val tintColor = if (isActive) Color.White else Color(0xFF5D6D7E)
    val shadowElev = if (isActive) 1.dp else 3.dp

    Box(
        modifier = modifier
            .size(size)
            .shadow(shadowElev, RoundedCornerShape(14.dp))
            .background(buttonBg, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE4EBF5), RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Arrow Button",
            tint = tintColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun RowScope.AuxButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    activeBg: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    isUrgent: Boolean = false,
    onClick: () -> Unit
) {
    val normalBg = Color.White
    val normalContentColor = Color(0xFF5D6D7E)
    val containerBg = if (isUrgent) Color(0xFFFFEBEE) else if (isActive) activeBg else normalBg
    val contentColor = if (isUrgent) Color(0xFFFF3B30) else if (isActive) activeColor else normalContentColor
    val borderColor = if (isUrgent) Color(0xFFFFD1D1) else if (isActive) activeColor.copy(alpha = 0.5f) else Color(0xFFE4EBF5)

    Card(
        modifier = modifier
            .fillMaxHeight()
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}
