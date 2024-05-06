package com.example.evolutionhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    // Navigation Bar için bu sınıfı oluşturmayı tercih ettim.
    // Bu sınıfı inherite ederek yani miras alarak işlemleri activitylerde sağladım.
    fun setupBottomNavigationView(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Ana Sayfa
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_daily_activities -> {
                    // Günlük İlerleme
                    val intent = Intent(this, PersonalProgressActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_motivation -> {
                    // Motivasyon
                    val intent = Intent(this, MotivationActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_analysis -> {
                    // Analiz
                    val intent = Intent(this, AnalysisActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_logout -> {
                    // Çıkış
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
