package com.example.nufianapp.presentation.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nufianapp.R
import com.example.nufianapp.presentation.screens.splash.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToOnboarding: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToSetPassword: (String) -> Unit,
    deepLinkOobCode: String?,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    var showSplash by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (showSplash) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Splash Alpha"
    )

    LaunchedEffect(Unit) {
        delay(3000) // Wait for Lottie animation or splash delay
        showSplash = false // Start fade out
        delay(1000) // Wait for fade-out animation to finish

        // Perform navigation
        when {
            deepLinkOobCode != null -> navigateToSetPassword(deepLinkOobCode)
            !viewModel.isOnboardingComplete.value -> navigateToOnboarding()
            viewModel.authState.value -> navigateToSignIn()
            viewModel.isEmailVerified -> navigateToHome()
        }
    }

    // Splash UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .alpha(alpha),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoaderAnimation(
            modifier = Modifier.size(100.dp),
            anim = R.raw.test
        )
    }
}

@Composable
fun LoaderAnimation(modifier: Modifier, anim: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(anim))

    LottieAnimation(
        composition = composition,
        iterations = 1,
        modifier = modifier
    )
}
