package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.DisabledColor
import com.example.nufianapp.ui.theme.Graphite

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textButton: String,
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonColors(
            containerColor = Blue,
            contentColor = Color.White,
            disabledContentColor = Color.White,
            disabledContainerColor = DisabledColor
        ),
        elevation = ButtonDefaults.buttonElevation(4.dp),
        enabled = enabled,
    ) {
        Text(
            text = textButton,
            fontSize = 15.sp
        )
    }
}