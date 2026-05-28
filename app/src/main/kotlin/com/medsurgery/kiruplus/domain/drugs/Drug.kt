package com.medsurgery.kiruplus.domain.drugs

data class Drug(
    val id: String,
    val nameEs: String,
    val genericName: String,
    val category: String,
    val indication: String,
    val mechanism: String,
    val dose: String,
    val route: String,
    val contraindication: String,
    val adverseEffects: String,
    val notes: String,
    val tags: List<String>,
) {
    companion object {
        val categories = listOf(
            "Analgésicos / Opioides",
            "Antibióticos Quirúrgicos",
            "Anestésicos / Sedación",
            "Vasopresores / UCI",
            "Gastrointestinal",
            "Anticoagulación",
            "Fluidos / Electrolitos",
        )
    }
}
