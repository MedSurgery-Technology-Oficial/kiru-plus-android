package com.medsurgery.kiruplus.feature.ktools

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.medsurgery.kiruplus.R
import java.util.Locale
import kotlin.math.exp

// ─── Dispatcher ──────────────────────────────────────────────────────────────

@Composable
fun CalculatorScreen(calculatorId: String, onBack: () -> Unit) {
    when (calculatorId) {
        "alvarado"   -> AlvaradoCalculator(onBack)
        "air"        -> AirCalculator(onBack)
        "asa"        -> AsaCalculator(onBack)
        "child_pugh" -> ChildPughCalculator(onBack)
        "bisap"      -> BisapCalculator(onBack)
        "sofa"       -> SofaCalculator(onBack)
        "apache2"    -> Apache2Calculator(onBack)
        "marshall"   -> MarshallCalculator(onBack)
        "ripasa"     -> RipasaCalculator(onBack)
        "ppossum"    -> PossumCalculator(onBack)
        else         -> Text("Calculator not found: $calculatorId")
    }
}

// ─── Shared components ────────────────────────────────────────────────────────

enum class RiskLevel { LOW, MODERATE, HIGH }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorScaffold(
    name: String,
    onBack: () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

@Composable
private fun ResultCard(
    scoreText: String,
    interpretation: String,
    level: RiskLevel,
) {
    val (bg, fg) = when (level) {
        RiskLevel.LOW      -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.tertiary
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondary
        RiskLevel.HIGH     -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.error
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, bg.copy(alpha = 0.4f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.ktools_result),
                style = MaterialTheme.typography.labelMedium,
                color = fg,
            )
            Text(
                text = scoreText,
                style = MaterialTheme.typography.headlineSmall,
                color = fg,
            )
            Text(
                text = interpretation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun CriterionRow(label: String, pts: Int, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            if (pts > 0) {
                Text(
                    text = "+$pts ${pluralStringResource(R.plurals.ktools_points, pts)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun NumericField(
    label: String,
    value: String,
    unit: String = "",
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = if (unit.isNotBlank()) ({ Text(unit) }) else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun OrdinalSelector(
    label: String,
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEachIndexed { index, opt ->
                FilterChip(
                    selected = selected == index,
                    onClick = { onSelect(index) },
                    label = { Text(opt, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun DisclaimerRow() {
    Text(
        text = stringResource(R.string.ktools_disclaimer),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

// ─── 1. Alvarado ─────────────────────────────────────────────────────────────

@Composable
private fun AlvaradoCalculator(onBack: () -> Unit) {
    var migration    by rememberSaveable { mutableStateOf(false) }
    var anorexia     by rememberSaveable { mutableStateOf(false) }
    var nausea       by rememberSaveable { mutableStateOf(false) }
    var rlqTender    by rememberSaveable { mutableStateOf(false) }
    var rebound      by rememberSaveable { mutableStateOf(false) }
    var fever        by rememberSaveable { mutableStateOf(false) }
    var leukocytosis by rememberSaveable { mutableStateOf(false) }
    var shiftLeft    by rememberSaveable { mutableStateOf(false) }

    val score = alvaradoScore(migration, anorexia, nausea, rlqTender, rebound, fever, leukocytosis, shiftLeft)
    val (level, interp) = alvaradoInterpretation(score)

    CalculatorScaffold(name = "Escala de Alvarado", onBack = onBack) {
        item { SectionHeader("Síntomas") }
        item { CriterionRow("Migración del dolor a FID", 1, migration) { migration = it } }
        item { CriterionRow("Anorexia", 1, anorexia) { anorexia = it } }
        item { CriterionRow("Náusea / Vómito", 1, nausea) { nausea = it } }
        item { SectionHeader("Signos") }
        item { CriterionRow("Dolor a palpación en FID", 2, rlqTender) { rlqTender = it } }
        item { CriterionRow("Signo de Blumberg (rebote)", 1, rebound) { rebound = it } }
        item { CriterionRow("Temperatura > 37.3 °C", 1, fever) { fever = it } }
        item { SectionHeader("Laboratorio") }
        item { CriterionRow("Leucocitosis > 10,000 /µL", 2, leukocytosis) { leukocytosis = it } }
        item { CriterionRow("Desviación izquierda > 75% neutrófilos", 1, shiftLeft) { shiftLeft = it } }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard("$score / 10 puntos", interp, level) }
        item { DisclaimerRow() }
    }
}

private fun alvaradoScore(
    migration: Boolean, anorexia: Boolean, nausea: Boolean,
    rlqTender: Boolean, rebound: Boolean, fever: Boolean,
    leukocytosis: Boolean, shiftLeft: Boolean,
): Int {
    var s = 0
    if (migration) s += 1; if (anorexia) s += 1; if (nausea) s += 1
    if (rlqTender) s += 2; if (rebound) s += 1; if (fever) s += 1
    if (leukocytosis) s += 2; if (shiftLeft) s += 1
    return s
}

private fun alvaradoInterpretation(score: Int): Pair<RiskLevel, String> = when {
    score <= 4  -> RiskLevel.LOW      to "Riesgo bajo (score $score/10). Probabilidad de apendicitis <25%. Considerar diagnósticos alternativos. Alta con indicaciones de alarma."
    score <= 6  -> RiskLevel.MODERATE to "Riesgo intermedio (score $score/10). Probabilidad 25–75%. Observación + USG/TAC. Valoración quirúrgica recomendada."
    else        -> RiskLevel.HIGH     to "Riesgo alto (score $score/10). Probabilidad >75%. Apendicectomía indicada. Preparación preoperatoria urgente."
}

// ─── 2. AIR Score ────────────────────────────────────────────────────────────

@Composable
private fun AirCalculator(onBack: () -> Unit) {
    var vomiting   by rememberSaveable { mutableStateOf(false) }
    var painRIF    by rememberSaveable { mutableStateOf(false) }
    var reboundIdx by rememberSaveable { mutableIntStateOf(0) }  // 0=No, 1=Leve, 2=Moderado, 3=Intenso
    var tempStr    by rememberSaveable { mutableStateOf("") }
    var wbcStr     by rememberSaveable { mutableStateOf("") }
    var neutStr    by rememberSaveable { mutableStateOf("") }
    var crpStr     by rememberSaveable { mutableStateOf("") }

    val temp = tempStr.toDoubleOrNull() ?: 37.0
    val wbc  = wbcStr.toDoubleOrNull() ?: 8.0
    val neut = neutStr.toIntOrNull() ?: 60
    val crp  = crpStr.toDoubleOrNull() ?: 5.0

    val score = airScore(vomiting, painRIF, reboundIdx, temp, wbc, neut, crp)
    val (level, interp) = airInterpretation(score)

    CalculatorScaffold(name = "Escala AIR", onBack = onBack) {
        item { SectionHeader("Síntomas y Signos") }
        item { CriterionRow("Vómito", 1, vomiting) { vomiting = it } }
        item { CriterionRow("Dolor en FID", 1, painRIF) { painRIF = it } }
        item {
            OrdinalSelector(
                label = "Dolor de rebote / defensa",
                options = listOf("Ninguno (0)", "Leve (1)", "Moderado (2)", "Intenso (3)"),
                selected = reboundIdx,
                onSelect = { reboundIdx = it },
            )
        }
        item { NumericField("Temperatura", tempStr, "°C") { tempStr = it } }
        item { SectionHeader("Laboratorio") }
        item { NumericField("Leucocitos (WBC)", wbcStr, "×10³/µL") { wbcStr = it } }
        item { NumericField("Neutrófilos", neutStr, "%") { neutStr = it } }
        item { NumericField("PCR (Proteína C Reactiva)", crpStr, "mg/L") { crpStr = it } }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard("$score / 12 puntos", interp, level) }
        item { DisclaimerRow() }
    }
}

private fun airScore(
    vomiting: Boolean, painRIF: Boolean, reboundIdx: Int,
    temp: Double, wbc: Double, neut: Int, crp: Double,
): Int {
    var s = 0
    if (vomiting) s += 1
    if (painRIF) s += 1
    s += reboundIdx  // 0-3
    if (temp >= 38.5) s += 1
    s += when {
        wbc >= 15.0  -> 2
        wbc >= 10.0  -> 1
        else         -> 0
    }
    s += when {
        neut >= 85 -> 2
        neut >= 70 -> 1
        else       -> 0
    }
    s += when {
        crp >= 50.0 -> 2
        crp >= 10.0 -> 1
        else        -> 0
    }
    return s
}

private fun airInterpretation(score: Int): Pair<RiskLevel, String> = when {
    score <= 4  -> RiskLevel.LOW      to "Riesgo bajo (score $score/12). Probabilidad de apendicitis baja. Alta con seguimiento."
    score <= 8  -> RiskLevel.MODERATE to "Riesgo intermedio (score $score/12). Investigación y observación. USG / TAC indicados."
    else        -> RiskLevel.HIGH     to "Riesgo alto (score $score/12). Alta probabilidad de apendicitis. Valoración quirúrgica urgente."
}

// ─── 3. ASA Classification ───────────────────────────────────────────────────

@Composable
private fun AsaCalculator(onBack: () -> Unit) {
    var asaClass by rememberSaveable { mutableIntStateOf(0) }  // 0-based index

    val classes = listOf(
        Triple("ASA I",   RiskLevel.LOW,      "Paciente sano, sin alteraciones orgánicas, fisiológicas o psiquiátricas. Mortalidad anestésica <0.1%."),
        Triple("ASA II",  RiskLevel.LOW,      "Enfermedad sistémica leve sin limitación funcional (HTA controlada, DM sin complicaciones). Mortalidad ~0.1–0.2%."),
        Triple("ASA III", RiskLevel.MODERATE, "Enfermedad sistémica grave con limitación funcional (EPOC, angina estable). Mortalidad ~0.4–1.8%. Optimización preoperatoria intensa."),
        Triple("ASA IV",  RiskLevel.HIGH,     "Enfermedad sistémica grave con amenaza constante para la vida (IAM reciente, insuficiencia cardíaca). Mortalidad ~7.8–23%. UCI postoperatoria requerida."),
        Triple("ASA V",   RiskLevel.HIGH,     "Paciente moribundo que no se espera sobreviva sin cirugía (ruptura de aneurisma). Mortalidad ~9.4–57.8%. Cirugía de salvamento vital únicamente."),
        Triple("ASA VI",  RiskLevel.HIGH,     "Paciente con muerte cerebral declarada, candidato a donación de órganos."),
    )
    val (label, level, interp) = classes[asaClass]

    CalculatorScaffold(name = "Clasificación ASA", onBack = onBack) {
        item { SectionHeader("Clase ASA del paciente") }
        classes.forEachIndexed { index, (cls, _, desc) ->
            item(key = "asa_$index") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (asaClass == index)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    onClick = { asaClass = index },
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(cls, style = MaterialTheme.typography.titleSmall)
                        Text(desc, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard(label, interp, level) }
        item { DisclaimerRow() }
    }
}

// ─── 4. Child-Pugh ───────────────────────────────────────────────────────────

@Composable
private fun ChildPughCalculator(onBack: () -> Unit) {
    var biliStr  by rememberSaveable { mutableStateOf("") }
    var albStr   by rememberSaveable { mutableStateOf("") }
    var inrStr   by rememberSaveable { mutableStateOf("") }
    var ascites  by rememberSaveable { mutableIntStateOf(0) }       // 0=None, 1=Mild, 2=Severe
    var enceph   by rememberSaveable { mutableIntStateOf(0) }       // 0=None, 1=I-II, 2=III-IV

    val bili = biliStr.toDoubleOrNull() ?: 0.0
    val alb  = albStr.toDoubleOrNull() ?: 0.0
    val inr  = inrStr.toDoubleOrNull() ?: 0.0

    val score = childPughScore(bili, alb, inr, ascites + 1, enceph + 1)
    val (cls, level, interp) = childPughInterpretation(score)

    CalculatorScaffold(name = "Child-Pugh", onBack = onBack) {
        item { SectionHeader("Laboratorio") }
        item { NumericField("Bilirrubina", biliStr, "mg/dL") { biliStr = it } }
        item { NumericField("Albúmina", albStr, "g/dL") { albStr = it } }
        item { NumericField("INR", inrStr, "") { inrStr = it } }
        item { SectionHeader("Clínica") }
        item {
            OrdinalSelector(
                "Ascitis",
                listOf("Ninguna", "Leve/Controlada", "Grave/Refractaria"),
                ascites,
            ) { ascites = it }
        }
        item {
            OrdinalSelector(
                "Encefalopatía",
                listOf("Ninguna", "Grado I–II", "Grado III–IV"),
                enceph,
            ) { enceph = it }
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard("$score pts — $cls", interp, level) }
        item { DisclaimerRow() }
    }
}

private fun childPughScore(bili: Double, alb: Double, inr: Double, ascites: Int, enceph: Int): Int {
    var p = 0
    p += when { bili < 2.0 -> 1; bili <= 3.0 -> 2; else -> 3 }
    p += when { alb > 3.5 -> 1; alb >= 2.8 -> 2; else -> 3 }
    p += when { inr < 1.7 -> 1; inr <= 2.3 -> 2; else -> 3 }
    p += ascites.coerceIn(1, 3)
    p += enceph.coerceIn(1, 3)
    return p
}

private fun childPughInterpretation(score: Int): Triple<String, RiskLevel, String> = when {
    score <= 6  -> Triple("Clase A", RiskLevel.LOW,      "Child-Pugh Clase A ($score pts). Cirrosis bien compensada. Supervivencia 1 año: ~100%.")
    score <= 9  -> Triple("Clase B", RiskLevel.MODERATE, "Child-Pugh Clase B ($score pts). Compromiso funcional significativo. Supervivencia 1 año: ~80%.")
    else        -> Triple("Clase C", RiskLevel.HIGH,     "Child-Pugh Clase C ($score pts). Cirrosis descompensada. Supervivencia 1 año: ~45%. Evaluar trasplante hepático.")
}

// ─── 5. BISAP ────────────────────────────────────────────────────────────────

@Composable
private fun BisapCalculator(onBack: () -> Unit) {
    var bunStr   by rememberSaveable { mutableStateOf("") }
    var altered  by rememberSaveable { mutableStateOf(false) }
    var sirsIdx  by rememberSaveable { mutableIntStateOf(0) }   // 0=0, 1=1, 2=2, 3=3, 4=4
    var ageStr   by rememberSaveable { mutableStateOf("") }
    var pleural  by rememberSaveable { mutableStateOf(false) }

    val bun  = bunStr.toDoubleOrNull() ?: 0.0
    val age  = ageStr.toIntOrNull() ?: 0
    val sirs = sirsIdx  // direct count

    val score = bisapScore(bun, altered, sirs, age, pleural)
    val (level, interp) = bisapInterpretation(score)

    CalculatorScaffold(name = "BISAP Score", onBack = onBack) {
        item { NumericField("BUN (Nitrógeno Ureico)", bunStr, "mg/dL") { bunStr = it } }
        item { CriterionRow("Estado mental alterado (GCS < 15 o desorientación)", 1, altered) { altered = it } }
        item {
            OrdinalSelector(
                label = "Criterios SIRS cumplidos (FR>20, FC>90, Temp<36 o >38, WBC<4k o >12k)",
                options = listOf("0", "1", "2", "3", "4"),
                selected = sirsIdx,
                onSelect = { sirsIdx = it },
            )
        }
        item { NumericField("Edad del paciente", ageStr, "años") { ageStr = it } }
        item { CriterionRow("Derrame pleural (por imagen)", 1, pleural) { pleural = it } }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard("$score / 5 puntos", interp, level) }
        item { DisclaimerRow() }
    }
}

private fun bisapScore(bun: Double, altered: Boolean, sirs: Int, age: Int, pleural: Boolean): Int {
    var s = 0
    if (bun > 25) s += 1
    if (altered) s += 1
    if (sirs >= 2) s += 1
    if (age > 60) s += 1
    if (pleural) s += 1
    return s
}

private fun bisapInterpretation(score: Int): Pair<RiskLevel, String> = when (score) {
    0       -> RiskLevel.LOW      to "Mortalidad hospitalaria muy baja (<1%). Manejo general."
    1       -> RiskLevel.LOW      to "Mortalidad ~1.9%. Seguimiento estrecho. Hidratación agresiva."
    2       -> RiskLevel.MODERATE to "Mortalidad ~3.6%. Monitoreo intensivo. Considerar UCI."
    3       -> RiskLevel.HIGH     to "Mortalidad ~5.3%. Ingreso a UCI. Soporte multisistémico."
    else    -> RiskLevel.HIGH     to "Mortalidad alta (~12.7–22.5%). UCI obligatoria. Pancreatitis grave."
}

// ─── 6. SOFA Score ───────────────────────────────────────────────────────────

@Composable
private fun SofaCalculator(onBack: () -> Unit) {
    var pf        by rememberSaveable { mutableStateOf("") }
    var platelets by rememberSaveable { mutableStateOf("") }
    var bili      by rememberSaveable { mutableStateOf("") }
    var mapStr    by rememberSaveable { mutableStateOf("") }
    var vasoIdx   by rememberSaveable { mutableIntStateOf(0) }
    var gcsStr    by rememberSaveable { mutableStateOf("") }
    var crStr     by rememberSaveable { mutableStateOf("") }

    val pfVal  = pf.toDoubleOrNull() ?: 500.0
    val platVal = platelets.toDoubleOrNull() ?: 200.0
    val biliVal = bili.toDoubleOrNull() ?: 0.5
    val mapVal  = mapStr.toDoubleOrNull() ?: 85.0
    val gcsVal  = gcsStr.toIntOrNull() ?: 15
    val crVal   = crStr.toDoubleOrNull() ?: 0.8

    val score = sofaScore(pfVal, platVal, biliVal, mapVal, vasoIdx, gcsVal, crVal)
    val (level, interp) = sofaInterpretation(score)

    CalculatorScaffold(name = "SOFA Score", onBack = onBack) {
        item { SectionHeader("Respiratorio") }
        item { NumericField("PaO₂ / FiO₂", pf, "mmHg") { pf = it } }
        item { SectionHeader("Coagulación") }
        item { NumericField("Plaquetas", platelets, "×10³/µL") { platelets = it } }
        item { SectionHeader("Hepático") }
        item { NumericField("Bilirrubina", bili, "mg/dL") { bili = it } }
        item { SectionHeader("Cardiovascular") }
        item { NumericField("Presión Arterial Media (PAM)", mapStr, "mmHg") { mapStr = it } }
        item {
            OrdinalSelector(
                "Soporte vasopresor",
                listOf("Sin soporte", "PAM<70", "Dopa≤5 o Dobuta", "Norepi≤0.1", "Norepi>0.1"),
                vasoIdx,
            ) { vasoIdx = it }
        }
        item { SectionHeader("Neurológico") }
        item { NumericField("Glasgow (GCS)", gcsStr, "/15") { gcsStr = it } }
        item { SectionHeader("Renal") }
        item { NumericField("Creatinina", crStr, "mg/dL") { crStr = it } }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard("$score / 24 puntos", interp, level) }
        item { DisclaimerRow() }
    }
}

private fun sofaScore(pf: Double, plat: Double, bili: Double, map: Double, vaso: Int, gcs: Int, cr: Double): Int {
    var s = 0
    s += when { pf >= 400 -> 0; pf >= 300 -> 1; pf >= 200 -> 2; pf >= 100 -> 3; else -> 4 }
    s += when { plat >= 150 -> 0; plat >= 100 -> 1; plat >= 50 -> 2; plat >= 20 -> 3; else -> 4 }
    s += when { bili < 1.2 -> 0; bili < 2.0 -> 1; bili < 6.0 -> 2; bili < 12.0 -> 3; else -> 4 }
    s += when {
        vaso == 0 && map >= 70 -> 0
        vaso == 0 -> 1
        else -> vaso  // 2, 3, 4 directly mapped
    }
    s += when { gcs >= 15 -> 0; gcs >= 13 -> 1; gcs >= 10 -> 2; gcs >= 6 -> 3; else -> 4 }
    s += when { cr < 1.2 -> 0; cr < 2.0 -> 1; cr < 3.5 -> 2; cr < 5.0 -> 3; else -> 4 }
    return s
}

private fun sofaInterpretation(score: Int): Pair<RiskLevel, String> = when {
    score <= 6  -> RiskLevel.LOW      to "Disfunción orgánica baja (score $score/24). Mortalidad ~10%."
    score <= 9  -> RiskLevel.MODERATE to "Disfunción orgánica moderada (score $score/24). Mortalidad ~15–20%."
    score <= 12 -> RiskLevel.HIGH     to "Disfunción orgánica grave (score $score/24). Mortalidad ~40–50%."
    else        -> RiskLevel.HIGH     to "Fallo multiorgánico (score $score/24). Mortalidad >80%. UCI obligatoria."
}

// ─── 7. APACHE II ────────────────────────────────────────────────────────────

@Composable
private fun Apache2Calculator(onBack: () -> Unit) {
    var tempS  by rememberSaveable { mutableStateOf("") }
    var mapS   by rememberSaveable { mutableStateOf("") }
    var hrS    by rememberSaveable { mutableStateOf("") }
    var rrS    by rememberSaveable { mutableStateOf("") }
    var phS    by rememberSaveable { mutableStateOf("") }
    var naS    by rememberSaveable { mutableStateOf("") }
    var kS     by rememberSaveable { mutableStateOf("") }
    var crS    by rememberSaveable { mutableStateOf("") }
    var arf    by rememberSaveable { mutableStateOf(false) }
    var hctS   by rememberSaveable { mutableStateOf("") }
    var wbcS   by rememberSaveable { mutableStateOf("") }
    var gcsS   by rememberSaveable { mutableStateOf("") }
    var fio2S  by rememberSaveable { mutableStateOf("") }
    var pao2S  by rememberSaveable { mutableStateOf("") }
    var paco2S by rememberSaveable { mutableStateOf("") }
    var ageS   by rememberSaveable { mutableStateOf("") }
    var chronIdx by rememberSaveable { mutableIntStateOf(0) }

    val chronicPts = listOf(0, 5, 7, 2)  // none, non-operative/immunocomp, emergency, elective

    val physPts = apachePhysScore(
        temp = tempS.toDoubleOrNull() ?: 37.0,
        map  = mapS.toDoubleOrNull() ?: 90.0,
        hr   = hrS.toDoubleOrNull() ?: 80.0,
        rr   = rrS.toDoubleOrNull() ?: 15.0,
        pH   = phS.toDoubleOrNull() ?: 7.40,
        na   = naS.toDoubleOrNull() ?: 140.0,
        k    = kS.toDoubleOrNull() ?: 4.0,
        cr   = crS.toDoubleOrNull() ?: 1.0,
        hasARF = arf,
        hct  = hctS.toDoubleOrNull() ?: 45.0,
        wbc  = wbcS.toDoubleOrNull() ?: 8.0,
        gcs  = gcsS.toIntOrNull() ?: 15,
        fiO2 = fio2S.toDoubleOrNull() ?: 0.21,
        paO2 = pao2S.toDoubleOrNull() ?: 95.0,
        paCO2 = paco2S.toDoubleOrNull() ?: 40.0,
    )
    val agePts   = apacheAgeScore(ageS.toIntOrNull() ?: 30)
    val total    = physPts + agePts + (chronicPts.getOrElse(chronIdx) { 0 })
    val (level, interp) = apacheInterpretation(total)

    CalculatorScaffold(name = "APACHE II", onBack = onBack) {
        item { SectionHeader("Variables Fisiológicas") }
        item { NumericField("Temperatura (rectal)", tempS, "°C") { tempS = it } }
        item { NumericField("Presión Arterial Media (PAM)", mapS, "mmHg") { mapS = it } }
        item { NumericField("Frecuencia Cardíaca", hrS, "lpm") { hrS = it } }
        item { NumericField("Frecuencia Respiratoria", rrS, "rpm") { rrS = it } }
        item { NumericField("pH Arterial", phS, "") { phS = it } }
        item { NumericField("Sodio (Na⁺)", naS, "mEq/L") { naS = it } }
        item { NumericField("Potasio (K⁺)", kS, "mEq/L") { kS = it } }
        item { NumericField("Creatinina", crS, "mg/dL") { crS = it } }
        item { CriterionRow("Falla Renal Aguda (duplica pts de creatinina)", 0, arf) { arf = it } }
        item { NumericField("Hematocrito", hctS, "%") { hctS = it } }
        item { NumericField("Leucocitos (WBC)", wbcS, "×10³/µL") { wbcS = it } }
        item { NumericField("Glasgow (GCS)", gcsS, "/15") { gcsS = it } }
        item { SectionHeader("Oxigenación") }
        item { NumericField("FiO₂", fio2S, "(0.21–1.0)") { fio2S = it } }
        item { NumericField("PaO₂", pao2S, "mmHg") { pao2S = it } }
        item { NumericField("PaCO₂ (solo si FiO₂ ≥ 0.5)", paco2S, "mmHg") { paco2S = it } }
        item { SectionHeader("Edad y Salud Crónica") }
        item { NumericField("Edad", ageS, "años") { ageS = it } }
        item {
            OrdinalSelector(
                "Salud Crónica",
                listOf("Sano/Ninguna", "No quirúrgico / Inmunodep.", "Post-op Urgencia", "Post-op Electivo"),
                chronIdx,
            ) { chronIdx = it }
        }
        item {
            Text(
                "Puntos fisiológicos: $physPts | Edad: $agePts | Crónico: ${chronicPts.getOrElse(chronIdx){0}}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard("$total pts APACHE II", interp, level) }
        item { DisclaimerRow() }
    }
}

private fun apachePhysScore(
    temp: Double, map: Double, hr: Double, rr: Double,
    pH: Double, na: Double, k: Double, cr: Double, hasARF: Boolean,
    hct: Double, wbc: Double, gcs: Int,
    fiO2: Double, paO2: Double, paCO2: Double,
): Int {
    var p = 0
    // Temperature
    p += when {
        temp >= 41.0 || temp <= 29.9 -> 4
        temp >= 39.0 || temp <= 31.9 -> 3
        temp >= 38.5                 -> 1
        temp >= 36.0                 -> 0
        temp >= 34.0                 -> 1
        temp >= 32.0                 -> 2
        else                         -> 3
    }
    // MAP
    p += when {
        map >= 160           -> 4
        map >= 130           -> 3
        map >= 110           -> 2
        map >= 70            -> 0
        map >= 50            -> 2
        else                 -> 4
    }
    // Heart Rate
    p += when {
        hr >= 180            -> 4
        hr >= 140            -> 3
        hr >= 110            -> 2
        hr >= 70             -> 0
        hr >= 55             -> 2
        hr >= 40             -> 3
        else                 -> 4
    }
    // Respiratory Rate
    p += when {
        rr >= 50             -> 4
        rr >= 35             -> 3
        rr >= 25             -> 1
        rr >= 12             -> 0
        rr >= 10             -> 1
        rr >= 6              -> 2
        else                 -> 4
    }
    // Oxygenation
    if (fiO2 >= 0.5) {
        val pao2Alv = fiO2 * 713.0 - paCO2 / 0.8
        val aaGrad  = pao2Alv - paO2
        p += when {
            aaGrad < 200 -> 0
            aaGrad < 350 -> 2
            aaGrad < 500 -> 3
            else         -> 4
        }
    } else {
        p += when {
            paO2 > 70 -> 0
            paO2 >= 61 -> 1
            paO2 >= 55 -> 3
            else       -> 4
        }
    }
    // pH
    p += when {
        pH >= 7.7             -> 4
        pH >= 7.6             -> 3
        pH >= 7.5             -> 1
        pH >= 7.33            -> 0
        pH >= 7.25            -> 2
        pH >= 7.15            -> 3
        else                  -> 4
    }
    // Sodium
    p += when {
        na >= 180             -> 4
        na >= 160             -> 3
        na >= 155             -> 2
        na >= 150             -> 1
        na >= 130             -> 0
        na >= 120             -> 2
        na >= 111             -> 3
        else                  -> 4
    }
    // Potassium
    p += when {
        k >= 7.0              -> 4
        k >= 6.0              -> 3
        k >= 5.5              -> 1
        k >= 3.5              -> 0
        k >= 3.0              -> 1
        k >= 2.5              -> 2
        else                  -> 4
    }
    // Creatinine
    var crPts = when {
        cr >= 3.5             -> 4
        cr >= 2.0             -> 3
        cr >= 1.5             -> 2
        cr >= 0.6             -> 0
        else                  -> 2
    }
    if (hasARF) crPts = minOf(crPts * 2, 8)
    p += crPts
    // Hematocrit
    p += when {
        hct >= 60             -> 4
        hct >= 50             -> 2
        hct >= 46             -> 1
        hct >= 30             -> 0
        hct >= 20             -> 2
        else                  -> 4
    }
    // WBC
    p += when {
        wbc >= 40             -> 4
        wbc >= 20             -> 2
        wbc >= 15             -> 1
        wbc >= 3              -> 0
        wbc >= 1              -> 2
        else                  -> 4
    }
    // GCS
    p += (15 - gcs.coerceIn(3, 15))
    return p
}

private fun apacheAgeScore(age: Int): Int = when {
    age >= 75 -> 6
    age >= 65 -> 5
    age >= 55 -> 3
    age >= 45 -> 2
    else      -> 0
}

private fun apacheInterpretation(total: Int): Pair<RiskLevel, String> {
    val mort = when {
        total <= 4  -> "~4%"
        total <= 9  -> "~8%"
        total <= 14 -> "~15%"
        total <= 19 -> "~25%"
        total <= 24 -> "~40%"
        total <= 29 -> "~55%"
        total <= 34 -> "~75%"
        else        -> ">85%"
    }
    val level = when {
        total <= 9  -> RiskLevel.LOW
        total <= 19 -> RiskLevel.MODERATE
        else        -> RiskLevel.HIGH
    }
    return level to "Score APACHE II: $total. Mortalidad hospitalaria estimada: $mort."
}

// ─── 8. Marshall Score ───────────────────────────────────────────────────────

@Composable
private fun MarshallCalculator(onBack: () -> Unit) {
    var pfStr  by rememberSaveable { mutableStateOf("") }
    var crStr  by rememberSaveable { mutableStateOf("") }
    var sbpStr by rememberSaveable { mutableStateOf("") }

    val pf  = pfStr.toDoubleOrNull() ?: 450.0
    val cr  = crStr.toDoubleOrNull() ?: 0.8
    val sbp = sbpStr.toDoubleOrNull() ?: 120.0

    val pfPts  = when { pf > 400 -> 0; pf > 300 -> 1; pf > 200 -> 2; pf > 100 -> 3; else -> 4 }
    val crPts  = when { cr < 1.4 -> 0; cr < 1.9 -> 1; cr <= 3.6 -> 2; cr < 5.0 -> 3; else -> 4 }
    val sbpPts = when { sbp > 90 -> 0; sbp >= 80 -> 1; sbp >= 70 -> 2; sbp >= 60 -> 3; else -> 4 }
    val total  = pfPts + crPts + sbpPts

    val level = when {
        total <= 3  -> RiskLevel.LOW
        total <= 6  -> RiskLevel.MODERATE
        else        -> RiskLevel.HIGH
    }
    val interp = buildString {
        append("Marshall Total: $total pts. ")
        if (pfPts >= 2) append("Falla respiratoria. ")
        if (crPts >= 2) append("Falla renal. ")
        if (sbpPts >= 2) append("Falla cardiovascular. ")
        if (pfPts < 2 && crPts < 2 && sbpPts < 2) append("Sin falla orgánica significativa. ")
        append("Score ≥2 en cualquier sistema = falla orgánica; ≥2 por >48h = falla persistente (alta mortalidad).")
    }

    CalculatorScaffold(name = "Marshall Score", onBack = onBack) {
        item { SectionHeader("Respiratorio") }
        item { NumericField("PaO₂ / FiO₂", pfStr, "mmHg") { pfStr = it } }
        item { SectionHeader("Renal") }
        item { NumericField("Creatinina", crStr, "mg/dL") { crStr = it } }
        item { SectionHeader("Cardiovascular") }
        item { NumericField("Presión Arterial Sistólica", sbpStr, "mmHg") { sbpStr = it } }
        item {
            Text(
                "PaO₂/FiO₂: $pfPts pts | Creatinina: $crPts pts | PAS: $sbpPts pts",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard("$total / 12 pts — Marshall", interp, level) }
        item { DisclaimerRow() }
    }
}

// ─── 9. RIPASA Score ─────────────────────────────────────────────────────────

@Composable
private fun RipasaCalculator(onBack: () -> Unit) {
    var genderMale   by rememberSaveable { mutableStateOf(true) }
    var ageUnder40   by rememberSaveable { mutableStateOf(true) }
    var rifPain      by rememberSaveable { mutableStateOf(false) }
    var migration    by rememberSaveable { mutableStateOf(false) }
    var anorexia     by rememberSaveable { mutableStateOf(false) }
    var nausea       by rememberSaveable { mutableStateOf(false) }
    var durIdx       by rememberSaveable { mutableIntStateOf(0) }  // 0=<12h, 1=12-24h, 2=>24h
    var rifTender    by rememberSaveable { mutableStateOf(false) }
    var rebound      by rememberSaveable { mutableStateOf(false) }
    var guarding     by rememberSaveable { mutableStateOf(false) }
    var rovsing      by rememberSaveable { mutableStateOf(false) }
    var fever        by rememberSaveable { mutableStateOf(false) }
    var elevatedWBC  by rememberSaveable { mutableStateOf(false) }
    var negUrine     by rememberSaveable { mutableStateOf(false) }

    val score = ripasaScore(genderMale, ageUnder40, rifPain, migration, anorexia, nausea, durIdx, rifTender, rebound, guarding, rovsing, fever, elevatedWBC, negUrine)
    val (level, interp) = ripasaInterpretation(score)

    CalculatorScaffold(name = "RIPASA Score", onBack = onBack) {
        item { SectionHeader("Demográficos") }
        item {
            OrdinalSelector("Sexo", listOf("Masculino (+1)", "Femenino (0)"), if (genderMale) 0 else 1) {
                genderMale = it == 0
            }
        }
        item {
            OrdinalSelector("Edad", listOf("<40 años (+1)", "≥40 años (+0.5)"), if (ageUnder40) 0 else 1) {
                ageUnder40 = it == 0
            }
        }
        item { SectionHeader("Síntomas") }
        item { CriterionRow("Dolor en FID", 0, rifPain) { rifPain = it } }
        item { CriterionRow("Migración del dolor a FID", 0, migration) { migration = it } }
        item { CriterionRow("Anorexia", 1, anorexia) { anorexia = it } }
        item { CriterionRow("Náusea / Vómito", 1, nausea) { nausea = it } }
        item {
            OrdinalSelector("Duración de síntomas", listOf("<12h (+1)", "12–24h (+1)", ">24h (+0.5)"), durIdx) {
                durIdx = it
            }
        }
        item { SectionHeader("Signos") }
        item { CriterionRow("Sensibilidad en FID", 1, rifTender) { rifTender = it } }
        item { CriterionRow("Signo de rebote (Blumberg)", 1, rebound) { rebound = it } }
        item { CriterionRow("Resistencia muscular (Guarding)", 2, guarding) { guarding = it } }
        item { CriterionRow("Signo de Rovsing", 2, rovsing) { rovsing = it } }
        item { CriterionRow("Fiebre (≥37.5 °C)", 1, fever) { fever = it } }
        item { SectionHeader("Laboratorio") }
        item { CriterionRow("Leucocitosis", 1, elevatedWBC) { elevatedWBC = it } }
        item { CriterionRow("Examen de orina negativo", 1, negUrine) { negUrine = it } }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item { ResultCard(String.format(Locale.US, "%.1f pts — RIPASA", score), interp, level) }
        item { DisclaimerRow() }
    }
}

private fun ripasaScore(
    genderMale: Boolean, ageUnder40: Boolean, rifPain: Boolean, migration: Boolean,
    anorexia: Boolean, nausea: Boolean, durIdx: Int, rifTender: Boolean, rebound: Boolean,
    guarding: Boolean, rovsing: Boolean, fever: Boolean, elevatedWBC: Boolean, negUrine: Boolean,
): Double {
    var s = 0.0
    if (genderMale) s += 1.0
    s += if (ageUnder40) 1.0 else 0.5
    if (rifPain) s += 0.5
    if (migration) s += 0.5
    if (anorexia) s += 1.0
    if (nausea) s += 1.0
    s += when (durIdx) { 0, 1 -> 1.0; else -> 0.5 }
    if (rifTender) s += 1.0
    if (rebound) s += 1.0
    if (guarding) s += 2.0
    if (rovsing) s += 2.0
    if (fever) s += 1.0
    if (elevatedWBC) s += 1.0
    if (negUrine) s += 1.0
    return s
}

private fun ripasaInterpretation(score: Double): Pair<RiskLevel, String> = when {
    score < 5.0   -> RiskLevel.LOW      to "Score RIPASA: ${"%.1f".format(score)}. Apendicitis improbable. Alta con seguimiento en 24h."
    score < 7.5   -> RiskLevel.LOW      to "Score RIPASA: ${"%.1f".format(score)}. Probabilidad baja. Observación e investigación adicional."
    score < 11.5  -> RiskLevel.MODERATE to "Score RIPASA: ${"%.1f".format(score)}. Alta probabilidad de apendicitis. Valoración quirúrgica indicada."
    else          -> RiskLevel.HIGH     to "Score RIPASA: ${"%.1f".format(score)}. Apendicitis definitiva. Intervención quirúrgica indicada."
}

// ─── 10. p-POSSUM ────────────────────────────────────────────────────────────

@Composable
private fun PossumCalculator(onBack: () -> Unit) {
    // Physiologic score: 12 ordinal variables, each option maps to pts: 1, 2, 4, 8
    var age    by rememberSaveable { mutableIntStateOf(0) }   // <60, 61-70, ≥71
    var card   by rememberSaveable { mutableIntStateOf(0) }
    var resp   by rememberSaveable { mutableIntStateOf(0) }
    var sbp    by rememberSaveable { mutableIntStateOf(0) }
    var pulse  by rememberSaveable { mutableIntStateOf(0) }
    var gcs    by rememberSaveable { mutableIntStateOf(0) }
    var hgb    by rememberSaveable { mutableIntStateOf(0) }
    var wbc    by rememberSaveable { mutableIntStateOf(0) }
    var urea   by rememberSaveable { mutableIntStateOf(0) }
    var na     by rememberSaveable { mutableIntStateOf(0) }
    var k      by rememberSaveable { mutableIntStateOf(0) }
    var ecg    by rememberSaveable { mutableIntStateOf(0) }

    // Operative score: 6 variables
    var opSev  by rememberSaveable { mutableIntStateOf(0) }
    var nProc  by rememberSaveable { mutableIntStateOf(0) }
    var blood  by rememberSaveable { mutableIntStateOf(0) }
    var periton by rememberSaveable { mutableIntStateOf(0) }
    var malign by rememberSaveable { mutableIntStateOf(0) }
    var urg    by rememberSaveable { mutableIntStateOf(0) }

    fun pts(idx: Int, max3: Boolean = false) = when (idx) {
        0 -> 1; 1 -> 2; 2 -> 4; else -> if (max3) 4 else 8
    }
    val physScore = pts(age, max3 = true) + pts(card) + pts(resp) + pts(sbp) +
            pts(pulse) + pts(gcs) + pts(hgb) + pts(wbc) + pts(urea) + pts(na) + pts(k) + pts(ecg, max3 = true)

    val opScore = pts(opSev) + when (nProc) { 0 -> 1; 1 -> 4; else -> 8 } +
            pts(blood) + pts(periton) + pts(malign) + when (urg) { 0 -> 1; 1 -> 4; else -> 8 }

    val mobLogit = -5.91 + 0.16 * physScore + 0.19 * opScore
    val morLogit = -7.04 + 0.13 * physScore + 0.16 * opScore
    val morbidity = exp(mobLogit) / (1 + exp(mobLogit)) * 100
    val mortality = exp(morLogit) / (1 + exp(morLogit)) * 100

    val level = when {
        mortality < 5   -> RiskLevel.LOW
        mortality < 20  -> RiskLevel.MODERATE
        else            -> RiskLevel.HIGH
    }

    CalculatorScaffold(name = "p-POSSUM", onBack = onBack) {
        item { SectionHeader("Variables Fisiológicas") }
        item {
            OrdinalSelector("Edad", listOf("<60", "61–70", "≥71"), age) { age = it }
        }
        item {
            OrdinalSelector("Estado Cardíaco", listOf("Normal", "CCF/meds", "Edema periférico", "Cardiomegalia"), card) { card = it }
        }
        item {
            OrdinalSelector("Estado Respiratorio", listOf("Sin disnea", "Disnea esfuerzo", "Disnea escaleras", "Disnea reposo"), resp) { resp = it }
        }
        item {
            OrdinalSelector("PAS", listOf("110–130", "90–109 ó 131–170", "≤89 ó >170", "<90 inestable"), sbp) { sbp = it }
        }
        item {
            OrdinalSelector("Pulso", listOf("50–80", "40–49 ó 81–100", "101–120", ">120 ó <40"), pulse) { pulse = it }
        }
        item {
            OrdinalSelector("GCS", listOf("15", "12–14", "9–11", "<9"), gcs) { gcs = it }
        }
        item {
            OrdinalSelector("Hemoglobina (g/dL)", listOf("13–16", "11.5–12.9 ó 16.1–17", "10–11.4 ó 17.1–18", "<10 ó >18"), hgb) { hgb = it }
        }
        item {
            OrdinalSelector("WBC (×10³)", listOf("4–10", "3–4 ó 10–20", "<3 ó 20–30", ">30"), wbc) { wbc = it }
        }
        item {
            OrdinalSelector("Urea (mmol/L)", listOf("<7.5", "7.5–10", "10–15", ">15"), urea) { urea = it }
        }
        item {
            OrdinalSelector("Sodio (mEq/L)", listOf("136–145", "131–135", "126–130", "<126"), na) { na = it }
        }
        item {
            OrdinalSelector("Potasio (mEq/L)", listOf("3.5–5.0", "3.2–3.4 ó 5.1–5.3", "2.9–3.1 ó 5.4–5.9", "<2.9 ó >6"), k) { k = it }
        }
        item {
            OrdinalSelector("ECG", listOf("Normal", "Fibrilación Auricular", "Cambios ST-T", ">4 extrasístoles/min"), ecg) { ecg = it }
        }
        item { SectionHeader("Variables Operatorias") }
        item {
            OrdinalSelector("Severidad de la Cirugía", listOf("Menor", "Moderada", "Mayor", "Mayor+"), opSev) { opSev = it }
        }
        item {
            OrdinalSelector("Número de Procedimientos", listOf("1", "2", ">2"), nProc) { nProc = it }
        }
        item {
            OrdinalSelector("Sangrado (mL)", listOf("<100", "101–500", "501–999", "≥1000"), blood) { blood = it }
        }
        item {
            OrdinalSelector("Contaminación Peritoneal", listOf("Ninguna", "Mínima", "Pus local", "Pus/heces libre"), periton) { periton = it }
        }
        item {
            OrdinalSelector("Malignidad", listOf("Sin cáncer", "Solo primario", "Linfonodos", "Metástasis distantes"), malign) { malign = it }
        }
        item {
            OrdinalSelector("Urgencia", listOf("Electiva", "Urgencia (resuc >2h)", "Urgencia (<2h)"), urg) { urg = it }
        }
        item {
            Text(
                "Fisiológico: $physScore pts | Operatorio: $opScore pts",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
        item {
            ResultCard(
                "${"%.1f".format(morbidity)}% morbilidad  |  ${"%.1f".format(mortality)}% mortalidad",
                "p-POSSUM: Phys $physScore + Op $opScore. Morbilidad esperada: ${"%.1f".format(morbidity)}%. Mortalidad esperada: ${"%.1f".format(mortality)}%.",
                level,
            )
        }
        item { DisclaimerRow() }
    }
}
