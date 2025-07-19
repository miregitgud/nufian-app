package com.example.nufianapp.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.nufianapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nufianapp.main.navigation.ScreenCustom
import com.example.nufianapp.presentation.screens.onboarding.viewmodel.OnboardingViewModel
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun PreAuthScreen(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel,
) {
    // Animation state to control the visibility of inner content
    val contentVisible = remember { mutableStateOf(false) }

    // Start the animation when the screen is composed
    LaunchedEffect(Unit) {
        // Small delay to ensure the background is drawn first
        delay(100)
        contentVisible.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue)
    ) {
        // Animated content
        AnimatedVisibility(
            visible = contentVisible.value,
            enter = fadeIn(animationSpec = tween(500)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.systemBarsPadding()
                ) {
                    TextButton(
                        onClick = {
                            navController.navigate(ScreenCustom.OnboardingScreenCustom.route)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Restart",
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Main content column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_logo_app),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(500.dp)
                            .padding(top = 40.dp, bottom = 10.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "Nufian Connect",
                        textAlign = TextAlign.Center,
                        color = White,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 40.dp)
                    )

                    Text(
                        text = "Be part of the\ncommunity, today.",
                        textAlign = TextAlign.Center,
                        color = White,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            handlePermissionGranted(navController, onboardingViewModel)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 32.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0A2E5A)
                        )
                    ) {
                        Text(
                            text = "Let's go!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun handlePermissionGranted(navController: NavHostController, onboardingViewModel: OnboardingViewModel) {
    onboardingViewModel.saveOnBoardingState(completed = true)
    navController.popBackStack()
    navController.navigate(ScreenCustom.AuthScreenCustom.route)
}