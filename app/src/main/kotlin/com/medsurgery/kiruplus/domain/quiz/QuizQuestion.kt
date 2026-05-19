package com.medsurgery.kiruplus.domain.quiz

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String,
    val topic: String,
    val specialty: String,
)
