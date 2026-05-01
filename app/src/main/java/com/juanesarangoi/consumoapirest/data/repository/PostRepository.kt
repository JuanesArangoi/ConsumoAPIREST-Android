package com.juanesarangoi.consumoapirest.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.juanesarangoi.consumoapirest.data.local.dao.PostDao
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.data.remote.api.JsonPlaceholderApi
import com.juanesarangoi.consumoapirest.data.remote.dto.PostDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio principal para gestionar datos de posts
 * Implementa el patrón Repository unificando fuentes de datos locales y remotas
 * Maneja tanto operaciones de API como de base de datos local
 */
@Singleton
class PostRepository @Inject constructor(
    private val postDao: PostDao, // DAO para operaciones de base de datos
    private val api: JsonPlaceholderApi // Cliente para llamadas a la API
) {
    
    // Obtener todos los posts desde la base de datos local
    fun getAllPosts(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }
    
    // Obtener solo los posts favoritos
    fun getFavoritePosts(): Flow<List<PostEntity>> {
        return postDao.getFavoritePosts()
    }
    
    // Obtener posts paginados para scroll infinito
    fun getPagedPosts(isFavorite: Boolean = false): Flow<PagingData<PostEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // 20 posts por página
                enablePlaceholders = false,
                prefetchDistance = 5 // Precargar 5 posts antes
            ),
            pagingSourceFactory = {
                PostPagingSource(postDao, isFavorite)
            }
        ).flow
    }
    
    // Obtener un post específico por su ID
    suspend fun getPostById(id: Int): PostEntity? {
        return postDao.getPostById(id)
    }
    
    // Sincronizar posts desde la API y guardarlos localmente
    suspend fun refreshPosts() {
        try {
            // Obtener posts frescos desde la API
            val postsFromApi = api.getPosts()
            // Convertir a entidades y guardar en base de datos
            val postEntities = postsFromApi.map { it.toEntity() }
            postDao.insertPosts(postEntities)
        } catch (e: Exception) {
            throw Exception("Error al cargar posts desde la API: ${e.message}")
        }
    }
    
    // Cambiar estado de favorito para un post
    suspend fun toggleFavorite(postId: Int) {
        val post = postDao.getPostById(postId)
        post?.let {
            postDao.updateFavoriteStatus(postId, !it.isFavorite)
        }
    }
    
    // Eliminar todos los posts de la base de datos
    suspend fun deleteAllPosts() {
        postDao.deleteAllPosts()
    }
    
    // Buscar posts por título y/o userId con lógica combinada
    fun searchPosts(titleQuery: String = "", userId: Int? = null): Flow<List<PostEntity>> {
        return when {
            // Búsqueda combinada: título y userId
            titleQuery.isNotBlank() && userId != null -> {
                postDao.searchPostsByTitleAndUserIdFlow(titleQuery, userId)
            }
            // Solo búsqueda por título
            titleQuery.isNotBlank() -> {
                postDao.searchPostsByTitleFlow(titleQuery)
            }
            // Solo filtro por userId
            userId != null -> {
                postDao.searchPostsByUserIdFlow(userId)
            }
            // Sin filtros, mostrar todos
            else -> {
                postDao.getAllPosts()
            }
        }
    }
}

// Función de extensión para convertir DTO de API a entidad de base de datos
private fun PostDto.toEntity(): PostEntity {
    return PostEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        body = this.body,
        isFavorite = false, // Los posts nuevos no son favoritos por defecto
        timestamp = System.currentTimeMillis() // Timestamp actual
    )
}
