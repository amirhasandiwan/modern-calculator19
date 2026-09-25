package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.theme.CalculatorTheme

enum class AppScreen {
    Calculator,
    Billing
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.Calculator) }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            AppScreen.Calculator -> {
                CalculatorScreen(
                    onOpenBilling = { currentScreen = AppScreen.Billing }
                )
            }
            AppScreen.Billing -> {
                BackHandler {
                    currentScreen = AppScreen.Calculator
                }
                BillingScreen(
                    onBackToCalculator = { currentScreen = AppScreen.Calculator }
                )
            }
        }
    }
}
