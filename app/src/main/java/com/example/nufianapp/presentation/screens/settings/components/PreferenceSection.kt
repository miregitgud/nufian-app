package com.example.nufianapp.presentation.screens.settings.components

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import android.provider.Settings
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.example.nufianapp.R
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Graphite

@Composable
fun PreferencesSection() {
    Text(
        text = "Preferences",
        color = Graphite,
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            NotificationSwitch()
//            Spacer(modifier = Modifier.height(10.dp))
//            LanguageSwitch()
//            Spacer(modifier = Modifier.height(10.dp))
//            DarkModeSwitch()
        }
    }
}

@Composable
fun NotificationSwitch() {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(isNotificationPermissionGranted(context)) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isChecked = true
            Log.d("NotificationSwitch", "Notification permission granted.")
        } else {
            isChecked = false
            Log.d("NotificationSwitch", "Notification permission denied.")
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Blue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_item_notification),
                contentDescription = "Notification",
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = "Notification",
            color = Color.Black,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = { checked ->
                if (checked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Request permission if not already granted (Android 13 and above)
                        if (!isNotificationPermissionGranted(context)) {
                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            isChecked = true
                            Log.d("NotificationSwitch", "Notifications are already enabled.")
                        }
                    } else {
                        // For lower API levels, enable notifications directly
                        isChecked = true
                        Log.d("NotificationSwitch", "Notifications are already enabled for lower API levels.")
                    }
                } else {
                    // Open notification settings to allow the user to disable notifications
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xff4986ea),
                uncheckedThumbColor = Color(0xFFCCCCCC)
            )
        )
    }
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

@Composable
fun LanguageSwitch() {
    var isChecked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.globe_light),
            contentDescription = "Language",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Language",
            color = Color.Gray,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = if (isChecked) Color(0xff4986ea) else Color(0xFFCCCCCC)
            )
        )
    }
}

@Composable
fun DarkModeSwitch() {
    var isChecked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.moon_light),
            contentDescription = "Dark Mode",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Dark Mode",
            color = Color.Gray,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = if (isChecked) Color(0xff4986ea) else Color(0xFFCCCCCC)
            )
        )
    }
}