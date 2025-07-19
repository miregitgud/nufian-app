package com.example.nufianapp.presentation.screens.home.view.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.ForumCategory
import com.example.nufianapp.data.model.User
import com.example.nufianapp.data.repository.ForumRepository
import com.example.nufianapp.data.repository.UserRepository
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.Constants.FORUM_CATEGORY
import com.example.nufianapp.presentation.core.GlideImageLoader
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val forumRepository: ForumRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {


    private val _hasShownWelcomeScreen = MutableStateFlow(false)

    fun markWelcomeScreenShown() {
        _hasShownWelcomeScreen.value = true
    }

    val isFirstLogin = mutableStateOf(false)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun getCurrentUserId(): String? {
        return userRepository.currentUser?.uid
    }

    private val defaultCategory = ForumCategory("0", "All")

    val categories: List<ForumCategory> = listOf(defaultCategory) + FORUM_CATEGORY

    private val _selectedForumCategory = MutableStateFlow(defaultCategory)
    val selectedForumCategory: StateFlow<ForumCategory> = _selectedForumCategory

    private val _userMap = MutableStateFlow<Map<String, User>>(emptyMap())
    val userMap: StateFlow<Map<String, User>> = _userMap

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private val _pagingFlow: MutableStateFlow<PagingData<Forum>> =
        MutableStateFlow(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Forum>> = _pagingFlow

    private val _isFirstFetchDone = MutableStateFlow(false)
    val isFirstFetchDone: StateFlow<Boolean> = _isFirstFetchDone

    init {
        observeForumByCategory()
        fetchCurrentUser()
    }

    @OptIn(FlowPreview::class)
    private fun observeForumByCategory() {
        viewModelScope.launch {
            _selectedForumCategory
                .debounce(300) // Debounce to avoid rapid state changes
                .collectLatest { selectedCategory ->
                    fetchData(selectedCategory)
                }
        }
    }

    fun getGreetingTexts(name: String): Pair<String, String> {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning, \n$name." to "Ready to start the day?"
            in 12..16 -> "Good afternoon, \n$name." to "Hope your day's going well!"
            in 17..20 -> "Good evening, \n$name." to "In a mood for some questions?"
            else -> "Good night, \n$name." to "Time to relax and recharge!"
        }
    }

    private suspend fun fetchData(selectedCategory: ForumCategory) {
        forumRepository.getForum()
            .distinctUntilChanged()
            .map { pagingData ->
                pagingData.filter { forum ->
                    selectedCategory.name == "All" || forum.topic == selectedCategory.name
                }
            }
            .map { filteredData ->
                filteredData.filter { forum ->
                    forum.forumUserPostId.isNotEmpty()
                }.map { forum ->
                    fetchUserById(forum.forumUserPostId)
                    forum
                }
            }
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

    suspend fun checkIfFirstLogin(uid: String): Boolean {
        val userDoc = Firebase.firestore.collection("users").document(uid).get().await()
        return userDoc.getBoolean("firstLogin") == true
    }


    private fun fetchCurrentUser() {
        val userId = getCurrentUserId() ?: return
        viewModelScope.launch {
            when (val response = userRepository.getUserDataById(userId)) {
                is Response.Success -> {
                    _currentUser.value = response.data
                }
                is Response.Failure -> {
                    _currentUser.value = User(displayName = "User", avatarUrl = "", interest = "")
                }
                Response.Loading -> {
                    // Optionally handle loading
                }
            }
        }
    }

    private suspend fun fetchUserById(userId: String) {
        when (val response = userRepository.getUserDataById(userId)) {
            is Response.Success -> {
                response.data?.let { user ->
                    _userMap.value += (userId to user)
                } ?: run {
                    _userMap.value += (userId to User(displayName = "", avatarUrl = "", interest = ""))
                }
            }

            is Response.Failure -> {
                _userMap.value += (userId to User(displayName = "", avatarUrl = "", interest = ""))
            }

            Response.Loading -> {
                // Optionally handle loading state
            }
        }
    }

    suspend fun loadUserLoginStatusDirect(uid: String) {
        Log.d("HomeViewModel", "Loading login status directly for uid: $uid")
        val isFirst = checkIfFirstLogin(uid)
        Log.d("HomeViewModel", "firstLogin = $isFirst for UID: $uid")
        isFirstLogin.value = isFirst
    }


    fun markFirstLoginComplete(uid: String) {
        Firebase.firestore.collection("users").document(uid)
            .update("firstLogin", false)
    }

    fun onSnackBarShown(message: String) {
        viewModelScope.launch {
            _snackBarFlow.emit(message)
        }
    }

    fun selectForumCategory(category: ForumCategory) {
        _selectedForumCategory.value = category
    }

    fun refreshData() {
        viewModelScope.launch {
            val selectedCategory = _selectedForumCategory.value
            fetchData(selectedCategory)
            _isFirstFetchDone.value = true
        }
    }

    fun initialFetchData() {
        viewModelScope.launch {
            fetchData(_selectedForumCategory.value)
            _isFirstFetchDone.value = true
        }
    }
}