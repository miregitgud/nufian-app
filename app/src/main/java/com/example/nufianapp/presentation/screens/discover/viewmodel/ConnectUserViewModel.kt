package com.example.nufianapp.presentation.screens.discover.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.repository.ConnectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ConnectUserViewModel @Inject constructor(
    private val connectRepository: ConnectRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _pagingFlow = MutableStateFlow<PagingData<User>>(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<User>> = _pagingFlow.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<String>()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)

    init {
        observeUsers()
        getCurrentUser()
    }

    fun refreshUsers() {
        _refreshTrigger.value++ // trigger paging reload
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()

                val user = snapshot.toObject(User::class.java)
                _currentUser.value = user
            } catch (e: Exception) {
                _snackBarFlow.emit("Failed to load current user: ${e.message}")
            }
        }
    }
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeUsers() {
        viewModelScope.launch {
            _searchText
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { searchText ->
                    _refreshTrigger.flatMapLatest {
                        connectRepository.getUser()
                            .map { pagingData ->
                                pagingData.filter { user ->
                                    user.displayName.startsWith(searchText, ignoreCase = true)
                                }
                            }
                            .cachedIn(viewModelScope)
                    }
                }
                .collectLatest { filteredPagingData ->
                    _pagingFlow.value = filteredPagingData
                }
        }
    }

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun clearSearchText() {
        _searchText.value = ""
    }

    fun onSnackBarShown(message: String) {
        viewModelScope.launch {
            _snackBarFlow.emit(message)
        }
    }

    fun banUser(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()

            try {
                // 1. Update user field `isBanned = true`
                db.collection("users")
                    .document(userId)
                    .update("isBanned", true)
                    .await()

                // 2. Delete all forums where forumUserPostId == userId
                val forumSnapshots = db.collection("forums")
                    .whereEqualTo("forumUserPostId", userId)
                    .get()
                    .await()

                for (doc in forumSnapshots.documents) {
                    doc.reference.delete().await()
                }

                // 3. Delete all documents in subcollections: notifications, certificates, projects
                val subCollections = listOf("notifications", "certificates", "projects")
                for (collection in subCollections) {
                    val snapshot = db.collection("users")
                        .document(userId)
                        .collection(collection)
                        .get()
                        .await()

                    for (doc in snapshot.documents) {
                        doc.reference.delete().await()
                    }
                }

                _snackBarFlow.emit("User banned successfully.")
                refreshUsers()
            } catch (e: Exception) {
                _snackBarFlow.emit("Failed to ban user: ${e.message}")
            }
        }
    }

    fun unbanUser(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()

            try {
                db.collection("users")
                    .document(userId)
                    .update("isBanned", false)
                    .await()

                _snackBarFlow.emit("User unbanned successfully.")
                refreshUsers()
            } catch (e: Exception) {
                _snackBarFlow.emit("Failed to unban user: ${e.message}")
            }
        }
    }

}
