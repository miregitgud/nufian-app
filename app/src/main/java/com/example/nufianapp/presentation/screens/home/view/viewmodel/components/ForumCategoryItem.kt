package com.example.nufianapp.presentation.screens.home.view.viewmodel.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nufianapp.data.model.ForumCategory
import com.example.nufianapp.presentation.core.Utils
import com.example.nufianapp.ui.theme.NeonWhite

@Composable
fun ForumCategoryItem(
    forumCategory: ForumCategory,
    selected: Boolean,
    onSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    userType: String
) {
    val selectedContainerColor = if (selected) {
        Utils().getColorForTopic(forumCategory.name)
    } else {
        NeonWhite
    }

    val selectedLabelColor = if (selected) {
        NeonWhite
    } else {
        Utils().getColorForTopic(forumCategory.name)
    }

    FilterChip(
        modifier = modifier,
        onClick = { onSelected(!selected) },
        border = FilterChipDefaults.filterChipBorder(
            borderColor = Color.Transparent,
            selected = selected,
            enabled = true
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = selectedContainerColor,
            containerColor = selectedContainerColor
        ),
        label = {
            Text(
                text = forumCategory.name,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                color = selectedLabelColor,
            )
        },
        shape = RoundedCornerShape(16.dp),
        selected = selected,
    )
}
