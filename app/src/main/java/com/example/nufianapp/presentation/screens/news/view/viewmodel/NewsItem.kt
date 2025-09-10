package com.example.nufianapp.presentation.screens.news.view.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.nufianapp.R
import com.example.nufianapp.data.model.News
import com.example.nufianapp.data.model.User
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.core.components.ImageContentUrl
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.NeonWhite
@Composable
fun NewsItem(
    news: News,
    modifier: Modifier = Modifier,
    onDeleteNews: (News) -> Unit,
    user: User? = null,
) {

    var showDropdown by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(264.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            news.contentImageUrls.firstOrNull()?.let { firstImageUrl ->
                ImageContentUrl(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .height(200.dp),
                    contentUrl = firstImageUrl,
                )
            }

            if (user?.userType == "admin") {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .zIndex(2f)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    ButtonIcon(
                        onClickButton = { showDropdown = true },
                        iconRes = R.drawable.icon_triple_dots,
                        tint = Charcoal
                    )

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete News") },
                            onClick = {
                                showDropdown = false
                                onDeleteNews(news)
                            }
                        )
                    }
                }
            }

            // ✅ Overlay text area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = Color.Black.copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Utils().calculateTimeAgo(news.dateTime),
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.subject,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.content,
                        color = Color.White,
                        maxLines = 4,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            // ✅ Footer
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(64.dp)
                    .fillMaxWidth()
                    .background(NeonWhite)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By STT Terpadu Nurul Fikri",
                    color = Charcoal,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}
