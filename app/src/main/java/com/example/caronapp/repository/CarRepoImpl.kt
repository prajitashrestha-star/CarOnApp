package com.example.caronapp.repository

import com.example.caronapp.model.CarModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CarRepoImpl : CarRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Cars")

    override fun addCar(carModel: CarModel, callback: (Boolean, String) -> Unit) {
        // Generate a unique key for each car
        val carId = ref.push().key ?: ""
        carModel.carId = carId

        ref.child(carId).setValue(carModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Car added successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun updateCar(
        carId: String, carModel: CarModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(carId).updateChildren(carModel.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Car updated successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun deleteCar(carId: String, callback: (Boolean, String) -> Unit) {
        ref.child(carId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Car deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getCarById(carId: String, callback: (Boolean, CarModel?) -> Unit) {
        ref.child(carId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val car = snapshot.getValue(CarModel::class.java)
                    callback(true, car)
                } else {
                    callback(false, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null)
            }
        })
    }

    override fun getAllCars(callback: (Boolean, List<CarModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val allCars = mutableListOf<CarModel>()
                    for (carSnapshot in snapshot.children) {
                        val car = carSnapshot.getValue(CarModel::class.java)
                        if (car != null) {
                            allCars.add(car)
                        }
                    }
                    callback(true, allCars)
                } else {
                    callback(true, emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, emptyList())
            }
        })
    }
}
