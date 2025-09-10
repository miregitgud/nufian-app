package com.example.nufianapp.domain.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.model.News
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(): Flow<PagingData<News>>
    fun getSingleNewsData(newsId: String): Flow<Response<News>>
    suspend fun storeNewsData(newsItem: News): Response<Boolean>
    suspend fun deleteNews(newsId: String): Response<Unit>
}



