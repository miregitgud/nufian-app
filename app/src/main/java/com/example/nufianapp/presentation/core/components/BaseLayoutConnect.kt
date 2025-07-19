package com.example.nufianapp.presentation.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nufianapp.ui.theme.Orange

@Composable
fun BaseLayoutConnect() {
    val animationSpec = remember {
        infiniteRepeatable(
            animation = keyframes {
                durationMillis = 10000
                0f at 0
                1f at 5000
                0f at 10000
            },
            repeatMode = RepeatMode.Restart
        )
    }

    val sizeTransition = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        sizeTransition.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }

    val size = sizeTransition.value
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .blur(80.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = (-50).dp)
        ) {
            CircleShapeConnect(
                modifier = Modifier.offset(x = (175).dp, y = (-250).dp),
                size = size,
                color = Magenta
            )
            CircleShapeConnect(
                modifier = Modifier.offset(x = (275).dp, y = (-150).dp),
                size = size,
                color = Magenta
            )
        }
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = (-50).dp)
        ) {
            CircleShapeConnect(
                modifier = Modifier.offset(x = (-250).dp, y = (600).dp),
                size = size,
                color = Orange
            )
        }

    }
}

@Composable
private fun CircleShapeConnect(modifier: Modifier, size: Float, color: Color) {
    val defaultSize = 425.dp
    val enlargedSize = 450.dp

    Box(
        modifier = modifier
            .size((defaultSize + (enlargedSize - defaultSize) * size))
            .background(color)
            .clip(CircleShape)
    ) {}
}