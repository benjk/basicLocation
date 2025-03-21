package com.example.basiclocation.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

class QuizViewModel : ViewModel() {
    // Liste des questions du quiz
    private val questions = listOf(
        QuizQuestion(
            question = "Quelle est la capitale de la France ?",
            options = listOf("Londres", "Berlin", "Paris", "Madrid"),
            correctAnswerIndex = 2
        ),
        QuizQuestion(
            question = "Quel est le plus grand océan du monde ?",
            options = listOf("Atlantique", "Indien", "Arctique", "Pacifique"),
            correctAnswerIndex = 3
        ),
        QuizQuestion(
            question = "Quelle planète est connue comme la planète rouge ?",
            options = listOf("Venus", "Mars", "Jupiter", "Saturne"),
            correctAnswerIndex = 1
        )
    )

    private val _currentQuestionIndex = mutableStateOf(0)
    val currentQuestionIndex: State<Int> = _currentQuestionIndex

    private val _selectedAnswers = mutableStateListOf<Int>().apply { repeat(3) { add(-1) } }
    val selectedAnswers: List<Int> get() = _selectedAnswers

    fun getCurrentQuestion(): QuizQuestion = questions[_currentQuestionIndex.value]

    fun isLastQuestion(): Boolean = _currentQuestionIndex.value == questions.size - 1

    fun getQuestionCount(): Int = questions.size

    fun selectAnswer(answerIndex: Int) {
        _selectedAnswers[_currentQuestionIndex.value] = answerIndex
    }

    fun getSelectedAnswer(): Int = _selectedAnswers[_currentQuestionIndex.value]

    fun goToNextQuestion() {
        if (_currentQuestionIndex.value < questions.size - 1) {
            _currentQuestionIndex.value++
        }
    }

    fun goToPreviousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value--
        }
    }

    fun isCurrentAnswerCorrect(): Boolean {
        return _selectedAnswers[_currentQuestionIndex.value] == questions[_currentQuestionIndex.value].correctAnswerIndex
    }

    fun calculateScore(): Int {
        var score = 0
        for (i in _selectedAnswers.indices) {
            val selectedAnswer = _selectedAnswers[i]
            if (selectedAnswer != -1 && i < questions.size) {
                if (selectedAnswer == questions[i].correctAnswerIndex) {
                    score++
                }
            }
        }
        Log.d("ZZZ", "Score : " + score)
        return score
    }

    fun allQuestionsAnswered(): Boolean {
        return _selectedAnswers.none { it == -1 }
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _selectedAnswers.clear()
        _selectedAnswers.addAll(List(questions.size) { -1 })
    }
}