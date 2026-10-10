package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.TransactionEntity
import com.example.model.TransactionCategory
import com.example.model.TransactionScope
import com.example.ui.screens.components.TransactionDialog
import com.example.ui.screens.components.TransactionItemRow
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CoupleCalculations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
  viewModel: FinanceViewModel,
  modifier: Modifier = Modifier
) {
  val transactions by viewModel.transactions.collectAsStateWithLifecycle()
  val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
  val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedScopeFilter by viewModel.selectedScopeFilter.collectAsStateWithLifecycle()
  val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

  var showAddDialog by remember { mutableStateOf(false) }
  var selectedTransactionForEdit by remember { mutableStateOf<TransactionEntity?>(null) }
  var selectedScopeTab by remember { mutableIntStateOf(0) } // 0 = All, 1 = Personal, 2 = Shared

  val netBalance = CoupleCalculations.calculateNetBalance(totalIncome, totalExpense)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Finance Management",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "ဝင်ငွေ နှင့် အသုံးစရိတ် စီမံခန့်ခွဲမှု",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = WarmGold
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MyanmarRedAccent,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_add_transaction")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Transaction")
      }
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Balance Overview Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Total Net Balance (လက်ကျန်ငွေ)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = CoupleCalculations.formatCurrency(netBalance),
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              fontSize = 28.sp
            ),
            color = if (netBalance >= 0) MaterialTheme.colorScheme.onSurface else ExpenseRed
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Income
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.TrendingUp, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Column {
                Text(text = "Total Income", style = MaterialTheme.typography.labelSmall)
                Text(
                  text = CoupleCalculations.formatCurrency(totalIncome),
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = IncomeGreen
                )
              }
            }

            // Divider
            Box(
              modifier = Modifier
                .height(30.dp)
                .width(1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )

            // Expense
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.TrendingDown, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Column {
                Text(text = "Total Expense", style = MaterialTheme.typography.labelSmall)
                Text(
                  text = CoupleCalculations.formatCurrency(totalExpense),
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = ExpenseRed
                )
              }
            }
          }
        }
      }

      // Scope Tabs: All, Personal, Shared
      TabRow(
        selectedTabIndex = selectedScopeTab,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      ) {
        Tab(
          selected = selectedScopeTab == 0,
          onClick = {
            selectedScopeTab = 0
            viewModel.onScopeFilterSelected(null)
          },
          text = { Text("All Records") }
        )
        Tab(
          selected = selectedScopeTab == 1,
          onClick = {
            selectedScopeTab = 1
            viewModel.onScopeFilterSelected(TransactionScope.PERSONAL)
          },
          text = { Text("Personal") }
        )
        Tab(
          selected = selectedScopeTab == 2,
          onClick = {
            selectedScopeTab = 2
            viewModel.onScopeFilterSelected(TransactionScope.SHARED)
          },
          text = { Text("Shared Couple") }
        )
      }

      // Search field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = viewModel::onSearchQueryChanged,
        placeholder = { Text("Search transactions...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .testTag("input_search_finance")
      )

      // Category filter chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
      ) {
        item {
          FilterChip(
            selected = selectedCategoryFilter == null,
            onClick = { viewModel.onCategoryFilterSelected(null) },
            label = { Text("All Categories") }
          )
        }
        items(TransactionCategory.entries.toTypedArray()) { cat ->
          FilterChip(
            selected = selectedCategoryFilter == cat.name,
            onClick = {
              if (selectedCategoryFilter == cat.name) {
                viewModel.onCategoryFilterSelected(null)
              } else {
                viewModel.onCategoryFilterSelected(cat.name)
              }
            },
            label = { Text(cat.displayName) }
          )
        }
      }

      // Transactions list
      if (transactions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "No transactions found",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tap the + button to add your first income or expense!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          items(transactions, key = { it.id }) { tx ->
            TransactionItemRow(
              transaction = tx,
              onClick = { selectedTransactionForEdit = tx }
            )
          }
          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      }
    }
  }

  // Add Dialog
  if (showAddDialog) {
    TransactionDialog(
      onDismiss = { showAddDialog = false },
      onSave = { id, type, amount, cat, desc, date, payer, scope ->
        viewModel.saveTransaction(id, type, amount, cat, desc, date, payer, scope)
      }
    )
  }

  // Edit Dialog
  if (selectedTransactionForEdit != null) {
    TransactionDialog(
      initialTransaction = selectedTransactionForEdit,
      onDismiss = { selectedTransactionForEdit = null },
      onSave = { id, type, amount, cat, desc, date, payer, scope ->
        viewModel.saveTransaction(id, type, amount, cat, desc, date, payer, scope)
      },
      onDelete = { id ->
        viewModel.deleteTransaction(id)
      }
    )
  }
}
