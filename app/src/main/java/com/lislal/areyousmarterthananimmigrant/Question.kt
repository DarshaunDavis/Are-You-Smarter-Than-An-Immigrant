package com.lislal.areyousmarterthananimmigrant

data class Question(
    val question: String = "",
    val options: List<String> = listOf(),
    val correctAnswer: String = ""
)