package com.example.evolutionhub

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.evolutionhub.databinding.ActivityPersonalProgressBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PersonalProgressActivity : BaseActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityPersonalProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val personalProgressLayout: RelativeLayout = findViewById(R.id.personal_progress_layout)
        val animationDrawable: AnimationDrawable = personalProgressLayout.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        // BaseActivity içindeki setupBottomNavigationBar fonksiyonunu çağırdık. Böylece navigation view'i çalıştırmış olacağız.
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigationView(bottomNavigationView)


        // Intent'ten adım sayısını al
        val stepCount = intent.getIntExtra("stepCount", 0)

        // Burada adım sayısını kullanabilirsin, örneğin bir TextView'e yazdırabilirsin
        val showStepCountTextView: TextView = findViewById(R.id.showStepCountTextView)
        showStepCountTextView.text = "Toplam Adım Sayısı: $stepCount"

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("personal_progress").child(auth.currentUser?.uid ?: "")

        val goalLayout: LinearLayout = findViewById(R.id.goalLayout)
        val progressLayout: LinearLayout = findViewById(R.id.progressLayout)
        val exerciseDetailsLayout: LinearLayout = findViewById(R.id.exerciseDetailsLayout)
        val showDataLayout: LinearLayout = findViewById(R.id.showDataLayout)

        val goalNameEditText: EditText = findViewById(R.id.goalNameEditText)
        val goalTargetEditText: EditText = findViewById(R.id.goalTargetEditText)
        val saveGoalButton: Button = findViewById(R.id.saveGoalButton)

        val progressNameEditText: EditText = findViewById(R.id.progressNameEditText)
        val progressAmountEditText: EditText = findViewById(R.id.progressAmountEditText)
        val saveProgressButton: Button = findViewById(R.id.saveProgressButton)

        val exerciseTimeEditText: EditText = findViewById(R.id.exerciseTimeEditText)
        val runningDistanceEditText: EditText = findViewById(R.id.runningDistanceEditText)
        val stepCountEditText: EditText = findViewById(R.id.stepCountEditText)
        val saveExerciseDetailsButton: Button = findViewById(R.id.saveExerciseDetailsButton)

        val showGoalsTextView: TextView = findViewById(R.id.showGoalsTextView)
        val showProgressTextView: TextView = findViewById(R.id.showProgressTextView)

        saveGoalButton.setOnClickListener {
            val goalName = goalNameEditText.text.toString()
            val goalTarget = goalTargetEditText.text.toString()

            if (goalName.isNotEmpty() && goalTarget.isNotEmpty()) {
                saveGoal(goalName, goalTarget)
                goalNameEditText.text.clear()
                goalTargetEditText.text.clear()
            }
        }

        saveProgressButton.setOnClickListener {
            val progressName = progressNameEditText.text.toString()
            val progressAmount = progressAmountEditText.text.toString()

            if (progressName.isNotEmpty() && progressAmount.isNotEmpty()) {
                saveProgress(progressName, progressAmount)
                progressNameEditText.text.clear()
                progressAmountEditText.text.clear()
            }
        }

        saveExerciseDetailsButton.setOnClickListener {
            val exerciseTime = exerciseTimeEditText.text.toString()
            val runningDistance = runningDistanceEditText.text.toString()
            val stepCount = stepCountEditText.text.toString()

            if (exerciseTime.isNotEmpty() || runningDistance.isNotEmpty() || stepCount.isNotEmpty()) {
                saveExerciseDetails(exerciseTime, runningDistance, stepCount)
                exerciseTimeEditText.text.clear()
                runningDistanceEditText.text.clear()
                stepCountEditText.text.clear()
            }
        }

        // Hedef, ilerleme ve egzersiz detayı verilerini gösterdik.
        showData(showGoalsTextView, showProgressTextView)
    }

    private fun saveGoal(goalName: String, goalTarget: String) {
        val goalKey = database.child("goals").push().key
        val goalData = GoalData(goalName, goalTarget)
        database.child("goals").child(goalKey ?: "").setValue(goalData)
    }

    private fun saveProgress(progressName: String, progressAmount: String) {
        val progressKey = database.child("progress").push().key
        val progressData = ProgressData(progressName, progressAmount)
        database.child("progress").child(progressKey ?: "").setValue(progressData)
    }

    private fun saveExerciseDetails(exerciseTime: String, runningDistance: String, stepCount: String) {
        val exerciseDetailsKey = database.child("exercise_details").push().key
        val exerciseDetailsData = ExerciseDetailsData(exerciseTime, runningDistance, stepCount)
        database.child("exercise_details").child(exerciseDetailsKey ?: "").setValue(exerciseDetailsData)
    }

    private fun showExerciseDetails(details: String) {
        val exerciseDetailsTextView: TextView = findViewById(R.id.showExerciseDetailsTextView)
        exerciseDetailsTextView.text = details
    }

    private fun showData(showGoalsTextView: TextView, showProgressTextView: TextView) {
        // Hedefleri göster
        database.child("goals").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val goals = StringBuilder()
                for (goalSnapshot in snapshot.children) {
                    val goalData = goalSnapshot.getValue(GoalData::class.java)
                    goals.append("${goalData?.name}: ${goalData?.target}\n")
                }
                showGoalsTextView.text = goals.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // İlerlemeleri göster
        database.child("progress").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val progress = StringBuilder()
                for (progressSnapshot in snapshot.children) {
                    val progressData = progressSnapshot.getValue(ProgressData::class.java)
                    progress.append("${progressData?.name}: ${progressData?.amount}\n")
                }
                showProgressTextView.text = progress.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // Egzersiz detaylarını göster
        database.child("exercise_details").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exerciseDetails = StringBuilder()
                for (exerciseDetailsSnapshot in snapshot.children) {
                    val exerciseDetailsData = exerciseDetailsSnapshot.getValue(ExerciseDetailsData::class.java)
                    exerciseDetails.append("Egzersiz Süresi: ${exerciseDetailsData?.exerciseTime}, Koşu Mesafesi: ${exerciseDetailsData?.runningDistance}\n")
                }
                // Gösterme işlemlerini yap
                showExerciseDetails(exerciseDetails.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}




