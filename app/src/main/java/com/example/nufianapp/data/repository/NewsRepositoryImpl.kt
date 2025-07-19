package com.example.nufianapp.data.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.firebase.StorageHelper
import com.example.nufianapp.data.model.News
import com.example.nufianapp.data.util.DataPreparer
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val fireStoreHelper: FireStoreHelper,
    private val storageHelper: StorageHelper
) : NewsRepository {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    override fun getNews(): Flow<PagingData<News>> =
        fireStoreHelper.getNews()
            .catch { e ->
                emit(PagingData.empty())
                ErrorUtils.getFriendlyErrorMessage(e)
            }

    override fun getSingleNewsData(newsId: String): Flow<Response<News>> = flow {
        emit(Response.Loading)
        val news = fireStoreHelper.getSingleNewsData(newsId)
        emit(Response.Success(news))
    }.catch { e ->
        emit(Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e))))
    }

    override suspend fun deleteNews(newsId: String): Response<Unit> = withContext(ioScope.coroutineContext) {
        return@withContext try {
            val news = fireStoreHelper.getSingleNewsData(newsId)
            news.contentImageUrls?.forEach { imageUrl ->
                storageHelper.deleteImageByUrl(imageUrl)
            }

            fireStoreHelper.deleteNews(newsId)

            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
        }
    }


    override suspend fun storeNewsData(newsItem: News): Response<Boolean> = withContext(ioScope.coroutineContext) {
        return@withContext try {
            val imagePaths = newsItem.contentImageUris?.map { uri ->
                "content_images/news/${newsItem.dateTime}/${uri.lastPathSegment}"
            } ?: emptyList()
            val contentImageUrls = if (imagePaths.isNotEmpty()) {
                storageHelper.uploadImages(imagePaths, newsItem.contentImageUris!!)
            } else {
                emptyList()
            }

            val newsData = DataPreparer.prepareNewsData(newsItem, contentImageUrls)
            fireStoreHelper.storeNewsData(newsData)

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
        }
    }
}
