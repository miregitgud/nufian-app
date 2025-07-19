package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nufianapp.ui.theme.Red

@Composable
fun CustomDeleteDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            modifier = Modifier
                .padding(10.dp),
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            },
            title =
            {
                Text(
                    text = title,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
            },
            text =
            {
                Text(
                    text = message,
                    color = Color(0xFF5C5C5C),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            },
            confirmButton =
            {
                Button(
                    onClick = {
                        onConfirm()
                        showDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 50.dp) // Adjust padding
                        .fillMaxWidth()
                ) {
                    Text(text = "Delete", color = Color.White)
                }
            },
            dismissButton =
            {
                Button(
                    onClick = {
                        showDialog.value = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier
                        .padding(vertical = 0.dp, horizontal = 50.dp) // Adjust padding
                        .fillMaxWidth()

                ) {
                    Text(text = "Cancel", color = Color.White)

                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun CustomDeleteDialogPreview() {
    var showDeleteDialog = remember { mutableStateOf(true) }
    CustomDeleteDialog(
        onConfirm = { showDeleteDialog.value = true },
        onDismiss = { showDeleteDialog.value = true },
        message = "hey",
        title = "so uh",
        showDialog = showDeleteDialog
    )
}