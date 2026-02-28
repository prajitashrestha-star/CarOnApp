package com.example.caronapp.model

data class BookingModel(
    var bookingId: String = "",
    var userId: String = "",
    var userEmail: String = "",
    var carId: String = "",
    var carName: String = "",
    var brand: String = "",
    var model: String = "",
    var pricePerDay: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var totalDays: String = "",
    var totalPrice: String = "",
    var status: String = "Pending",   // Pending, Confirmed, Completed, Cancelled
    var bookingDate: String = ""       // When the booking was made
)
