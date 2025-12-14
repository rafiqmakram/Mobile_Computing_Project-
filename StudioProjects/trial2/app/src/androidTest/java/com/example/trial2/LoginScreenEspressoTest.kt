package com.example.trial2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class LoginScreenEspressoTest {


    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun loginScreen_isDisplayed_onAppStart() {

        composeTestRule.onNodeWithTag("LoginTitle").assertIsDisplayed()


        composeTestRule.onNodeWithText("Username").assertIsDisplayed()


        composeTestRule.onNodeWithText("Password").assertIsDisplayed()


        composeTestRule.onNodeWithTag("LoginButton").assertIsDisplayed()
    }
}
