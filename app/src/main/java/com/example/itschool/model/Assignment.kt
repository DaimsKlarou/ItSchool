package com.example.itschool.model

import java.util.Date

data class Assignment(
    val type: String? = null,
    val title: String? = null,
    val date: Date? = null,
    val classe: String? = null,
    val matiere: String? = null,
    val description: String? = null,
    val userId: String? = null,
)

