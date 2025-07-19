package com.example.nufianapp.presentation.screens.news.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.data.model.News
import com.example.nufianapp.data.repository.NewsRepository
import com.example.nufianapp.domain.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _newsByIdData = MutableStateFlow<Response<News>>(Response.Loading)
    val newsByIdData = _newsByIdData.asStateFlow()

    fun getSingleNewsData(newsId: String) {
        viewModelScope.launch {
            try {
                newsRepository.getSingleNewsData(newsId).collect { response ->
                    _newsByIdData.value = response
                }
            } catch (e: Exception) {
                _newsByIdData.value = Response.Failure(e)
            }
        }
    }

}