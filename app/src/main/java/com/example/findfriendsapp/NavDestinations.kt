package com.example.findfriendsapp

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface NavDestination {
    val route: String
}

object Registration : NavDestination {
    override val route: String = "Registration"
}

object Maps : NavDestination {
    override val route: String = "Maps"
    const val args = "username"
    val navArgs = listOf(navArgument(name = args) { type = NavType.StringType })
    val routeWithArgs = "$route/{$args}"
}