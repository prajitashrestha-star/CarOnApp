package com.example.caronapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caronapp.model.CarModel
import com.example.caronapp.repository.CarRepo

class CarViewModel(val repo: CarRepo) : ViewModel() {

    private val _allCars = MutableLiveData<List<CarModel>?>()
    val allCars: MutableLiveData<List<CarModel>?>
        get() = _allCars

    private val _car = MutableLiveData<CarModel?>()
    val car: MutableLiveData<CarModel?>
        get() = _car

    fun addCar(carModel: CarModel, callback: (Boolean, String) -> Unit) {
        repo.addCar(carModel, callback)
    }

    fun updateCar(carId: String, carModel: CarModel, callback: (Boolean, String) -> Unit) {
        repo.updateCar(carId, carModel, callback)
    }

    fun deleteCar(carId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteCar(carId, callback)
    }

    fun getCarById(carId: String) {
        repo.getCarById(carId) { success, carModel ->
            if (success) {
                _car.postValue(carModel)
            }
        }
    }

    fun getAllCars() {
        repo.getAllCars { success, cars ->
            if (success) {
                _allCars.postValue(cars)
            }
        }
    }
}
