package com.eastercalculator.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eastercalculator.app.ui.MainAppScreen
import com.eastercalculator.app.ui.screens.CalculatorViewModel
import com.eastercalculator.app.ui.theme.EasterCalculatorTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      EasterCalculatorTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val app = application
          val vm: CalculatorViewModel = viewModel {
            CalculatorViewModel(app)
          }
          MainAppScreen(viewModel = vm)
        }
      }
    }
  }
}
