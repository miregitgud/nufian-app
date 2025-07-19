package com.example.nufianapp.presentation.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.Red

@Composable
fun LogoutDialog(
    userName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .padding(20.dp),
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Logout Account",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Are you sure you want to logout \n$userName?",
                    textAlign = TextAlign.Center
                    )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Red.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 30.dp) // Adjust padding
                        .fillMaxWidth() // Make the button fill the available width
                ) {
                    Text(
                        text = "Logout",
                        color = Red
                    )
                }
            }
        },
        dismissButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier
                        .padding(horizontal = 30.dp) // Adjust padding
                        .fillMaxWidth() // Make the button fill the available width
                ) {
                    Text(
                        text = "Cancel",
                        color = Graphite
                    )
                }
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true)
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LogoutDialogPreview() {
    var showDialog by remember { mutableStateOf(false) }
    LogoutDialog(
        userName = "Rahmad Noor Ikhsan",
        onConfirm = { showDialog = false },
        onDismiss = { showDialog = false }
    )
}
