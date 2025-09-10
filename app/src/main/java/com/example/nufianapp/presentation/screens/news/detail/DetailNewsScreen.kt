package com.example.nufianapp.presentation.screens.news.detail

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.R
import com.example.nufianapp.data.model.News
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.Constants.TAG_DETAIL_NEWS_ERROR
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.core.content.ContentResponseError
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.core.content.DisplayImages
import com.example.nufianapp.ui.theme.NeonWhite

@Composable
fun DetailNewsScreen(
    modifier: Modifier = Modifier,
    newsId: String,
    navigateBack: () -> Unit,
    viewModel: DetailNewsViewModel = hiltViewModel(),
) {
    LaunchedEffect(newsId) {
        viewModel.getSingleNewsData(newsId)
    }

    val newsByIdDataResponse by viewModel.newsByIdData.collectAsState()

    when (val response = newsByIdDataResponse) {
        is Response.Loading -> ContentResponseLoading()

        is Response.Success<News> -> {
            val news = response.data
            ShowNewsContent(news = news, navigateBack = navigateBack, modifier = modifier)
        }

        is Response.Failure -> {
            val errorMessage = stringResource(id = R.string.failed_fetch_details_news)
            Log.e(TAG_DETAIL_NEWS_ERROR, errorMessage, response.e)
            ContentResponseError(
                modifier,
                message = errorMessage,
                navigateBack = navigateBack,
                onRetry = { viewModel.getSingleNewsData(newsId) }
            )
        }
    }
}

@Composable
fun ShowNewsContent(
    news: News,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(modifier = modifier
        .fillMaxSize()
        .background(NeonWhite)) {
        Column {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false)
            ) {
                Utils().SpacerHeightLarge()
                ButtonIcon(onClickButton = navigateBack)
                PublisherInfo()
                Text(
                    text = news.subject,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black
                )
                Text(
                    text = Utils().calculateTimeAgo(news.dateTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
                DisplayImages(news.contentImageUrls)
                Text(text = news.content, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }
        }
    }
}

@Composable
fun PublisherInfo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.img_avatar_company),
            contentDescription = stringResource(id = R.string.description_image_forum_detail),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Published by",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
            )
            Text(
                text = "STT Terpadu Nurul Fikri",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black,
            )
        }
    }
}

