package com.example.trial2.data

import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao, private val userDao: UserDao) {

    fun getRecipes(userId: Int): Flow<List<Recipe>> = recipeDao.getAll(userId)

    suspend fun getRecipeById(id: Int, userId: Int): Recipe? = recipeDao.getById(id, userId)

    suspend fun insert(recipe: Recipe) = recipeDao.insert(recipe)

    suspend fun update(recipe: Recipe) = recipeDao.update(recipe)

    suspend fun delete(recipe: Recipe) = recipeDao.delete(recipe)


    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)

    suspend fun insertUser(user: User) = userDao.insert(user)
}
