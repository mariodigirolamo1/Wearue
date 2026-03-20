package com.clothesmatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clothesmatcher.ui.ColorMatcherScreen
import com.clothesmatcher.ui.FavoritesScreen
import com.clothesmatcher.ui.MainMenuScreen
import com.clothesmatcher.ui.MatchingScreen
import com.clothesmatcher.ui.OutputScreen
import com.clothesmatcher.ui.SplashScreen
import com.clothesmatcher.ui.theme.ClothesMatcherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClothesMatcherTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(onAnimationFinished = {
                            navController.navigate("main_menu") {
                                popUpTo("splash") { inclusive = true }
                            }
                        })
                    }
                    composable("main_menu") {
                        MainMenuScreen(
                            onMakeMatchClick = {
                                navController.navigate("matching_screen")
                            },
                            onViewFavoritesClick = {
                                navController.navigate("favorites")
                            }
                        )
                    }
                    composable("matching_screen") {
                        MatchingScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToColorMatcher = { selectedCategories ->
                                val categoriesJson = selectedCategories.joinToString(",")
                                navController.navigate("color_matcher/$categoriesJson")
                            }
                        )
                    }
                    composable(
                        route = "color_matcher/{categories}",
                        arguments = listOf(navArgument("categories") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val categories = backStackEntry.arguments?.getString("categories") ?: ""
                        ColorMatcherScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToResults = { paletteId ->
                                navController.navigate("output/$categories/$paletteId")
                            }
                        )
                    }
                    composable(
                        route = "output/{categories}/{paletteId}",
                        arguments = listOf(
                            navArgument("categories") { type = NavType.StringType },
                            navArgument("paletteId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val categories = backStackEntry.arguments?.getString("categories")?.split(",") ?: emptyList()
                        val paletteId = backStackEntry.arguments?.getString("paletteId") ?: ""
                        OutputScreen(
                            categories = categories,
                            paletteId = paletteId,
                            onBack = { navController.popBackStack() },
                            onNavigateToMainMenu = {
                                navController.navigate("main_menu") {
                                    popUpTo("main_menu") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToDetails = { paletteId, categories ->
                                val categoriesStr = categories.joinToString(",")
                                navController.navigate("output/$categoriesStr/$paletteId")
                            }
                        )
                    }
                }
            }
        }
    }
}
