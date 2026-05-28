package com.medsurgery.kiruplus.domain.cases

data class ClinicalCase(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val presentation: String,
    val vitalSigns: String,
    val physicalExam: String,
    val labResults: String,
    val imaging: String,
    val diagnosis: String,
    val differentialDiagnosis: List<String>,
    val management: String,
    val teaching: String,
    val tags: List<String>,
) {
    companion object {
        val categories = listOf(
            "Cirugía General",
            "Trauma / ATLS",
            "Cirugía de Urgencia",
            "Hepatobiliar",
            "Colorrectal",
        )
        val difficulties = listOf("Básico", "Intermedio", "Avanzado")
    }
}
