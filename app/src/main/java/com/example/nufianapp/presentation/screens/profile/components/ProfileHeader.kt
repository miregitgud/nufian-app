package com.example.nufianapp.presentation.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nufianapp.R
import com.example.nufianapp.presentation.core.components.ButtonIcon

@Composable
fun ProfileHeader(
    navigateBack: () -> Unit,
    navigateToSetting: () -> Unit,
    isProfileView: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ButtonIcon(onClickButton = navigateBack)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Profile",
            color = Color(0xff000000),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Spacer to push the image to the center

        Spacer(modifier = Modifier.weight(1f)) // Spacer to push the image to the center

        if (isProfileView) {
            IconButton(
                onClick = { navigateToSetting() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_icon),
                    contentDescription = null,
                    modifier = Modifier.requiredSize(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(24.dp)) // Spacer to balance the row if the settings icon is not shown
        }
    }
}