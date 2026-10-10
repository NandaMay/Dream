package com.example.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.SavingsGoalEntity
import com.example.model.TransactionScope
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.WarmGold

@Composable
fun SavingsGoalDialog(
  initialGoal: SavingsGoalEntity? = null,
  onDismiss: () -> Unit,
  onSave: (
    id: String?,
    title: String,
    targetAmount: Double,
    currentAmount: Double,
    targetDate: String?,
    scope: TransactionScope
  ) -> Unit,
  onDelete: ((String) -> Unit)? = null
) {
  var title by remember { mutableStateOf(initialGoal?.title ?: "") }
  var targetAmountText by remember {
    mutableStateOf(initialGoal?.targetAmount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "")
  }
  var currentAmountText by remember {
    mutableStateOf(initialGoal?.currentAmount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "0")
  }
  var targetDate by remember { mutableStateOf(initialGoal?.targetDate ?: "") }
  var scope by remember {
    mutableStateOf(
      if (initialGoal?.scope == "PERSONAL") TransactionScope.PERSONAL else TransactionScope.SHARED
    )
  }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("dialog_savings_goal")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (initialGoal == null) "New Savings Goal" else "Edit Savings Goal",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Goal Title (စုငွေရည်မှန်းချက်)") },
          placeholder = { Text("e.g. Wedding, Dream House, Vacation") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_goal_title")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = targetAmountText,
          onValueChange = {
            targetAmountText = it
            errorMessage = null
          },
          label = { Text("Target Amount (฿)") },
          placeholder = { Text("50000") },
          leadingIcon = {
            Text(
              text = "฿",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = WarmGold
            )
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorMessage != null,
          supportingText = errorMessage?.let { { Text(it, color = ExpenseRed) } },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_goal_target")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = currentAmountText,
          onValueChange = { currentAmountText = it },
          label = { Text("Current Saved Amount (฿)") },
          placeholder = { Text("0") },
          leadingIcon = {
            Text(
              text = "฿",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = WarmGold
            )
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = targetDate,
          onValueChange = { targetDate = it },
          label = { Text("Target Date (Optional: YYYY-MM-DD)") },
          placeholder = { Text("2027-12-31") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Scope (ပိုင်ဆိုင်မှု)",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = scope == TransactionScope.SHARED,
            onClick = { scope = TransactionScope.SHARED },
            label = { Text("Shared Couple (အတူစုငွေ)") }
          )
          FilterChip(
            selected = scope == TransactionScope.PERSONAL,
            onClick = { scope = TransactionScope.PERSONAL },
            label = { Text("Personal (ကိုယ်ပိုင်စုငွေ)") }
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (initialGoal != null && onDelete != null) {
            IconButton(
              onClick = { showDeleteConfirmation = true },
              modifier = Modifier.testTag("button_delete_goal")
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
            }
            Spacer(modifier = Modifier.weight(1f))
          }

          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              if (title.isBlank()) {
                errorMessage = "Please enter a title for this goal"
                return@Button
              }
              val targetAmt = targetAmountText.toDoubleOrNull()
              if (targetAmt == null || targetAmt <= 0.0) {
                errorMessage = "Please enter a valid target amount greater than 0"
                return@Button
              }
              val currentAmt = currentAmountText.toDoubleOrNull() ?: 0.0
              onSave(
                initialGoal?.id,
                title,
                targetAmt,
                currentAmt,
                targetDate.ifBlank { null },
                scope
              )
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = WarmGold),
            modifier = Modifier.testTag("button_save_goal")
          ) {
            Text("Save Goal", color = Color.Black)
          }
        }
      }
    }
  }

  if (showDeleteConfirmation && initialGoal != null && onDelete != null) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmation = false },
      title = { Text("Delete Savings Goal?") },
      text = { Text("Are you sure you want to delete '${initialGoal.title}'?") },
      confirmButton = {
        Button(
          onClick = {
            onDelete(initialGoal.id)
            showDeleteConfirmation = false
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
        ) {
          Text("Delete", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmation = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun AddContributionDialog(
  goalTitle: String,
  onDismiss: () -> Unit,
  onConfirm: (amount: Double, note: String) -> Unit
) {
  var amountText by remember { mutableStateOf("") }
  var note by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Add Savings Contribution",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Goal: $goalTitle",
          style = MaterialTheme.typography.bodyMedium,
          color = WarmGold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorMessage = null
          },
          label = { Text("Deposit Amount (฿)") },
          placeholder = { Text("1000") },
          leadingIcon = {
            Text(
              text = "฿",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = WarmGold
            )
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorMessage != null,
          supportingText = errorMessage?.let { { Text(it, color = ExpenseRed) } },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_deposit_amount")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("Note (Optional)") },
          placeholder = { Text("e.g. End of month savings") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val amt = amountText.toDoubleOrNull()
              if (amt == null || amt <= 0.0) {
                errorMessage = "Please enter an amount greater than 0"
                return@Button
              }
              onConfirm(amt, note)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = WarmGold),
            modifier = Modifier.testTag("button_confirm_deposit")
          ) {
            Text("Deposit ฿", color = Color.Black)
          }
        }
      }
    }
  }
}
