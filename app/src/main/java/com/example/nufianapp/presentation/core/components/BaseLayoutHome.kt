package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.zIndex
import kotlin.random.Random

private fun Animatable<Float, *>.generateNextTarget(): Float {
    val delta = Random.nextFloat() * 0.4f - 0.2f
    return (value + delta).coerceIn(0f, 1f)
}

@Composable
fun BaseLayoutHome() {
    val scope = rememberCoroutineScope()

    val x1 = remember { Animatable(0.3f) }
    val y1 = remember { Animatable(0.3f) }
    val x2 = remember { Animatable(0.7f) }
    val y2 = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                x1.animateTo(x1.generateNextTarget(), tween(20000, easing = LinearEasing))
                delay(500)
            }
        }
        scope.launch {
            while (true) {
                y1.animateTo(y1.generateNextTarget(), tween(22000, easing = LinearEasing))
                delay(500)
            }
        }
        scope.launch {
            while (true) {
                x2.animateTo(x2.generateNextTarget(), tween(25000, easing = LinearEasing))
                delay(500)
            }
        }
        scope.launch {
            while (true) {
                y2.animateTo(y2.generateNextTarget(), tween(23000, easing = LinearEasing))
                delay(500)
            }
        }
    }

    val blueGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF4986EA), Color(0xFF5B83B1)),
        center = Offset(x1.value * 1000f, y1.value * 1000f),
        radius = 800f
    )

    val overlayGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF9EC9FF), Color(0x004986EA)),
        center = Offset(x2.value * 1000f, y2.value * 1000f),
        radius = 1000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(blueGradient)
            .blur(80.dp)
            .zIndex(-1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayGradient)
                .blur(50.dp)
        )
    }
}
