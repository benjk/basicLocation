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
            question = "À l'origine, le bâtiment central de la place a été construit pour abriter",
            options = listOf("Un marché couvert", "Un théâtre", "Un tribunal", "Un hôtel de ville"),
            correctAnswerIndex = 3
        ),
        QuizQuestion(
            question = "Combien de poteaux trouve-t-on sur les rembardes des balcons du Moulin à café ?",
            options = listOf("9", "11", "13", "15"),
            correctAnswerIndex = 2
        ),
        QuizQuestion(
            question = "Comment s'appelle l'association qui gère la salle de théâtre de la Grand-Place",
            options = listOf("La Barcarolle", "La compagnie du Moulin", "Le Ribot", "La Scène Audomaroise"),
            correctAnswerIndex = 0
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
        Log.d("ZZZ", "TONEXTQ")
        if (_currentQuestionIndex.value < questions.size - 1) {
            _currentQuestionIndex.value++
        }
    }

    fun goToPreviousQuestion() {
        Log.d("ZZZ", "TOPREVQ")

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