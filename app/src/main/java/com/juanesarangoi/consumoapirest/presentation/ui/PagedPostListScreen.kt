package com.juanesarangoi.consumoapirest.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.presentation.viewmodel.PostViewModel
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagedPostListScreen(
    viewModel: PostViewModel = hiltViewModel(),
    onPostClick: (PostEntity) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val showFavorites by viewModel.showFavorites.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val pagedPosts = viewModel.pagedPosts.collectAsLazyPagingItems()

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
                    // Indicador de conexión
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = if (isConnected) "Conectado" else "Desconectado",
                            tint = if (isConnected) Color.Green else Color.Red
                        )
                        Text(
                            text = if (isConnected) "Online" else "Offline",
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    
                    IconButton(onClick = { viewModel.refreshPosts() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                    IconButton(onClick = { viewModel.toggleShowFavorites() }) {
                        Icon(
                            if (showFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favoritos"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleSearchMode() }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
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
                uiState.isSearchMode -> {
                    // En modo búsqueda, mostrar la pantalla de búsqueda
                    SearchPostListScreen(
                        viewModel = viewModel,
                        onPostClick = onPostClick,
                        onBackClick = { viewModel.toggleSearchMode() }
                    )
                }
                uiState.isLoading && pagedPosts.itemCount == 0 -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                pagedPosts.itemCount == 0 && !uiState.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (showFavorites) "No hay posts favoritos" else "No hay posts disponibles",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (!isConnected) {
                            Text(
                                text = "Conéctate a internet para cargar nuevos posts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = pagedPosts.itemCount,
                            key = { index -> pagedPosts[index]?.id ?: index }
                        ) { index ->
                            pagedPosts[index]?.let { post ->
                                PostItem(
                                    post = post,
                                    onClick = { onPostClick(post) },
                                    onFavoriteClick = { viewModel.toggleFavorite(post.id) }
                                )
                            }
                        }
                        
                        // Loading indicator at the bottom
                        when (pagedPosts.loadState.append) {
                            is androidx.paging.LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                            is androidx.paging.LoadState.Error -> {
                                item {
                                    Text(
                                        text = "Error al cargar más posts",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
            
            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
