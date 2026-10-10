package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.TransactionEntity
import com.example.model.TransactionScope
import com.example.model.TransactionType
import com.example.ui.screens.components.CoupleCodeDialog
import com.example.ui.screens.components.MyanmarOrnamentalDivider
import com.example.ui.screens.components.MyFinanceCard
import com.example.ui.screens.components.OurLoveCard
import com.example.ui.screens.components.TransactionDialog
import com.example.ui.screens.components.TransactionItemRow
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.HomeViewModel
import com.example.util.CoupleCalculations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: HomeViewModel,
  onNavigateToFinance: () -> Unit,
  onNavigateToSavings: () -> Unit,
  onNavigateToLove: () -> Unit,
  onNavigateToSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  var showAddIncomeDialog by remember { mutableStateOf(false) }
  var showAddExpenseDialog by remember { mutableStateOf(false) }
  var selectedTransactionForEdit by remember { mutableStateOf<TransactionEntity?>(null) }
  var showCoupleConnectionDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Our Dream",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
            )
            Text(
              text = "တို့နှစ်ယောက်ရဲ့ အိမ်မက်",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MyanmarRedAccent
            )
          }
        },
        actions = {
          // Couple Status chip
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (uiState.isCoupleConnected) MyanmarRedAccent.copy(alpha = 0.12f) else WarmGold.copy(alpha = 0.15f),
            modifier = Modifier
              .clickable { showCoupleConnectionDialog = true }
              .padding(end = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = if (uiState.isCoupleConnected) MyanmarRedAccent else WarmGold,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (uiState.isCoupleConnected) "Connected 💕" else "Connect Code",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (uiState.isCoupleConnected) MyanmarRedAccent else WarmGold
                )
              )
            }
          }

          IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier.testTag("button_settings")
          ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
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
      item {
        Spacer(modifier = Modifier.height(2.dp))
        // CARD A: MY FINANCE (Prominent, Compact)
        MyFinanceCard(
          currentBalance = uiState.currentBalance,
          savingsBalance = uiState.savingsBalance,
          onClick = onNavigateToFinance
        )
      }

      item {
        // CARD B: OUR LOVE (Prominent)
        OurLoveCard(
          relationshipStartDate = uiState.relationshipStartDate,
          daysTogether = uiState.daysTogether,
          nextAnniversaryDate = uiState.nextAnniversaryDate,
          countdownDays = uiState.anniversaryCountdownDays,
          onClick = onNavigateToLove
        )
      }

      item {
        MyanmarOrnamentalDivider(modifier = Modifier.padding(vertical = 2.dp))
      }

      // Quick Add Buttons Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Quick Add Income Button
          ButtonCard(
            title = "Add Income",
            subtitle = "+ ဝင်ငွေထည့်မည်",
            color = IncomeGreen,
            icon = Icons.Default.Add,
            modifier = Modifier
              .weight(1f)
              .testTag("button_quick_income"),
            onClick = { showAddIncomeDialog = true }
          )

          // Quick Add Expense Button
          ButtonCard(
            title = "Add Expense",
            subtitle = "- အသုံးစရိတ်ထည့်မည်",
            color = MyanmarRedAccent,
            icon = Icons.Default.Remove,
            modifier = Modifier
              .weight(1f)
              .testTag("button_quick_expense"),
            onClick = { showAddExpenseDialog = true }
          )
        }
      }

      // Monthly Summary Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "THIS MONTH'S SUMMARY",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              // Income
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.TrendingUp,
                  contentDescription = null,
                  tint = IncomeGreen,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                  Text(text = "Income", style = MaterialTheme.typography.labelSmall)
                  Text(
                    text = CoupleCalculations.formatCurrency(uiState.monthlyIncome),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = IncomeGreen
                  )
                }
              }

              // Expense
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.TrendingDown,
                  contentDescription = null,
                  tint = ExpenseRed,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                  Text(text = "Expense", style = MaterialTheme.typography.labelSmall)
                  Text(
                    text = CoupleCalculations.formatCurrency(uiState.monthlyExpense),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ExpenseRed
                  )
                }
              }
            }
          }
        }
      }

      // Savings Goal Progress section
      if (uiState.savingsGoals.isNotEmpty()) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Savings, contentDescription = null, tint = WarmGold, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Savings Goals (စုငွေပန်းတိုင်များ)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
            TextButton(onClick = onNavigateToSavings) {
              Text("View All", style = MaterialTheme.typography.labelMedium.copy(color = WarmGold))
            }
          }
        }

        items(uiState.savingsGoals) { goal ->
          val progress = CoupleCalculations.calculateGoalProgress(goal.currentAmount, goal.targetAmount)
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = onNavigateToSavings)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = goal.title,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                  text = "${progress.toInt()}%",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = WarmGold
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { (progress / 100f).toFloat() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(7.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = WarmGold,
                trackColor = WarmGold.copy(alpha = 0.2f)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Saved: " + CoupleCalculations.formatCurrency(goal.currentAmount),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Target: " + CoupleCalculations.formatCurrency(goal.targetAmount),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // Recent Transactions section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Recent Transactions (လတ်တလောစာရင်း)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          TextButton(onClick = onNavigateToFinance) {
            Text("See All", style = MaterialTheme.typography.labelMedium)
          }
        }
      }

      if (uiState.recentTransactions.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No transactions yet. Tap Add Income or Expense above!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        items(uiState.recentTransactions) { tx ->
          TransactionItemRow(
            transaction = tx,
            onClick = { selectedTransactionForEdit = tx }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Quick Add Income Dialog
  if (showAddIncomeDialog) {
    TransactionDialog(
      initialType = TransactionType.INCOME,
      onDismiss = { showAddIncomeDialog = false },
      onSave = { _, type, amount, category, desc, _, _, _ ->
        viewModel.quickAddTransaction(type, amount, category, desc.ifBlank { "Income" })
      }
    )
  }

  // Quick Add Expense Dialog
  if (showAddExpenseDialog) {
    TransactionDialog(
      initialType = TransactionType.EXPENSE,
      onDismiss = { showAddExpenseDialog = false },
      onSave = { _, type, amount, category, desc, _, _, _ ->
        viewModel.quickAddTransaction(type, amount, category, desc.ifBlank { "Expense" })
      }
    )
  }

  // Edit Transaction Dialog
  if (selectedTransactionForEdit != null) {
    TransactionDialog(
      initialTransaction = selectedTransactionForEdit,
      onDismiss = { selectedTransactionForEdit = null },
      onSave = { _, _, _, _, _, _, _, _ ->
        selectedTransactionForEdit = null
      }
    )
  }

  // Couple Code dialog
  if (showCoupleConnectionDialog) {
    CoupleCodeDialog(
      currentCode = "DREAM-2025",
      isConnected = uiState.isCoupleConnected,
      partnerName = uiState.partnerName,
      onGenerateCode = {},
      onConnectCode = { _, onResult -> onResult(true) },
      onDisconnect = {},
      onDismiss = { showCoupleConnectionDialog = false }
    )
  }
}

@Composable
private fun ButtonCard(
  title: String,
  subtitle: String,
  color: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    color = color.copy(alpha = 0.1f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f))
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(color),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = color
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
          color = color.copy(alpha = 0.8f)
        )
      }
    }
  }
}
