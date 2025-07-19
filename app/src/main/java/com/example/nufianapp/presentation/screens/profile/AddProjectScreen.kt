package com.example.nufianapp.presentation.screens.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.presentation.core.components.CustomTextFieldLabel
import com.example.nufianapp.presentation.core.components.PrimaryButton
import com.example.nufianapp.presentation.screens.home.detail.components.ButtonNavigateUpTitle
import com.example.nufianapp.presentation.screens.profile.viewmodel.AddProjectViewModel

@Composable
fun AddProjectScreen(
    navigateBack: () -> Unit,
    viewModel: AddProjectViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }

    var projectName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var projectOwner by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var linkProject by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isFormValid by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                ButtonNavigateUpTitle(title = "Add Project", navigateBack = navigateBack)
                Spacer(modifier = Modifier.height(16.dp))
                BoxUploadCertificate(
                    imageUri = imageUri,
                    onImageSelected = { selectedImageUri ->
                        imageUri = selectedImageUri
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AddData(
                    projectName = projectName,
                    onProjectNameChange = { newValue -> projectName = newValue },
                    projectOwner = projectOwner,
                    onProjectOwnerChange = { newValue -> projectOwner = newValue },
                    description = description,
                    onDescriptionChange = { newValue -> description = newValue },
                    linkProject = linkProject,
                    onLinkProjectChange = { newValue -> linkProject = newValue }
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Check if any field is empty or password and confirm password do not match
                isFormValid = projectName.text.isNotEmpty() &&
                        projectOwner.text.isNotEmpty() &&
                        description.text.isNotEmpty() &&
                        linkProject.text.isNotEmpty() && imageUri != null

                PrimaryButton(
                    onClick = {
                        if (isFormValid) {
                            viewModel.addProject(
                                Project(
                                    projectName = projectName.text,
                                    projectOwner = projectOwner.text,
                                    description = description.text,
                                    linkProject = linkProject.text,
                                    projectImageUri = imageUri
                                )
                            )
                        }
                    },
                    enabled = isFormValid,
                    textButton = "Save", modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    AddProject(navigateToHome = navigateBack)

    LaunchedEffect(viewModel.snackBarFlow) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun AddData(
    modifier: Modifier = Modifier,
    projectName: TextFieldValue,
    onProjectNameChange: (TextFieldValue) -> Unit,
    projectOwner: TextFieldValue,
    onProjectOwnerChange: (TextFieldValue) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit,
    linkProject: TextFieldValue,
    onLinkProjectChange: (TextFieldValue) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomTextFieldLabel(
            textFieldValue = projectName,
            onTextFieldValueChange = onProjectNameChange,
            textFieldLabel = "Project Name",
            placeholder = "Enter your project name",
            isNotEmpty = true
        )
        CustomTextFieldLabel(
            textFieldValue = projectOwner,
            onTextFieldValueChange = onProjectOwnerChange,
            textFieldLabel = "Project Owner",
            placeholder = "Enter your project owner name",
            isNotEmpty = true
        )
        CustomTextFieldLabel(
            textFieldValue = description,
            onTextFieldValueChange = onDescriptionChange,
            textFieldLabel = "Description",
            placeholder = "Enter your project description",
            isNotEmpty = true
        )
        CustomTextFieldLabel(
            textFieldValue = linkProject,
            onTextFieldValueChange = onLinkProjectChange,
            textFieldLabel = "Link Project",
            placeholder = "Enter your project link",
            isNotEmpty = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddProjectActivityPreview() {
    AddProjectScreen(navigateBack = {})
}
