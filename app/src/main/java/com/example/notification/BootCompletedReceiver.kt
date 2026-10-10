package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.OurDreamDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
      intent.action == "android.intent.action.QUICKBOOT_POWERON"
    ) {
      val pendingResult = goAsync()
      CoroutineScope(Dispatchers.IO).launch {
        try {
          val database = OurDreamDatabase.getDatabase(context)
          val events = database.memoryEventDao().getAllEvents().firstOrNull() ?: emptyList()
          for (event in events) {
            ReminderScheduler.scheduleEventReminders(context, event)
          }
        } finally {
          pendingResult.finish()
        }
      }
    }
  }
}
