package com.example.nufianapp.presentation.screens.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nufianapp.R
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Graphite

@Composable
fun AccountSection(
    navigateToEditProfile: () -> Unit,
    navigateToChangePassword: () -> Unit,
) {
    Text(
        text = "Account",
        color = Graphite,
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column {
            AccountOption(
                "Edit Profile",
                R.drawable.icon_item_person,
                R.drawable.icon_item_expand_right,
                navigateToEditProfile = navigateToEditProfile
            )
//            Spacer(modifier = Modifier.height(16.dp))
//            ChangePasswordOption(
//                "Change password",
//                R.drawable.icon_item_lock,
//                R.drawable.icon_item_expand_right,
//                navigateToChangePassword = navigateToChangePassword
//            )
        }
    }
}

@Composable
fun ChangePasswordOption(
    text: String,
    iconRes: Int,
    expandIconRes: Int,
    navigateToChangePassword: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = expandIconRes),
            contentDescription = "Expand_right_light",
            Modifier.size(24.dp)
        )
    }
}

@Composable
fun AccountOption(
    text: String,
    iconRes: Int,
    expandIconRes: Int,
    navigateToEditProfile: () -> Unit,

    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { navigateToEditProfile() }
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Blue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = text,
            color = Color.Black,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = expandIconRes),
            contentDescription = "Expand_right_light",
            Modifier.size(24.dp)
        )
    }
}