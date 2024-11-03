package com.example.itschool.model

import java.util.Date

data class Assignment(
    val type: String,
    val title: String,
    val date: Date,
    val classe: String,
    val description: String,
    val userId: String?
)

