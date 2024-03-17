package com.lislal.areyousmarterthananimmigrant

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_setup)

        val uploadButton: Button = findViewById(R.id.uploadQuestionsButton)
        uploadButton.setOnClickListener {
            uploadQuestions()
        }
    }

    private fun uploadQuestions() {
        val db = FirebaseFirestore.getInstance()

        // Example question for difficulty level 7
        val questionsLevel7 = listOf(
            Question(
                question = "What is the name given to the study of ancient inscriptions?",
                options = listOf("Etymology", "Epigraphy", "Paleontology", "Philology"),
                correctAnswer = "Epigraphy"
            ),
            Question(
                question = "Who formulated the laws of motion and universal gravitation?",
                options = listOf("Albert Einstein", "Isaac Newton", "Galileo Galilei", "Nicolaus Copernicus"),
                correctAnswer = "Isaac Newton"
            ),
            Question(
                question = "What is the sum of the interior angles of a hexagon?",
                options = listOf("720 degrees", "360 degrees", "180 degrees", "540 degrees"),
                correctAnswer = "720 degrees"
            ),
            Question(
                question = "Which element has the highest melting point?",
                options = listOf("Carbon", "Tungsten", "Iron", "Platinum"),
                correctAnswer = "Tungsten"
            ),
            Question(
                question = "What phenomenon explains the natural force of attraction between physical objects?",
                options = listOf("Magnetism", "Gravity", "Electromagnetism", "Strong Nuclear Force"),
                correctAnswer = "Gravity"
            )
        )


        // Assuming a similar list exists for levels 2 through 7...

        // This is a simplified example to upload level 7 questions.
        // Repeat this process for each difficulty level.
        questionsLevel7.forEachIndexed { index, question ->
            db.collection("questions").document("difficulty_7")
                .collection("questionPool").document("question_$index")
                .set(question)
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }
}
