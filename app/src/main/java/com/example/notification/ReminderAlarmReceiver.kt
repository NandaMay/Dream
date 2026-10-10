package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class ReminderAlarmReceiver : BroadcastReceiver() {

  companion object {
    const val CHANNEL_ID = "our_dream_anniversary_channel"
    const val EXTRA_TITLE = "extra_event_title"
    const val EXTRA_DAYS_REMAINING = "extra_days_remaining"
    const val EXTRA_EVENT_DATE = "extra_event_date"
    const val EXTRA_EVENT_ID = "extra_event_id"

    fun createNotificationChannel(context: Context) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          "Our Dream Reminders",
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = "Milestone and anniversary countdown notifications for couples"
          enableVibration(true)
          vibrationPattern = longArrayOf(0, 250, 150, 250)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
      }
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    createNotificationChannel(context)

    val title = intent.getStringExtra(EXTRA_TITLE) ?: "Relationship Milestone"
    val daysRemaining = intent.getIntExtra(EXTRA_DAYS_REMAINING, 0)
    val eventDate = intent.getStringExtra(EXTRA_EVENT_DATE) ?: ""
    val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: ""

    val notificationText = when (daysRemaining) {
      0 -> "Today is $title! Happy celebration together! 💕"
      1 -> "Tomorrow is $title! Get ready for something special! ✨"
      3 -> "Only 3 days until $title! Don't forget your surprise! 🎁"
      7 -> "1 week left until $title! Time to plan! 🌸"
      else -> "$daysRemaining days left until $title! ($eventDate)"
    }

    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      eventId.hashCode(),
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("Our Dream (တို့နှစ်ယောက်ရဲ့ အိမ်မက်)")
      .setContentText(notificationText)
      .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notifId = (title + daysRemaining).hashCode()
    notificationManager.notify(notifId, notification)
  }
}
