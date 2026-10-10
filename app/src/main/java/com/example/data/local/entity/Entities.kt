package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val id: String,
  val email: String,
  val displayName: String,
  val birthDate: String, // YYYY-MM-DD
  val zodiacSign: String,
  val bio: String,
  val photoUri: String? = null,
  val isCurrentDeviceUser: Boolean = false,
  val partnerId: String? = null,
  val coupleId: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "couples")
data class CoupleEntity(
  @PrimaryKey val id: String,
  val coupleCode: String,
  val partner1Id: String,
  val partner2Id: String? = null,
  val relationshipStartDate: String = "2025-08-15",
  val isConnected: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey val id: String,
  val type: String, // INCOME, EXPENSE
  val amount: Double,
  val category: String,
  val date: Long, // Epoch millis
  val description: String,
  val payerOrRecipient: String,
  val scope: String, // PERSONAL, SHARED
  val userId: String,
  val coupleId: String? = null,
  val syncStatus: String = "PENDING",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
  @PrimaryKey val id: String,
  val title: String,
  val targetAmount: Double,
  val currentAmount: Double,
  val targetDate: String? = null, // YYYY-MM-DD
  val scope: String = "SHARED", // PERSONAL, SHARED
  val userId: String,
  val coupleId: String? = null,
  val syncStatus: String = "PENDING",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_contributions")
data class SavingsContributionEntity(
  @PrimaryKey val id: String,
  val goalId: String,
  val amount: Double,
  val date: Long = System.currentTimeMillis(),
  val note: String = "",
  val userId: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memory_events")
data class MemoryEventEntity(
  @PrimaryKey val id: String,
  val title: String,
  val eventType: String,
  val eventDate: String, // YYYY-MM-DD
  val description: String,
  val photoUri: String? = null,
  val isRecurringYearly: Boolean = true,
  val coupleId: String? = null,
  val userId: String,
  val syncStatus: String = "PENDING",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sync_outbox")
data class SyncOutboxEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val entityType: String,
  val entityId: String,
  val operation: String, // INSERT, UPDATE, DELETE
  val payloadJson: String,
  val status: String = "PENDING",
  val attempts: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
)
