package com.medsurgery.kiruplus.domain.ktools

enum class CalculatorCategory(val labelEs: String) {
    GENERAL("Cirugía General"),
    UCI("UCI / Cuidados Críticos"),
}

data class KToolsCalculator(
    val id: String,
    val nameEs: String,
    val descriptionEs: String,
    val category: CalculatorCategory,
) {
    companion object {
        val all: List<KToolsCalculator> = listOf(
            KToolsCalculator("alvarado", "Escala de Alvarado", "Diagnóstico de Apendicitis Aguda", CalculatorCategory.GENERAL),
            KToolsCalculator("air", "Escala AIR", "Appendicitis Inflammatory Response Score", CalculatorCategory.GENERAL),
            KToolsCalculator("asa", "Clasificación ASA", "Riesgo Anestésico Preoperatorio", CalculatorCategory.GENERAL),
            KToolsCalculator("child_pugh", "Child-Pugh", "Función Hepática y Cirrosis", CalculatorCategory.GENERAL),
            KToolsCalculator("ripasa", "RIPASA Score", "Apendicitis — Poblaciones No Occidentales", CalculatorCategory.GENERAL),
            KToolsCalculator("ppossum", "p-POSSUM", "Riesgo Morbi-mortalidad Quirúrgica", CalculatorCategory.GENERAL),
            KToolsCalculator("bisap", "BISAP Score", "Gravedad de Pancreatitis Aguda", CalculatorCategory.UCI),
            KToolsCalculator("sofa", "SOFA Score", "Disfunción Orgánica en Sepsis", CalculatorCategory.UCI),
            KToolsCalculator("apache2", "APACHE II", "Pronóstico en Paciente Crítico", CalculatorCategory.UCI),
            KToolsCalculator("marshall", "Marshall Score", "Fallo Orgánico en Pancreatitis", CalculatorCategory.UCI),
        )

        val grouped: Map<CalculatorCategory, List<KToolsCalculator>> =
            all.groupBy { it.category }
    }
}
