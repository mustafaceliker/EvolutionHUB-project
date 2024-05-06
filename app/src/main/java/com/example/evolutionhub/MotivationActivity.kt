package com.example.evolutionhub

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.evolutionhub.databinding.ActivityMotivationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MotivationActivity : BaseActivity() {

    private lateinit var binding: ActivityMotivationBinding
    private lateinit var notificationSwitch: Switch
    private lateinit var frequencySeekBar: SeekBar
    private lateinit var frequencyLabel: TextView
    private lateinit var viewHistoryButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMotivationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationSwitch = binding.notificationSwitch
        frequencySeekBar = binding.notificationFrequencySeekBar
        frequencyLabel = binding.frequencyLabel
        viewHistoryButton = binding.viewHistoryButton

        val motivationLayout: RelativeLayout = findViewById(R.id.layout_motivation)
        val animationDrawable: AnimationDrawable = motivationLayout.background as AnimationDrawable

        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigationView(bottomNavigationView)

        // Firebase Realtime Database referansı
        database = FirebaseDatabase.getInstance().reference

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference(isChecked)
            if (isChecked) {
                startNotificationService()
            } else {
                stopNotificationService()
            }
        }

        // Buton referansını al
        val viewHistoryButton: Button = findViewById(R.id.viewHistoryButton)

        // Bildirim sıklığı ayarlama
        frequencySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                saveNotificationFrequency(progress)
                frequencyLabel.text = "Bildirim Sıklığı: $progress"
                // Gerekli işlemleri buraya ekle
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Bildirim geçmişi görüntüleme
        viewHistoryButton.setOnClickListener {
            showNotificationHistory()
        }

        // Önceki tercihlere göre arayüzü güncelle
        loadNotificationPreference()
        loadNotificationFrequency()
    }

    // Bildirim tercihini sakla
    private fun saveNotificationPreference(isEnabled: Boolean) {
        database.child("notificationPreference").setValue(isEnabled)
    }

    // Bildirim sıklığını sakla
    private fun saveNotificationFrequency(frequency: Int) {
        database.child("notificationFrequency").setValue(frequency)
    }

    // Bildirim tercihini yükle
    private fun loadNotificationPreference() {
        database.child("notificationPreference").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isEnabled = snapshot.getValue(Boolean::class.java) ?: true
                notificationSwitch.isChecked = isEnabled
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Bildirim sıklığını yükle
    private fun loadNotificationFrequency() {
        database.child("notificationFrequency").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val frequency = snapshot.getValue(Int::class.java) ?: 5
                frequencySeekBar.progress = frequency
                frequencyLabel.text = "Bildirim Sıklığı: $frequency"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun startNotificationService() {
        val intent = Intent(this, MyFirebaseMessagingService::class.java)
        startService(intent)
    }

    // Bildirim servisini durdur
    fun stopNotificationService() {
        val intent = Intent(this, MyFirebaseMessagingService::class.java)
        stopService(intent)
    }
    private fun showNotificationList(notificationHistory: List<String>) {
        val listView: ListView = ListView(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notificationHistory)
        listView.adapter = adapter

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Bildirim Geçmişi")
        alertDialogBuilder.setView(listView)

        val alertDialog = alertDialogBuilder.create()

        // Liste öğelerine tıklama olayını dinle
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedNotification = notificationHistory[position]
            showToast("Seçilen Bildirim: $selectedNotification")
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    // Bilgi mesajı gösterme fonksiyonu
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    // Bildirim geçmişini görüntüle
    fun showNotificationHistory() {
        val notificationHistory: MutableList<String> = mutableListOf()

        // Firebase Realtime Database üzerinden bildirim geçmişini al
        database.child("notificationHistory").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val notification = data.getValue(String::class.java)
                    notification?.let {
                        notificationHistory.add(it)
                    }
                }

                if (notificationHistory.isNotEmpty()) {
                    // Bildirim geçmişini bir liste olarak göster
                    showNotificationList(notificationHistory)
                } else {
                    showToast("Bildirim geçmişi boş.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Bildirim geçmişi alınamadı.")
            }
        })
    }

    // Firebase Cloud Messaging Service
    class MyFirebaseMessagingService : FirebaseMessagingService() {

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            // Gelen bildirimi işle
            val notification = remoteMessage.notification
            if (notification != null) {
                Log.d("Notification", "Received: ${notification.body}")
            }
        }

        override fun onNewToken(token: String) {
            Log.d("FCM Token", "Refreshed token: $token")
            // Sunucunuza token'ı gönderme işlemlerini buraya ekleyebilirsiniz
        }
    }
}
