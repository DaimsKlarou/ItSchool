package com.example.itschool.model

import com.google.firebase.Timestamp

data class ClassroomModel(
    var classroomId: String? = null,
    var userIds: List<String?> = emptyList(),
    var createdTimestamp: Timestamp? = null,
    var nomClasse: String? = null,
)
