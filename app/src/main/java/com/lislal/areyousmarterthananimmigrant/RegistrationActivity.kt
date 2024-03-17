package com.lislal.areyousmarterthananimmigrant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentScoreTextView: TextView
    private lateinit var highestScoreTextView: TextView
    private lateinit var signInLinkTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmPasswordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)
        signInLinkTextView = findViewById(R.id.signInLink)

        // In MainActivity or the appropriate activity
        signInLinkTextView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""
                            // Including role in the user document
                            val userMap = hashMapOf(
                                "username" to username,
                                "role" to "User" // Default role for new registrations
                            )

                            firestore.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(baseContext, "Failed to save user profile.", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            }
        }

        currentScoreTextView = findViewById(R.id.currentScore)
        highestScoreTextView = findViewById(R.id.highestScore)
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