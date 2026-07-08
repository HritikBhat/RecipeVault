package com.hritik.recipevault.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hritik.recipevault.ui.screen.addedit.AddEditRecipeScreen
import com.hritik.recipevault.ui.screen.collection.CollectionScreen
import com.hritik.recipevault.ui.screen.detail.RecipeDetailScreen
import com.hritik.recipevault.ui.screen.home.HomeScreen
import com.hritik.recipevault.ui.screen.login.LoginScreen
import com.hritik.recipevault.ui.screen.premium.PremiumScreen
import com.hritik.recipevault.ui.screen.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToAddRecipe = {
                    navController.navigate(Screen.AddRecipe.route)
                },
                onNavigateToEditRecipe = { recipeId ->
                    navController.navigate(Screen.EditRecipe.passId(recipeId))
                },
                onNavigateToRecipeDetail = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.passId(recipeId))
                },
                onNavigateToCollections = {
                    navController.navigate(Screen.Collections.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = Screen.AddRecipe.route
        ) {
            AddEditRecipeScreen(
                onPopBackStack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditRecipe.route,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                }
            )
        ) {
            AddEditRecipeScreen(
                onPopBackStack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                }
            )
        ) {
            RecipeDetailScreen(
                onPopBackStack = {
                    navController.popBackStack()
                },
                onNavigateToEditRecipe = { recipeId ->
                    navController.navigate(Screen.EditRecipe.passId(recipeId))
                }
            )
        }

        composable(route = Screen.Collections.route) {
            CollectionScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCollections = {
                    navController.navigate(Screen.Collections.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        composable(route = Screen.Premium.route) {
            PremiumScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
