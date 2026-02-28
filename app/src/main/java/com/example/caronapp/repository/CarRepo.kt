package com.example.caronapp.repository

import com.example.caronapp.model.CarModel

interface CarRepo {

    fun addCar(carModel: CarModel, callback: (Boolean, String) -> Unit)

    fun updateCar(carId: String, carModel: CarModel, callback: (Boolean, String) -> Unit)

    fun deleteCar(carId: String, callback: (Boolean, String) -> Unit)

    fun getCarById(carId: String, callback: (Boolean, CarModel?) -> Unit)

    fun getAllCars(callback: (Boolean, List<CarModel>?) -> Unit)
}
