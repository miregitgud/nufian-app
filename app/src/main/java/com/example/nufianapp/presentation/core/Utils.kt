package com.example.nufianapp.presentation.core

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.unit.dp
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.ClearBlue
import com.example.nufianapp.ui.theme.DarkGreen
import com.example.nufianapp.ui.theme.DisabledColor
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.Purple
import com.example.nufianapp.ui.theme.Red
import com.example.nufianapp.ui.theme.Tosca
import com.example.nufianapp.ui.theme.TwitchPurple
import com.example.nufianapp.ui.theme.Yellow
import java.util.Date

class Utils {
    companion object {
        fun print(e: Exception) = Log.e(TAG, e.stackTraceToString())

        fun showMessage(
            context: Context,
            message: String?
        ) = makeText(context, message, LENGTH_LONG).show()
    }

    fun calculateTimeAgo(dateTime: Date): String {
        val dateTimeItem = dateTime.time
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - dateTimeItem

        return calculateTimeAgoString(timeDiff)
    }

    private fun calculateTimeAgoString(timeDiff: Long): String {
        val seconds = timeDiff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
    }

    @Composable
    fun getColorForTopic(topic: String?): Color {
        return when (topic) {
            "All" -> Red
            "Official Announcement" -> Charcoal
            "General" -> Orange
            "Discussion" -> Blue
            "Recruit/Collab" -> TwitchPurple
            "Fluff" -> Tosca
            "Shout out" -> Magenta
            "Showcase" -> DarkGreen
            "Tips & Tricks" -> ClearBlue
            else -> DisabledColor
        }
    }


    @Composable
    fun SpacerHeightVeryLarge(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.height(48.dp))
    }

    @Composable
    fun SpacerHeightLarge(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.height(32.dp))
    }

    @Composable
    fun SpacerHeightMedium(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.height(16.dp))
    }

    @Composable
    fun SpacerHeightSmall(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.height(8.dp))
    }

    @Composable
    fun SpacerHeightVerySmall(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.height(4.dp))
    }

    @Composable
    fun SpacerWidthMedium(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.width(16.dp))
    }

    @Composable
    fun SpacerWidthSmall(modifier: Modifier = Modifier) {
        Spacer(modifier = modifier.width(8.dp))
    }
}