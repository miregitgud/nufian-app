package com.example.nufianapp.presentation.screens.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    indicatorColors: List<List<Color>>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            IndicatorSingleDot(
                isSelected = index == currentPage,
                color = if (index == currentPage)
                    Color(0xFF4FB5FF) // Light blue for active indicator to match design
                else
                    Color.White.copy(alpha = 0.4f) // Translucent white for inactive indicators
            )
        }
    }
}