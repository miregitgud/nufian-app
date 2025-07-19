package com.example.nufianapp.presentation.screens.home.view.viewmodel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nufianapp.data.model.ForumCategory

@Composable
fun CategoryList(
    categories: List<ForumCategory>,
    selectedCategory: ForumCategory,
    onCategorySelected: (ForumCategory) -> Unit,
    modifier: Modifier = Modifier,
    userType: String,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(categories) { category ->
            ForumCategoryItem(
                forumCategory = category,
                selected = category == selectedCategory,
                onSelected = { onCategorySelected(category) },
                userType = userType,
            )
        }
    }
}