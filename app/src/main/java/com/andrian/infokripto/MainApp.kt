package com.andrian.infokripto

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.andrian.infokripto.ui.navigation.NavItem
import com.andrian.infokripto.ui.navigation.Screen
import com.andrian.infokripto.ui.screen.about.AboutScreen
import com.andrian.infokripto.ui.screen.detail.DetailScreen
import com.andrian.infokripto.ui.screen.favorite.FavoriteScreen
import com.andrian.infokripto.ui.screen.home.HomeScreen
import com.andrian.infokripto.ui.theme.InfoKriptoTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainApp(
    modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val systemUiController = rememberSystemUiController()
    val color = Color(0xFF2894B8)

    SideEffect {
        when (currentRoute) {
            Screen.Home.route -> systemUiController.setStatusBarColor(color, darkIcons = true)
            Screen.Favorite.route -> systemUiController.setStatusBarColor(color, darkIcons = true)
            Screen.About.route -> systemUiController.setStatusBarColor(color, darkIcons = true)
            Screen.DetailInfo.route -> systemUiController.setStatusBarColor(color, darkIcons = true)
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(), bottomBar = {
        if (currentRoute != Screen.DetailInfo.route) {
            BottomBar(navController)
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navToDetail = { id ->
                    navController.navigate("detail_info/$id")
                })
            }

            composable(Screen.Favorite.route) {
                FavoriteScreen(navToDetail = { id ->
                    navController.navigate("detail_info/$id")
                })
            }

            composable(Screen.About.route) {
                AboutScreen()
            }

            composable(
                route = "${Screen.DetailInfo.route}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                val id = it.arguments?.getString("id") ?: ""
                DetailScreen(marketId = id, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val color = Color(0xFF2894B8)

    val navigationItems = listOf(
        NavItem(
            title = stringResource(R.string.menu_favorite),
            icon = Icons.Rounded.Favorite,
            screen = Screen.Favorite
        ),
        NavItem(
            title = stringResource(R.string.menu_home),
            icon = Icons.Default.Home,
            screen = Screen.Home
        ),
        NavItem(
            title = stringResource(R.string.menu_About),
            icon = Icons.Default.AccountCircle,
            screen = Screen.About,
        ),
    )

    NavigationBar(
        modifier = modifier.height(56.dp), containerColor = color
    ) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon, contentDescription = item.title,
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                modifier = if (item.screen.route == Screen.About.route) {
                    Modifier.semantics { contentDescription = "about_page" }
                } else {
                    Modifier
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = color,
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.White
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true, device = Devices.PIXEL_7)
fun MainAppPreview() {
    InfoKriptoTheme {
        MainApp()
    }
}