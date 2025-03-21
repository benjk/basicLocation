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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
    val coroutineScope = rememberCoroutineScope()

    // Définir la durée d'animation
    val questionTransitionDuration = 150

    // Questions DATA
    val currentQuestionIndex by remember { quizViewModel.currentQuestionIndex }
    val currentQuestion = quizViewModel.getCurrentQuestion()
    val selectedAnswerIndex = quizViewModel.getSelectedAnswer()
    val totalQuestions = quizViewModel.getQuestionCount()

    // BUTTON
    val buttonText = "CONTINUER"
    val buttonEnabled = (quizViewModel.isLastQuestion() && quizViewModel.allQuestionsAnswered()) ||
            (!quizViewModel.isLastQuestion() && quizViewModel.getSelectedAnswer() != -1)

    // Swipe utils
    var isSwipingEnabled by remember { mutableStateOf(true) }
    // drag
    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragCurrentX by remember { mutableFloatStateOf(0f) }
    val dragThreshold = 100f  // Seuil pour considérer qu'un swipe a eu lieu
    // Pour l'animation entre les questions
    var isQuestionVisible by remember { mutableStateOf(true) }
    var slideDirection by remember { mutableIntStateOf(1) }

    fun handleSwipe(isSwipeLeft: Boolean) {
        // Hide question and disable swipe while animation runs
        isQuestionVisible = false
        slideDirection = if (isSwipeLeft) 1 else -1
        isSwipingEnabled = false

        // Coroutine pour gérer l'animation et la mise à jour de la question
        coroutineScope.launch {
            kotlinx.coroutines.delay(questionTransitionDuration.toLong())
            if (isSwipeLeft) {
                quizViewModel.goToNextQuestion()
            } else {
                quizViewModel.goToPreviousQuestion()
            }
            kotlinx.coroutines.delay(100)
            isQuestionVisible = true
            isSwipingEnabled = true
        }
    }

    TabComponent(
        title = gameTitle,
        buttonText = buttonText,
        onButtonClick = {
            if (quizViewModel.isLastQuestion()) {
                val score = quizViewModel.calculateScore()
                onClose()
                onQuizCompleted(score, totalQuestions)
            } else {
                handleSwipe(true)
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
                            val dragDistance = abs(dragCurrentX - dragStartX)
                            if (isSwipingEnabled && dragDistance > dragThreshold) {
                                if (dragCurrentX < dragStartX && !quizViewModel.isLastQuestion()) {
                                    handleSwipe(true)
                                } else if (dragCurrentX > dragStartX && currentQuestionIndex > 0) {
                                    handleSwipe(false)
                                }
                            }
                        },
                        onDragCancel = { },
                        onDrag = { change, _ -> dragCurrentX = change.position.x }
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
                        animationSpec = tween(questionTransitionDuration)
                    ) + fadeIn(animationSpec = tween(questionTransitionDuration)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { if (slideDirection > 0) -it else it },
                        animationSpec = tween(questionTransitionDuration)
                    ) + fadeOut(animationSpec = tween(questionTransitionDuration))
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