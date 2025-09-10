package com.example.nufianapp.presentation.screens.news.add.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.utils.Utils.Companion.showMessage
import com.example.nufianapp.presentation.core.content.ContentResponseLoading

@Composable
fun AddNews(
    viewModel: AddNewsViewModel = hiltViewModel(),
    navigateToNews: () -> Unit
) {
    val context = LocalContext.current

    when (val addNewsResponse = viewModel.addNewsResponse) {
        is Response.Loading -> ContentResponseLoading()
        is Response.Success<*> -> {
            val isAddNewsSuccess = addNewsResponse.data
            LaunchedEffect(isAddNewsSuccess) {
                if (isAddNewsSuccess as Boolean) {
                    showMessage(context, "News added successfully")
                    navigateToNews()
                }
            }
        }

        is Response.Failure -> {
            val error = addNewsResponse.e.message ?: "Unknown error occurred"
            LaunchedEffect(error) {
                showMessage(context, error)
                print(error)
            }
        }
    }
}
