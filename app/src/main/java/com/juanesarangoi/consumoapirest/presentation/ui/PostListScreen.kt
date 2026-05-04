package com.juanesarangoi.consumoapirest.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.presentation.viewmodel.PostViewModel

/**
 * Pantalla de lista simple de posts (sin paginación)
 * Usada como fallback o para vistas específicas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostViewModel = hiltViewModel(),
    onPostClick: (PostEntity) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val showFavorites by viewModel.showFavorites.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Handle error (show snackbar, etc.)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (showFavorites) "Posts Favoritos" else "Todos los Posts") 
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshPosts() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                    IconButton(onClick = { viewModel.toggleShowFavorites() }) {
                        Icon(
                            if (showFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favoritos"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.posts.isEmpty() -> {
                    Text(
                        text = "No hay posts disponibles",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.posts) { post ->
                            PostItem(
                                post = post,
                                onClick = { onPostClick(post) },
                                onFavoriteClick = { viewModel.toggleFavorite(post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
