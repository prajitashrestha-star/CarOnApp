package com.example.caronapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caronapp.model.BookingModel
import com.example.caronapp.repository.BookingRepo

class BookingViewModel(val repo: BookingRepo) : ViewModel() {

    private val _userBookings = MutableLiveData<List<BookingModel>?>()
    val userBookings: MutableLiveData<List<BookingModel>?>
        get() = _userBookings

    private val _allBookings = MutableLiveData<List<BookingModel>?>()
    val allBookings: MutableLiveData<List<BookingModel>?>
        get() = _allBookings

    fun createBooking(bookingModel: BookingModel, callback: (Boolean, String) -> Unit) {
        repo.createBooking(bookingModel, callback)
    }

    fun getBookingsByUser(userId: String) {
        repo.getBookingsByUser(userId) { success, bookings ->
            if (success) {
                _userBookings.postValue(bookings)
            }
        }
    }

    fun getAllBookings() {
        repo.getAllBookings { success, bookings ->
            if (success) {
                _allBookings.postValue(bookings)
            }
        }
    }

    fun updateBookingStatus(bookingId: String, status: String, callback: (Boolean, String) -> Unit) {
        repo.updateBookingStatus(bookingId, status, callback)
    }

    fun cancelBooking(bookingId: String, callback: (Boolean, String) -> Unit) {
        repo.cancelBooking(bookingId, callback)
    }
}
