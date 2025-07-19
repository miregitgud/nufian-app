package com.example.nufianapp.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.nufianapp.data.firebase.FirebaseModule.storage
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.data.model.Comment
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.News
import com.example.nufianapp.data.model.Notification
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.data.model.User
import com.example.nufianapp.data.source.CertificatePagingSource
import com.example.nufianapp.data.source.CommentPagingSource
import com.example.nufianapp.data.source.ForumPagingSource
import com.example.nufianapp.data.source.NotificationPagingSource
import com.example.nufianapp.data.source.ProjectPagingSource
import com.example.nufianapp.data.source.UserPagingSource
import com.example.nufianapp.domain.model.Response
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.example.nufianapp.data.source.NewsPagingSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    fireStore: FirebaseFirestore
) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val forumsCollection = fireStore.collection(COLLECTION_FORUMS)
    private val newsCollection = fireStore.collection(COLLECTION_NEWS)
    private val usersCollection = fireStore.collection(COLLECTION_USERS)

    private val firestoreBatch = fireStore.batch()
    private val storageReference = FirebaseStorage.getInstance().reference

    companion object {
        private const val COLLECTION_FORUMS = "forums"
        private const val COLLECTION_NEWS = "news"
        private const val COLLECTION_USERS = "users"
    }

    suspend fun getProjectByUserAndProjectId(userId: String, projectId: String): Project {
        val projectSnapshot = usersCollection
            .document(userId)
            .collection("projects")
            .document(projectId)
            .get()
            .await()

        return projectSnapshot.toObject(Project::class.java) ?: throw Exception("Project not found")
    }

    suspend fun getProfileImageUrl(userId: String): String {
        val imageRef = storage.reference.child("profile_images/$userId.jpg")
        return imageRef.downloadUrl.await().toString()
    }

    suspend fun uploadImage(userId: String, imageUri: Uri): String {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "profile.jpg" // You can make this dynamic if needed

        val avatarRef = storageRef.child("avatarUrl/$userId/$fileName")

        // Upload the file
        avatarRef.putFile(imageUri).await()

        // Get the download URL
        return avatarRef.downloadUrl.await().toString()
    }


    private suspend fun cropCenter(imageUri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUri)
                .build()

            val result = (loader.execute(request) as? SuccessResult)?.drawable as? BitmapDrawable
            val bitmap = result?.bitmap
            bitmap?.let { centerCrop(it) }
        }
    }

    private fun centerCrop(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val newWidth = if (width > height) height else width
        val newHeight = if (width > height) height else width

        val x = (width - newWidth) / 2
        val y = (height - newHeight) / 2

        return Bitmap.createBitmap(src, x, y, newWidth, newHeight)
    }

    suspend fun saveBitmapToUri(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        val tempFile = File(context.cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        Uri.fromFile(tempFile)
    }


    suspend fun getImageUrl(userId: String): String {
        val imageRef = storageReference.child("avatarUrl/$userId/profile.jpg")
        return imageRef.downloadUrl.await().toString()
    }

    fun getForum(): Flow<PagingData<Forum>> {
        val query = forumsCollection.orderBy("dateTime", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { ForumPagingSource(query) }
        ).flow
    }

    fun getForumByUserId(userId: String): Flow<PagingData<Forum>> {
        val query = forumsCollection.whereEqualTo("forumUserPostId", userId)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { ForumPagingSource(query) }
        ).flow
    }

    suspend fun getSingleForumData(forumId: String): Forum = try {
        val snapshot = forumsCollection.document(forumId).get().await()
        snapshot.toObject(Forum::class.java)?.apply {
            contentImageUrls = getContentImageUrls(snapshot)
        } ?: throw Exception("Forum data is null")
    } catch (e: Exception) {
        throw Exception("Error fetching forum data", e)
    }

    suspend fun storeForumData(forumData: Map<String, Any>) {
        val forumDocRef = forumsCollection.document()
        val forumId = forumDocRef.id
        val updatedForumData = forumData.toMutableMap().apply { put("forumId", forumId) }
        forumDocRef.set(updatedForumData).await()
    }

    fun generateCommentId(forumId: String): String =
        forumsCollection.document(forumId).collection("comments").document().id

    suspend fun storeForumComment(comment: Comment) {
        val forumDocRef = forumsCollection.document(comment.forumId)
        val commentRef = forumDocRef.collection("comments").document(comment.commentId)
        ioScope.launch {
            commentRef.set(comment.toMap()).await()
            val currentForumDoc = forumDocRef.get().await()
            val currentComments = currentForumDoc.getLong("comments") ?: 0
            forumDocRef.update("comments", currentComments + 1).await()
        }
    }

    fun getCommentsByForumId(forumId: String): Flow<PagingData<Comment>> {
        val query = forumsCollection.document(forumId).collection("comments")
            .orderBy("dateTime", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { CommentPagingSource(query) }
        ).flow
    }

    suspend fun likeForum(forumId: String, userId: String): Boolean {
        val forumDocument = forumsCollection.document(forumId).get().await()
        val forum = forumDocument.toObject(Forum::class.java) ?: throw Exception("Forum not found")
        val isLiked = userId in forum.likedBy

        if (isLiked) {
            forum.likedBy.remove(userId)
            forum.likes -= 1
        } else {
            forum.likedBy.add(userId)
            forum.likes += 1
        }

        forumsCollection.document(forumId).set(forum.toMap()).await()
        return !isLiked
    }

    suspend fun isUserLikedForum(forumId: String, userId: String): Boolean {
        val forumDocument = forumsCollection.document(forumId).get().await()
        val forum = forumDocument.toObject(Forum::class.java) ?: throw Exception("Forum not found")
        return userId in forum.likedBy
    }

    suspend fun getLatestLikes(forumId: String): Int {
        val forumDocument = forumsCollection.document(forumId).get().await()
        val forum = forumDocument.toObject(Forum::class.java) ?: throw Exception("Forum not found")
        return forum.likes
    }

    suspend fun getLatestCommentsCount(forumId: String): Int {
        val forumDocument = forumsCollection.document(forumId).get().await()
        return forumDocument.getLong("comments")?.toInt() ?: 0
    }

    private fun getContentImageUrls(document: DocumentSnapshot): List<String> {
        return (document.get("contentImageUrls") as? List<*>)?.filterIsInstance<String>()
            ?: emptyList()
    }

    suspend fun deleteUserForum(forumId: String) {
        forumsCollection
            .document(forumId).delete().await()
    }

    suspend fun deleteNews(newsId: String) {
        newsCollection
            .document(newsId)
            .delete()
            .await()
    }

    fun getNews(): Flow<PagingData<News>> {
        val query = newsCollection.orderBy("dateTime", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { NewsPagingSource(query) }
        ).flow
    }

    suspend fun storeNewsData(newsData: Map<String, Any?>) {
        val newsDocRef = newsCollection.document()
        val newsId = newsDocRef.id
        val updatedNewsData = newsData.toMutableMap().apply {
            put("newsId", newsId)
            put("contentImageUrl", this["contentImageUrl"] ?: "")
        }
        newsDocRef.set(updatedNewsData).await()
    }

    fun getUser(): Flow<PagingData<User>> {
        val query = usersCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { UserPagingSource(query) }
        ).flow
    }

    suspend fun storeUserData(user: User) {
        val existingUser = usersCollection.whereEqualTo("email", user.email).get().await()
        if (existingUser.isEmpty) {
            usersCollection.document(user.uid).set(user).await()
        } else {
            throw Exception("Email already in use")
        }
    }

    suspend fun getUserDataById(userId: String): User? {
        val document = usersCollection.document(userId).get().await()
        return document.toObject(User::class.java)
    }

    suspend fun getUserData(userId: String): User {
        val document = usersCollection.document(userId).get().await()
        return document.toObject(User::class.java) ?: throw Exception("Failed to parse user data")
    }

    suspend fun storeUserToken(userId: String, token: String) {
        usersCollection.document(userId).update("fcmToken", token).await()
    }

    suspend fun getAllUserTokens(): List<String> {
        val usersSnapshot = usersCollection.get().await()
        return usersSnapshot.documents.mapNotNull { it.getString("fcmToken") }
    }

    suspend fun getUserFcmToken(userId: String): String {
        val userDocRef = usersCollection.document(userId).get().await()
        return userDocRef.getString("fcmToken")
            ?: throw Exception("FCM token not found for user with ID: $userId")
    }

    suspend fun getUserIdByToken(token: String): String {
        val userSnapshot = usersCollection.whereEqualTo("fcmToken", token).get().await()
        if (userSnapshot.documents.isNotEmpty()) {
            return userSnapshot.documents[0].id
        } else {
            throw Exception("User with the given token not found")
        }
    }

    suspend fun storeUserNotification(userId: String, notificationData: Map<String, Any>) {
        val notificationsCollection = usersCollection.document(userId).collection("notifications")
        val notificationDocRef = notificationsCollection.document()
        val notificationId = notificationDocRef.id
        val updatedNotificationData = notificationData.toMutableMap().apply {
            put("notificationId", notificationId)
        }
        notificationDocRef.set(updatedNotificationData).await()
    }

    suspend fun markAllUserNotificationsAsRead(userId: String) {
        val notificationsSnapshot = usersCollection
            .document(userId)
            .collection("notifications")
            .whereEqualTo("isRead", false)
            .get()
            .await()

        val batch = firestoreBatch

        notificationsSnapshot.documents.forEach { document ->
            batch.update(document.reference, "isRead", true)
        }

        if (notificationsSnapshot.documents.isNotEmpty()) {
            batch.commit().await()
        }
    }

    suspend fun markUserNotificationAsRead(userId: String, notificationId: String) {
        val notificationRef = usersCollection
            .document(userId)
            .collection("notifications")
            .document(notificationId)

        notificationRef.update("isRead", true).await()
    }

    fun getUserNotificationsById(userId: String): Flow<PagingData<Notification>> {
        val query = usersCollection.document(userId).collection("notifications")
            .orderBy("dateTime", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { NotificationPagingSource(query) }
        ).flow
    }

    suspend fun getSingleNewsData(newsId: String): News {
        val snapshot = newsCollection.document(newsId).get().await()
        return snapshot.toObject(News::class.java)?.apply {
            contentImageUrls = getContentImageUrls(snapshot)
        } ?: throw Exception("News data is null for ID: $newsId")
    }

    suspend fun storeUserCertificate(userId: String, certificateData: Map<String, Any>): Response<Boolean> {
        return try {
            val certificatesCollection = usersCollection.document(userId).collection("certificates")
            val certificateDocRef = certificatesCollection.document()
            val certificateId = certificateDocRef.id
            val updatedCertificateData = certificateData.toMutableMap().apply {
                put("certificateId", certificateId)
                put("userId", userId) // Ensure userId is set
            }
            certificateDocRef.set(updatedCertificateData).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    fun getCertificateByUserId(userId: String): Flow<PagingData<Certificate>> {
        val query = usersCollection.document(userId).collection("certificates")
            .orderBy("createdAt", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { CertificatePagingSource(query) }
        ).flow
    }

    suspend fun updateUserCertificate(userId: String, certificate: Certificate) {
        usersCollection.document(userId).collection("certificates")
            .document(certificate.certificateId).set(certificate).await()
    }

    suspend fun deleteUserCertificate(userId: String, certificateId: String) {
        usersCollection.document(userId).collection("certificates")
            .document(certificateId).delete().await()
    }

    suspend fun storeUserProject(userId: String, projectData: Map<String, Any>) {
        val projectsCollection = usersCollection.document(userId).collection("projects")
        val projectDocRef = projectsCollection.document()
        val projectId = projectDocRef.id
        val updatedProjectData = projectData.toMutableMap().apply {
            put("projectId", projectId)
        }
        projectDocRef.set(updatedProjectData).await()
    }

    fun getProjectByUserId(userId: String): Flow<PagingData<Project>> {
        val query = usersCollection.document(userId).collection("projects")
            .orderBy("createdAt", Query.Direction.DESCENDING)
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { ProjectPagingSource(query) }
        ).flow
    }

    suspend fun updateUserProject(userId: String, project: Project) {
        usersCollection.document(userId).collection("projects")
            .document(project.projectId).set(project).await()
    }

    suspend fun deleteUserProject(userId: String, projectId: String) {
        usersCollection.document(userId).collection("projects")
            .document(projectId).delete().await()
    }

    suspend fun updateUser(user: User): Response<User> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}