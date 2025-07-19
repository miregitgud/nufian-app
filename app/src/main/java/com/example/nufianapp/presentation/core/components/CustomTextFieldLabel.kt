package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun CustomTextFieldLabel(
    textFieldValue: TextFieldValue,
    onTextFieldValueChange: (newValue: TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldIcon: Int? = null,
    textFieldLabel: String,
    placeholder: String = "",
    isNotEmpty: Boolean = false,
) {
    var isError by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row {
            if (isNotEmpty) {
                Text(text = "*", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xffe0594c)))
            }
            Text(text = textFieldLabel, style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
        }
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                onTextFieldValueChange(newValue)
                isError = newValue.text.isEmpty()
            },
            placeholder = { Text(text = placeholder) },
            leadingIcon = if (textFieldIcon != null && textFieldIcon != 0) {
                {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = textFieldIcon),
                        contentDescription = textFieldLabel
                    )
                }
            } else null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                ),
                cursorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (isError) {
            Text(
                text = "$textFieldLabel cannot be empty",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}