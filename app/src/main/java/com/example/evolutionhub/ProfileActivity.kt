package com.example.evolutionhub

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.evolutionhub.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference?=null
    var database: FirebaseDatabase?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // gerekli database bağlantıları yapıldı. (klasik işlemler.)

        /* aşağıdaki kodlarımız ise giriş yapan kullanıcının id'sini auth'dan alıp realtime database'de
        karşılık gelen id'nin bilgilerini listeleyerek profil sayfasına aktaracaktır.
         */

        val profileLayout: ConstraintLayout = findViewById(R.id.layout_profile)
        val animationDrawable: AnimationDrawable = profileLayout.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")
        var currentUser = auth.currentUser
        binding.profilmail.text = "Email: "+currentUser?.email

        var userReference = databaseReference?.child(currentUser?.uid!!)
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { //ad soyad verisini çektik
                binding.profilisim.text = "Adınız: " + snapshot.child("adisoyadi").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
        // oturum profil sayfasından sonlandırılırsa ilk sayfaya yönlendireceğiz:
        binding.profilcikisyap.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this@ProfileActivity,LoginActivity::class.java))
            finish()
        }
        // Hesabı sil butonu tıklanırsa:
        binding.profilsil.setOnClickListener {
            currentUser?.delete()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(applicationContext,"Hesabınız başarıyla silindi :(",
                    Toast.LENGTH_LONG).show()
                    auth.signOut()
                    startActivity(Intent(this@ProfileActivity,LoginActivity::class.java))
                    finish()
                    var currentUserDb = currentUser?.let { it1 -> databaseReference?.child(it1.uid) }
                    currentUserDb?.removeValue()
                    Toast.makeText(applicationContext,"Tüm veriler kaldırıldı.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
        // Bilgileri güncelleme butonu:
        binding.profilguncelle2.setOnClickListener {
            val intent = Intent(this, ProfilGuncelleActivity::class.java)
            startActivity(intent)
            true
        }
    }
}