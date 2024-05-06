package com.example.evolutionhub

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StepCounterActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var stepCount: Int = 0
    private lateinit var showStepCountTextView: TextView  // Adım sayısı text view'e yansıtıldı.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sensör tanımları
        setContentView(R.layout.activity_step_counter)
        showStepCountTextView = findViewById(R.id.showStepCountTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
            if (stepDetectorSensor != null) {
                sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL)
            } else {
                // Kullanılacak cihazda iki sensör de olmalı, yoksa adım sayısını izleyemezsiniz.
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Adım sensörü olayları dinleniyor
        if (event.sensor == stepCounterSensor || event.sensor == stepDetectorSensor) {
            stepCount++
            updateStepCount(stepCount) // Atılan adım sayısı güncellendi.
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sensör hassasiyeti değişirse burada işlem yapılacak.
    }

    private fun updateStepCount(count: Int) {
        showStepCountTextView.text = "Toplam Adım Sayısı: $count"

        // Adım sayısını PersonalProgressActivity'ye iletmek için Intent kullanıyoruz.
        val intent = Intent(this, PersonalProgressActivity::class.java)
        intent.putExtra("stepCount", count)
        startActivity(intent)
    }

}
