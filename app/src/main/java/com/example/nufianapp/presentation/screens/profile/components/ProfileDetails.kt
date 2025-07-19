package com.example.nufianapp.presentation.screens.profile.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.test.espresso.action.ViewActions.openLink
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.R
import com.example.nufianapp.data.model.User
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.Red

@Composable
fun ProfileDetails(
    user: User,
    onImageClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileAvatarImage(
            avatarUrl = user.avatarUrl ?: "",
            user = user,
            modifier = Modifier
                .size(100.dp)
                .shadow(4.dp, CircleShape)
                .clickable { onImageClick() }
        )
        Text(
            text = user.displayName,
            textAlign = TextAlign.Center,
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = if (user.userType == "admin") {
                user.interest
            } else {
                user.interest + " " + user.batch + " • " + user.status
            },
            color = Charcoal,
            style = MaterialTheme.typography.bodyMedium
        )
        Column(
            modifier = Modifier.clickable(
                indication = null, // Remove the click effect
                interactionSource = remember { MutableInteractionSource() },
                onClick = { isExpanded = !isExpanded }
            )
        ) {
            Text(
                text = user.bioData.ifEmpty { "No bio available." },
                maxLines = if (isExpanded) 10 else 3,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    SocialMediaHandles(user = user)
}

@Composable
fun ProfileAvatarImage(
    avatarUrl: String,
    modifier: Modifier = Modifier,
    user: User,
) {
    val painter = if (avatarUrl.isNotEmpty()) {
        rememberAsyncImagePainter(
            model = avatarUrl,
            placeholder = painterResource(R.drawable.img_avatar_default),
            error = painterResource(R.drawable.img_avatar_default)
        )
    } else {
        painterResource(R.drawable.img_avatar_default)
    }

    val borderColor = if (user.interest == "TI") {
        Blue
    } else if (user.interest == "SI") {
        Orange
    } else if (user.interest == "BD") {
        Red
    } else {
        Charcoal
    }

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
    )
}

// Data class to hold extraction results
data class SocialMediaResult(
    val displayText: String,
    val isValid: Boolean,
    val originalUrl: String?
)

// Enhanced helper functions with comprehensive error handling
private fun extractInstagramUsername(url: String?): SocialMediaResult {
    if (url.isNullOrEmpty() || url.isBlank()) {
        return SocialMediaResult("Instagram not set", false, null)
    }

    return try {
        val cleanUrl = url.trim()

        // Check for obviously invalid URLs
        if (cleanUrl.length < 4 || cleanUrl == "null" || cleanUrl == "N/A") {
            return SocialMediaResult("Invalid Instagram link", false, url)
        }

        // Handle various Instagram URL formats
        val patterns = listOf(
            Regex("(?:https?://)?(?:www\\.)?instagram\\.com/([a-zA-Z0-9_.]{1,30})/?(?:\\?.*)?", RegexOption.IGNORE_CASE),
            Regex("(?:https?://)?(?:www\\.)?ig\\.me/([a-zA-Z0-9_.]{1,30})/?", RegexOption.IGNORE_CASE),
            Regex("@([a-zA-Z0-9_.]{1,30})", RegexOption.IGNORE_CASE) // Handle @username format
        )

        for (pattern in patterns) {
            val match = pattern.find(cleanUrl)
            if (match != null) {
                val username = match.groupValues[1]
                // Validate username format
                if (username.isNotEmpty() && username.matches(Regex("[a-zA-Z0-9_.]{1,30}"))) {
                    return SocialMediaResult("@$username", true, url)
                }
            }
        }

        // If no pattern matches but URL seems to be attempting Instagram
        if (cleanUrl.contains("instagram", ignoreCase = true) || cleanUrl.contains("ig.me", ignoreCase = true)) {
            return SocialMediaResult("Invalid Instagram format", false, url)
        }

        // Generic invalid URL
        return SocialMediaResult("Invalid link format", false, url)

    } catch (e: Exception) {
        return SocialMediaResult("Error parsing Instagram link", false, url)
    }
}

private fun extractLinkedInUsername(url: String?): SocialMediaResult {
    if (url.isNullOrEmpty() || url.isBlank()) {
        return SocialMediaResult("LinkedIn not set", false, null)
    }

    return try {
        val cleanUrl = url.trim()

        // Check for obviously invalid URLs
        if (cleanUrl.length < 4 || cleanUrl == "null" || cleanUrl == "N/A") {
            return SocialMediaResult("Invalid LinkedIn link", false, url)
        }

        // Handle various LinkedIn URL formats
        val patterns = listOf(
            Regex("(?:https?://)?(?:www\\.)?linkedin\\.com/in/([a-zA-Z0-9-]{1,100})/?(?:\\?.*)?", RegexOption.IGNORE_CASE),
            Regex("(?:https?://)?(?:www\\.)?linkedin\\.com/pub/([a-zA-Z0-9-]{1,100})/?(?:\\?.*)?", RegexOption.IGNORE_CASE),
            Regex("(?:https?://)?(?:www\\.)?linkedin\\.com/profile/view\\?id=([a-zA-Z0-9-]{1,100})", RegexOption.IGNORE_CASE)
        )

        for (pattern in patterns) {
            val match = pattern.find(cleanUrl)
            if (match != null) {
                val username = match.groupValues[1]
                if (username.isNotEmpty() && username.matches(Regex("[a-zA-Z0-9-]{1,100}"))) {
                    return SocialMediaResult("in/$username", true, url)
                }
            }
        }

        // If no pattern matches but URL seems to be attempting LinkedIn
        if (cleanUrl.contains("linkedin", ignoreCase = true)) {
            return SocialMediaResult("Invalid LinkedIn format", false, url)
        }

        // Generic invalid URL
        return SocialMediaResult("Invalid link format", false, url)

    } catch (e: Exception) {
        return SocialMediaResult("Error parsing LinkedIn link", false, url)
    }
}

// Enhanced SocialMediaHandles component with error handling
@Composable
private fun SocialMediaHandles(user: User) {
    var showDialog by remember { mutableStateOf(false) }
    var currentLink by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val instagramResult = remember(user.instagramUrl) { extractInstagramUsername(user.instagramUrl) }
        val linkedinResult = remember(user.linkedinUrl) { extractLinkedInUsername(user.linkedinUrl) }

        SocialMediaRow(
            iconId = R.drawable.instagram_icon,
            result = instagramResult,
            onClick = { link, isValid ->
                if (isValid && link != null) {
                    currentLink = link
                    showDialog = true
                } else {
                    errorMessage = when {
                        link == null -> "No Instagram profile available"
                        else -> "Invalid Instagram link format"
                    }
                }
            }
        )

        SocialMediaRow(
            iconId = R.drawable.linkedin_icon,
            result = linkedinResult,
            onClick = { link, isValid ->
                if (isValid && link != null) {
                    currentLink = link
                    showDialog = true
                } else {
                    errorMessage = when {
                        link == null -> "No LinkedIn profile available"
                        else -> "Invalid LinkedIn link format"
                    }
                }
            }
        )
    }

    // Success dialog for valid links
    if (showDialog && currentLink != null) {
        val context = LocalContext.current
        LinkAlertDialog(
            link = currentLink!!,
            onDismiss = { showDialog = false },
            onConfirm = {
                openLink(context, currentLink!!)
                showDialog = false
            }
        )
    }

    // Error dialog for invalid links
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text(text = "Link Unavailable") },
            text = { Text(text = errorMessage!!) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}

// Enhanced SocialMediaRow component with comprehensive error handling
@Composable
private fun SocialMediaRow(
    iconId: Int,
    result: SocialMediaResult,
    onClick: (String?, Boolean) -> Unit
) {
    val textColor = if (result.isValid) Color.Black else Color.Gray
    val iconTint = if (result.isValid) Color(0xff333333) else Color.Gray

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            onClick(result.originalUrl, result.isValid)
        }
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconTint),
            modifier = Modifier.requiredSize(12.dp)
        )
        Text(
            text = result.displayText,
            color = textColor,
            lineHeight = 1.4.em,
            style = TextStyle(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .requiredWidthIn(max = 100.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
    }
}

// Alternative: Backward-compatible version with your original signature
@Composable
private fun SocialMediaRowCompatible(
    iconId: Int,
    handle: String,
    link: String?,
    onClick: (String?) -> Unit
) {
    val result = when {
        iconId == R.drawable.instagram_icon -> extractInstagramUsername(link)
        iconId == R.drawable.linkedin_icon -> extractLinkedInUsername(link)
        else -> SocialMediaResult(handle, false, link)
    }

    val textColor = if (result.isValid) Color.Black else Color.Gray
    val iconTint = if (result.isValid) Color(0xff333333) else Color.Gray

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            if (result.isValid && result.originalUrl != null) {
                onClick(result.originalUrl)
            }
            // Do nothing for invalid links - could show toast/snackbar here
        }
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconTint),
            modifier = Modifier.requiredSize(12.dp)
        )
        Text(
            text = result.displayText,
            color = textColor,
            lineHeight = 1.4.em,
            style = TextStyle(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .requiredWidthIn(max = 100.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
    }
}

@Composable
fun LinkAlertDialog(
    link: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Open Link") },
        text = { Text(text = link) },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("Open")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EnlargedImageDialog(
    onDismiss: () -> Unit,
    avatarUrl: String,
) {
    val painter = if (avatarUrl.isNotEmpty()) {
        rememberAsyncImagePainter(model = avatarUrl)
    } else {
        painterResource(R.drawable.img_avatar_default)
    }
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = "Intersect",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }
    }
}

private fun openLink(context: Context, link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    context.startActivity(intent)
}