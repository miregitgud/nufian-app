package com.example.nufianapp.domain.model

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import java.io.IOException
import java.util.concurrent.TimeoutException

object ErrorUtils {

    private const val TAG = "ErrorUtils"

    fun getFriendlyErrorMessage(exception: Throwable): String {
        logError(exception)
        return when (exception) {
            is IOException -> "Network error. Please check your internet connection."
            is TimeoutException -> "Request timed out. Please try again later."
            is FirebaseAuthException -> getFirebaseAuthErrorMessage(exception)
            is FirebaseFirestoreException -> getFirestoreErrorMessage(exception)
            is StorageException -> getStorageErrorMessage(exception)
            is FirebaseException -> "A Firebase error occurred. Please try again."
            else -> "An unexpected error occurred. Please try again."
        }
    }

    private fun logError(exception: Throwable) {
        Log.e(TAG, "Error occurred: ${exception.localizedMessage}", exception)
    }

    private fun getFirebaseAuthErrorMessage(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_INVALID_CUSTOM_TOKEN" -> "The custom token format is incorrect. Please check the documentation."
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> "The custom token corresponds to a different audience."
            "ERROR_INVALID_CREDENTIAL" -> "The supplied auth credential is malformed or has expired."
            "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
            "ERROR_WRONG_PASSWORD" -> "The password is invalid or the user does not have a password."
            "ERROR_USER_MISMATCH" -> "The supplied credentials do not correspond to the previously signed in user."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "This operation is sensitive and requires recent authentication. Log in again before retrying this request."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with the same email address but different sign-in credentials."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already associated with a different user account."
            "ERROR_USER_DISABLED" -> "The user account has been disabled by an administrator."
            "ERROR_USER_TOKEN_EXPIRED" -> "The user's credential is no longer valid. The user must sign in again."
            "ERROR_USER_NOT_FOUND" -> "There is no user record corresponding to this identifier."
            "ERROR_INVALID_USER_TOKEN" -> "The user's credential is no longer valid. The user must sign in again."
            "ERROR_OPERATION_NOT_ALLOWED" -> "This operation is not allowed. Please contact support."
            "ERROR_WEAK_PASSWORD" -> "The given password is invalid."
            "ERROR_TOO_MANY_REQUESTS" -> "We have blocked all requests from this device due to unusual activity. Try again later."
            "ERROR_MISSING_EMAIL" -> "An email address must be provided."
            "ERROR_MISSING_PASSWORD" -> "A password must be provided."
            else -> "Authentication error. Please try again."
        }
    }

    private fun getFirestoreErrorMessage(exception: FirebaseFirestoreException): String {
        return when (exception.code) {
            FirebaseFirestoreException.Code.CANCELLED -> "The operation was cancelled."
            FirebaseFirestoreException.Code.UNKNOWN -> "An unknown error occurred."
            FirebaseFirestoreException.Code.INVALID_ARGUMENT -> "An invalid argument was provided."
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> "The operation took too long to complete."
            FirebaseFirestoreException.Code.NOT_FOUND -> "The requested document was not found."
            FirebaseFirestoreException.Code.ALREADY_EXISTS -> "The document already exists."
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "You do not have permission to perform this operation."
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> "Resource exhaustion occurred. Please try again later."
            FirebaseFirestoreException.Code.FAILED_PRECONDITION -> "The operation was rejected due to a failed precondition."
            FirebaseFirestoreException.Code.ABORTED -> "The operation was aborted."
            FirebaseFirestoreException.Code.OUT_OF_RANGE -> "The operation was attempted outside of a valid range."
            FirebaseFirestoreException.Code.UNIMPLEMENTED -> "The operation is not implemented."
            FirebaseFirestoreException.Code.INTERNAL -> "An internal error occurred."
            FirebaseFirestoreException.Code.UNAVAILABLE -> "The service is currently unavailable. Please try again later."
            FirebaseFirestoreException.Code.DATA_LOSS -> "Data loss occurred."
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> "You need to authenticate to perform this operation."
            else -> "A Firestore error occurred. Please try again."
        }
    }

    private fun getStorageErrorMessage(exception: StorageException): String {
        return when (exception.errorCode) {
            StorageException.ERROR_UNKNOWN -> "An unknown error occurred."
            StorageException.ERROR_OBJECT_NOT_FOUND -> "The specified object was not found."
            StorageException.ERROR_BUCKET_NOT_FOUND -> "The specified bucket was not found."
            StorageException.ERROR_PROJECT_NOT_FOUND -> "The specified project was not found."
            StorageException.ERROR_QUOTA_EXCEEDED -> "Quota exceeded. Please try again later."
            StorageException.ERROR_NOT_AUTHORIZED -> "You are not authorized to perform this operation."
            StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> "The operation retry limit was exceeded. Please try again."
            StorageException.ERROR_INVALID_CHECKSUM -> "The file checksum is invalid."
            StorageException.ERROR_CANCELED -> "The operation was cancelled."
            else -> "A Storage error occurred. Please try again."
        }
    }

}
