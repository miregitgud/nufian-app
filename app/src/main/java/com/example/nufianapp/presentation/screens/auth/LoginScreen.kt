package com.example.nufianapp.presentation.screens.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nufianapp.R
import com.example.nufianapp.main.navigation.ScreenCustom
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.DisabledColor
import com.example.nufianapp.ui.theme.NeonWhite
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.White
import com.example.nufianapp.ui.theme.textBackground
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    navController: NavController,
    tabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    isVisible: Boolean = true,
    slideOffset: Int = 0
) {

    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                painter = painterResource(id = R.drawable.loginillust),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopStart),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.login_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
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

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color.White)
                ) {
                    TabRow(
                        selectedTabIndex = tabIndex,
                        containerColor = NeonWhite,
                        contentColor =
                        if (tabIndex == 0) Blue
                        else Orange,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[tabIndex])
                                    .fillMaxWidth(),
                                height = 3.dp,
                                color =
                                if (tabIndex == 0) Blue
                                else Orange
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
                                            if (index == 0) Color(0xFF4A85D5) else Color(0xFFE4A676)
                                        } else Color.Gray,
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier
                                            .padding(top = 10.dp, bottom = 10.dp)
                                    )
                                }
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(32.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp))
                {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email or Username", color = Color.LightGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
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
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
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

                    // Password input
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = DisabledColor) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
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
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description = if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = description,
                                    tint = Color.LightGray
                                )
                            }
                        },
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
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    val input = email.trim()

                                    // 1. Convert username to email if needed
                                    val actualEmail = if (input.contains("@")) {
                                        input
                                    } else {
                                        val usernameDoc = Firebase.firestore.collection("usernames").document(input).get().await()
                                        if (!usernameDoc.exists()) {
                                            Toast.makeText(context, "Username not found", Toast.LENGTH_SHORT).show()
                                            isLoading = false
                                            return@launch
                                        }
                                        usernameDoc.getString("email") ?: run {
                                            Toast.makeText(context, "Email not found for this username", Toast.LENGTH_SHORT).show()
                                            isLoading = false
                                            return@launch
                                        }
                                    }

                                    // 2. Sign in
                                    val authResult = auth.signInWithEmailAndPassword(actualEmail, password).await()
                                    val user = authResult.user

                                    if (user != null) {
                                        // 3. Check banned status
                                        val userDoc = Firebase.firestore.collection("users").document(user.uid).get().await()
                                        val isBanned = userDoc.getBoolean("isBanned") ?: false

                                        if (isBanned) {
                                            auth.signOut()
                                            navController.navigate(ScreenCustom.BannedScreenCustom.route) {
                                                popUpTo(0)
                                            }
                                            return@launch
                                        }

                                        // 4. Check email verification
                                        if (!user.isEmailVerified) {
                                            user.sendEmailVerification().await()
                                            auth.signOut()
                                            Toast.makeText(
                                                context,
                                                "Email not verified. A new verification link has been sent to your email.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            // 5. Login success, update FCM, navigate
                                            try {
                                                val token = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
                                                Firebase.firestore.collection("users").document(user.uid)
                                                    .update("fcmToken", token).await()
                                            } catch (_: Exception) { }

                                            navController.navigate(ScreenCustom.HomeScreenCustom.route) {
                                                popUpTo(0)
                                            }
                                        }
                                    }

                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                                } catch (e: FirebaseAuthInvalidUserException) {
                                    Toast.makeText(context, "No account found with this email", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "You have been banned.", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White
                        ),
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Login",
                                color = Blue,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                }

            }
        }
    }
}