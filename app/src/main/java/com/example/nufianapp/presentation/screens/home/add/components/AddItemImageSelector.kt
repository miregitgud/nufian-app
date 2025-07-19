package com.example.nufianapp.presentation.screens.home.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nufianapp.ui.theme.Blue

@Composable
fun AddItemImageSelector(modifier: Modifier, onClick: () -> Unit, icon: Int) {
    Box(modifier = modifier
        .clickable {
            onClick()
        }
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Blue)
        .aspectRatio(1f), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
        )
    }
}