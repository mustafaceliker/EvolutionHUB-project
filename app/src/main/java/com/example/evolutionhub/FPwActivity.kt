package com.example.evolutionhub

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.evolutionhub.databinding.ActivityFpwBinding
import com.google.firebase.auth.FirebaseAuth

class FPwActivity : AppCompatActivity() {
    //Klasik firebase bağlantı komutları
    lateinit var binding: ActivityFpwBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFpwBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val fpwLayout: ConstraintLayout = findViewById(R.id.layout_fpw)
        val animationDrawable: AnimationDrawable = fpwLayout.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        binding.fpwbutton.setOnClickListener {// Şifremi unuttum butonuna basıldığında ne yapılacağı bu blokta.
            var fpweposta = binding.fpweposta.text.toString().trim()
            if (TextUtils.isEmpty(fpweposta)) {
                binding.fpweposta.error = "Lütfen e-postanızı yazınız."
            }
            else { //e-posta yazıldığı durumda bir sorun yoksa bağlantıyı gönderecek ve ekrana mesajı çıkaracak kod.
                auth.sendPasswordResetEmail(fpweposta)
                    .addOnCompleteListener(this) { sifirlama ->
                        if (sifirlama.isSuccessful) {
                            binding.fpwmsg.text= "Sıfırlama bağlantısı başarıyla gönderildi."
                        }
                        else {
                            binding.fpwmsg.text= "Sıfırlama işlemi sırasında bir hata meydana geldi."
                        }
                    }
            }
        }
        // Giriş yap butonuna tıklanırsa.
        binding.fpwlogin.setOnClickListener {
            intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}