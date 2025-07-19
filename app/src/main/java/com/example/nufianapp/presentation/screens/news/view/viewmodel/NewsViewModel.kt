package com.example.nufianapp.presentation.screens.news.view.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.nufianapp.data.model.News
import com.example.nufianapp.data.repository.NewsRepository
import com.example.nufianapp.data.repository.UserRepository
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.GlideImageLoader
import com.example.nufianapp.presentation.core.components.content.ContentResponseLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    private val _pagingFlow: MutableStateFlow<PagingData<News>> =
        MutableStateFlow(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<News>> = _pagingFlow

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private val _isAdmin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _userId: MutableStateFlow<String?> = MutableStateFlow(null)
    val userId: StateFlow<String?> = _userId

    init {
        getNews()
        checkUserType()
        fetchCurrentUserId()
    }

    private fun getNews() {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepository.getNews()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    pagingData.map { news ->
                        GlideImageLoader.preloadImages(
                            applicationContext,
                            news.contentImageUrls
                        )
                    }
                    _pagingFlow.value = pagingData
                }
        }
    }

    fun deleteNews(newsId: String) {
        viewModelScope.launch {
            when (val result = newsRepository.deleteNews(newsId)) {
                is Response.Success -> {
                    onSnackBarShown("News deleted successfully.")
                    getNews()
                }
                is Response.Failure -> {
                    onSnackBarShown("Failed to delete news: ${result.e.message}")
                }

                Response.Loading -> {}
            }
        }
    }

    private fun checkUserType() {
        viewModelScope.launch {
            val userId = userRepository.currentUser?.uid
            if (userId != null) {
                val response = userRepository.getUserDataById(userId)
                _isAdmin.value = response is Response.Success && response.data?.userType == "admin"
            } else {
                _isAdmin.value = false
            }
        }
    }

    private fun fetchCurrentUserId() {
        viewModelScope.launch {
            _userId.value = userRepository.currentUser?.uid
        }
    }

    fun onSnackBarShown(message: String) {
        viewModelScope.launch {
            _snackBarFlow.emit(message)
        }
    }
}
