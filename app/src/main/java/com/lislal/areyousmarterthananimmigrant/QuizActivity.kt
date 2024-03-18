package com.lislal.areyousmarterthananimmigrant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    private lateinit var currentScoreTextView: TextView
    private lateinit var highestScoreTextView: TextView
    private var currentScore = 0
    private var highestScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        displayNextQuestion()

        currentScoreTextView = findViewById(R.id.currentScore)
        highestScoreTextView = findViewById(R.id.highestScore)

        // Access SharedPreferences
        val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
        currentScore = sharedPref.getInt("currentScore", 0)
        highestScore = sharedPref.getInt("highestScore", 0)
        updateScoresDisplay()

        val addButton: Button = findViewById(R.id.addButton)
        val resetButton: Button = findViewById(R.id.resetButton)

        addButton.setOnClickListener {
            incrementScore()
        }

        resetButton.setOnClickListener {
            resetScore()
        }
    }

    // Inside QuizActivity
    private fun displayQuestionAndOptions(question: Question) {
        val questionTextView: TextView = findViewById(R.id.questionTextView)
        val answersRadioGroup: RadioGroup = findViewById(R.id.answersRadioGroup)
        val submitButton: Button = findViewById(R.id.submitAnswerButton)

        // Set the question text
        questionTextView.text = question.question

        // Clear previous answers
        answersRadioGroup.removeAllViews()

        // Dynamically add radio buttons for each answer option
        question.options.forEach { option ->
            val radioButton = RadioButton(this).apply {
                text = option
            }
            answersRadioGroup.addView(radioButton)
        }

        // Handle submit button click
        submitButton.setOnClickListener {
            val selectedOptionIndex =
                answersRadioGroup.indexOfChild(findViewById(answersRadioGroup.checkedRadioButtonId))
            val selectedOption = question.options.getOrNull(selectedOptionIndex)

            // Check if the selected option is correct
            if (selectedOption != null && selectedOption == question.correctAnswer) {
                incrementScore()
                displayNextQuestion()
            } else {
                onIncorrectAnswerSelected()
            }
        }
    }


    private fun displayNextQuestion() {
        val question = QuestionPoolManager.getRandomQuestion()
        if (question != null) {
            displayQuestionAndOptions(question)
        } else {
            // Handle the case where no question is available
            finish()
        }

        // Check if the pool needs replenishing
        if (QuestionPoolManager.shouldReloadPool()) {
            QuestionPoolManager.initializeQuestionPool {}
        }
    }

    private fun incrementScore() {
        currentScore++
        updateScoresDisplay()

        val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
        highestScore = sharedPref.getInt("highestScore", 0)

        if (currentScore > highestScore) {
            highestScore = currentScore
            with(sharedPref.edit()) {
                putInt("highestScore", highestScore)
                apply()
            }
            updateScoresDisplay()

            // Update high score in Firestore
            FirebaseAuth.getInstance().currentUser?.let { user ->
                FirebaseFirestore.getInstance().collection("users").document(user.uid)
                    .update("highScore", highestScore)
                    .addOnSuccessListener {
                        // Successfully updated high score in Firestore
                    }
                    .addOnFailureListener {
                        // Failed to update high score
                    }
            }
        }
    }

    private fun resetScore() {
        currentScore = 0
        updateScoresDisplay()

        // Persist reset current score in SharedPreferences
        val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("currentScore", currentScore)
            apply()
        }
    }

    private fun updateScoresDisplay() {
        currentScoreTextView.text = currentScore.toString()
        highestScoreTextView.text = highestScore.toString()
    }

    // When an incorrect answer is selected
    private fun onIncorrectAnswerSelected() {
        // You might want to reset the current score or handle it differently depending on your app's logic
        resetScore() // Only call this if you wish to reset the score on incorrect answers

        // Navigate back to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close QuizActivity
    }
}

object QuestionPoolManager {
    private var questionPool = mutableListOf<Question>()
    private const val questionsPerDifficulty = 5 // x amount of questions from each difficulty

    // Fetches a set number of questions from each difficulty level
    fun initializeQuestionPool(onComplete: () -> Unit) {
        questionPool.clear() // Clear the existing pool
        val db = FirebaseFirestore.getInstance()

        for (difficulty in 1..7) { // For each difficulty level
            db.collection("questions").document("difficulty_$difficulty")
                .collection("questionPool").limit(questionsPerDifficulty.toLong()).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val question = document.toObject(Question::class.java)
                        questionPool.add(question)
                    }
                    if (difficulty == 7) { // Last difficulty level fetched
                        onComplete()
                    }
                }
        }
    }

    // Gets a random question from the pool and optionally removes it
    fun getRandomQuestion(removeFetched: Boolean = true): Question? {
        if (questionPool.isEmpty()) return null
        val randomIndex = Random.nextInt(questionPool.size)
        return if (removeFetched) questionPool.removeAt(randomIndex) else questionPool[randomIndex]
    }

    // Checks if the pool is running low to trigger a reload
    fun shouldReloadPool(): Boolean {
        // Define your threshold for low. For example, if less than 10 questions are left.
        return questionPool.size < 10
    }
}

