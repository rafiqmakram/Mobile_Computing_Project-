package com.example.trial2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trial2.data.Recipe
import com.example.trial2.viewmodel.RecipeViewModel
import com.example.trial2.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    navController: NavController,
    viewModel: RecipeViewModel,
    themeViewModel: ThemeViewModel
) {
    val recipes by viewModel.recipes.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showRecipeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val onLogoutConfirmed = {
        viewModel.logout()
        navController.navigate("login") { popUpTo("recipe_list") { inclusive = true } }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = onLogoutConfirmed,
            onDismiss = { showLogoutDialog = false }
        )
    }

    RecipeDialog(
        showDialog = showRecipeDialog,
        onDismiss = { showRecipeDialog = false },
        viewModel = viewModel,
        recipe = selectedRecipe
    )

    Scaffold(
        topBar = {
            RecipeListTopBar(
                onToggleTheme = { isDark -> themeViewModel.isDarkTheme.value = isDark },
                onLogoutClick = { showLogoutDialog = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showRecipeDialog = true; selectedRecipe = null }) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (currentUser != null) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { selectedRecipe = recipe; showRecipeDialog = true },
                            onDelete = { 
                                viewModel.deleteRecipe(recipe)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Recipe deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoDelete()
                                    }
                                }
                             }
                        )
                    }
                }
            } else {
                LoginPrompt(navController)
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(recipe.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(recipe.description, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Recipe")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListTopBar(onToggleTheme: (Boolean) -> Unit, onLogoutClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = "My Recipes") },
        actions = {
            IconButton(onClick = { onToggleTheme(false) }) {
                Icon(Icons.Default.LightMode, contentDescription = "Light Mode")
            }
            IconButton(onClick = { onToggleTheme(true) }) {
                Icon(Icons.Default.Nightlight, contentDescription = "Dark Mode")
            }
            IconButton(onClick = onLogoutClick) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
private fun LoginPrompt(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please log in to see your recipes")
        Button(onClick = { navController.navigate("login") }) {
            Text("Login")
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Logout") },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}
