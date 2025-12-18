package com.example.caronapp.model

import kotlin.String

data class UserModel(
    var userId: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var dob: String = "",
    var contact: String = ""
) {
    // This helper method is useful for the updateProfile function in your Repo
    fun toMap(): Map<String, Any?> {
        return mapOf(
//            "userId" to userId,
//            "email" to email,
//            "firstName" to firstName,
//            "lastName" to lastName,
//            "dob" to dob,
            "contact" to contact,
        )
    }
}
