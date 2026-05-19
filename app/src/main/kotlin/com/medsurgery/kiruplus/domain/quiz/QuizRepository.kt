package com.medsurgery.kiruplus.domain.quiz

interface QuizRepository {
    suspend fun fetchSpecialties(): Result<List<QuizSpecialty>>
    suspend fun fetchQuestions(specialty: String): Result<List<QuizQuestion>>
}
