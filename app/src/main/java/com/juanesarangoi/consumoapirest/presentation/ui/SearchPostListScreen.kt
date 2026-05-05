package com.juanesarangoi.consumoapirest.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.presentation.viewmodel.PostViewModel

/**
 * Pantalla de búsqueda de posts
 * Permite buscar posts por título y filtrar por ID de usuario
 * Muestra resultados en tiempo real desde la base de datos local
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPostListScreen(
    viewModel: PostViewModel = hiltViewModel(), // ViewModel inyectado con Hilt
    onPostClick: (PostEntity) -> Unit = {}, // Acción al hacer click en un post
    onBackClick: () -> Unit // Acción para volver atrás
) {
    // Obtener estados del ViewModel en tiempo real
    val uiState by viewModel.uiState.collectAsState() // Estado general de la UI
    val isConnected by viewModel.isConnected.collectAsState() // Estado de conexión

    // Efecto para manejar errores (podría mostrar un snackbar)
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Aquí se podría mostrar notificación de error
        }
    }

    // Estructura principal de la pantalla con barra superior
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Posts") }, // Título de la pantalla
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Indicador visual del estado de conexión
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
                }
            )
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección de búsqueda con campos de filtrado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Buscar Posts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Campo de búsqueda por título del post
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        label = { Text("Buscar por título") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Campo de filtrado por ID de usuario
                    OutlinedTextField(
                        value = uiState.searchUserId,
                        onValueChange = { viewModel.updateSearchUserId(it) },
                        label = { Text("Filtrar por ID de Usuario") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Filtrar")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    // Información de búsqueda activa
                    if (uiState.searchQuery.isNotBlank() || uiState.searchUserId.isNotBlank()) {
                        Text(
                            text = "Mostrando resultados para: " +
                                    if (uiState.searchQuery.isNotBlank() && uiState.searchUserId.isNotBlank()) {
                                        "título '${uiState.searchQuery}' y usuario ${uiState.searchUserId}"
                                    } else if (uiState.searchQuery.isNotBlank()) {
                                        "título '${uiState.searchQuery}'"
                                    } else {
                                        "usuario ${uiState.searchUserId}"
                                    },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            // Sección de resultados de búsqueda
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    // Mostrar indicador de carga
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    // Mostrar mensaje cuando no hay resultados
                    uiState.posts.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (uiState.searchQuery.isNotBlank() || uiState.searchUserId.isNotBlank()) {
                                    "No se encontraron resultados"
                                } else {
                                    "Ingresa criterios de búsqueda"
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (!isConnected) {
                                Text(
                                    text = "Conéctate a internet para buscar nuevos posts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                    // Mostrar lista de resultados
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
                
                // Mensaje de error si existe
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
}
