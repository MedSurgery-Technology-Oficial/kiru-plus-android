package com.medsurgery.kiruplus.data.quiz

import com.medsurgery.kiruplus.domain.quiz.QuizQuestion
import com.medsurgery.kiruplus.domain.quiz.QuizRepository
import com.medsurgery.kiruplus.domain.quiz.QuizSpecialty
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : QuizRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun fetchSpecialties(): Result<List<QuizSpecialty>> =
        runCatching {
            withContext(Dispatchers.IO) {
                // Fetch only id+specialty to minimize payload, then group in Kotlin.
                supabase.from(TABLE)
                    .select(columns = io.github.jan.supabase.postgrest.query.Columns.list("id", "specialty")) {
                        filter { eq("category", CATEGORY) }
                    }
                    .decodeList<SpecialtyRow>()
                    .groupBy { it.specialty }
                    .map { (specialty, rows) -> QuizSpecialty(name = specialty, questionCount = rows.size) }
                    .sortedBy { it.name }
            }
        }.onFailure { Timber.w(it, "fetchSpecialties failed") }

    override suspend fun fetchQuestions(specialty: String): Result<List<QuizQuestion>> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE)
                    .select {
                        filter {
                            eq("category", CATEGORY)
                            eq("specialty", specialty)
                        }
                    }
                    .decodeList<QuestionRow>()
                    .mapNotNull { row ->
                        val parsed = runCatching {
                            json.decodeFromString<QuestionContent>(row.content)
                        }.getOrNull() ?: return@mapNotNull null

                        QuizQuestion(
                            id = row.id,
                            questionText = row.title,
                            options = parsed.options,
                            correctAnswer = parsed.correctAnswer,
                            explanation = parsed.explanation,
                            topic = row.type ?: "",
                            specialty = row.specialty ?: "",
                        )
                    }
                    .shuffled()
            }
        }.onFailure { Timber.w(it, "fetchQuestions(%s) failed", specialty) }

    @Serializable
    private data class SpecialtyRow(
        val id: String,
        val specialty: String,
    )

    @Serializable
    private data class QuestionRow(
        val id: String,
        val title: String,
        val content: String,
        val type: String? = null,
        val specialty: String? = null,
    )

    @Serializable
    private data class QuestionContent(
        val options: List<String>,
        @SerialName("correctAnswer") val correctAnswer: String,
        val explanation: String,
    )

    private companion object {
        const val TABLE = "content_items"
        const val CATEGORY = "question"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizRepositoryBindings {
    @Binds
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository
}
