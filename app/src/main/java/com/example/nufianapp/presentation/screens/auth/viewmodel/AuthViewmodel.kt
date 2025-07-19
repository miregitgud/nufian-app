    package com.example.nufianapp.presentation.screens.auth.viewmodel
    
    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.nufianapp.data.firebase.FirebaseModule
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.FirebaseAuthException
    import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
    import com.google.firebase.auth.FirebaseAuthInvalidUserException
    import com.google.firebase.auth.userProfileChangeRequest
    import com.google.firebase.firestore.SetOptions
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.tasks.await
    import kotlinx.coroutines.delay
    import java.util.UUID
    import java.util.concurrent.TimeUnit
    
    private const val TAG = "AuthViewModel"
    
    // State classes for different authentication flows
    data class LoginState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null
    )
    
    data class SignupState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null,
        val signupTimestamp: Long = 0L,
        val email: String = ""
    )
    
    data class EmailLoginState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null
    )
    
    data class EmailSignupState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null
    )
    
    data class ForgotPasswordState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null
    )
    
    data class VerificationState(
        val isResending: Boolean = false,
        val resendSuccess: Boolean = false,
        val resendError: String? = null,
        val canResend: Boolean = false,
        val countdownSeconds: Int = 0
    )
    
    class AuthViewModel : ViewModel() {
        private val auth = FirebaseModule.auth
        private val firestore = FirebaseModule.firestore
    
        // State flows for different authentication processes
        private val _loginState = MutableStateFlow(LoginState())
        val loginState: StateFlow<LoginState> = _loginState
    
        private val _signupState = MutableStateFlow(SignupState())
        val signupState: StateFlow<SignupState> = _signupState
    
        private val _emailLoginState = MutableStateFlow(EmailLoginState())
        val emailLoginState: StateFlow<EmailLoginState> = _emailLoginState
    
        private val _emailSignupState = MutableStateFlow(EmailSignupState())
        val emailSignupState: StateFlow<EmailSignupState> = _emailSignupState
    
        private val _forgotPasswordState = MutableStateFlow(ForgotPasswordState())
        val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState
    
        private val _verificationState = MutableStateFlow(VerificationState())
        val verificationState: StateFlow<VerificationState> = _verificationState
    
        // Track user cleanup jobs
        private val userCleanupJobs = mutableMapOf<String, kotlinx.coroutines.Job>()

        private val _pendingVerificationEmail = MutableStateFlow<String?>(null)
    
        // Standard email/password login
        fun login(email: String, password: String) {
            viewModelScope.launch {
                _loginState.value = LoginState(isLoading = true)
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    Log.d(TAG, "Login successful for user: ${auth.currentUser?.uid}")
                    _loginState.value = LoginState(isSuccess = true)
                } catch (e: FirebaseAuthInvalidUserException) {
                    Log.e(TAG, "User does not exist", e)
                    _loginState.value = LoginState(errorMessage = "Account does not exist. Please sign up.")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "Invalid credentials", e)
                    _loginState.value = LoginState(errorMessage = "Invalid email or password")
                } catch (e: Exception) {
                    Log.e(TAG, "Login failed", e)
                    _loginState.value = LoginState(errorMessage = e.message ?: "Login failed")
                }
            }
        }
    
        // Standard email/password sign up
        fun signUp(email: String, password: String, fullName: String) {
            viewModelScope.launch {
                _signupState.value = SignupState(isLoading = true)
                try {
                    // Create user with email and password
                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                    val user = result.user
    
                    // Update user profile with display name
                    user?.let {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = fullName
                        }
                        it.updateProfile(profileUpdates).await()
    
                        // Send email verification
                        it.sendEmailVerification().await()
    
                        // Create user document in Firestore
                        val userData = hashMapOf(
                            "fullName" to fullName,
                            "email" to email,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )
    
                        firestore.collection("users")
                            .document(it.uid)
                            .set(userData, SetOptions.merge())
                            .await()
    
                        Log.d(TAG, "User created successfully: ${it.uid}")
                        _signupState.value = SignupState(isSuccess = true)
                    } ?: run {
                        _signupState.value = SignupState(errorMessage = "Failed to create user")
                    }
                } catch (e: FirebaseAuthException) {
                    when {
                        e.message?.contains("email-already-in-use") == true -> {
                            Log.e(TAG, "Email already in use", e)
                            _signupState.value = SignupState(errorMessage = "Email already in use")
                        }
                        e.message?.contains("weak-password") == true -> {
                            Log.e(TAG, "Weak password", e)
                            _signupState.value = SignupState(errorMessage = "Password is too weak")
                        }
                        else -> {
                            Log.e(TAG, "Signup failed with FirebaseAuthException", e)
                            _signupState.value = SignupState(errorMessage = e.message)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Signup failed", e)
                    _signupState.value = SignupState(errorMessage = e.message ?: "Signup failed")
                }
            }
        }

        fun signUpWithCampusEmail(email: String) {
            viewModelScope.launch {
                _signupState.value = SignupState(isLoading = true)
                try {
                    // Check if user already exists and is unverified
                    val currentUser = auth.currentUser
                    if (currentUser?.email == email && !currentUser.isEmailVerified) {
                        // User exists but unverified, resend verification
                        resendVerificationEmail()
                        return@launch
                    }

                    val tempPassword = generateTempPassword()
                    val result = auth.createUserWithEmailAndPassword(email, tempPassword).await()

                    result.user?.let { user ->
                        // Send email verification
                        user.sendEmailVerification().await()

                        val currentTime = System.currentTimeMillis()
                        _signupState.value = SignupState(
                            isSuccess = true,
                            signupTimestamp = currentTime,
                            email = email
                        )

                        // Start verification countdown
                        startVerificationCountdown()

                        // Schedule user cleanup after 1 hour
                        scheduleUserCleanup(user.uid, email, currentTime)

                        Log.d(TAG, "Campus email signup successful: ${user.uid}")
                    } ?: run {
                        _signupState.value = SignupState(errorMessage = "Failed to create user")
                    }

                } catch (e: FirebaseAuthException) {
                    when {
                        e.message?.contains("email-already-in-use") == true -> {
                            Log.e(TAG, "Email already in use", e)
                            _signupState.value = SignupState(errorMessage = "Email already in use")
                        }
                        else -> {
                            Log.e(TAG, "Campus signup failed", e)
                            _signupState.value = SignupState(errorMessage = e.message ?: "Signup failed")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Campus signup failed", e)
                    _signupState.value = SignupState(errorMessage = e.message ?: "Signup failed")
                }
            }
        }
    
        fun resendVerificationEmail() {
            viewModelScope.launch {
                _verificationState.value = _verificationState.value.copy(isResending = true)
    
                try {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        currentUser.sendEmailVerification().await()
    
                        _verificationState.value = _verificationState.value.copy(
                            isResending = false,
                            resendSuccess = true,
                            resendError = null
                        )
    
                        // Restart countdown
                        startVerificationCountdown()
    
                        Log.d(TAG, "Verification email resent successfully")
                    } else {
                        _verificationState.value = _verificationState.value.copy(
                            isResending = false,
                            resendError = "No user found. Please sign up again."
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to resend verification email", e)
                    _verificationState.value = _verificationState.value.copy(
                        isResending = false,
                        resendError = e.message ?: "Failed to resend verification email"
                    )
                }
            }
        }
    
        private fun startVerificationCountdown() {
            viewModelScope.launch {
                _verificationState.value = _verificationState.value.copy(
                    canResend = false,
                    countdownSeconds = 60
                )
    
                // Countdown from 60 to 0
                for (i in 60 downTo 1) {
                    _verificationState.value = _verificationState.value.copy(countdownSeconds = i)
                    delay(1000)
                }
    
                // Enable resend after countdown
                _verificationState.value = _verificationState.value.copy(
                    canResend = true,
                    countdownSeconds = 0
                )
            }
        }
    
        private fun scheduleUserCleanup(userId: String, email: String, signupTime: Long) {
            // Cancel any existing cleanup job for this user
            userCleanupJobs[userId]?.cancel()
    
            // Schedule new cleanup job
            val cleanupJob = viewModelScope.launch {
                try {
                    // Wait for 1 hour
                    delay(TimeUnit.HOURS.toMillis(1))
    
                    // Check if user still exists and is unverified
                    val currentUser = auth.currentUser
                    if (currentUser?.uid == userId && !currentUser.isEmailVerified) {
                        // Delete the unverified user
                        currentUser.delete().await()
    
                        Log.d(TAG, "Cleaned up unverified user: $userId")
    
                        // Reset signup state if this was the current signup
                        if (_signupState.value.email == email) {
                            _signupState.value = SignupState()
                            _verificationState.value = VerificationState()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to cleanup user $userId", e)
                } finally {
                    // Remove job from tracking
                    userCleanupJobs.remove(userId)
                }
            }
    
            userCleanupJobs[userId] = cleanupJob
        }
    
        fun cancelUserCleanup(userId: String) {
            userCleanupJobs[userId]?.cancel()
            userCleanupJobs.remove(userId)
        }
    
        fun checkEmailVerificationStatus(onVerified: () -> Unit, onNotVerified: (String) -> Unit) {
            viewModelScope.launch {
                try {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // Reload user to get latest verification status
                        currentUser.reload().await()
    
                        if (currentUser.isEmailVerified) {
                            // Cancel cleanup since user is now verified
                            cancelUserCleanup(currentUser.uid)
                            onVerified()
                        } else {
                            onNotVerified("Email is not verified yet. Please check your inbox and verify your email first.")
                        }
                    } else {
                        onNotVerified("No user found. Please sign up again.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to check verification status", e)
                    onNotVerified("Failed to check verification status. Please try again.")
                }
            }
        }
    
        suspend fun isUserProfileComplete(): Boolean {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid ?: return false
    
            return try {
                val doc = firestore.collection("users").document(userId).get().await()
                val username = doc.getString("username")
                Log.d("AuthViewModel", "Fetched username: $username for userId: $userId")
                !username.isNullOrBlank()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to check user profile", e)
                false
            }
        }
    
        private fun generateTempPassword(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*"
            return (1..12)
                .map { chars.random() }
                .joinToString("")
        }
    
        // Verify email sign-in link
        fun verifyEmailLink(email: String, link: String) {
            viewModelScope.launch {
                try {
                    if (auth.isSignInWithEmailLink(link)) {
                        val result = auth.signInWithEmailLink(email, link).await()
                        Log.d(TAG, "Email link sign-in successful: ${result.user?.uid}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to verify email link", e)
                }
            }
        }
    
        // Reset password
        fun sendPasswordResetEmail(email: String) {
            viewModelScope.launch {
                _forgotPasswordState.value = ForgotPasswordState(isLoading = true)
                try {
                    auth.sendPasswordResetEmail(email).await()
                    Log.d(TAG, "Password reset email sent to $email")
                    _forgotPasswordState.value = ForgotPasswordState(isSuccess = true)
                } catch (e: FirebaseAuthInvalidUserException) {
                    Log.e(TAG, "No user found with this email", e)
                    _forgotPasswordState.value = ForgotPasswordState(errorMessage = "No account found with this email")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send password reset email", e)
                    _forgotPasswordState.value = ForgotPasswordState(errorMessage = e.message ?: "Failed to send reset email")
                }
            }
        }
    
        // Reset states
        fun resetSignupState() {
            _signupState.value = SignupState()
            _verificationState.value = VerificationState()
        }
    
        fun resetVerificationState() {
            _verificationState.value = VerificationState()
        }
    
        // Check if user is logged in
        fun isUserLoggedIn(): Boolean {
            return auth.currentUser != null
        }
    
        // Sign out
        fun signOut() {
            // Cancel any pending cleanup jobs
            userCleanupJobs.values.forEach { it.cancel() }
            userCleanupJobs.clear()
    
            auth.signOut()
    
            // Reset all states
            _loginState.value = LoginState()
            _signupState.value = SignupState()
            _verificationState.value = VerificationState()
        }
    
        override fun onCleared() {
            super.onCleared()
            // Cancel all cleanup jobs when ViewModel is destroyed
            userCleanupJobs.values.forEach { it.cancel() }
            userCleanupJobs.clear()
        }
    }