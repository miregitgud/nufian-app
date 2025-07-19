package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nufianapp.R
import com.example.nufianapp.ui.theme.DisabledColor
import com.example.nufianapp.ui.theme.Graphite

@Composable
fun ButtonIcon(
    modifier: Modifier = Modifier,
    onClickButton: () -> Unit,
    background: Color = Color.Transparent,
    tint: Color = Graphite,
    iconRes: Int = R.drawable.icon_navigation_back,
) {
    IconButton(
        modifier = modifier
            .size(48.dp)
        ,
        colors = IconButtonColors(
            containerColor = background,
            contentColor = tint,
            disabledContentColor = DisabledColor,
            disabledContainerColor = DisabledColor
        ),
        onClick = { onClickButton() }
    ) {
        Icon(
            tint = tint,
            painter = painterResource(id = iconRes),
            contentDescription = "Icon",
            modifier = Modifier
                .padding(12.dp)
        )
    }
}