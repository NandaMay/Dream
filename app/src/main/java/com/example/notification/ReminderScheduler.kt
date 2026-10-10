package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.entity.MemoryEventEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ReminderScheduler {

  private val intervals = listOf(7, 3, 1, 0) // days before

  fun scheduleEventReminders(
    context: Context,
    event: MemoryEventEntity,
    remind7Days: Boolean = true,
    remind3Days: Boolean = true,
    remind1Day: Boolean = true,
    remindSameDay: Boolean = true
  ) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val parsedDate = try {
      LocalDate.parse(event.eventDate, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: Exception) {
      return
    }

    val now = LocalDateTime.now()
    val targetYear = if (event.isRecurringYearly) {
      val thisYearDate = LocalDate.of(now.year, parsedDate.monthValue, parsedDate.dayOfMonth)
      if (thisYearDate.isBefore(now.toLocalDate())) now.year + 1 else now.year
    } else {
      parsedDate.year
    }

    val targetEventDate = LocalDate.of(targetYear, parsedDate.monthValue, parsedDate.dayOfMonth)

    for (daysBefore in intervals) {
      val isEnabled = when (daysBefore) {
        7 -> remind7Days
        3 -> remind3Days
        1 -> remind1Day
        0 -> remindSameDay
        else -> true
      }
      if (!isEnabled) continue

      val reminderDate = targetEventDate.minusDays(daysBefore.toLong())
      val reminderDateTime = LocalDateTime.of(reminderDate, LocalTime.of(9, 0, 0))

      if (reminderDateTime.isAfter(now)) {
        val triggerMillis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
          putExtra(ReminderAlarmReceiver.EXTRA_TITLE, event.title)
          putExtra(ReminderAlarmReceiver.EXTRA_DAYS_REMAINING, daysBefore)
          putExtra(ReminderAlarmReceiver.EXTRA_EVENT_DATE, event.eventDate)
          putExtra(ReminderAlarmReceiver.EXTRA_EVENT_ID, event.id)
        }

        val requestCode = (event.id.hashCode() * 31 + daysBefore)
        val pendingIntent = PendingIntent.getBroadcast(
          context,
          requestCode,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
          } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
          }
        } catch (_: SecurityException) {
          // If exact alarm permission is restricted, fallback to standard set
          alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
      }
    }
  }

  fun triggerTestNotification(context: Context) {
    ReminderAlarmReceiver.createNotificationChannel(context)
    val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
      putExtra(ReminderAlarmReceiver.EXTRA_TITLE, "Our Dream 1st Anniversary")
      putExtra(ReminderAlarmReceiver.EXTRA_DAYS_REMAINING, 0)
      putExtra(ReminderAlarmReceiver.EXTRA_EVENT_DATE, "2026-08-15")
      putExtra(ReminderAlarmReceiver.EXTRA_EVENT_ID, "test-milestone")
    }
    context.sendBroadcast(intent)
  }
}
