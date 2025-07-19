package com.example.nufianapp.presentation.screens.onboarding.components

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.nufianapp.R
import com.example.nufianapp.main.navigation.ScreenCustom
import com.example.nufianapp.ui.theme.Blue

@Composable
fun ButtonSection(
    pagerState: PagerState,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val isLastPage = pagerState.currentPage == pagerState.pageCount - 1
    var buttonVisible by remember { mutableStateOf(false) }
    var triggerExpand by remember { mutableStateOf(false) }

    // Track if we've been on the last page
    var wasOnLastPage by remember { mutableStateOf(false) }

    val scaleAnim by animateFloatAsState(
        targetValue = if (buttonVisible) 1f else 0f,
        animationSpec = tween(500)
    )

    val expandScale by animateFloatAsState(
        targetValue = if (triggerExpand) 40f else 0f,
        animationSpec = tween(durationMillis = 600),
        finishedListener = {
            if (triggerExpand) {
                navController.navigate(ScreenCustom.PreAuthScreenCustom.route)
            }
        }
    )

    val fabSize = 60.dp

    val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        triggerExpand = true
    }

    // Show button when on last page
    LaunchedEffect(isLastPage) {
        if (isLastPage) {
            buttonVisible = true
            wasOnLastPage = true
        } else if (wasOnLastPage) {
            // Only hide button when leaving the last page
            buttonVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 20.dp, bottom = 20.dp)
    ) {
        // Expanding circle from FAB position
        if (triggerExpand) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
                    .size(fabSize)
                    .graphicsLayer {
                        scaleX = expandScale
                        scaleY = expandScale
                    }
                    .background(Blue, shape = CircleShape)
            )
        }

        // Only show button on last page or when animating out
        if (isLastPage || (wasOnLastPage && !buttonVisible)) {
            val shape = if (triggerExpand) CircleShape else RoundedCornerShape(14.dp)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
                    .size(fabSize)
                    .graphicsLayer {
                        scaleX = if (triggerExpand) expandScale else scaleAnim
                        scaleY = if (triggerExpand) expandScale else scaleAnim
                    }
                    .background(color = Blue, shape = shape)
                    .then(
                        Modifier.let {
                            if (!triggerExpand && isLastPage) it.clickable {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    requestMultiplePermissionsLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.POST_NOTIFICATIONS,
                                            android.Manifest.permission.CAMERA
                                        )
                                    )
                                } else {
                                    requestMultiplePermissionsLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                                            android.Manifest.permission.CAMERA
                                        )
                                    )
                                }
                            } else it
                        }
                    )
            ) {
                if (!triggerExpand && isLastPage) {
                    Image(
                        painter = painterResource(R.drawable.arrow_next),
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}