package com.example.itschool.model

import com.google.firebase.Timestamp

data class  GrouproomModel(
    var grouproomId: String? = null,
    var userIds: List<String?> = emptyList(),
    var lastMessageTimestamp: Timestamp? = null,
    var lastMessageSenderId: String? = null,
    var lastMessage: String? = null,
    var classId: String? = null,
    var nomGroup : String? = null,
    var profId : String? = null
)
