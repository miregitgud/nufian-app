package com.example.nufianapp.data.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface CertificateRepository {
    suspend fun storeCertificate(certificate: Certificate): AddForumResponse
    fun getCertificatesByUserId(userId: String): Flow<PagingData<Certificate>>
    suspend fun updateCertificate(certificate: Certificate): Response<Unit>
    suspend fun deleteCertificate(certificateId: String): Response<Unit>
}