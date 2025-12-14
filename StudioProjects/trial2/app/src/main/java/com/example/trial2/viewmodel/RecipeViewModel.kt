package com.example.trial2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trial2.data.Recipe
import com.example.trial2.data.RecipeRepository
import com.example.trial2.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val repo: RecipeRepository) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private var lastDeletedRecipe: Recipe? = null

    fun login(username: String, passwordHash: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repo.getUserByUsername(username)
            if (user != null && user.passwordHash == passwordHash) {
                _currentUser.value = user
                loadRecipes(user.userId)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun register(username: String, passwordHash: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (repo.getUserByUsername(username) == null) {
                val newUser = User(username = username, passwordHash = passwordHash)
                repo.insertUser(newUser)
                val user = repo.getUserByUsername(username)
                if (user != null) {
                    _currentUser.value = user
                    loadRecipes(user.userId)
                    onResult(true)
                } else {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _recipes.value = emptyList()
    }

    fun loadRecipes(userId: Int) {
        viewModelScope.launch {
            repo.getRecipes(userId).collect { list ->
                _recipes.value = list
            }
        }
    }

    fun addRecipe(title: String, description: String) {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                repo.insert(Recipe(userId = user.userId, title = title, description = description))
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                if (recipe.userId == user.userId) {
                    repo.update(recipe)
                }
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                if (recipe.userId == user.userId) {
                    lastDeletedRecipe = recipe
                    repo.delete(recipe)
                }
            }
        }
    }

    fun undoDelete() {
        lastDeletedRecipe?.let {
            viewModelScope.launch {
                repo.insert(it)
                lastDeletedRecipe = null
            }
        }
    }

    fun getRecipeById(id: Int, onResult: (Recipe?) -> Unit) {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                onResult(repo.getRecipeById(id, user.userId))
            }
        }
    }
}
