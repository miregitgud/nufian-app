package com.example.nufianapp.presentation.screens.home.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.utils.Utils.Companion.showMessage
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.screens.home.add.viewmodel.AddForumViewModel

@Composable
fun AddForum(
    viewModel: AddForumViewModel = hiltViewModel(),
    navigateToForum: () -> Unit
) {
    val context = LocalContext.current

    when (val addForumResponse = viewModel.addForumResponse) {
        is Response.Loading -> ContentResponseLoading()
        is Response.Success<*> -> {
            val isAddForumSuccess = addForumResponse.data
            LaunchedEffect(isAddForumSuccess) {
                if (isAddForumSuccess as Boolean) {
                    showMessage(context, "Forum added successfully")
                    navigateToForum()
                }
            }
        }

        is Response.Failure -> {
            val error = addForumResponse.e.message ?: "Unknown error occurred"
            LaunchedEffect(error) {
                showMessage(context, error)
                print(error)
            }
        }
    }
}