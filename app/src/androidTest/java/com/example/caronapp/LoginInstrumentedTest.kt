package com.example.caronapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.caronapp.view.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun testLoginUI() {
        composeRule.onNodeWithTag("emailInput").assertExists()
        composeRule.onNodeWithTag("passwordInput").assertExists()
        composeRule.onNodeWithTag("loginButton").assertExists()
    }

    @Test
    fun testLoginInput() {
        composeRule.onNodeWithTag("emailInput").performTextInput("admin@caronapp.com")
        composeRule.onNodeWithTag("passwordInput").performTextInput("Admin@123")
        
        // Use performScrollTo to ensure the button is in view
        composeRule.onNodeWithTag("loginButton").performScrollTo().performClick()
    }
}
