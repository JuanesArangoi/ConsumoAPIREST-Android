package com.juanesarangoi.consumoapirest.data.local.dao

import androidx.room.*
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>
    
    @Query("SELECT * FROM posts WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoritePosts(): Flow<List<PostEntity>>
    
    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Int): PostEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)
    
    @Update
    suspend fun updatePost(post: PostEntity)
    
    @Delete
    suspend fun deletePost(post: PostEntity)
    
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
    
    @Query("UPDATE posts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
}
