package com.example.evolutionhub

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Gelen bildirimleri işliyoruz.
        Log.d(TAG, "Mesaj alındı: ${remoteMessage.data}")
        showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
    }
    private fun showNotification(title: String?, body: String?) {
        // Bildirim gösterme işlemleri.
        val channelId = "my_channel"
        createNotificationChannel(channelId)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(this)) {
            val notificationManagerCompat = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManagerCompat.areNotificationsEnabled()) {
                // Bildirim için izni manifeste ekledik, izinler etkinse işlemleri gerçekleştir.
                notificationManagerCompat.notify(1, builder.build())
            } else {
                // Kullanıcı bildirimleri devre dışı bırakmışsa veya izin vermemişse
                // Kullanıcıyı bilgilendirme vb. işlemleri gerçekleştirilir.
                Log.d(TAG, "Kullanıcı bildirimleri devre dışı bırakmış veya izin vermemiş.")
                val message = "Bildirim izinleriniz kapalı veya bildirimler devre dışı, haberdar kalmak için bildirimleri açın veya izin verin."
            }

        }
    }
    // Cloud Messaging işlemleri.
    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "My Notification Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onNewToken(token: String) {
        // Yeni bir FCM token'ı oluşturulduğunda buraya düşer
        Log.d(TAG, "Yeni token: $token")
    }
    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }
}
