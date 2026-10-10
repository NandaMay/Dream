package com.example.ui.screens.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.WarmGold

@Composable
fun CoupleCodeDialog(
  currentCode: String?,
  isConnected: Boolean,
  partnerName: String?,
  onGenerateCode: () -> Unit,
  onConnectCode: (String, (Boolean) -> Unit) -> Unit,
  onDisconnect: () -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var inputCode by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf<String?>(null) }
  var isError by remember { mutableStateOf(false) }
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("dialog_couple_code")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Link, contentDescription = null, tint = MyanmarRedAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Couple Connection",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isConnected) {
          // Connected state
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MyanmarRedAccent.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = MyanmarRedAccent,
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Connected with ${partnerName ?: "Partner"} 💕",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MyanmarRedAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Couple Code: ${currentCode ?: "DREAM-CONNECTED"}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "You are sharing financial records, savings goals, and memories together.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
              onClick = {
                onDisconnect()
                onDismiss()
                Toast.makeText(context, "Couple connection disconnected", Toast.LENGTH_SHORT).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed.copy(alpha = 0.15f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Disconnect Couple (သီးသန့်ခွဲမည်)", color = ExpenseRed)
            }
          }
        } else {
          // Tab Row (Share Code / Enter Code)
          TabRow(selectedTabIndex = selectedTab) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              text = { Text("My Couple Code") }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              text = { Text("Enter Code") }
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (selectedTab == 0) {
            // My Code Tab
            Text(
              text = "Share this invitation code with your partner to link your Our Dream accounts:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, WarmGold, RoundedCornerShape(14.dp))
                .background(WarmGold.copy(alpha = 0.08f))
                .padding(16.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = currentCode ?: "DREAM-2025",
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 2.sp
                ),
                color = MyanmarRedAccent
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = onGenerateCode,
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Code")
              }

              Button(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Couple Code", currentCode ?: "DREAM-2025")
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MyanmarRedAccent),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Code")
              }
            }
          } else {
            // Enter Code Tab
            Text(
              text = "Enter your partner's Couple Code below to connect your accounts together:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = inputCode,
              onValueChange = {
                inputCode = it.uppercase()
                statusMessage = null
              },
              label = { Text("Partner's Couple Code") },
              placeholder = { Text("e.g. DREAM-4921") },
              singleLine = true,
              isError = isError,
              modifier = Modifier.fillMaxWidth().testTag("input_couple_code")
            )

            if (statusMessage != null) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = statusMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) ExpenseRed else MyanmarRedAccent
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                if (inputCode.isBlank()) {
                  statusMessage = "Please enter a valid code"
                  isError = true
                  return@Button
                }
                onConnectCode(inputCode) { success ->
                  if (success) {
                    statusMessage = "Successfully connected with your partner! 💕"
                    isError = false
                    Toast.makeText(context, "Connected successfully!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                  } else {
                    statusMessage = "Invalid or expired couple code. Please recheck."
                    isError = true
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = MyanmarRedAccent),
              modifier = Modifier.fillMaxWidth().testTag("button_submit_couple_code")
            ) {
              Text("Connect With Partner (ချိတ်ဆက်မည်)", color = Color.White)
            }
          }
        }
      }
    }
  }
}
