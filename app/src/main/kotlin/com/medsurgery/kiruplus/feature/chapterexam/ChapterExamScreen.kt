package com.medsurgery.kiruplus.feature.chapterexam

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsurgery.kiruplus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterExamScreen(
    onBack: () -> Unit,
    viewModel: ChapterExamViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.exam?.title ?: stringResource(R.string.chapter_exam_title),
                        maxLines = 1,
                    )
                },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                state.error != null -> ChapterExamErrorState(
                    onRetry = viewModel::load,
                )

                state.isEmpty -> ChapterExamEmptyState(onBack = onBack)

                state.isCompleted -> ChapterExamScoreScreen(
                    correct = state.correctCount,
                    total = state.totalQuestions,
                    scorePct = state.scorePct,
                    onRestart = viewModel::restart,
                    onBack = onBack,
                )

                state.currentQuestion != null -> ChapterExamQuestionPage(
                    state = state,
                    onSelectOption = viewModel::selectOption,
                    onNext = viewModel::nextQuestion,
                )
            }
        }
    }
}

@Composable
private fun ChapterExamQuestionPage(
    state: ChapterExamUiState,
    onSelectOption: (Int) -> Unit,
    onNext: () -> Unit,
) {
    val question = state.currentQuestion ?: return
    val progress = if (state.totalQuestions > 0)
        (state.currentIndex + 1).toFloat() / state.totalQuestions else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = stringResource(
                R.string.chapter_exam_progress,
                state.currentIndex + 1,
                state.totalQuestions,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        )

        Text(
            text = question.prompt,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        Spacer(Modifier.height(8.dp))

        question.options.forEachIndexed { index, option ->
            val isSelected = state.selectedOptionIndex == index
            val isAnswered = state.isAnswered
            val isCorrect = index == question.correctIndex

            val borderColor = when {
                !isAnswered -> MaterialTheme.colorScheme.outline
                isCorrect -> MaterialTheme.colorScheme.tertiary
                isSelected -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline
            }
            val containerColor = when {
                !isAnswered -> MaterialTheme.colorScheme.surface
                isCorrect -> MaterialTheme.colorScheme.tertiaryContainer
                isSelected -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
            val contentColor = when {
                !isAnswered -> MaterialTheme.colorScheme.onSurface
                isCorrect -> MaterialTheme.colorScheme.onTertiaryContainer
                isSelected -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onSurface
            }

            val optionLabel = ('A' + index).toString()
            val answerStateDesc = when {
                !isAnswered -> ""
                isCorrect && isSelected -> stringResource(R.string.chapter_exam_correct)
                isCorrect -> stringResource(R.string.chapter_exam_correct)
                isSelected -> stringResource(R.string.chapter_exam_incorrect)
                else -> ""
            }

            OutlinedButton(
                onClick = { if (!isAnswered) onSelectOption(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .semantics { stateDescription = answerStateDesc },
                border = BorderStroke(
                    width = if (isSelected || (isAnswered && isCorrect)) 2.dp else 1.dp,
                    color = borderColor,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    disabledContainerColor = containerColor,
                    disabledContentColor = contentColor,
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = optionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        if (state.isAnswered && question.rationale.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.chapter_exam_rationale_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = question.rationale,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }

        if (state.isAnswered) {
            Spacer(Modifier.height(16.dp))
            val isLast = state.currentIndex + 1 >= state.totalQuestions
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = if (isLast)
                        stringResource(R.string.chapter_exam_see_results)
                    else
                        stringResource(R.string.chapter_exam_next),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ChapterExamScoreScreen(
    correct: Int,
    total: Int,
    scorePct: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.chapter_exam_score_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        val scoreColor = when {
            scorePct >= 80 -> MaterialTheme.colorScheme.tertiary
            scorePct >= 60 -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.error
        }

        Text(
            text = stringResource(R.string.chapter_exam_score, correct, total, scorePct),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = scoreColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics {
                stateDescription = "$scorePct%"
            },
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.chapter_exam_restart))
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.chapter_exam_back_to_library))
        }
    }
}

@Composable
private fun ChapterExamErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.chapter_exam_error_load),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun ChapterExamEmptyState(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.chapter_exam_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.action_back))
            }
        }
    }
}
