package com.example.caronapp.repository

import com.example.caronapp.model.BookingModel

interface BookingRepo {

    fun createBooking(bookingModel: BookingModel, callback: (Boolean, String) -> Unit)

    fun getBookingsByUser(userId: String, callback: (Boolean, List<BookingModel>?) -> Unit)

    fun getAllBookings(callback: (Boolean, List<BookingModel>?) -> Unit)

    fun updateBookingStatus(bookingId: String, status: String, callback: (Boolean, String) -> Unit)

    fun cancelBooking(bookingId: String, callback: (Boolean, String) -> Unit)
}
