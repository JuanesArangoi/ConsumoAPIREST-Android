package com.juanesarangoi.consumoapirest.data.repository

import com.juanesarangoi.consumoapirest.data.local.dao.PostDao
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.data.remote.api.JsonPlaceholderApi
import com.juanesarangoi.consumoapirest.data.remote.dto.PostDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val postDao: PostDao,
    private val api: JsonPlaceholderApi
) {
    
    fun getAllPosts(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }
    
    fun getFavoritePosts(): Flow<List<PostEntity>> {
        return postDao.getFavoritePosts()
    }
    
    suspend fun getPostById(id: Int): PostEntity? {
        return postDao.getPostById(id)
    }
    
    suspend fun refreshPosts() {
        try {
            val postsFromApi = api.getPosts()
            val postEntities = postsFromApi.map { it.toEntity() }
            postDao.insertPosts(postEntities)
        } catch (e: Exception) {
            throw Exception("Error al cargar posts desde la API: ${e.message}")
        }
    }
    
    suspend fun toggleFavorite(postId: Int) {
        val post = postDao.getPostById(postId)
        post?.let {
            postDao.updateFavoriteStatus(postId, !it.isFavorite)
        }
    }
    
    suspend fun deleteAllPosts() {
        postDao.deleteAllPosts()
    }
}

private fun PostDto.toEntity(): PostEntity {
    return PostEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        body = this.body,
        isFavorite = false,
        timestamp = System.currentTimeMillis()
    )
}
