package com.example.nufianapp.presentation.screens.home.add.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nufianapp.R


@Composable
fun AddItemContentImageNull(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.img_logo_app),
        contentDescription = null,
        modifier = modifier
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .aspectRatio(1f),
        contentScale = ContentScale.Fit
    )
}