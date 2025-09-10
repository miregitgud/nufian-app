package com.example.nufianapp.domain.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ConnectRepository {
    fun getUser(): Flow<PagingData<User>>
    suspend fun getUserDataById(userId: String): Response<User?>
}