package com.eastercalculator.app

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.eastercalculator.app.ui.MainAppScreen
import com.eastercalculator.app.ui.theme.EasterCalculatorTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainAppScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            EasterCalculatorTheme {
                MainAppScreen()
            }
        }
    }

    @Test
    fun calculatorTabs_exist() {
        composeTestRule.onNodeWithText("Hesapla").assertExists()
        composeTestRule.onNodeWithText("Koleksiyon").assertExists()
        composeTestRule.onNodeWithText("Sıralama").assertExists()
        composeTestRule.onNodeWithText("Profil").assertExists()
    }
}
