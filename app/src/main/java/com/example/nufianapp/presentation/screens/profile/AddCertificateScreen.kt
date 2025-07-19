package com.example.nufianapp.presentation.screens.profile

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.presentation.core.components.CustomTextFieldLabel
import com.example.nufianapp.presentation.core.components.PrimaryButton
import com.example.nufianapp.presentation.screens.home.detail.components.ButtonNavigateUpTitle
import com.example.nufianapp.presentation.screens.profile.viewmodel.AddCertificateViewModel
import com.example.nufianapp.ui.theme.Blue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddCertificateScreen(
    navigateBack: () -> Unit,
    viewModel: AddCertificateViewModel = hiltViewModel()
) {
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                ""
            )
        )
    }
    var organization by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var credentialId by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var isFormValid by remember {
        mutableStateOf(false)
    }

    val snackBarHostState = remember { SnackbarHostState() }
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
                ButtonNavigateUpTitle(title = "Add Certificate", navigateBack = navigateBack)
                Spacer(modifier = Modifier.height(16.dp))
                BoxUploadCertificate(
                    imageUri = imageUri,
                    onImageSelected = { selectedImageUri ->
                        imageUri = selectedImageUri
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AddDataCertificate(
                    name = name,
                    onNameChange = { newValue -> name = newValue },
                    organization = organization,
                    onOrganizationChange = { newValue -> organization = newValue },
                    startDate = startDate,
                    onStartDateChange = { newValue -> startDate = newValue },
                    endDate = endDate,
                    onEndDateChange = { newValue -> endDate = newValue },
                    credentialId = credentialId,
                    onCredentialIdChange = { newValue -> credentialId = newValue }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Check if any field is empty or password and confirm password do not match
                isFormValid = name.text.isNotEmpty() &&
                        organization.text.isNotEmpty() &&
                        credentialId.text.isNotEmpty() &&
                        imageUri != null

                PrimaryButton(
                    onClick = {
                        if (isFormValid) {
                            viewModel.addCertificate(
                                Certificate(
                                    name = name.text,
                                    organization = organization.text,
                                    startDate = startDate,
                                    endDate = endDate,
                                    credentialId = credentialId.text,
                                    certificateImageUri = imageUri
                                )
                            )
                        }
                    },
                    enabled = isFormValid,
                    textButton = "Save", modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    AddCertificate(navigateToHome = navigateBack)

    LaunchedEffect(viewModel.snackBarFlow) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun BoxUploadCertificate(
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Image background
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected Background",
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            // Black overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
            )
        }

        // Foreground content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_item_file_upload),
                contentDescription = "File Upload",
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Text(text = "Choose file to upload", color = Color.White)
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Add media",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}



@Composable
fun AddDataCertificate(
    name: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit,
    organization: TextFieldValue,
    onOrganizationChange: (TextFieldValue) -> Unit,
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    credentialId: TextFieldValue,
    onCredentialIdChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomTextFieldLabel(
            textFieldValue = name,
            onTextFieldValueChange = onNameChange,
            textFieldLabel = "Certificate Name",
            placeholder = "Enter your certificate name",
            isNotEmpty = true,
        )
        CustomTextFieldLabel(
            textFieldValue = organization,
            onTextFieldValueChange = onOrganizationChange,
            textFieldLabel = "Issuing Organization",
            placeholder = "Enter the issuing organization",
            isNotEmpty = true,
        )
        AddDateFields(
            startDate = startDate,
            onStartDateChange = onStartDateChange,
            endDate = endDate,
            onEndDateChange = onEndDateChange
        )
        CustomTextFieldLabel(
            textFieldValue = credentialId,
            onTextFieldValueChange = onCredentialIdChange,
            textFieldLabel = "Credential ID",
            placeholder = "Enter your credential ID",
            isNotEmpty = true,
        )
    }
}

@Composable
fun AddDateFields(
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            DateInputField(
                label = "Date Issued",
                date = startDate,
                onDateChange = onStartDateChange,
                context = context,
                dateFormat = dateFormat
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            DateInputField(
                label = "Expiration Date",
                date = endDate,
                onDateChange = onEndDateChange,
                context = context,
                dateFormat = dateFormat
            )
        }
    }
}

@Composable
fun DateInputField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit,
    context: Context,
    dateFormat: SimpleDateFormat
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
    )
    OutlinedTextField(
        value = date,
        onValueChange = onDateChange,
        placeholder = { Text(text = label) },
        trailingIcon = {
            IconButton(onClick = {
                showDatePicker(context) { selectedDate ->
                    onDateChange(dateFormat.format(selectedDate))
                }
            }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date Range Icon"
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

fun showDatePicker(context: Context, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        calendar.set(selectedYear, selectedMonth, selectedDay)
        onDateSelected(calendar.time)
    }, year, month, day).show()
}

@Preview(showSystemUi = true)
@Composable
private fun AddCertificateActivityPreview() {
    AddCertificateScreen(navigateBack = {})
}
