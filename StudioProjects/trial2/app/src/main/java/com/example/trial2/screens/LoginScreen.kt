package com.example.trial2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.trial2.utils.HashingUtils
import com.example.trial2.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

private fun isPasswordValid(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: RecipeViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showSnackbar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val onLoginOrRegister: () -> Unit = {
        val hashedPassword = HashingUtils.hashPassword(password)
        val onLoginSuccess = { 
            navController.navigate("recipe_list") {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        if (isLogin) {
            viewModel.login(username, hashedPassword) { success ->
                if (success) {
                    onLoginSuccess()
                } else {
                    showSnackbar("Invalid username or password")
                }
            }
        } else {
            if (isPasswordValid(password)) {
                viewModel.register(username, hashedPassword) { success ->
                    if (success) {
                        onLoginSuccess()
                    } else {
                        showSnackbar("Username already exists")
                    }
                }
            } else {
                showSnackbar("Password must be at least 8 characters long and contain an uppercase letter, a lowercase letter, and a number.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (isLogin) "Login" else "Register",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .testTag("LoginTitle")
                        )
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onLoginOrRegister,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("LoginButton")
                        ) {
                            Text(if (isLogin) "Login" else "Register")
                        }
                        TextButton(onClick = { isLogin = !isLogin }) {
                            Text(if (isLogin) "Don't have an account? Register" else "Already have an account? Login")
                        }
                    }
                }
            }
        }
    }
}
