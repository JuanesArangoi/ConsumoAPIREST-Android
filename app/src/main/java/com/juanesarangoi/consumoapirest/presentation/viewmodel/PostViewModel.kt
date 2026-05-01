package com.juanesarangoi.consumoapirest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.data.network.NetworkManager
import com.juanesarangoi.consumoapirest.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

/**
 * ViewModel principal para gestionar los posts
 * Maneja la lógica de negocio, estado de conexión y búsqueda
 * Utiliza Hilt para inyección de dependencias
 */

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository, // Repositorio para datos de posts
    private val networkManager: NetworkManager // Gestor de estado de conexión
) : ViewModel() {
    
    // Estado principal de la UI (posts, loading, errores, búsqueda)
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()
    
    // Estado para mostrar solo posts favoritos
    private val _showFavorites = MutableStateFlow(false)
    val showFavorites: StateFlow<Boolean> = _showFavorites.asStateFlow()
    
    // Posts paginados para scroll infinito
    private val _pagedPosts = MutableStateFlow<Flow<PagingData<PostEntity>>>(emptyFlow())
    val pagedPosts: StateFlow<Flow<PagingData<PostEntity>>> = _pagedPosts.asStateFlow()
    
    // Estado de conexión a internet
    val isConnected: StateFlow<Boolean> = networkManager.isConnected
    
    // Inicialización: cargar posts y observar cambios
    init {
        loadPosts()
        observePosts()
    }
    
    // Observar cambios en los posts desde la base de datos local
    private fun observePosts() {
        viewModelScope.launch {
            if (_showFavorites.value) {
                // Observar solo posts favoritos
                repository.getFavoritePosts().collect { posts ->
                    _uiState.value = _uiState.value.copy(
                        posts = posts,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                // Observar todos los posts
                repository.getAllPosts().collect { posts ->
                    _uiState.value = _uiState.value.copy(
                        posts = posts,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }
    
    // Refrescar posts desde la API (si hay conexión)
    fun refreshPosts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            if (networkManager.isConnected.value) {
                try {
                    // Intentar obtener posts frescos desde la API
                    repository.refreshPosts()
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error de conexión: ${e.message}"
                    )
                }
            } else {
                // Sin conexión, mostrar mensaje informativo
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sin conexión a internet. Mostrando datos locales."
                )
            }
        }
    }
    
    // Cambiar estado de favorito para un post específico
    fun toggleFavorite(postId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(postId)
        }
    }
    
    // Alternar entre mostrar todos los posts o solo favoritos
    fun toggleShowFavorites() {
        _showFavorites.value = !_showFavorites.value
        _pagedPosts.value = repository.getPagedPosts(_showFavorites.value)
    }
    
    // Limpiar mensaje de error
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    // Actualizar texto de búsqueda por título
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        performSearch()
    }
    
    // Actualizar filtro por ID de usuario
    fun updateSearchUserId(userId: String) {
        _uiState.value = _uiState.value.copy(searchUserId = userId)
        performSearch()
    }
    
    // Activar/desactivar modo de búsqueda
    fun toggleSearchMode() {
        val newSearchMode = !_uiState.value.isSearchMode
        _uiState.value = _uiState.value.copy(
            isSearchMode = newSearchMode,
            searchQuery = if (!newSearchMode) "" else _uiState.value.searchQuery,
            searchUserId = if (!newSearchMode) "" else _uiState.value.searchUserId
        )
        
        if (!newSearchMode) {
            // Volver a la vista normal de posts
            observePosts()
        } else {
            // Iniciar búsqueda con criterios actuales
            performSearch()
        }
    }
    
    // Ejecutar búsqueda con los criterios actuales
    private fun performSearch() {
        val titleQuery = _uiState.value.searchQuery
        val userIdStr = _uiState.value.searchUserId
        val userId = userIdStr.toIntOrNull()
        
        viewModelScope.launch {
            repository.searchPosts(titleQuery, userId).collect { posts ->
                _uiState.value = _uiState.value.copy(posts = posts)
            }
        }
    }
    
    // Cargar posts iniciales (desde API si hay conexión, desde BD local si no)
    private fun loadPosts() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        _pagedPosts.value = repository.getPagedPosts(_showFavorites.value)
        
        viewModelScope.launch {
            if (networkManager.isConnected.value) {
                try {
                    // Intentar sincronizar con la API
                    repository.refreshPosts()
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                } catch (e: Exception) {
                    // Si falla la API, mostrar datos locales con error
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error de conexión: ${e.message}"
                    )
                }
            } else {
                // Sin conexión, mostrar solo datos locales
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            }
        }
    }
}

/**
 * Estado de la UI para la pantalla de posts
 * Contiene toda la información necesaria para renderizar la interfaz
 */
data class PostUiState(
    val posts: List<PostEntity> = emptyList(), // Lista de posts a mostrar
    val isLoading: Boolean = false, // Indicador de carga
    val error: String? = null, // Mensaje de error si existe
    val searchQuery: String = "", // Texto de búsqueda por título
    val searchUserId: String = "", // ID de usuario para filtrar
    val isSearchMode: Boolean = false // Indica si está en modo búsqueda
)
