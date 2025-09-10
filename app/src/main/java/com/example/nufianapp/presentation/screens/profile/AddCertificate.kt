package com.example.nufianapp.presentation.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.utils.Utils.Companion.showMessage
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.screens.profile.viewmodel.AddCertificateViewModel

@Composable
fun AddCertificate(
    viewModel: AddCertificateViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current

    when (val addCertificateResponse = viewModel.addCertificateResponse) {
        is Response.Loading -> ContentResponseLoading()
        is Response.Success -> {
            val isAddCertificateSuccess = addCertificateResponse.data
            LaunchedEffect(isAddCertificateSuccess) {
                if (isAddCertificateSuccess) {
                    showMessage(context, "Certificate added successfully")
                    navigateToHome()
                }
            }
        }

        is Response.Failure -> {
            val error = addCertificateResponse.e.message ?: "Unknown error occurred"
            LaunchedEffect(error) {
                showMessage(context, error)
                print(error)
            }
        }
    }
}