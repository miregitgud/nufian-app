package com.example.nufianapp.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.nufianapp.data.store.DataStoreRepository
import com.example.nufianapp.main.navigation.ScreenCustom
import com.example.nufianapp.ui.theme.NufianAppTheme
import com.example.nufianapp.utils.LanguageHelper.getUserPreferredLanguage
import com.example.nufianapp.utils.LocaleHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.util.Log

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var updatedContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Set language preference
        val preferredLanguage = getUserPreferredLanguage(this) ?: "en"
        val contextWithLocale = LocaleHelper.setLocale(this, preferredLanguage)
        updatedContext = contextWithLocale.createConfigurationContext(contextWithLocale.resources.configuration)

        // Handle email verification link (deep link)
        val deepLinkOobCode = intent?.data?.takeIf { intent.action == Intent.ACTION_VIEW }
            ?.getQueryParameter("oobCode")

        // Set Compose UI
        setContent {
            NufianAppTheme(darkTheme = false) {
                val navController = rememberNavController()
                MainController(
                    navController = navController,
                    context = updatedContext,
                    startDestination = ScreenCustom.SplashCustom.route,
                    deepLinkOobCode = deepLinkOobCode
                )
            }
        }
        FirebaseAuth.getInstance().currentUser?.let { user ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM_TOKEN", token)

                    val db = Firebase.firestore
                    db.collection("users").document(user.uid)
                        .update("fcmToken", token)
                        .addOnSuccessListener {
                            Log.d("FCM", "Token successfully updated.")
                        }
                        .addOnFailureListener {
                            Log.e("FCM", "Failed to update token.", it)
                        }
                } else {
                    Log.w("FCM", "Fetching FCM token failed", task.exception)
                }
            }
        }
    }
}
