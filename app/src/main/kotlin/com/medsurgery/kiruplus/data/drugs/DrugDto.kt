package com.medsurgery.kiruplus.data.drugs

import com.medsurgery.kiruplus.domain.drugs.Drug
import kotlinx.serialization.Serializable

@Serializable
data class DrugDto(
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
    val notes: String = "",
    val tags: List<String> = emptyList(),
) {
    fun toDomain() = Drug(
        id = id,
        nameEs = nameEs,
        genericName = genericName,
        category = category,
        indication = indication,
        mechanism = mechanism,
        dose = dose,
        route = route,
        contraindication = contraindication,
        adverseEffects = adverseEffects,
        notes = notes,
        tags = tags,
    )
}
