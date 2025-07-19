package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.White

@Composable
fun DetailForumButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    text: String,
    iconPainter: Painter,
    contentDescription: String,
    isEnable: Boolean = true
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) White else Graphite
            )
        },
        leadingIcon = {
            Icon(
                painter = iconPainter,
                contentDescription = contentDescription,
                tint = if (isSelected) White else Graphite
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Graphite,
            selectedLabelColor = White,
            selectedLeadingIconColor = White,
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = Graphite,
            selectedBorderColor = Graphite,
            selected = isSelected,
            enabled = true
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = isEnable
    )
}