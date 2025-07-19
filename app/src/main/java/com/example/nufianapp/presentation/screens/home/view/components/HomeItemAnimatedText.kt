package com.example.nufianapp.presentation.screens.home.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nufianapp.ui.theme.NeonWhite
import kotlinx.coroutines.delay

@Composable
fun HomeItemAnimatedText(
    firstText: String,
    secondText: String,
    lineDelayMillis: Int = 500
) {
    var showFirstLine by remember { mutableStateOf(false) }
    var showSecondLine by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(lineDelayMillis.toLong())
        showFirstLine = true
        delay(lineDelayMillis.toLong())
        showSecondLine = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            AnimatedVisibility(visible = showFirstLine, enter = fadeIn()) {
                Text(
                    text = firstText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonWhite,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedVisibility(visible = showSecondLine, enter = fadeIn()) {
                Text(
                    text = secondText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonWhite,
                )
            }
        }
    }
}

