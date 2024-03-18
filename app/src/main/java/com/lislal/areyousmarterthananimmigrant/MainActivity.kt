package com.lislal.areyousmarterthananimmigrant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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
        fetchHighScore()
        fetchLeaderboardData()
        fetchUserRoleAndAdjustUI()
    }

    override fun onResume() {
        super.onResume()
        updateScoresFromSharedPreferences()
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
        Log.d("MainActivity", "User role: $userRole")
        setupDatabaseButton.visibility = if (userRole == "admin") View.VISIBLE else View.GONE
    }

    private fun fetchUserRoleAndAdjustUI() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .get().addOnSuccessListener { document ->
                    val role = document.getString("role") ?: "user"
                    // Update UI based on fetched role
                    setupDatabaseButton.visibility = if (role == "admin") View.VISIBLE else View.GONE
                    // Optionally, save the fetched role into SharedPreferences again
                    getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit().apply {
                        putString("$uid-UserRole", role)
                        apply()
                    }
                }
        }
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

    private fun fetchHighScore() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val highScore = document.getLong("highScore")?.toInt() ?: 0
                        highestScoreTextView.text = "$highScore"

                        // Update SharedPreferences with the latest high score
                        val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putInt("highestScore", highScore)
                            apply()
                        }
                    }
                }
        }
    }

    private fun updateScoresFromSharedPreferences() {
        val sharedPref = getSharedPreferences("com.yourapp.preferences", Context.MODE_PRIVATE)
        val currentScore = sharedPref.getInt("currentScore", 0)
        val highestScore = sharedPref.getInt("highestScore", 0)
        currentScoreTextView.text = currentScore.toString()
        highestScoreTextView.text = highestScore.toString()
    }

    private fun fetchLeaderboardData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .orderBy("highScore", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("MainActivity", "No leaderboard entries found.")
                    return@addOnSuccessListener
                }
                val leaderboardEntries = documents.mapNotNull { doc ->
                    // Assuming the username and highScore fields exist
                    val username = doc.getString("username") ?: "Anonymous"
                    val highScore = doc.getLong("highScore")?.toInt() ?: 0
                    LeaderboardEntry(username, highScore)
                }
                Log.d("MainActivity", "Leaderboard entries: $leaderboardEntries")
                val adapter = LeaderboardAdapter(leaderboardEntries)
                findViewById<RecyclerView>(R.id.leaderboardRecyclerView).apply {
                    layoutManager = LinearLayoutManager(context)
                    this.adapter = adapter
                }
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error fetching leaderboard data", e)
            }
    }

}
