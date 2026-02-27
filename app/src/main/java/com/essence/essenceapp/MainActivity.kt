package com.essence.essenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import com.essence.essenceapp.feature.home.ui.HomeScreen
import com.essence.essenceapp.ui.theme.EssenceAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EssenceAppTheme {
                Text("EssenceApp")
            }
            HomeScreen()
        }
    }
}
