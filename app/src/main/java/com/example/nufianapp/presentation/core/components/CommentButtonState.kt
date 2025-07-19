package com.example.nufianapp.presentation.core.components

import androidx.compose.runtime.mutableStateOf

class CommentButtonState {
    val commentState = mutableStateOf("")

    fun clearComment() {
        commentState.value = ""
    }
}