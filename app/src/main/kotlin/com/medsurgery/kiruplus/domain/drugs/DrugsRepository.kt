package com.medsurgery.kiruplus.domain.drugs

interface DrugsRepository {
    suspend fun getDrugs(): List<Drug>
    suspend fun getDrugById(id: String): Drug?
    suspend fun getDrugsByCategory(category: String): List<Drug>
    fun searchDrugs(query: String, drugs: List<Drug>): List<Drug>
}
