package com.lislal.areyousmarterthananimmigrant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var currentScoreTextView: TextView
    private lateinit var highestScoreTextView: TextView
    private lateinit var startQuizButton: Button
    private lateinit var signInLinkTextView: TextView
    private lateinit var setupDatabaseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUIComponents()
        updateUIBasedOnUserState()
        setClickListeners()
    }

    private fun initializeUIComponents() {
        currentScoreTextView = findViewById(R.id.currentScore)
        highestScoreTextView = findViewById(R.id.highestScore)
        startQuizButton = findViewById(R.id.startQuizButton)
        signInLinkTextView = findViewById(R.id.signInLink)
        setupDatabaseButton = findViewById(R.id.setupDatabaseButton)
    }

    private fun updateUIBasedOnUserState() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // User is signed in
            signInLinkTextView.visibility = View.GONE
            checkUserRoleAndUpdateUI(auth.currentUser!!.uid)
        } else {
            // No user is signed in
            signInLinkTextView.visibility = View.VISIBLE
            setupDatabaseButton.visibility = View.GONE
        }
    }

    private fun checkUserRoleAndUpdateUI(uid: String) {
        // Assuming the role has been saved in SharedPreferences during login or registration
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("$uid-UserRole", "user")
        setupDatabaseButton.visibility = if (userRole == "admin") View.VISIBLE else View.GONE
    }

    private fun setClickListeners() {
        startQuizButton.setOnClickListener {
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            startActivity(intent)
        }

        setupDatabaseButton.setOnClickListener {
            val intent = Intent(this, DatabaseSetupActivity::class.java)
            startActivity(intent)
        }

        signInLinkTextView.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateScoresFromSharedPreferences()
    }

    private fun updateScoresFromSharedPreferences() {
        val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
        val currentScore = sharedPref.getInt("currentScore", 0)
        val highestScore = sharedPref.getInt("highestScore", 0)
        currentScoreTextView.text = currentScore.toString()
        highestScoreTextView.text = highestScore.toString()
    }
}
