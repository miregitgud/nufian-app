package com.example.nufianapp.presentation.screens.home.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import kotlinx.coroutines.delay

/**
 * A welcome screen that is displayed when users first launch the app.
 * It shows a loading animation first, then displays a greeting with the user's name
 * and requires an upward scroll to transition to the home screen.
 *
 * This improved version uses a simpler and more reliable gesture detection approach.
 */
@Composable
fun WelcomeScreen(
    userName: String,
    onUpwardScroll: () -> Unit,
    modifier: Modifier = Modifier,
    loadingDuration: Long = 2000,
    isFirstLogin: Boolean
) {
    // State to track if content is loaded
    var isContentLoaded by remember { mutableStateOf(false) }

    // State to track if scroll has been detected (to prevent multiple triggers)
    var hasScrolledUp by remember { mutableStateOf(false) }

    val greetingText = if (isFirstLogin) "Welcome," else "Welcome back,"
    val subtitleText = if (isFirstLogin) "We're excited to have you here." else "Let's start exploring."


    // Add debug logging for troubleshooting
    LaunchedEffect(isContentLoaded) {
        println("Welcome screen content loaded: $isContentLoaded")
    }

    LaunchedEffect(Unit) {
        println("Gesture detector initialized: isContentLoaded=$isContentLoaded, hasScrolledUp=$hasScrolledUp")
    }

    // Simulate content loading
    LaunchedEffect(key1 = Unit) {
        delay(loadingDuration)
        isContentLoaded = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            // Using a simpler vertical drag detection that's more reliable
            .pointerInput(isContentLoaded, hasScrolledUp) {
                if (isContentLoaded && !hasScrolledUp) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            if (dragAmount < -10f && !hasScrolledUp) {
                                println("Detected upward swipe with amount: $dragAmount")
                                hasScrolledUp = true
                                onUpwardScroll()
                            }
                        }
                    )
                }
            }
    ) {
        // Show loading animation while content is loading
        AnimatedVisibility(
            visible = !isContentLoaded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ContentResponseLoading()
        }

        // Show welcome content after loading is complete
        AnimatedVisibility(
            visible = isContentLoaded,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut()
        ) {
            WelcomeContent(
                greetingText = greetingText,
                userName = userName,
                subtitleText = subtitleText
            )
        }

        // Manual trigger button for testing/accessibility
        // Make this a translucent swipe area at the bottom of the screen
        if (isContentLoaded && !hasScrolledUp) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            println("Gesture dragAmount: $dragAmount, isContentLoaded=$isContentLoaded, hasScrolledUp=$hasScrolledUp")
                            if (isContentLoaded && !hasScrolledUp && dragAmount < -10f) {
                                println("Detected upward swipe with amount: $dragAmount")
                                hasScrolledUp = true
                                onUpwardScroll()
                            }
                        }
                    }

            ) {
                // This is just a touch target, it doesn't need content
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun WelcomeContent(
    greetingText: String,
    userName: String,
    subtitleText: String
) {
    // Animation for the scroll indicator
    val infiniteTransition = rememberInfiniteTransition(label = "scrollIndicator")
    val arrowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome message
            Text(
                text = greetingText,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
            Text(
                text = "$userName.",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = subtitleText,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }

        // Scroll indicator at the bottom with improved visibility
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Swipe up to continue",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Up arrow with pulsing animation
            Icon(
                painter = painterResource(id = android.R.drawable.arrow_up_float),
                contentDescription = "Swipe up",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .alpha(arrowAlpha)
            )
        }
    }
}