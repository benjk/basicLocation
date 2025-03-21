package com.example.basiclocation.ui.comp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basiclocation.ui.theme.secondaryColor
import com.example.basiclocation.ui.theme.thirdColor
import com.example.basiclocation.viewmodels.QuizViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun QuizzTab(
    gameTitle: String,
    onClose: () -> Unit,
    onQuizCompleted: (Int, Int) -> Unit = { _, _ -> }
) {
    val quizViewModel: QuizViewModel = viewModel()
    var isSwipingEnabled by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()


    // Pour gérer le drag
    var dragStartX by remember { mutableStateOf(0f) }
    val dragThreshold = 100f  // Seuil pour considérer qu'un swipe a eu lieu

    val currentQuestionIndex = quizViewModel.currentQuestionIndex.value
    val selectedAnswers by remember { mutableStateOf(quizViewModel.selectedAnswers) }
    val currentQuestion = quizViewModel.getCurrentQuestion()
    val selectedAnswerIndex = quizViewModel.getSelectedAnswer()

    // Si l'on veut observer le changement dans la question actuelle
    val isLastQuestion = quizViewModel.isLastQuestion()
    val totalQuestions = quizViewModel.getQuestionCount()

    // Définir les textes pour le bouton
    val buttonText = if (isLastQuestion) "ENVOYER LE QUIZ" else "QUESTION SUIVANTE"
    val buttonEnabled = quizViewModel.getSelectedAnswer() != -1

    // Pour l'animation entre les questions
    var isQuestionVisible by remember { mutableStateOf(true) }
    var slideDirection by remember { mutableStateOf(1) } // 1 pour droite->gauche, -1 pour gauche->droite

    TabComponent(
        title = gameTitle,
        buttonText = buttonText,
        onButtonClick = {
            if (isLastQuestion) {
                // Envoyer le quiz
                val score = quizViewModel.calculateScore()
                onQuizCompleted(score, totalQuestions)
            } else {
                // Passer à la question suivante avec animation
                isQuestionVisible = false
                slideDirection = 1
                isSwipingEnabled = false

                // Attendre que l'animation soit terminée avant de changer la question
                coroutineScope.launch {
                    kotlinx.coroutines.delay(300)
                    quizViewModel.goToNextQuestion()
                    kotlinx.coroutines.delay(100)
                    isQuestionVisible = true
                    isSwipingEnabled = true
                }
            }
        },
        buttonEnabled = buttonEnabled,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startPoint ->
                            dragStartX = startPoint.x
                        },
                        onDragEnd = {
                            val dragEndX = dragStartX
                            val dragDistance = abs(dragEndX - dragStartX)

                            if (isSwipingEnabled && dragDistance > dragThreshold) {
                                if (dragEndX < dragStartX && !isLastQuestion) {
                                    // Swipe vers la gauche (question suivante)
                                    isQuestionVisible = false
                                    slideDirection = 1
                                    isSwipingEnabled = false

                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(300)
                                        quizViewModel.goToNextQuestion()
                                        kotlinx.coroutines.delay(100)
                                        isQuestionVisible = true
                                        isSwipingEnabled = true
                                    }
                                } else if (dragEndX > dragStartX && currentQuestionIndex > 0) {
                                    // Swipe vers la droite (question précédente)
                                    isQuestionVisible = false
                                    slideDirection = -1
                                    isSwipingEnabled = false

                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(300)
                                        quizViewModel.goToPreviousQuestion()
                                        kotlinx.coroutines.delay(100)
                                        isQuestionVisible = true
                                        isSwipingEnabled = true
                                    }
                                }
                            }
                        },
                        onDragCancel = { },
                        onDrag = { change, _ -> change.consume() }
                    )
                }
        ) {
            // Indicateur de progression
            LinearProgressIndicator(
                progress = (currentQuestionIndex + 1).toFloat() / totalQuestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = secondaryColor,
                trackColor = thirdColor
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Animation de transition entre les questions
                this@Column.AnimatedVisibility(
                    visible = isQuestionVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { if (slideDirection > 0) it else -it },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { if (slideDirection > 0) -it else it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    QuestionComponent(
                        question = currentQuestion,
                        selectedAnswerIndex = selectedAnswerIndex,
                        onAnswerSelected = { index ->
                            quizViewModel.selectAnswer(index)
                        }
                    )
                }
            }
        }
    }
}