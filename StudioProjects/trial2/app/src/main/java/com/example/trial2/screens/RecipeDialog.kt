package com.example.trial2.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trial2.data.Recipe
import com.example.trial2.viewmodel.RecipeViewModel

@Composable
fun RecipeDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: RecipeViewModel,
    recipe: Recipe?
) {
    if (showDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        LaunchedEffect(recipe) {
            title = recipe?.title ?: ""
            description = recipe?.description ?: ""
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(if (recipe == null) "Add Recipe" else "Edit Recipe") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("How to make it") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (recipe == null) {
                        viewModel.addRecipe(title, description)
                    } else {
                        viewModel.currentUser.value?.let {
                            val updatedRecipe = recipe.copy(
                                title = title,
                                description = description
                            )
                            viewModel.updateRecipe(updatedRecipe)
                        }
                    }
                    onDismiss()
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
