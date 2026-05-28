package com.medsurgery.kiruplus.domain.cases

interface ClinicalCasesRepository {
    suspend fun getCases(): List<ClinicalCase>
    suspend fun getCaseById(id: String): ClinicalCase?
    suspend fun getCasesByCategory(category: String): List<ClinicalCase>
    fun searchCases(query: String, cases: List<ClinicalCase>): List<ClinicalCase>
}
