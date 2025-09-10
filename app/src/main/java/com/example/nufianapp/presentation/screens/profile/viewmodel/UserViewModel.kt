package com.example.nufianapp.presentation.screens.profile.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.firebase.FirebaseModule.firestore
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.repository.UserRepository
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.ImageHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth, // Inject FirebaseAuth
    private val fireStoreHelper: FireStoreHelper, // Inject FireStoreHelper
    @ApplicationContext private val context: Context // Inject Context
) : ViewModel() {

    private val _userByIdData = MutableStateFlow<Response<User?>>(Response.Loading)
    val userByIdData: StateFlow<Response<User?>> = _userByIdData.asStateFlow()
    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun uploadImage(userId: String, imageUri: String?): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            try {
                val croppedBitmap = ImageHelper(context).cropCenter(imageUri)
                if (croppedBitmap != null) {
                    val croppedUri = fireStoreHelper.saveBitmapToUri(croppedBitmap)  // Ensure this function returns a Uri
                    val imageUrl = fireStoreHelper.uploadImage(userId, croppedUri)
                    result.postValue(imageUrl)
                } else {
                    result.postValue("")
                }
            } catch (e: Exception) {
                // Handle error
                result.postValue("")
            }
        }
        return result
    }

    private suspend fun saveBitmapToUri(bitmap: Bitmap) {
        // Implement a method to save the cropped Bitmap to a Uri
        // This can involve saving the Bitmap to the cache directory and getting its Uri
    }

    fun observeCurrentUser(uid: String) {
        firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    _currentUser.value = snapshot.toObject(User::class.java)
                }
            }
    }


    fun loadCurrentUser() {
        val userId = currentUserId
        if (userId != null) {
            viewModelScope.launch {
                try {
                    val result = userRepository.getUserDataById(userId)
                    if (result is Response.Success) {
                        _currentUser.value = result.data
                    } else {
                        _currentUser.value = null
                    }
                } catch (e: Exception) {
                    _currentUser.value = null
                }
            }
        }
    }


    fun loadAvatarUrl() {
        val userId = currentUserId
        if (userId != null) {
            viewModelScope.launch {
                try {
                    val url = userRepository.getImageUrl(userId)
                    Log.d("UserViewModel", "Fetched avatarUrl = $url")
                    _avatarUrl.value = url
                } catch (e: Exception) {
                    _avatarUrl.value = null
                    Log.e("UserViewModel", "Error loading avatarUrl", e)
                }
            }
        }
    }

    fun fetchUserById(userId: String) {
        viewModelScope.launch {
            _userByIdData.value = try {
                userRepository.getUserDataById(userId)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _userByIdData.value = try {
                fireStoreHelper.updateUser(user)
                Response.Success(user)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun <T> handleException(e: Exception): Response<T> {
        return Response.Failure(e)
    }

    // Get current user ID from Firebase Auth
    val currentUserId: String?
        get() = auth.currentUser?.uid
}