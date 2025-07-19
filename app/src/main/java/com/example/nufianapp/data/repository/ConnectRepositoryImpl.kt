package com.example.nufianapp.data.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectRepositoryImpl @Inject constructor(
    private val fireStoreHelper: FireStoreHelper
) : ConnectRepository {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun getUser(): Flow<PagingData<User>> = fireStoreHelper.getUser()

    override suspend fun getUserDataById(userId: String): Response<User?> =
        withContext(ioScope.coroutineContext) {
            return@withContext try {
                val user = fireStoreHelper.getUserDataById(userId)
                Response.Success(user)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }
}
