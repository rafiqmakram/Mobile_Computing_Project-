package com.example.trial2

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trial2.data.AppDatabase
import com.example.trial2.data.RecipeRepository
import com.example.trial2.screens.LoginScreen
import com.example.trial2.screens.RecipeListScreen
import com.example.trial2.viewmodel.RecipeViewModel
import com.example.trial2.viewmodel.RecipeViewModelFactory
import com.example.trial2.viewmodel.ThemeViewModel

@Composable
fun NavGraph(navController: NavHostController, themeViewModel: ThemeViewModel) {


    val context = LocalContext.current


    val db = AppDatabase.getDatabase(context)
    val repository = RecipeRepository(db.recipeDao(), db.userDao())


    val viewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModelFactory(repository)
    )

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController, viewModel)
        }

        composable("recipe_list") {
            RecipeListScreen(navController, viewModel, themeViewModel)
        }
    }
}
