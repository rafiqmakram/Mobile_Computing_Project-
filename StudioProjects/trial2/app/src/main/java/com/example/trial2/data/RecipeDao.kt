package com.example.trial2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM Recipe WHERE userId = :userId")
    fun getAll(userId: Int): Flow<List<Recipe>>

    @Query("SELECT * FROM Recipe WHERE id = :id AND userId = :userId")
    suspend fun getById(id: Int, userId: Int): Recipe?

    @Insert
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)
}
