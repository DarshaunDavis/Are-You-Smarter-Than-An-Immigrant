package com.lislal.areyousmarterthananimmigrant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var currentScoreTextView: TextView
    private lateinit var highestScoreTextView: TextView
    private lateinit var startQuizButton: Button
    private lateinit var signInLinkTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI Components
        currentScoreTextView = findViewById(R.id.currentScore)
        highestScoreTextView = findViewById(R.id.highestScore)
        startQuizButton = findViewById(R.id.startQuizButton)
        signInLinkTextView = findViewById(R.id.signInLink)

        // Set up click listeners
        startQuizButton.setOnClickListener {
            QuestionPoolManager.initializeQuestionPool {
                // Once questions are loaded, start the QuestionActivity
                val intent = Intent(this@MainActivity, QuizActivity::class.java)
                startActivity(intent)
            }
        }

        val setupDatabaseButton: Button = findViewById(R.id.setupDatabaseButton)
        setupDatabaseButton.setOnClickListener {
            val intent = Intent(this, DatabaseSetupActivity::class.java)
            startActivity(intent)
        }

        signInLinkTextView.setOnClickListener {
            // Handle Sign-In (This could redirect to a Sign-In Activity)
            // For example:
            // val signInIntent = Intent(this, SignInActivity::class.java)
            // startActivity(signInIntent)
        }

        // For demonstration purposes, update the score directly here
        // In a real app, you would update these values based on the user's progress and actions
        updateScores(currentScore = 5, highestScore = 10)
    }

    private fun updateScores(currentScore: Int, highestScore: Int) {
        currentScoreTextView.text = "Current Score: $currentScore"
        highestScoreTextView.text = "Highest Score: $highestScore"
    }

    override fun onResume() {
        super.onResume()
        // Refresh scores from SharedPreferences
        val sharedPref = this.getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
        val currentScore = sharedPref.getInt("currentScore", 0)
        val highestScore = sharedPref.getInt("highestScore", 0)

        // Assuming you have TextViews in MainActivity similar to those in QuizActivity
        val currentScoreTextView: TextView = findViewById(R.id.currentScore)
        val highestScoreTextView: TextView = findViewById(R.id.highestScore)

        currentScoreTextView.text = currentScore.toString()
        highestScoreTextView.text = highestScore.toString()
    }

}
