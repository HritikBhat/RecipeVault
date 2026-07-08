package com.hritik.recipevault.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddRecipe : Screen("add_recipe")
    object EditRecipe : Screen("edit_recipe/{recipeId}") {
        fun passId(id: Long) = "edit_recipe/$id"
    }
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun passId(id: Long) = "recipe_detail/$id"
    }
    object Collections : Screen("collections")
    object Profile : Screen("profile")
    object Premium : Screen("premium")
}
