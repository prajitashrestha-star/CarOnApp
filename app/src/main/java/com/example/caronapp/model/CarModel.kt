package com.example.caronapp.model

data class CarModel(
    var carId: String = "",
    var carName: String = "",
    var brand: String = "",
    var model: String = "",
    var year: String = "",
    var pricePerDay: String = "",
    var imageUrl: String = "",
    var isAvailable: Boolean = true,
    var description: String = "",
    var fuelType: String = "",
    var seats: String = "",
    var transmission: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "carId" to carId,
            "carName" to carName,
            "brand" to brand,
            "model" to model,
            "year" to year,
            "pricePerDay" to pricePerDay,
            "imageUrl" to imageUrl,
            "isAvailable" to isAvailable,
            "description" to description,
            "fuelType" to fuelType,
            "seats" to seats,
            "transmission" to transmission
        )
    }
}
