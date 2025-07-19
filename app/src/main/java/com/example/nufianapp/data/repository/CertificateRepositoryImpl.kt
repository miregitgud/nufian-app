package com.example.nufianapp.data.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.firebase.StorageHelper
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CertificateRepositoryImpl @Inject constructor(
    private val fireStoreHelper: FireStoreHelper,
    private val storageHelper: StorageHelper,
    userRepository: UserRepository
) : CertificateRepository {

    private val currentUser = userRepository.currentUser?.uid ?: throw Exception(
        ErrorUtils.getFriendlyErrorMessage(Exception("User not logged in"))
    )

    override fun getCertificatesByUserId(userId: String): Flow<PagingData<Certificate>> =
        fireStoreHelper.getCertificateByUserId(userId)
            .catch { e ->
                emit(PagingData.empty())
                ErrorUtils.getFriendlyErrorMessage(e)
            }

    override suspend fun storeCertificate(
        certificate: Certificate,
    ): Response<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedCertificate = certificate.certificateImageUri?.let {
                val uniqueId = java.util.UUID.randomUUID().toString()
                val imagePath = "certificates/$currentUser/$uniqueId.jpg"
                val imageUrl = storageHelper.uploadImage(imagePath, it)
                certificate.copy(
                    userId = currentUser,
                    certificateImageUrl = imageUrl,
                    certificateId = uniqueId
                )
            } ?: certificate.copy(userId = currentUser)

            fireStoreHelper.storeUserCertificate(currentUser, updatedCertificate.toMap())
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
        }
    }

    override suspend fun updateCertificate(certificate: Certificate): Response<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                fireStoreHelper.updateUserCertificate(currentUser, certificate)
                Response.Success(Unit)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }

    override suspend fun deleteCertificate(certificateId: String): Response<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                fireStoreHelper.deleteUserCertificate(currentUser, certificateId)
                Response.Success(Unit)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }
}
