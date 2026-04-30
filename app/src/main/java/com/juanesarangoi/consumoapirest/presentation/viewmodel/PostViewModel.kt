package com.juanesarangoi.consumoapirest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()
    
    private val _showFavorites = MutableStateFlow(false)
    val showFavorites: StateFlow<Boolean> = _showFavorites.asStateFlow()
    
    init {
        loadPosts()
        observePosts()
    }
    
    private fun observePosts() {
        viewModelScope.launch {
            if (_showFavorites.value) {
                repository.getFavoritePosts().collect { posts ->
                    _uiState.value = _uiState.value.copy(
                        posts = posts,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
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
    
    fun refreshPosts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                repository.refreshPosts()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun toggleFavorite(postId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(postId)
        }
    }
    
    fun toggleShowFavorites() {
        _showFavorites.value = !_showFavorites.value
        observePosts()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun loadPosts() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        refreshPosts()
    }
}

data class PostUiState(
    val posts: List<PostEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
