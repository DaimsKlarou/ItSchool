package com.example.itschool.model

import com.google.firebase.Timestamp

data class UserModel(
    var phone: String? = null,
    var username: String? = null,
    var email: String? = null,
    var createdTimestamp: Timestamp? = null,
    var userId: String? = null,
    var fcmToken: String? = null,
    var matricule: String? = null,
    var role: String? = null,
    var classe : String? = null,
    var isOnline: Boolean = false,
    var lastConnection: Timestamp? = null
) {

    companion object {
        val phone: String? = null
        val username: String? = null
        val email: String? = null
        val createdTimestamp: Timestamp = Timestamp.now()
        val userId: String? = null
        val fcmToken: String? = null
        val matricule: String? = null
        val role: String? = null
        val classe : String? = null
        val isOnline: Boolean = false
        val lastConnection: Timestamp = Timestamp.now()
    }
}
