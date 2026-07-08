package com.example.wanna_believe.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Feed : Screen("feed")
    data object NewPost : Screen("new_post")
    data object Profile : Screen("profile")
    data object Comments : Screen("comments/{postId}") {
        fun createRoute(postId: String) = "comments/$postId"
    }
}
