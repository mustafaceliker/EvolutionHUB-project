package com.example.evolutionhub

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.evolutionhub.databinding.ActivityProfilGuncelleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfilGuncelleActivity : BaseActivity() { // Referanslar ve klasik komutlar (database bağlantı)

    lateinit var binding: ActivityProfilGuncelleBinding
    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference?=null
    var database: FirebaseDatabase?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityProfilGuncelleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profilGuncelleLayout: ConstraintLayout = findViewById(R.id.layout_profil_guncelle)
        val animationDrawable: AnimationDrawable = profilGuncelleLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")



        var currentUser = auth.currentUser // Giriş yapılı kullanıcığa eriştik ve current user'a aktardık.
        binding.guncelleeposta.setText(currentUser?.email) // Epostayı aldık sıra id'e erişip ismi almakta.

        var userReference = databaseReference?.child(currentUser?.uid!!)
        userReference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.guncelleisim.setText(snapshot.child("adisoyadi").value.toString()) // Realtime db'deki id'den ad soyadı aldık.
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        // Bilgileri güncelleyecek butonun işlevi.
        binding.guncellebutton.setOnClickListener {
            // Yeni e-posta adresini alıyoruz.
            val yeniEposta = binding.guncelleeposta.text.toString().trim()

            // Kullanıcının e-posta adresini güncelledik.
            currentUser!!.verifyBeforeUpdateEmail(yeniEposta)  // // Yeni method gelmiş, email değişikliği öncesi doğrulama gidiyor.
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "E-posta güncelleme talebiniz alındı, doğrulama postasını onaylayın.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Güncelleme başarısız.", Toast.LENGTH_LONG).show()
                    }
                }

            // Şifreyi güncelleyin
            val yeniSifre = binding.guncellesifre.text.toString().trim()
            currentUser?.updatePassword(yeniSifre)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Şifreniz güncellendi.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Şifre güncelleme başarısız.", Toast.LENGTH_LONG).show()
                    }
                }

            // Ad ve soyadı güncelleyin
            val yeniAdSoyad = binding.guncelleisim.text.toString().trim()
            val currentUserDb = currentUser?.uid?.let { uid -> databaseReference?.child(uid) }
            currentUserDb?.child("adisoyadi")?.setValue(yeniAdSoyad)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "İsim güncellendi.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "İsim güncelleme başarısız.", Toast.LENGTH_LONG).show()
                    }
                }
        }
        binding.guncellelogin.setOnClickListener {
            intent = Intent(applicationContext,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}