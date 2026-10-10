package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.local.entity.SavingsGoalEntity
import com.example.model.TransactionScope
import com.example.ui.screens.components.AddContributionDialog
import com.example.ui.screens.components.MyanmarOrnamentalDivider
import com.example.ui.screens.components.SavingsGoalDialog
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.PersonalBadgeBlue
import com.example.ui.theme.SharedBadgePurple
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.SavingsViewModel
import com.example.util.CoupleCalculations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsScreen(
  viewModel: SavingsViewModel,
  modifier: Modifier = Modifier
) {
  val goals by viewModel.goals.collectAsStateWithLifecycle()
  val totalSaved by viewModel.totalSaved.collectAsStateWithLifecycle()

  var showAddGoalDialog by remember { mutableStateOf(false) }
  var selectedGoalForEdit by remember { mutableStateOf<SavingsGoalEntity?>(null) }
  var goalForContribution by remember { mutableStateOf<SavingsGoalEntity?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Savings Goals",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "နှစ်ယောက်အတူ စုငွေပန်းတိုင်များ",
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
        onClick = { showAddGoalDialog = true },
        containerColor = WarmGold,
        contentColor = Color.Black,
        modifier = Modifier.testTag("fab_add_savings_goal")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Savings Goal")
      }
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
        // Total Savings Card
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Savings,
                contentDescription = null,
                tint = WarmGold,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Total Combined Savings (စုစုပေါင်းစုငွေ)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = CoupleCalculations.formatCurrency(totalSaved),
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
              ),
              color = WarmGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Growing our future together with every step 🌸",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      item {
        MyanmarOrnamentalDivider()
      }

      item {
        Text(
          text = "Our Milestones (${goals.size} Goals)",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      if (goals.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                Icons.Default.Savings,
                contentDescription = null,
                tint = WarmGold.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No savings goals created yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap the + button to create a savings goal for your dream house, trip, or wedding!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(goals, key = { it.id }) { goal ->
          val progress = CoupleCalculations.calculateGoalProgress(goal.currentAmount, goal.targetAmount)
          val remaining = CoupleCalculations.calculateGoalRemaining(goal.currentAmount, goal.targetAmount)

          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedGoalForEdit = goal }
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (!goal.targetDate.isNullOrBlank()) {
                    Text(
                      text = "Target Date: ${goal.targetDate}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (goal.scope == "SHARED") SharedBadgePurple.copy(alpha = 0.12f) else PersonalBadgeBlue.copy(alpha = 0.12f)
                ) {
                  Text(
                    text = if (goal.scope == "SHARED") "Shared Couple" else "Personal",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (goal.scope == "SHARED") SharedBadgePurple else PersonalBadgeBlue
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Progress Bar
              LinearProgressIndicator(
                progress = { (progress / 100f).toFloat() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = WarmGold,
                trackColor = WarmGold.copy(alpha = 0.2f)
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "${progress.toInt()}% Complete",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = WarmGold
                  )
                )
                Text(
                  text = "Remaining: " + CoupleCalculations.formatCurrency(remaining),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Saved: " + CoupleCalculations.formatCurrency(goal.currentAmount),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Target: " + CoupleCalculations.formatCurrency(goal.targetAmount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                OutlinedButton(
                  onClick = { goalForContribution = goal },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("button_deposit_${goal.id}")
                ) {
                  Icon(
                    Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = WarmGold
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Deposit", color = WarmGold)
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // Add Goal Dialog
  if (showAddGoalDialog) {
    SavingsGoalDialog(
      onDismiss = { showAddGoalDialog = false },
      onSave = { id, title, target, current, targetDate, scope ->
        viewModel.saveGoal(id, title, target, current, targetDate, scope)
      }
    )
  }

  // Edit Goal Dialog
  if (selectedGoalForEdit != null) {
    SavingsGoalDialog(
      initialGoal = selectedGoalForEdit,
      onDismiss = { selectedGoalForEdit = null },
      onSave = { id, title, target, current, targetDate, scope ->
        viewModel.saveGoal(id, title, target, current, targetDate, scope)
      },
      onDelete = { id ->
        viewModel.deleteGoal(id)
      }
    )
  }

  // Add Contribution Dialog
  if (goalForContribution != null) {
    val goal = goalForContribution!!
    AddContributionDialog(
      goalTitle = goal.title,
      onDismiss = { goalForContribution = null },
      onConfirm = { amount, note ->
        viewModel.addContribution(goal.id, amount, note)
      }
    )
  }
}
