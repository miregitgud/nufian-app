package com.example.nufianapp.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.R
import com.example.nufianapp.presentation.screens.auth.viewmodel.AuthViewModel
import com.example.nufianapp.ui.theme.*
import android.widget.Toast

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSetPassword: () -> Unit,
    tabIndex: Int,
    isVisible: Boolean = true,
    slideOffset: Int = 0,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>
) {
    val context = LocalContext.current
    val signupState by viewModel.signupState.collectAsState()

    var emailLocalPart by remember { mutableStateOf("") }
    val suffixes = listOf("@student.nurulfikri.ac.id", "@nurulfikri.ac.id", "@gmail.com")
    val placeholderSuffix = "Choose your email type"
    var selectedSuffix by remember { mutableStateOf(placeholderSuffix) }

    val manuallyEnteredFullEmail = emailLocalPart.contains("@")

    val email = if (manuallyEnteredFullEmail) emailLocalPart
    else if (selectedSuffix == placeholderSuffix) ""
    else emailLocalPart + selectedSuffix

    val showSuffix = !manuallyEnteredFullEmail

    var showPostSignupMessage by remember { mutableStateOf(false) }
    var checkingVerification by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    val isEmailValid =
        manuallyEnteredFullEmail || (emailLocalPart.isNotBlank() && selectedSuffix != placeholderSuffix)

    var hasClickedSignUp by remember { mutableStateOf(false) }
    // Track if signup was successful to disable the button
    var signupSuccessful by remember { mutableStateOf(false) }

    // Get verification state from ViewModel
    val verificationState by viewModel.verificationState.collectAsState()

    LaunchedEffect(signupState.isSuccess) {
        if (signupState.isSuccess) {
            signupSuccessful = true // Mark signup as successful
            showPostSignupMessage = true
            Toast.makeText(
                context,
                "A verification email has been sent. Please verify your email and then continue.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Show resend success message
    LaunchedEffect(verificationState.resendSuccess) {
        if (verificationState.resendSuccess) {
            Toast.makeText(context, "Verification email sent again!", Toast.LENGTH_SHORT).show()
            viewModel.resetVerificationState()
        }
    }

    // Show resend error message
    LaunchedEffect(verificationState.resendError) {
        verificationState.resendError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.resetVerificationState()
        }
    }

    // Show error messages using Toast
    LaunchedEffect(signupState.errorMessage) {
        signupState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { slideOffset },
                animationSpec = tween(500)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -slideOffset },
                animationSpec = tween(500)
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.registerillust),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopStart),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush = textBackground)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 300.dp, 16.dp, 48.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.regist_title),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 16.dp),
                    color = White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.regist_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = NeonWhite,
                        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                        .background(Color.White)
                ) {
                    TabRow(
                        selectedTabIndex = tabIndex,
                        containerColor = NeonWhite,
                        contentColor = if (tabIndex == 0) Blue else Orange,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                                height = 3.dp,
                                color = if (tabIndex == 0) Blue else Orange
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = tabIndex == index,
                                onClick = { onTabSelected(index) },
                                text = {
                                    Text(
                                        text = title,
                                        color = if (tabIndex == index) {
                                            if (index == 0) Blue else Orange
                                        } else Charcoal,
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = emailLocalPart,
                        onValueChange = { emailLocalPart = it.trim() },
                        label = { Text("Email", color = Color.LightGray) },
                        trailingIcon = {
                            if (showSuffix) {
                                Box {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = 12.dp,
                                            bottomStart = 12.dp
                                        ),
                                        shadowElevation = 4.dp,
                                        tonalElevation = 2.dp,
                                        color = Color(0xFFF0F0F0),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { expanded = true }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (selectedSuffix == placeholderSuffix) placeholderSuffix else selectedSuffix,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = if (selectedSuffix == placeholderSuffix) Color.Gray else Color.DarkGray
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Choose suffix",
                                                tint = Color.Gray
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        suffixes.forEach { suffix ->
                                            DropdownMenuItem(
                                                text = { Text(suffix) },
                                                onClick = {
                                                    selectedSuffix = suffix
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = false
                            )
                            .background(
                                color = White,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedTextColor = Color.Black,
                            focusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            hasClickedSignUp = true
                            viewModel.signUpWithCampusEmail(email)
                        },
                        // Disable button if signup was successful OR if email is invalid OR if loading
                        enabled = isEmailValid && !signupState.isLoading && !signupSuccessful,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (signupSuccessful) DisabledColor else White,
                            disabledContainerColor = DisabledColor
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        if (signupState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Signing up...")
                        } else if (signupSuccessful) {
                            Text(
                                "Email Sent",
                                color = White,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        } else {
                            Text(
                                "Sign Up",
                                color = Blue,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (hasClickedSignUp && signupState.isSuccess) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            // "I have verified" button
                            TextButton(
                                onClick = {
                                    checkingVerification = true
                                    viewModel.checkEmailVerificationStatus(
                                        onVerified = {
                                            checkingVerification = false
                                            onNavigateToSetPassword()
                                        },
                                        onNotVerified = { message ->
                                            checkingVerification = false
                                            Toast.makeText(context, message, Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    )
                                },
                                enabled = !checkingVerification,
                            ) {
                                if (checkingVerification) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Checking...", color = Blue)
                                } else {
                                    Text("I have verified", color = Blue)
                                }
                            }

                            // Resend verification button with countdown
                            TextButton(
                                onClick = {
                                    viewModel.resendVerificationEmail()
                                },
                                enabled = verificationState.canResend && !verificationState.isResending
                            ) {
                                if (verificationState.isResending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending...", color = Blue)
                                } else if (verificationState.canResend) {
                                    Text("Resend verification email", color = Blue)
                                } else {
                                    Text(
                                        "Resend verification email (${verificationState.countdownSeconds}s)",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}