package com.example.nufianapp.presentation.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import androidx.navigation.NavHostController
import com.example.nufianapp.data.model.getOnboardingData
import com.example.nufianapp.presentation.screens.onboarding.components.ButtonSection
import com.example.nufianapp.presentation.screens.onboarding.components.PageIndicator
import com.example.nufianapp.ui.theme.DarkBlueGradient
import com.example.nufianapp.ui.theme.OrangeGradient
import com.example.nufianapp.ui.theme.RedGradient
import com.example.nufianapp.ui.theme.textBackground
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    navController: NavHostController,
) {
    val onboardingData = getOnboardingData()
    val animations = onboardingData.animations
    val titles = onboardingData.titles
    val subtitles = onboardingData.subtitles
    val indicatorColors = onboardingData.indicatorColors


    // List of background gradients to cycle through
    val backgroundGradients = listOf(
        DarkBlueGradient,
        OrangeGradient,
        RedGradient
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { titles.size }
    )

    var visible by remember { mutableStateOf(false) }
    val currentPageOffset = pagerState.currentPageOffsetFraction
    val transitionProgress = 1f - abs(currentPageOffset)
    val contentAlpha = max(0f, transitionProgress)

    // Determine which gradients to show during transition
    val currentPage = pagerState.currentPage
    val isSwipingForward = currentPageOffset < 0

    // Calculate which page we're swiping towards
    val nextPageIndex = if (isSwipingForward) {
        if (currentPage < animations.size - 1) currentPage + 1 else 0
    } else {
        if (currentPage > 0) currentPage - 1 else animations.size - 1
    }

    // Calculate opacity values for both current and next background gradients
    val currentBackgroundAlpha = contentAlpha
    val nextBackgroundAlpha = 1f - contentAlpha

    LaunchedEffect(Unit) {
        visible = true
    }

    // Track if user has interacted with the pager
    var userHasInteracted by remember { mutableStateOf(false) }

    // Only show the nudge animation on the first page if user hasn't interacted
    LaunchedEffect(pagerState.currentPage) {
        // If page changes from 0, user has interacted
        if (pagerState.currentPage > 0) {
            userHasInteracted = true
        }
    }

    // Nudge animation only on first page and only if user hasn't interacted
    LaunchedEffect(Unit) {
        // Wait a moment before showing the hint
        delay(3000)

        // Only animate if we're still on the first page and user hasn't interacted
        if (pagerState.currentPage == 0 && !userHasInteracted) {
            pagerState.animateScrollBy(70f)
            delay(150)
            pagerState.animateScrollBy(-70f)

            // Wait and repeat the nudge once more if still no interaction
            delay(2000)
            if (pagerState.currentPage == 0 && !userHasInteracted) {
                pagerState.animateScrollBy(70f)
                delay(150)
                pagerState.animateScrollBy(-70f)
            }
        }
    }

    // Mark as interacted when user swipes
    LaunchedEffect(currentPageOffset) {
        if (currentPageOffset != 0f) {
            userHasInteracted = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000))
    ) {
        Box(modifier = Modifier
            .fillMaxSize()){
            // Current background gradient - fades out during swipe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(currentBackgroundAlpha)
                    .background(brush = backgroundGradients[currentPage % backgroundGradients.size])
            )

            // Next background gradient - fades in during swipe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(nextBackgroundAlpha)
                    .background(brush = backgroundGradients[nextPageIndex % backgroundGradients.size])
            )

            // Background images with pager - full screen
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { currentPage ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomStart)
                        .alpha(contentAlpha) // Apply fade effect to images
                ) {
                    when (currentPage) {
                        0 -> {
                            Image(
                                painter = painterResource(animations[currentPage]),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxHeight(0.9f)
                                    .align(Alignment.BottomStart),
                                contentScale = ContentScale.Crop
                            )
                        }
                        1 -> {
                            Image(
                                painter = painterResource(animations[currentPage]),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxHeight(0.9f)
                                    .align(Alignment.BottomEnd),
                                contentScale = ContentScale.Crop
                            )
                        } 2 -> {
                        Image(
                            painter = painterResource(animations[currentPage]),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .fillMaxHeight(0.7f)
                                .align(Alignment.BottomStart),
                            contentScale = ContentScale.Crop
                        )
                    }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = textBackground)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 26.dp)
                        .systemBarsPadding(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(contentAlpha) // Use the same alpha for text
                            .padding(bottom = 130.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = titles[pagerState.currentPage],
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White,
                                    textAlign = TextAlign.Start
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = subtitles[pagerState.currentPage],
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp)
                        .systemBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Page indicators
                    PageIndicator(
                        pageCount = animations.size,
                        currentPage = pagerState.currentPage,
                        indicatorColors = indicatorColors,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                ButtonSection(
                    pagerState = pagerState,
                    navController = navController,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .systemBarsPadding()
                )
            }
        }
    }
}