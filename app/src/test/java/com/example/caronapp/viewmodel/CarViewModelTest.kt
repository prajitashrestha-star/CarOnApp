package com.example.caronapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.caronapp.model.CarModel
import com.example.caronapp.repository.CarRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CarViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun addCar_success_test() {
        val repo = mock<CarRepo>()
        val viewModel = CarViewModel(repo)
        
        val testCar = CarModel(
            carId = "c1",
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

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Car added")
            null
        }.`when`(repo).addCar(eq(testCar), any())

        var successResult = false
        var messageResult = ""

        viewModel.addCar(testCar) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Car added", messageResult)
        verify(repo).addCar(eq(testCar), any())
    }

    @Test
    fun deleteCar_success_test() {
        val repo = mock<CarRepo>()
        val viewModel = CarViewModel(repo)
        val carId = "c1"

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Car deleted")
            null
        }.`when`(repo).deleteCar(eq(carId), any())

        var successResult = false
        var messageResult = ""

        viewModel.deleteCar(carId) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Car deleted", messageResult)
        verify(repo).deleteCar(eq(carId), any())
    }

    @Test
    fun updateCar_success_test() {
        val repo = mock<CarRepo>()
        val viewModel = CarViewModel(repo)
        
        val testCar = CarModel(
            carId = "c1",
            carName = "Test Car 1",
            brand = "Toyota",
            model = "Camry",
            pricePerDay = "50",
            stock = 2,
            isAvailable = true,
            fuelType = "Petrol",
            seats = "5",
            transmission = "Automatic",
            description = "Updated content"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Car updated")
            null
        }.`when`(repo).updateCar(eq(testCar.carId), eq(testCar), any())

        var successResult = false
        var messageResult = ""

        viewModel.updateCar(testCar.carId, testCar) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Car updated", messageResult)
        verify(repo).updateCar(eq(testCar.carId), eq(testCar), any())
    }
}
