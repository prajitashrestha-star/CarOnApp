package com.example.caronapp.repository

import com.example.caronapp.model.BookingModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingRepoImpl : BookingRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Bookings")

    override fun createBooking(bookingModel: BookingModel, callback: (Boolean, String) -> Unit) {
        val bookingId = ref.push().key ?: ""
        bookingModel.bookingId = bookingId

        ref.child(bookingId).setValue(bookingModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Booking created successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getBookingsByUser(userId: String, callback: (Boolean, List<BookingModel>?) -> Unit) {
        ref.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookings = mutableListOf<BookingModel>()
                    for (bookingSnapshot in snapshot.children) {
                        val booking = bookingSnapshot.getValue(BookingModel::class.java)
                        if (booking != null) {
                            bookings.add(booking)
                        }
                    }
                    callback(true, bookings)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, emptyList())
                }
            })
    }

    override fun getAllBookings(callback: (Boolean, List<BookingModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = mutableListOf<BookingModel>()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(BookingModel::class.java)
                    if (booking != null) {
                        bookings.add(booking)
                    }
                }
                callback(true, bookings)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, emptyList())
            }
        })
    }

    override fun updateBookingStatus(
        bookingId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(bookingId).child("status").setValue(status).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Booking status updated")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun cancelBooking(bookingId: String, callback: (Boolean, String) -> Unit) {
        ref.child(bookingId).child("status").setValue("Cancelled").addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Booking cancelled")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }
}
