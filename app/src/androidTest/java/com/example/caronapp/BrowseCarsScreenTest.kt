package com.example.caronapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.caronapp.model.CarModel
import com.example.caronapp.view.BrowseCarsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowseCarsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEmptyCarListShowsEmptyMessage() {
        composeTestRule.setContent {
            BrowseCarsScreen(carList = emptyList(), onBookCar = {})
        }

        composeTestRule.onNodeWithText("No cars available right now").assertIsDisplayed()
        composeTestRule.onNodeWithText("Check back later for available cars").assertIsDisplayed()
    }

    @Test
    fun testCarListDisplaysCars() {
        val mockCars = listOf(
            CarModel(
                carId = "1",
                carName = "Test Car 1",
                brand = "Toyota",
                model = "Camry",
                pricePerDay = "50",
                stock = 2,
                isAvailable = true,
                fuelType = "Petrol",
                seats = "5",
                transmission = "Automatic"
            )
        )

        composeTestRule.setContent {
            BrowseCarsScreen(carList = mockCars, onBookCar = {})
        }

        composeTestRule.onNodeWithText("Available Cars (1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Car 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rs. 50").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 Available").assertIsDisplayed()
    }
}
