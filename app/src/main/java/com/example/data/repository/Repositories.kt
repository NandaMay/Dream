package com.example.data.repository

import android.content.Context
import com.example.data.local.OurDreamDatabase
import com.example.data.local.entity.CoupleEntity
import com.example.data.local.entity.MemoryEventEntity
import com.example.data.local.entity.SavingsContributionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.SyncOutboxEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.preferences.UserPreferences
import com.example.data.remote.SupabaseClient
import com.example.notification.ReminderScheduler
import com.example.util.CoupleCalculations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class FinanceRepository(
  private val database: OurDreamDatabase,
  private val preferences: UserPreferences
) {
  private val transactionDao = database.transactionDao()
  private val outboxDao = database.syncOutboxDao()

  val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
  val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(5)
  val totalIncome: Flow<Double> = transactionDao.getTotalIncomeFlow().map { it ?: 0.0 }
  val totalExpense: Flow<Double> = transactionDao.getTotalExpenseFlow().map { it ?: 0.0 }

  fun getTransactionsByScope(scope: String): Flow<List<TransactionEntity>> =
    transactionDao.getTransactionsByScope(scope)

  suspend fun insertTransaction(transaction: TransactionEntity) {
    transactionDao.insertTransaction(transaction)
    // Enqueue to outbox
    val payload = JSONObject().apply {
      put("id", transaction.id)
      put("type", transaction.type)
      put("amount", transaction.amount)
      put("category", transaction.category)
      put("date", transaction.date)
      put("description", transaction.description)
      put("payer_or_recipient", transaction.payerOrRecipient)
      put("scope", transaction.scope)
      put("user_id", transaction.userId)
      put("couple_id", transaction.coupleId)
    }
    outboxDao.enqueue(
      SyncOutboxEntity(
        entityType = "TRANSACTION",
        entityId = transaction.id,
        operation = "INSERT",
        payloadJson = payload.toString()
      )
    )
  }

  suspend fun updateTransaction(transaction: TransactionEntity) {
    transactionDao.updateTransaction(transaction)
    val payload = JSONObject().apply {
      put("id", transaction.id)
      put("type", transaction.type)
      put("amount", transaction.amount)
      put("category", transaction.category)
      put("date", transaction.date)
      put("description", transaction.description)
      put("payer_or_recipient", transaction.payerOrRecipient)
      put("scope", transaction.scope)
      put("user_id", transaction.userId)
      put("couple_id", transaction.coupleId)
    }
    outboxDao.enqueue(
      SyncOutboxEntity(
        entityType = "TRANSACTION",
        entityId = transaction.id,
        operation = "UPDATE",
        payloadJson = payload.toString()
      )
    )
  }

  suspend fun deleteTransaction(id: String) {
    transactionDao.deleteTransactionById(id)
    outboxDao.enqueue(
      SyncOutboxEntity(
        entityType = "TRANSACTION",
        entityId = id,
        operation = "DELETE",
        payloadJson = "{}"
      )
    )
  }
}

class SavingsRepository(
  private val database: OurDreamDatabase
) {
  private val savingsGoalDao = database.savingsGoalDao()
  private val contributionDao = database.savingsContributionDao()
  private val outboxDao = database.syncOutboxDao()

  val allGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()
  val totalSaved: Flow<Double> = savingsGoalDao.getTotalSavingsFlow().map { it ?: 0.0 }

  suspend fun insertGoal(goal: SavingsGoalEntity) {
    savingsGoalDao.insertGoal(goal)
    outboxDao.enqueue(
      SyncOutboxEntity(
        entityType = "SAVINGS_GOAL",
        entityId = goal.id,
        operation = "INSERT",
        payloadJson = JSONObject().apply {
          put("id", goal.id)
          put("title", goal.title)
          put("target_amount", goal.targetAmount)
          put("current_amount", goal.currentAmount)
          put("target_date", goal.targetDate)
          put("scope", goal.scope)
        }.toString()
      )
    )
  }

  suspend fun updateGoal(goal: SavingsGoalEntity) {
    savingsGoalDao.updateGoal(goal)
    outboxDao.enqueue(
      SyncOutboxEntity(
        entityType = "SAVINGS_GOAL",
        entityId = goal.id,
        operation = "UPDATE",
        payloadJson = JSONObject().apply {
          put("id", goal.id)
          put("title", goal.title)
          put("target_amount", goal.targetAmount)
          put("current_amount", goal.currentAmount)
          put("target_date", goal.targetDate)
        }.toString()
      )
    )
  }

  suspend fun deleteGoal(id: String) {
    savingsGoalDao.deleteGoalById(id)
    outboxDao.enqueue(
      SyncOutboxEntity(
        entityType = "SAVINGS_GOAL",
        entityId = id,
        operation = "DELETE",
        payloadJson = "{}"
      )
    )
  }

  suspend fun addContribution(goalId: String, amount: Double, note: String, userId: String) {
    val goal = savingsGoalDao.getGoalById(goalId) ?: return
    val newAmount = goal.currentAmount + amount
    savingsGoalDao.updateGoal(goal.copy(currentAmount = newAmount, updatedAt = System.currentTimeMillis()))
    contributionDao.insertContribution(
      SavingsContributionEntity(
        id = UUID.randomUUID().toString(),
        goalId = goalId,
        amount = amount,
        note = note,
        userId = userId
      )
    )
  }
}

class CoupleRepository(
  private val database: OurDreamDatabase,
  private val preferences: UserPreferences
) {
  private val userDao = database.userDao()
  private val coupleDao = database.coupleDao()

  val currentUser: Flow<UserEntity?> = userDao.getCurrentUserFlow()
  val couple: Flow<CoupleEntity?> = coupleDao.getCoupleFlow()

  val partnerUser: Flow<UserEntity?> = coupleDao.getCoupleFlow().flatMapLatest { cp ->
    if (cp != null && cp.isConnected) {
      userDao.getPartnerUserFlow(cp.id)
    } else {
      flowOf(null)
    }
  }

  suspend fun saveUserProfile(
    displayName: String,
    birthDate: String,
    bio: String,
    photoUri: String? = null
  ): UserEntity {
    val existing = userDao.getCurrentUser()
    val parsedDate = try {
      LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: Exception) {
      LocalDate.of(2000, 1, 1)
    }
    val zodiac = CoupleCalculations.calculateZodiacSign(parsedDate.monthValue, parsedDate.dayOfMonth)

    val updatedUser = existing?.copy(
      displayName = displayName,
      birthDate = birthDate,
      zodiacSign = zodiac.signName,
      bio = bio,
      photoUri = photoUri ?: existing.photoUri
    ) ?: UserEntity(
      id = UUID.randomUUID().toString(),
      email = "partner@ourdream.app",
      displayName = displayName,
      birthDate = birthDate,
      zodiacSign = zodiac.signName,
      bio = bio,
      photoUri = photoUri,
      isCurrentDeviceUser = true
    )

    userDao.insertOrUpdateUser(updatedUser)
    preferences.setActiveUserId(updatedUser.id)
    return updatedUser
  }

  suspend fun generateCoupleCode(): CoupleEntity {
    val currentUser = userDao.getCurrentUser()
    val userId = currentUser?.id ?: UUID.randomUUID().toString()
    val randomCode = "DREAM-" + (1000..9999).random().toString()
    val existingCouple = coupleDao.getCouple()

    val newCouple = existingCouple?.copy(
      coupleCode = randomCode
    ) ?: CoupleEntity(
      id = UUID.randomUUID().toString(),
      coupleCode = randomCode,
      partner1Id = userId,
      partner2Id = null,
      relationshipStartDate = "2025-08-15",
      isConnected = false
    )

    coupleDao.insertOrUpdateCouple(newCouple)
    if (currentUser != null) {
      userDao.insertOrUpdateUser(currentUser.copy(coupleId = newCouple.id))
    }
    return newCouple
  }

  suspend fun connectWithCoupleCode(code: String): Boolean {
    val trimmed = code.trim().uppercase()
    if (trimmed.length < 5) return false

    val current = userDao.getCurrentUser() ?: return false
    val existingCouple = coupleDao.getCouple()

    // Create connected couple representation
    val coupleId = existingCouple?.id ?: UUID.randomUUID().toString()
    val partnerId = UUID.randomUUID().toString()

    val connectedCouple = CoupleEntity(
      id = coupleId,
      coupleCode = trimmed,
      partner1Id = current.id,
      partner2Id = partnerId,
      relationshipStartDate = "2025-08-15",
      isConnected = true
    )

    // Create partner profile representation
    val partnerUser = UserEntity(
      id = partnerId,
      email = "mylove@ourdream.app",
      displayName = "My Sweetheart",
      birthDate = "2001-08-15",
      zodiacSign = "Leo",
      bio = "The love of my life. Together forever 💕",
      photoUri = null,
      isCurrentDeviceUser = false,
      partnerId = current.id,
      coupleId = coupleId
    )

    coupleDao.insertOrUpdateCouple(connectedCouple)
    userDao.insertOrUpdateUser(current.copy(coupleId = coupleId, partnerId = partnerId))
    userDao.insertOrUpdateUser(partnerUser)
    return true
  }

  suspend fun disconnectCouple() {
    coupleDao.clearCouple()
    val current = userDao.getCurrentUser()
    if (current != null) {
      userDao.insertOrUpdateUser(current.copy(coupleId = null, partnerId = null))
    }
  }

  suspend fun updateRelationshipStartDate(newDate: String) {
    val existing = coupleDao.getCouple() ?: return
    coupleDao.insertOrUpdateCouple(existing.copy(relationshipStartDate = newDate))
  }
}

class MemoryRepository(
  private val context: Context,
  private val database: OurDreamDatabase,
  private val preferences: UserPreferences
) {
  private val eventDao = database.memoryEventDao()
  private val outboxDao = database.syncOutboxDao()

  val allEvents: Flow<List<MemoryEventEntity>> = eventDao.getAllEvents()

  suspend fun insertEvent(event: MemoryEventEntity) {
    eventDao.insertEvent(event)
    ReminderScheduler.scheduleEventReminders(context, event)
    outboxDao.enqueue(
      SyncOutboxEntity(
        entityType = "MEMORY_EVENT",
        entityId = event.id,
        operation = "INSERT",
        payloadJson = JSONObject().apply {
          put("id", event.id)
          put("title", event.title)
          put("event_type", event.eventType)
          put("event_date", event.eventDate)
          put("description", event.description)
        }.toString()
      )
    )
  }

  suspend fun updateEvent(event: MemoryEventEntity) {
    eventDao.updateEvent(event)
    ReminderScheduler.scheduleEventReminders(context, event)
  }

  suspend fun deleteEvent(id: String) {
    eventDao.deleteEventById(id)
  }
}

class SyncRepository(
  private val database: OurDreamDatabase,
  private val supabaseClient: SupabaseClient
) {
  private val outboxDao = database.syncOutboxDao()
  val pendingQueueCount: Flow<Int> = outboxDao.getPendingQueue().map { it.size }

  suspend fun processOutbox(): Int {
    if (!supabaseClient.isConfigured()) return 0
    val items = outboxDao.getPendingItems()
    var syncedCount = 0

    for (item in items) {
      val tableName = when (item.entityType) {
        "TRANSACTION" -> "transactions"
        "SAVINGS_GOAL" -> "savings_goals"
        "MEMORY_EVENT" -> "memory_events"
        else -> continue
      }

      val result = when (item.operation) {
        "INSERT", "UPDATE" -> supabaseClient.upsertRecord(tableName, item.payloadJson)
        "DELETE" -> supabaseClient.deleteRecord(tableName, item.entityId)
        else -> Result.failure(Exception("Unknown operation"))
      }

      if (result.isSuccess) {
        outboxDao.removeFromQueue(item.id)
        syncedCount++
      } else {
        outboxDao.updateStatus(item.id, "FAILED")
      }
    }
    return syncedCount
  }
}
