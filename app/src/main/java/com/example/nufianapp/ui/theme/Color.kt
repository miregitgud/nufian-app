package com.example.nufianapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Charcoal = Color(0xFF181823)
val Red = Color(0xFFF43F3F)
val Orange = Color(0xFFF26A18)
val NeonWhite = Color(0xFFF0F0F0)
val ClearBlue = Color(0xFF88C7FF)
val Purple = Color(0xFFBE2CD2)
val Tosca = Color(0xFF32C7C4)
val TwitchPurple = Color(0xFF8A3DFF)
val DarkBlue = Color(0xFF214CE0)
val Yellow = Color(0xFFF5D364)
val Blue = Color(0xFF005BAB)
val Graphite = Color(0xFF5A5A5A)
val DisabledColor = Color(0xFFBFBFBF)
val White = Color(0xFFFFFFFF)
val DarkGreen = Color(0xFF008000)

val DarkBlueGradient = Brush.verticalGradient(
    listOf(Color(0xFF5B83B1), Color(0xFF465A78))
)

val OrangeGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFF9F80), // Lighter peach at top
        Color(0xFFFF7F50))  // Deeper orange at bottom)
)

val RedGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFF9AA2), // Lighter pink at top
        Color(0xFFFF6B81))
)

// Semi-transparent black background for text
val textBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0x20000000), // Transparent at top
        Color(0xF0000000)  // Semi-transparent black at bottom
    )
)




