package com.example.evolutionhub

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.evolutionhub.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth




class LoginActivity : AppCompatActivity() {
    // Klasikleşmiş firebase bağlantı komutlarımız.
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val layoutLogin: ConstraintLayout = findViewById(R.id.layout_login)
        val animationDrawable: AnimationDrawable = layoutLogin.background as AnimationDrawable
        // Her ekranda yapılan animasyon tanımı 4 ve 2 saniye.
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        // Giriş yap butonuna tıklanırsa giriş ekranına yönlendirir.
        binding.girislogin.setOnClickListener {
            val giriseposta = binding.giriseposta.text.toString()
            val girissifre = binding.girissifre.text.toString()

            if (TextUtils.isEmpty(giriseposta)) {
                binding.giriseposta.error = "Lütfen e-postanızı yazınız."
                return@setOnClickListener
            }
            else if (TextUtils.isEmpty(girissifre)) {
                binding.girissifre.error = "Lütfen şifrenizi yazınız."
                return@setOnClickListener
            }
            // Bilgileri auth ile doğrulayıp girişi sağlar.
            auth.signInWithEmailAndPassword(giriseposta,girissifre)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        intent = Intent(applicationContext,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(applicationContext,"Giriş hatalı, lütfen tekrar deneyiniz."
                        ,Toast.LENGTH_LONG).show()
                    }
                }


        }

        // yeni üyelik sayfasına gitmek için:

        binding.girissignup.setOnClickListener {
            intent = Intent(applicationContext,SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
        // şifre sıfırlama sayfasına gitmek için:

        binding.girisparolamiunuttum.setOnClickListener {
            intent = Intent(applicationContext, FPwActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}