package com.example.trial2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.trial2.ui.theme.Trial2Theme
import com.example.trial2.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel by viewModels<ThemeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Trial2Theme(darkTheme = themeViewModel.isDarkTheme.value) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavGraph(navController, themeViewModel)
                }
            }
        }
    }
}
