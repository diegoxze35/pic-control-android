package com.example.piccontrol.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.example.piccontrol.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent { 
        MainScreen(
            uiState = UiState(p1 = 123),
            pairedDevices = emptyList(),
            onDeviceSelected = {},
            onFanSpeedChange = {}
        ) 
    }
  }

  @Test
  fun ui_elements_exist() {
    composeTestRule.onNodeWithText("PIC Dashboard").assertExists()
    composeTestRule.onNodeWithText("123").assertExists()
  }
}

