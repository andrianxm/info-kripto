package com.andrian.infokripto.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorite : Screen("favorite")
    data object About : Screen("about")
    data object DetailInfo : Screen("detail_info")
}