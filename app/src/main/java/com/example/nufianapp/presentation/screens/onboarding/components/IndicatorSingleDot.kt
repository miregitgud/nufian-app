package com.example.nufianapp.presentation.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun IndicatorSingleDot(
    isSelected: Boolean,
    color: Color
) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}