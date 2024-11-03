package com.example.itschool.model

import com.google.firebase.Timestamp

data class ChatMessageModel(
    var message: String? = null,
    var senderId: String? = null,
    var timestamp: Timestamp? = null
)
