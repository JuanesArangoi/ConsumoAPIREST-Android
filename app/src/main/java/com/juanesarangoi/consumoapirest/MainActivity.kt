package com.juanesarangoi.consumoapirest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.juanesarangoi.consumoapirest.data.local.entity.PostEntity
import com.juanesarangoi.consumoapirest.presentation.ui.PostDetailScreen
import com.juanesarangoi.consumoapirest.presentation.ui.PostListScreen
import com.juanesarangoi.consumoapirest.presentation.viewmodel.PostViewModel
import com.juanesarangoi.consumoapirest.ui.theme.ConsumoAPIRESTAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConsumoAPIRESTAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostNavigation()
                }
            }
        }
    }
}

@Composable
fun PostNavigation() {
    val navController = rememberNavController()
    val viewModel: PostViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "post_list"
    ) {
        composable("post_list") {
            PostListScreen(
                viewModel = viewModel,
                onPostClick = { post ->
                    navController.navigate("post_detail/${post.id}")
                }
            )
        }
        
        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
            
            LaunchedEffect(postId) {
                // Post details will be handled by the ViewModel
            }
            
            val uiState by viewModel.uiState.collectAsState()
            val post = uiState.posts.find { it.id == postId }
            
            post?.let {
                PostDetailScreen(
                    post = it,
                    onBackClick = { navController.popBackStack() },
                    onFavoriteClick = { viewModel.toggleFavorite(it.id) }
                )
            }
        }
    }
}
