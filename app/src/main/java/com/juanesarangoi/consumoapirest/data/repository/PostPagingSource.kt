package com.juanesarangoi.consumoapirest.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.juanesarangoi.consumoapirest.data.local.dao.PostDao
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity

class PostPagingSource(
    private val postDao: PostDao,
    private val isFavorite: Boolean = false
) : PagingSource<Int, PostEntity>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostEntity> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            
            // Para JSONPlaceholder, simularemos paginación local
            // ya que la API real no soporta paginación en posts
            val allPosts = if (isFavorite) {
                postDao.getFavoritePostsSync()
            } else {
                postDao.getAllPostsSync()
            }
            
            val fromIndex = (page - 1) * pageSize
            val toIndex = minOf(fromIndex + pageSize, allPosts.size)
            
            val posts = if (fromIndex < allPosts.size) {
                allPosts.subList(fromIndex, toIndex)
            } else {
                emptyList()
            }
            
            LoadResult.Page(
                data = posts,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (toIndex < allPosts.size) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, PostEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
