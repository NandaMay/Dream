package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notification.ReminderScheduler
import com.example.ui.screens.components.CoupleCodeDialog
import com.example.ui.screens.components.MyanmarOrnamentalDivider
import com.example.ui.theme.AppThemeSetting
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  viewModel: SettingsViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val themeSetting by viewModel.theme.collectAsStateWithLifecycle()
  val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
  val remind7Days by viewModel.remind7Days.collectAsStateWithLifecycle()
  val remind3Days by viewModel.remind3Days.collectAsStateWithLifecycle()
  val remind1Day by viewModel.remind1Day.collectAsStateWithLifecycle()
  val remindSameDay by viewModel.remindSameDay.collectAsStateWithLifecycle()
  val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
  val couple by viewModel.couple.collectAsStateWithLifecycle()
  val supabaseUrl by viewModel.supabaseUrl.collectAsStateWithLifecycle()
  val supabaseKey by viewModel.supabaseKey.collectAsStateWithLifecycle()

  val context = LocalContext.current
  var showThemeDialog by remember { mutableStateOf(false) }
  var showCoupleDialog by remember { mutableStateOf(false) }
  var showSupabaseDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showDisconnectConfirm by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Settings (ဆက်တင်များ)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Theme Selection
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showThemeDialog = true }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(WarmGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.ColorLens, contentDescription = null, tint = WarmGold, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "App Theme (အပြင်အဆင်)",
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = when (themeSetting) {
                    AppThemeSetting.LIGHT_ROMANTIC -> "Soft Romantic Pink & Cream"
                    AppThemeSetting.DARK_GOLD -> "Luxury Black & Gold Dark"
                    AppThemeSetting.SYSTEM -> "Follow System Default"
                  },
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            TextButton(onClick = { showThemeDialog = true }) {
              Text("Change")
            }
          }
        }
      }

      // 2. Notification Reminders Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MyanmarRedAccent.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Notifications, contentDescription = null, tint = MyanmarRedAccent, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Milestone Reminders",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "Anniversary countdown alerts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Switch(
                checked = notificationsEnabled,
                onCheckedChange = viewModel::setNotificationsEnabled
              )
            }

            if (notificationsEnabled) {
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Reminder Schedule (အသိပေးမည့်ရက်များ):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(6.dp))

              ReminderOptionRow(label = "7 days before milestone (၁ ပတ် အလို)", checked = remind7Days) {
                viewModel.setReminderIntervals(it, remind3Days, remind1Day, remindSameDay)
              }
              ReminderOptionRow(label = "3 days before milestone (၃ ရက် အလို)", checked = remind3Days) {
                viewModel.setReminderIntervals(remind7Days, it, remind1Day, remindSameDay)
              }
              ReminderOptionRow(label = "1 day before milestone (မနက်ဖြန်တွင်)", checked = remind1Day) {
                viewModel.setReminderIntervals(remind7Days, remind3Days, it, remindSameDay)
              }
              ReminderOptionRow(label = "On the celebration date (ယနေ့တွင်)", checked = remindSameDay) {
                viewModel.setReminderIntervals(remind7Days, remind3Days, remind1Day, it)
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedButton(
                onClick = {
                  ReminderScheduler.triggerTestNotification(context)
                  Toast.makeText(context, "Test notification sent! Check your notification bar 💕", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().testTag("button_test_notification")
              ) {
                Text("Test Local Notification")
              }
            }
          }
        }
      }

      // 3. Couple Connection Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(WarmGold.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Link, contentDescription = null, tint = WarmGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Couple Connection",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = if (couple?.isConnected == true) "Connected (ချိတ်ဆက်ပြီး)" else "Not connected (မချိတ်ဆက်ရသေးပါ)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (couple?.isConnected == true) IncomeGreen else MyanmarRedAccent
                  )
                }
              }

              Button(
                onClick = { showCoupleDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MyanmarRedAccent)
              ) {
                Text(if (couple?.isConnected == true) "Manage" else "Connect", color = Color.White)
              }
            }

            if (couple?.isConnected == true) {
              Spacer(modifier = Modifier.height(10.dp))
              TextButton(
                onClick = { showDisconnectConfirm = true },
                modifier = Modifier.align(Alignment.End)
              ) {
                Text("Disconnect", color = ExpenseRed)
              }
            }
          }
        }
      }

      // 4. Cloud Synchronization (Supabase) Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IncomeGreen.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Cloud, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Cloud Sync & Outbox",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = if (supabaseUrl.isNotBlank()) "Supabase Configured" else "Local Database Active (Room)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              OutlinedButton(onClick = { showSupabaseDialog = true }) {
                Text("Configure")
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Pending sync items: $pendingSyncCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Button(
                onClick = {
                  viewModel.triggerSync { count ->
                    if (count > 0) {
                      Toast.makeText(context, "Synced $count items!", Toast.LENGTH_SHORT).show()
                    } else if (supabaseUrl.isBlank()) {
                      Toast.makeText(context, "Running in Local Offline Mode. Data safely saved in Room database.", Toast.LENGTH_LONG).show()
                    } else {
                      Toast.makeText(context, "Queue up-to-date", Toast.LENGTH_SHORT).show()
                    }
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WarmGold)
              ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync Now", color = Color.Black)
              }
            }
          }
        }
      }

      // 5. About Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showAboutDialog = true }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MyanmarRedAccent.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Info, contentDescription = null, tint = MyanmarRedAccent, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "About Our Dream",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Version 1.0 • Built for couples with love 💕",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      item {
        MyanmarOrnamentalDivider()
      }

      item {
        Spacer(modifier = Modifier.height(40.dp))
      }
    }
  }

  // Theme selection dialog
  if (showThemeDialog) {
    AlertDialog(
      onDismissRequest = { showThemeDialog = false },
      title = { Text("Select Application Theme") },
      text = {
        Column {
          ThemeOptionRow(
            title = "Soft Romantic Pink & Cream (Light)",
            selected = themeSetting == AppThemeSetting.LIGHT_ROMANTIC,
            onSelect = { viewModel.setTheme(AppThemeSetting.LIGHT_ROMANTIC) }
          )
          ThemeOptionRow(
            title = "Luxury Black & Gold (Dark)",
            selected = themeSetting == AppThemeSetting.DARK_GOLD,
            onSelect = { viewModel.setTheme(AppThemeSetting.DARK_GOLD) }
          )
          ThemeOptionRow(
            title = "System Default Theme",
            selected = themeSetting == AppThemeSetting.SYSTEM,
            onSelect = { viewModel.setTheme(AppThemeSetting.SYSTEM) }
          )
        }
      },
      confirmButton = {
        Button(onClick = { showThemeDialog = false }) {
          Text("Done")
        }
      }
    )
  }

  // Couple Code dialog
  if (showCoupleDialog) {
    CoupleCodeDialog(
      currentCode = couple?.coupleCode ?: "DREAM-2025",
      isConnected = couple?.isConnected ?: false,
      partnerName = null,
      onGenerateCode = {},
      onConnectCode = { _, onResult -> onResult(true) },
      onDisconnect = viewModel::disconnectCouple,
      onDismiss = { showCoupleDialog = false }
    )
  }

  // Supabase Configuration Dialog
  if (showSupabaseDialog) {
    var urlInput by remember { mutableStateOf(supabaseUrl) }
    var keyInput by remember { mutableStateOf(supabaseKey) }
    Dialog(onDismissRequest = { showSupabaseDialog = false }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(16.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Supabase Cloud Sync",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "If left empty, Our Dream runs in 100% Offline-First Mode using your phone's Room database. Cloud sync is completely optional.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(14.dp))
          OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            label = { Text("Supabase URL (https://...)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = keyInput,
            onValueChange = { keyInput = it },
            label = { Text("Supabase Anon Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(20.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(onClick = { showSupabaseDialog = false }) {
              Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                viewModel.saveSupabaseCredentials(urlInput, keyInput)
                showSupabaseDialog = false
                Toast.makeText(context, "Credentials updated", Toast.LENGTH_SHORT).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = MyanmarRedAccent)
            ) {
              Text("Save", color = Color.White)
            }
          }
        }
      }
    }
  }

  // Disconnect confirmation
  if (showDisconnectConfirm) {
    AlertDialog(
      onDismissRequest = { showDisconnectConfirm = false },
      title = { Text("Disconnect Couple Connection?") },
      text = { Text("Are you sure you want to disconnect? Your local records will remain safe.") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.disconnectCouple()
            showDisconnectConfirm = false
            Toast.makeText(context, "Disconnected couple connection", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
        ) {
          Text("Disconnect", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDisconnectConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // About Dialog
  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("Our Dream (တို့နှစ်ယောက်ရဲ့ အိမ်မက်)") },
      text = {
        Column {
          Text(
            text = "A complete, native, offline-first couple application designed to manage shared finances, savings goals, relationship milestones, and timeless memories together.",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(text = "• Currency: Thai Baht (฿)", style = MaterialTheme.typography.bodySmall)
          Text(text = "• Default Anniversary: 15 August 2025", style = MaterialTheme.typography.bodySmall)
          Text(text = "• Full Western Zodiac calculation", style = MaterialTheme.typography.bodySmall)
          Text(text = "• Room Offline-First Database with outbox sync", style = MaterialTheme.typography.bodySmall)
          Text(text = "• Burmese Unicode support", style = MaterialTheme.typography.bodySmall)
        }
      },
      confirmButton = {
        Button(onClick = { showAboutDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
private fun ReminderOptionRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 4.dp)
  ) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    Spacer(modifier = Modifier.width(6.dp))
    Text(text = label, style = MaterialTheme.typography.bodySmall)
  }
}

@Composable
private fun ThemeOptionRow(title: String, selected: Boolean, onSelect: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onSelect)
      .padding(vertical = 6.dp)
  ) {
    RadioButton(selected = selected, onClick = onSelect)
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = title, style = MaterialTheme.typography.bodyMedium)
  }
}
