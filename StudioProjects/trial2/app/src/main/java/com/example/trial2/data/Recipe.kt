package com.example.trial2.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // Foreign key to User
    val title: String,
    val description: String
)
