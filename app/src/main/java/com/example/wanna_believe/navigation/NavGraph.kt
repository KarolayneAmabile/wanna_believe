package com.example.wanna_believe.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wanna_believe.data.repository.AuthRepository
import com.example.wanna_believe.ui.screens.auth.LoginScreen
import com.example.wanna_believe.ui.screens.auth.RegisterScreen
import com.example.wanna_believe.ui.screens.feed.FeedScreen
import com.example.wanna_believe.ui.screens.newpost.NewPostScreen
import com.example.wanna_believe.ui.screens.profile.ProfileScreen

import com.example.wanna_believe.ui.screens.comments.CommentsScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    // Gerenciamento de sessão (requisito 3a): se já existe usuário logado no
    // Firebase Auth, pula direto para o feed.
    val startDestination =
        if (AuthRepository().currentUser != null) Screen.Feed.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Screen.Feed.route) {
            FeedScreen(
                onNavigateToNewPost = { navController.navigate(Screen.NewPost.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToComments = { postId ->
                    navController.navigate(Screen.Comments.createRoute(postId))
                }
            )
        }
        composable(Screen.NewPost.route) {
            NewPostScreen(onPostPublished = { navController.popBackStack() })
        }
        composable(Screen.Comments.route) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            CommentsScreen(postId = postId, onBack = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
