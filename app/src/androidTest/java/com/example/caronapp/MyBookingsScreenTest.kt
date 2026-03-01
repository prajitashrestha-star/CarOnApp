package com.example.caronapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.caronapp.model.BookingModel
import com.example.caronapp.view.MyBookingsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyBookingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEmptyBookingsShowsEmptyMessage() {
        composeTestRule.setContent {
            MyBookingsScreen(bookings = emptyList(), onCancelBooking = {})
        }

        composeTestRule.onNodeWithText("No bookings yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Browse available cars and book one!").assertIsDisplayed()
    }

    @Test
    fun testBookingsListDisplaysBookings() {
        val mockBookings = listOf(
            BookingModel(
                bookingId = "b1",
                carId = "1",
                userId = "test_user_id",
                userEmail = "test@example.com",
                carName = "Test Car 1",
                brand = "Toyota",
                model = "Camry",
                pricePerDay = "50",
                startDate = "2026-03-01",
                endDate = "2026-03-05",
                totalDays = "5",
                totalPrice = "250.00",
                status = "Confirmed",
                bookingDate = "2026-02-28 10:00"
            )
        )

        composeTestRule.setContent {
            MyBookingsScreen(bookings = mockBookings, onCancelBooking = {})
        }

        composeTestRule.onNodeWithText("My Bookings (1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Car 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirmed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rs. 250.00").assertIsDisplayed()
    }
}
