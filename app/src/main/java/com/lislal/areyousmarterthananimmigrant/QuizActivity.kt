    package com.lislal.areyousmarterthananimmigrant

    import android.content.Context
    import android.os.Bundle
    import android.widget.Button
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity

    class QuizActivity : AppCompatActivity() {

        private lateinit var currentScoreTextView: TextView
        private lateinit var highestScoreTextView: TextView
        private var currentScore = 0
        private var highestScore = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_quiz)

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

        private fun incrementScore() {
            currentScore++
            if (currentScore > highestScore) {
                highestScore = currentScore
                // Persist updated highest score in SharedPreferences
                val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    putInt("highestScore", highestScore)
                    apply()
                }
            }
            updateScoresDisplay()
        }

        private fun resetScore() {
            currentScore = 0
            updateScoresDisplay()

            // Persist reset current score in SharedPreferences
            val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putInt("currentScore", currentScore)
                apply()
            }
        }

        private fun updateScoresDisplay() {
            currentScoreTextView.text = currentScore.toString()
            highestScoreTextView.text = highestScore.toString()
        }
    }
