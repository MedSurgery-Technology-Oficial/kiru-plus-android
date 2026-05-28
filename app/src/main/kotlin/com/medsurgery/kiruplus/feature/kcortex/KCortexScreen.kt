package com.medsurgery.kiruplus.feature.kcortex

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medsurgery.kiruplus.domain.kcortex.ClinicalDataRow
import com.medsurgery.kiruplus.domain.kcortex.KCortexAnalysis
import com.medsurgery.kiruplus.domain.kcortex.KCortexAnalysisType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KCortexScreen(
    onBack: () -> Unit,
    viewModel: KCortexViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("K-CORTEX") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
                actions = {
                    if (uiState.phase == KCortexPhase.Result) {
                        IconButton(onClick = viewModel::reset) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Nuevo análisis",
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        AnimatedContent(
            targetState = uiState.phase,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            label = "KCortexPhaseTransition",
        ) { phase ->
            when (phase) {
                KCortexPhase.Input -> KCortexInputContent(
                    selectedType = uiState.selectedType,
                    clinicalInput = uiState.clinicalInput,
                    onTypeSelected = viewModel::onTypeSelected,
                    onInputChanged = viewModel::onInputChanged,
                    onSubmit = viewModel::submitAnalysis,
                )

                KCortexPhase.Analyzing -> KCortexAnalyzingContent()

                KCortexPhase.Result -> {
                    val analysis = uiState.result
                    if (analysis != null) {
                        KCortexResultContent(
                            analysis = analysis,
                            onReset = viewModel::reset,
                        )
                    } else {
                        KCortexAnalyzingContent()
                    }
                }
            }
        }
    }
}

// ── Input screen ─────────────────────────────────────────────────────────────

@Composable
private fun KCortexInputContent(
    selectedType: KCortexAnalysisType,
    clinicalInput: String,
    onTypeSelected: (KCortexAnalysisType) -> Unit,
    onInputChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp),
                )
                Column {
                    Text(
                        text = "Dr. Kapibaya K-CORTEX",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = "Análisis clínico asistido por IA. Solo para uso educativo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                }
            }
        }

        // Type selector
        Text(
            text = "Tipo de análisis",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        KCortexTypeSelector(
            selectedType = selectedType,
            onTypeSelected = onTypeSelected,
        )

        // Input field
        OutlinedTextField(
            value = clinicalInput,
            onValueChange = onInputChanged,
            label = { Text("Datos clínicos") },
            placeholder = {
                Text(
                    text = placeholderForType(selectedType),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            maxLines = 12,
            supportingText = {
                Text(
                    "${clinicalInput.length} caracteres · mín. 10",
                    style = MaterialTheme.typography.bodySmall,
                )
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        // Disclaimer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Herramienta educativa. No sustituye el juicio clínico del médico tratante.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = clinicalInput.trim().length >= 10,
        ) {
            Icon(
                imageVector = Icons.Filled.Science,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text("Analizar con K-CORTEX")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KCortexTypeSelector(
    selectedType: KCortexAnalysisType,
    onTypeSelected: (KCortexAnalysisType) -> Unit,
) {
    val visibleTypes = listOf(
        KCortexAnalysisType.LABORATORIOS,
        KCortexAnalysisType.GASOMETRIA,
        KCortexAnalysisType.ECG,
        KCortexAnalysisType.IMAGEN_MEDICA,
        KCortexAnalysisType.TEXTO_CLINICO,
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            visibleTypes.take(3).forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.displayName, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            visibleTypes.drop(3).forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.displayName, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun placeholderForType(type: KCortexAnalysisType): String = when (type) {
    KCortexAnalysisType.LABORATORIOS ->
        "Hb 8.5 g/dL, Leucocitos 14,000, Plaquetas 320,000, Na 138, K 3.8, Cr 1.2..."
    KCortexAnalysisType.GASOMETRIA ->
        "pH 7.28, PaCO2 52 mmHg, HCO3 22 mEq/L, PaO2 68 mmHg, BE -4, Lactato 3.1..."
    KCortexAnalysisType.ECG ->
        "FC 110 lpm, ritmo sinusal, QRS 0.10s, QTc 420ms, ST deprimido 1mm en V4-V6..."
    KCortexAnalysisType.IMAGEN_MEDICA ->
        "Rx tórax PA: opacidad en base pulmonar derecha, borramiento del seno costodiafragmático..."
    KCortexAnalysisType.TEXTO_CLINICO ->
        "Masculino 45 años con dolor abdominal en FID de 24h de evolución, náusea, fiebre 38.5°C..."
}

// ── Analyzing screen ─────────────────────────────────────────────────────────

@Composable
private fun KCortexAnalyzingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "K-CORTEX analizando...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Dr. Kapibaya está procesando los datos clínicos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Result screen ─────────────────────────────────────────────────────────────

@Composable
private fun KCortexResultContent(
    analysis: KCortexAnalysis,
    onReset: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Quality badge + modality
        item {
            KCortexQualityBadge(analysis)
        }

        // Clinical data table (if structured data available)
        if (analysis.clinicalData.isNotEmpty()) {
            item {
                KCortexSectionCard(title = "Datos Clínicos Extraídos") {
                    analysis.clinicalData.forEach { row ->
                        ClinicalDataRowItem(row)
                        if (row != analysis.clinicalData.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Red flags
        if (analysis.redFlags.isNotEmpty()) {
            item {
                KCortexSectionCard(
                    title = "Alertas Críticas",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ) {
                    analysis.redFlags.forEach { flag ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = flag,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }
                }
            }
        }

        // Findings
        if (analysis.findings.isNotBlank()) {
            item {
                KCortexSectionCard(title = "Hallazgos") {
                    Text(
                        text = analysis.findings,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Preliminary interpretation
        if (analysis.preliminaryInterpretation.isNotBlank()) {
            item {
                KCortexSectionCard(title = "Interpretación Preliminar") {
                    Text(
                        text = analysis.preliminaryInterpretation,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Limitations
        if (analysis.limitations.isNotBlank()) {
            item {
                KCortexSectionCard(title = "Limitaciones") {
                    Text(
                        text = analysis.limitations,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Missing data
        if (analysis.missingData.isNotBlank()) {
            item {
                KCortexSectionCard(title = "Datos No Identificables") {
                    Text(
                        text = analysis.missingData,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // Recommendations
        if (analysis.recommendations.isNotBlank()) {
            item {
                KCortexSectionCard(title = "Recomendaciones") {
                    Text(
                        text = analysis.recommendations,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Rejection reason
        if (!analysis.isUsable && analysis.rejectionReason != null) {
            item {
                KCortexSectionCard(
                    title = "No analizable",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = analysis.rejectionReason,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // Disclaimer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Text(
                    text = "⚠️ Resultado educativo. No constituye diagnóstico ni tratamiento. Confirma con el médico tratante.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        // New analysis button
        item {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Text("Nuevo análisis")
            }
        }
    }
}

@Composable
private fun KCortexQualityBadge(analysis: KCortexAnalysis) {
    val (bgColor, fgColor) = when (analysis.quality) {
        "Analizable" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "Parcialmente analizable" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = analysis.quality,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = fgColor,
                )
                Text(
                    text = analysis.modality,
                    style = MaterialTheme.typography.labelMedium,
                    color = fgColor.copy(alpha = 0.7f),
                )
            }
            Text(
                text = "${analysis.clinicalDataExtractedCount} datos clínicos extraídos",
                style = MaterialTheme.typography.bodySmall,
                color = fgColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun KCortexSectionCard(
    title: String,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            content()
        }
    }
}

@Composable
private fun ClinicalDataRowItem(row: ClinicalDataRow) {
    val valueColor = when {
        row.isCritical -> MaterialTheme.colorScheme.error
        row.isAbnormal -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(
                text = row.analyte,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            if (row.referenceRange.isNotBlank()) {
                Text(
                    text = "Ref: ${row.referenceRange}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = "${row.value} ${row.unit}".trim(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
            if (row.interpretation.isNotBlank()) {
                Text(
                    text = row.interpretation,
                    style = MaterialTheme.typography.labelSmall,
                    color = valueColor.copy(alpha = 0.8f),
                )
            }
        }
    }
}
