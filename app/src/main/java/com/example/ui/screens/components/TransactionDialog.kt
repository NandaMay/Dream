package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.TransactionEntity
import com.example.model.TransactionCategory
import com.example.model.TransactionScope
import com.example.model.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyanmarRedAccent

@Composable
fun TransactionDialog(
  initialTransaction: TransactionEntity? = null,
  initialType: TransactionType = TransactionType.EXPENSE,
  onDismiss: () -> Unit,
  onSave: (
    id: String?,
    type: TransactionType,
    amount: Double,
    category: String,
    description: String,
    dateMillis: Long,
    payer: String,
    scope: TransactionScope
  ) -> Unit,
  onDelete: ((String) -> Unit)? = null
) {
  var selectedType by remember {
    mutableStateOf(
      if (initialTransaction != null) {
        if (initialTransaction.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
      } else initialType
    )
  }

  var amountText by remember {
    mutableStateOf(initialTransaction?.amount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "")
  }

  var selectedCategory by remember {
    mutableStateOf(
      initialTransaction?.let { TransactionCategory.fromName(it.category) }
        ?: if (selectedType == TransactionType.INCOME) TransactionCategory.SALARY else TransactionCategory.FOOD
    )
  }

  var description by remember { mutableStateOf(initialTransaction?.description ?: "") }
  var payer by remember { mutableStateOf(initialTransaction?.payerOrRecipient ?: "Me") }
  var selectedScope by remember {
    mutableStateOf(
      if (initialTransaction?.scope == "SHARED") TransactionScope.SHARED else TransactionScope.PERSONAL
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
        .testTag("dialog_transaction")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Title row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (initialTransaction == null) "Add Transaction" else "Edit Transaction",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Type Selector (Income vs Expense)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { selectedType = TransactionType.EXPENSE }
              .testTag("chip_type_expense"),
            color = if (selectedType == TransactionType.EXPENSE) ExpenseRed else ExpenseRed.copy(alpha = 0.1f)
          ) {
            Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
              Text(
                text = "Expense (-)",
                fontWeight = FontWeight.Bold,
                color = if (selectedType == TransactionType.EXPENSE) Color.White else ExpenseRed
              )
            }
          }

          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { selectedType = TransactionType.INCOME }
              .testTag("chip_type_income"),
            color = if (selectedType == TransactionType.INCOME) IncomeGreen else IncomeGreen.copy(alpha = 0.1f)
          ) {
            Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
              Text(
                text = "Income (+)",
                fontWeight = FontWeight.Bold,
                color = if (selectedType == TransactionType.INCOME) Color.White else IncomeGreen
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Amount input
        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorMessage = null
          },
          label = { Text("Amount (฿ Thai Baht)") },
          placeholder = { Text("0.00") },
          leadingIcon = {
            Text(
              text = "฿",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorMessage != null,
          supportingText = errorMessage?.let { { Text(it, color = ExpenseRed) } },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_transaction_amount")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Picker
        Text(
          text = "Category",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(TransactionCategory.entries.toTypedArray()) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat.displayName) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.primary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description (အကြောင်းအရာ)") },
          placeholder = { Text("e.g. Dinner date, Grocery, Salary") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_transaction_desc")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Payer
        OutlinedTextField(
          value = payer,
          onValueChange = { payer = it },
          label = { Text("Payer / Recipient") },
          placeholder = { Text("e.g. Min Min, Hnin Hnin, Both") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Scope (Personal vs Shared)
        Text(
          text = "Scope (အမျိုးအစား)",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = selectedScope == TransactionScope.PERSONAL,
            onClick = { selectedScope = TransactionScope.PERSONAL },
            label = { Text("Personal (ကိုယ်ပိုင်)") }
          )
          FilterChip(
            selected = selectedScope == TransactionScope.SHARED,
            onClick = { selectedScope = TransactionScope.SHARED },
            label = { Text("Shared Couple (နှစ်ယောက်အတူ)") }
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (initialTransaction != null && onDelete != null) {
            IconButton(
              onClick = { showDeleteConfirmation = true },
              modifier = Modifier.testTag("button_delete_transaction")
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
              val amt = amountText.toDoubleOrNull()
              if (amt == null || amt <= 0.0) {
                errorMessage = "Please enter a valid amount greater than 0"
                return@Button
              }
              onSave(
                initialTransaction?.id,
                selectedType,
                amt,
                selectedCategory.name,
                description,
                initialTransaction?.date ?: System.currentTimeMillis(),
                payer,
                selectedScope
              )
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (selectedType == TransactionType.INCOME) IncomeGreen else MyanmarRedAccent
            ),
            modifier = Modifier.testTag("button_save_transaction")
          ) {
            Text("Save (သိမ်းမည်)", color = Color.White)
          }
        }
      }
    }
  }

  if (showDeleteConfirmation && initialTransaction != null && onDelete != null) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmation = false },
      title = { Text("Delete Transaction?") },
      text = { Text("Are you sure you want to permanently delete this transaction record?") },
      confirmButton = {
        Button(
          onClick = {
            onDelete(initialTransaction.id)
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
