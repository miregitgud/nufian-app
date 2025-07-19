package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.DisabledColor

@Composable
fun PrimaryButtonSmall(
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
            .height(36.dp),
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
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}