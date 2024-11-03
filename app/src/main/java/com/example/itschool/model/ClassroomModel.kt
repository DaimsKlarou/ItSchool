package com.example.itschool.model

import com.google.firebase.Timestamp

data class ClassroomModel(
    var classroomId: String? = null,
    var userIds: List<String?> = emptyList(),
    var lastMessageTimestamp: Timestamp? = null,
    var lastMessageSenderId: String? = null,
    var lastMessage: String? = null,
    var classe: String? = null,
    var prof: String? = null,
    var groupIds : List<String?> = emptyList(),
    var nomClasse : String? = null
)
