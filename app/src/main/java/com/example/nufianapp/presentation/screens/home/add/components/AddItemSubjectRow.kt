package com.example.nufianapp.presentation.screens.home.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.nufianapp.presentation.core.components.ImageAvatarUrlPreview
import com.example.nufianapp.ui.theme.DisabledColor
import com.example.nufianapp.ui.theme.Graphite

@Composable
fun AddItemSubjectRow(
    modifier: Modifier = Modifier,
    avatarUrl: String?,
    userType: String,
    subjectState: String,
    onSubjectChange: (String) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageAvatarUrlPreview(
            avatarUrl = avatarUrl,
            isAdmin = userType == "admin",
        )
        Spacer(modifier = modifier.width(8.dp))
        TextField(
            value = subjectState,
            onValueChange = onSubjectChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Graphite,
                unfocusedTextColor = DisabledColor.copy(0.4f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            placeholder = {
                Text(
                    text = "Enter your post title here ...",
                    color = Graphite
                )
            },
        )
    }
}