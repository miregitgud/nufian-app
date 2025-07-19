package com.example.nufianapp.presentation.screens.home.add.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun AddItemContentInput(contentState: String, onContentChange: (String) -> Unit) {
    OutlinedTextField(
        value = contentState,
        onValueChange = onContentChange,
        label = { Text(text = "What do you want to talk about?") },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        singleLine = false,
    )
}
