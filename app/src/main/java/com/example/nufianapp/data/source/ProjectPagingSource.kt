package com.example.nufianapp.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nufianapp.data.model.Project
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProjectPagingSource @Inject constructor(
    private val query: Query
) : PagingSource<DocumentSnapshot, Project>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Project>): DocumentSnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Project> {
        return try {
            val currentPage = params.key?.let {
                query.startAfter(it).limit(params.loadSize.toLong()).get().await()
            } ?: query.limit(params.loadSize.toLong()).get().await()

            val lastVisible = currentPage.documents.lastOrNull()

            LoadResult.Page(
                data = currentPage.toObjects(Project::class.java),
                prevKey = null,
                nextKey = lastVisible
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}