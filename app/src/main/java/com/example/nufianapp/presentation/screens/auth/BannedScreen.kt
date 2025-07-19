package com.example.nufianapp.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nufianapp.main.navigation.ScreenCustom
import com.example.nufianapp.ui.theme.Red
import com.example.nufianapp.ui.theme.White

@Composable
fun BannedScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Red),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Access Denied",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "You have been banned.\n\nIf you receive this message, it means you've ignored our final warning. " +
                        "Please be mindful about your behavior and respect others, especially in a community. \n" +
                        "\nIf you believe this is a mistake, please contact the admin at:\n\ncyra.coni@gmail.com",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = White
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Red
                ),
                onClick = {
                    navController.navigate(ScreenCustom.AuthScreenCustom.route)
                }
            ) {
                Text("Back to Login",
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}
