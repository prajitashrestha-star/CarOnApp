package com.example.caronapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.caronapp.model.BookingModel
import com.example.caronapp.repository.BookingRepo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.argumentCaptor
import org.junit.Assert.assertEquals

class BookingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var bookingRepo: BookingRepo

    @Mock
    private lateinit var userBookingsObserver: Observer<List<BookingModel>?>
    
    @Mock
    private lateinit var allBookingsObserver: Observer<List<BookingModel>?>

    private lateinit var viewModel: BookingViewModel
    private lateinit var closeable: AutoCloseable

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
        viewModel = BookingViewModel(bookingRepo)
        viewModel.userBookings.observeForever(userBookingsObserver)
        viewModel.allBookings.observeForever(allBookingsObserver)
    }

    @After
    fun tearDown() {
        viewModel.userBookings.removeObserver(userBookingsObserver)
        viewModel.allBookings.removeObserver(allBookingsObserver)
        closeable.close()
    }

    @Test
    fun testGetBookingsByUser_success() {
        val mockList = listOf(BookingModel(bookingId = "book1"))
        
        doAnswer { invocation ->
            val cb = invocation.arguments[1] as (Boolean, List<BookingModel>) -> Unit
            cb(true, mockList)
            null
        }.`when`(bookingRepo).getBookingsByUser(eq("testUser"), any())

        viewModel.getBookingsByUser("testUser")

        verify(userBookingsObserver).onChanged(mockList)
        assertEquals(mockList, viewModel.userBookings.value)
    }

    @Test
    fun testGetAllBookings_success() {
        val mockList = listOf(BookingModel(bookingId = "book2"))
        
        doAnswer { invocation ->
            val cb = invocation.arguments[0] as (Boolean, List<BookingModel>) -> Unit
            cb(true, mockList)
            null
        }.`when`(bookingRepo).getAllBookings(any())

        viewModel.getAllBookings()

        verify(allBookingsObserver).onChanged(mockList)
        assertEquals(mockList, viewModel.allBookings.value)
    }

    @Test
    fun testCreateBooking() {
        val booking = BookingModel(bookingId = "b1")
        var successResult = false
        var messageResult = ""
        
        doAnswer {
            val cb = it.arguments[1] as (Boolean, String) -> Unit
            cb(true, "Created")
            null
        }.`when`(bookingRepo).createBooking(eq(booking), any())

        viewModel.createBooking(booking) { success, msg ->
            successResult = success
            messageResult = msg
        }

        verify(bookingRepo).createBooking(eq(booking), any())
        assertEquals(true, successResult)
        assertEquals("Created", messageResult)
    }

    @Test
    fun testUpdateBookingStatus() {
        var successResult = false
        
        doAnswer {
            val cb = it.arguments[2] as (Boolean, String) -> Unit
            cb(true, "Updated")
            null
        }.`when`(bookingRepo).updateBookingStatus(eq("b1"), eq("Completed"), any())

        viewModel.updateBookingStatus("b1", "Completed") { success, _ ->
            successResult = success
        }

        verify(bookingRepo).updateBookingStatus(eq("b1"), eq("Completed"), any())
        assertEquals(true, successResult)
    }

    @Test
    fun testCancelBooking() {
        var successResult = false
        
        doAnswer {
            val cb = it.arguments[1] as (Boolean, String) -> Unit
            cb(true, "Cancelled")
            null
        }.`when`(bookingRepo).cancelBooking(eq("b1"), any())

        viewModel.cancelBooking("b1") { success, _ ->
            successResult = success
        }

        verify(bookingRepo).cancelBooking(eq("b1"), any())
        assertEquals(true, successResult)
    }
}
