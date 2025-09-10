package com.example.nufianapp.presentation.screens.profile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.domain.repository.ForumRepository
import com.example.nufianapp.domain.repository.UserRepository
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.GlideImageLoader
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
class PostViewModel @Inject constructor(
    private val forumRepository: ForumRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    private val _pagingFlow: MutableStateFlow<PagingData<Forum>> =
        MutableStateFlow(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Forum>> = _pagingFlow

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private fun getForumWithPreview() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = userRepository.currentUser?.uid ?: return@launch
            forumRepository.getForumsByUserId(userId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    pagingData.map { forum ->
                        GlideImageLoader.preloadImages(
                            applicationContext,
                            forum.contentImageUrls
                        )
                    }
                    _pagingFlow.value = pagingData
                }
        }
    }

    private fun getForumWithoutPreview(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            forumRepository.getForumsByUserId(userId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    pagingData.map { forum ->
                        GlideImageLoader.preloadImages(
                            applicationContext,
                            forum.contentImageUrls
                        )
                    }
                    _pagingFlow.value = pagingData
                }
        }
    }

    fun getPagingFlow(isProfile: Boolean, userId: String?) {
        if (isProfile) {
            getForumWithPreview()
        } else {
            if (userId != null) {
                getForumWithoutPreview(userId)
            }
        }
    }

    fun deletePost(forumId: String) {
        viewModelScope.launch {
            val response = forumRepository.deleteForum(forumId)
            if (response is Response.Success) {
                onSnackBarShown("Forum deleted successfully")
                getForumWithPreview()
            } else if (response is Response.Failure) {
                onSnackBarShown(response.e.toString())
            }
        }
    }

    fun onSnackBarShown(message: String) {
        viewModelScope.launch {
            _snackBarFlow.emit(message)
        }
    }
}