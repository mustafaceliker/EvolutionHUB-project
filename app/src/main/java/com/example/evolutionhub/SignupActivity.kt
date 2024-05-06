package com.example.evolutionhub

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.evolutionhub.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        val signupLayout: ConstraintLayout = findViewById(R.id.layout_signup)
        val animationDrawable: AnimationDrawable = signupLayout.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        val checkBoxAcceptTerms: CheckBox = findViewById(R.id.checkBoxAcceptTerms)

        binding.uyeolkaydet.setOnClickListener {
            if (!checkBoxAcceptTerms.isChecked) {
                showTermsNotAcceptedMessage()
                return@setOnClickListener
            }

            var uyeolisim = binding.uyeolisim.text.toString()
            var uyeolposta = binding.uyeoleposta.text.toString()
            var uyeolsifre = binding.uyeolsifre.text.toString()

            if (TextUtils.isEmpty(uyeolisim)) {
                binding.uyeolisim.error = "Ad ve soyad giriniz."
                return@setOnClickListener
            } else if (TextUtils.isEmpty(uyeolposta)) {
                binding.uyeoleposta.error = "E-posta adresinizi giriniz."
                return@setOnClickListener
            } else if (TextUtils.isEmpty(uyeolsifre)) {
                binding.uyeolsifre.error = "Şifrenizi giriniz."
                return@setOnClickListener
            }

            // Email ekleme (auth'a)
            auth.createUserWithEmailAndPassword(uyeolposta, uyeolsifre)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Kullanıcı ad soyadı alır.
                        var currentUser = auth.currentUser
                        // Kullanıcı id'sini alıp id altında ad soyadı kaydeder.
                        // Id ile kaydederek kullanıcıya benzersiz bir tanım oluşturduk bu sayede realtime database'e de isim soyismi aktardık.
                        var currentUserDb = currentUser?.let { it1 -> databaseReference?.child(it1.uid) }
                        currentUserDb?.child("adisoyadi")?.setValue(uyeolisim)
                        Toast.makeText(this@SignupActivity, "Kayıt Başarılı.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@SignupActivity, "Kayıt Hatalı.", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.uyeollogin.setOnClickListener {
            intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showTermsNotAcceptedMessage() {
        // Kullanıcıya koşulları kabul etmesi gerektiğini belirten bir hata mesajı göster
        Toast.makeText(this, "Kullanım ve gizlilik koşullarını kabul etmelisiniz.", Toast.LENGTH_SHORT).show()
    }
}
