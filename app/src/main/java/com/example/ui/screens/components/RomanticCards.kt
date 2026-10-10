package com.example.ui.screens.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.model.TransactionCategory
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.theme.PersonalBadgeBlue
import com.example.ui.theme.RomanticPink
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.SharedBadgePurple
import com.example.ui.theme.WarmGold
import com.example.util.CoupleCalculations
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * CARD A: MY FINANCE (Compact, displays Current balance and Savings balance)
 */
@Composable
fun MyFinanceCard(
  currentBalance: Double,
  savingsBalance: Double,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("card_my_finance")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(WarmGold.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AccountBalanceWallet,
              contentDescription = "Finance",
              tint = WarmGold,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "MY FINANCE",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "ငွေစာရင်း",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = WarmGold
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable(onClick = onClick)
        ) {
          Text(
            text = "Details",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          )
          Spacer(modifier = Modifier.width(2.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Go to Finance",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Current Balance
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Current Balance",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = CoupleCalculations.formatCurrency(currentBalance),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = if (currentBalance >= 0) MaterialTheme.colorScheme.onSurface else ExpenseRed
          )
        }

        // Small vertical divider
        Box(
          modifier = Modifier
            .height(30.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        )

        // Savings Balance
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(start = 14.dp)
        ) {
          Text(
            text = "Savings Balance",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Savings,
              contentDescription = null,
              tint = WarmGold,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = CoupleCalculations.formatCurrency(savingsBalance),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
              ),
              color = WarmGold
            )
          }
        }
      }
    }
  }
}

/**
 * CARD B: OUR LOVE (Displays Days Together, Next Anniversary, and Countdown)
 */
@Composable
fun OurLoveCard(
  relationshipStartDate: String,
  daysTogether: Long,
  nextAnniversaryDate: String,
  countdownDays: Long,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "heartPulse")
  val heartScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "heartScale"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("card_our_love")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            colors = listOf(
              RomanticRose.copy(alpha = 0.08f),
              WarmGold.copy(alpha = 0.06f)
            )
          )
        )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(RomanticRose.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Love",
                tint = MyanmarRedAccent,
                modifier = Modifier
                  .size(18.dp)
                  .scale(heartScale)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "OUR LOVE",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp,
                  fontSize = 11.sp
                ),
                color = MyanmarRedAccent
              )
              Text(
                text = "တို့နှစ်ယောက်ရဲ့ အချစ်",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MyanmarRedAccent.copy(alpha = 0.1f),
            modifier = Modifier.padding(2.dp)
          ) {
            Text(
              text = "Since 15 Aug 2025",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MyanmarRedAccent
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Days Together
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Days Together",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "$daysTogether",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 22.sp
                ),
                color = MyanmarRedAccent
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Days 💕",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = MyanmarRedAccent.copy(alpha = 0.8f)
              )
            }
          }

          // Small vertical divider
          Box(
            modifier = Modifier
              .height(30.dp)
              .width(1.dp)
              .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
          )

          // Next Anniversary & Countdown
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(start = 14.dp)
          ) {
            Text(
              text = "Next Anniversary",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "$countdownDays",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 22.sp
                ),
                color = WarmGold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Days Left ⏳",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = WarmGold
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Transaction Item Row displaying category, amount, scope badge, and date
 */
@Composable
fun TransactionItemRow(
  transaction: TransactionEntity,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isIncome = transaction.type == "INCOME"
  val category = TransactionCategory.fromName(transaction.category)
  val icon = getCategoryIcon(category)

  val dateText = try {
    val instant = Instant.ofEpochMilli(transaction.date)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    localDate.format(DateTimeFormatter.ofPattern("dd MMM"))
  } catch (_: Exception) {
    ""
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(
            if (isIncome) IncomeGreen.copy(alpha = 0.12f)
            else ExpenseRed.copy(alpha = 0.12f)
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = category.displayName,
          tint = if (isIncome) IncomeGreen else ExpenseRed,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = transaction.description.ifBlank { category.displayName },
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = dateText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (transaction.scope == "SHARED") SharedBadgePurple.copy(alpha = 0.12f)
            else PersonalBadgeBlue.copy(alpha = 0.12f)
          ) {
            Text(
              text = if (transaction.scope == "SHARED") "Shared" else "Personal",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.scope == "SHARED") SharedBadgePurple else PersonalBadgeBlue
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = (if (isIncome) "+ " else "- ") + CoupleCalculations.formatCurrency(transaction.amount),
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        ),
        color = if (isIncome) IncomeGreen else ExpenseRed
      )
    }
  }
}

fun getCategoryIcon(category: TransactionCategory): ImageVector {
  return when (category) {
    TransactionCategory.SALARY -> Icons.Default.Payments
    TransactionCategory.FOOD -> Icons.Default.Restaurant
    TransactionCategory.TRANSPORT -> Icons.Default.DirectionsCar
    TransactionCategory.RENT -> Icons.Default.Home
    TransactionCategory.SHOPPING -> Icons.Default.ShoppingBag
    TransactionCategory.BILLS -> Icons.Default.ReceiptLong
    TransactionCategory.ENTERTAINMENT -> Icons.Default.Movie
    TransactionCategory.HEALTH -> Icons.Default.LocalHospital
    TransactionCategory.GIFTS -> Icons.Default.Favorite
    TransactionCategory.OTHER -> Icons.Default.Category
  }
}
