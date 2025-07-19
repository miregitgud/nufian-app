package com.example.nufianapp.presentation.screens.home.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.nufianapp.data.model.ForumCategory
import com.example.nufianapp.presentation.screens.home.view.viewmodel.components.ForumCategoryItem

@Composable
fun AddItemCategorySelection(
    categories: List<ForumCategory>,
    selectedForumCategory: ForumCategory?,
    onCategorySelected: (ForumCategory) -> Unit,
    userType: String // <-- added
) {
    val visibleCategories = categories.filterNot {
        it.name == "Official Announcement" && userType != "admin"
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        items(visibleCategories) { category ->
            ForumCategoryItem(
                forumCategory = category,
                selected = category == selectedForumCategory,
                onSelected = { isSelected ->
                    if (isSelected) onCategorySelected(category)
                },
                userType = userType // <-- passed here
            )
        }
    }
}
