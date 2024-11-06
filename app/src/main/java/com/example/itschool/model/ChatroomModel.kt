package com.example.itschool.model

import com.google.firebase.Timestamp

data class ChatroomModel(
    var chatroomId: String? = null,
    var userIds: List<String?> = emptyList(),  // Utilisez une liste vide par défaut
    var lastMessageTimestamp: Timestamp? = null,
    var lastMessageSenderId: String? = null,
    var lastMessage: String? = null
)
