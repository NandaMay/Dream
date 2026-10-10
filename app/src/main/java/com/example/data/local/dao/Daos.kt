package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CoupleEntity
import com.example.data.local.entity.MemoryEventEntity
import com.example.data.local.entity.SavingsContributionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.SyncOutboxEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE isCurrentDeviceUser = 1 LIMIT 1")
  fun getCurrentUserFlow(): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE isCurrentDeviceUser = 1 LIMIT 1")
  suspend fun getCurrentUser(): UserEntity?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun getUserById(id: String): UserEntity?

  @Query("SELECT * FROM users WHERE isCurrentDeviceUser = 0 AND coupleId = :coupleId LIMIT 1")
  fun getPartnerUserFlow(coupleId: String): Flow<UserEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateUser(user: UserEntity)

  @Query("DELETE FROM users")
  suspend fun clearAll()
}

@Dao
interface CoupleDao {
  @Query("SELECT * FROM couples LIMIT 1")
  fun getCoupleFlow(): Flow<CoupleEntity?>

  @Query("SELECT * FROM couples LIMIT 1")
  suspend fun getCouple(): CoupleEntity?

  @Query("SELECT * FROM couples WHERE coupleCode = :code LIMIT 1")
  suspend fun getCoupleByCode(code: String): CoupleEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateCouple(couple: CoupleEntity)

  @Query("DELETE FROM couples")
  suspend fun clearCouple()
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE scope = :scope ORDER BY date DESC")
  fun getTransactionsByScope(scope: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
  fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE date >= :startEpoch AND date <= :endEpoch ORDER BY date DESC")
  fun getTransactionsInRange(startEpoch: Long, endEpoch: Long): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
  suspend fun getTransactionById(id: String): TransactionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity)

  @Update
  suspend fun updateTransaction(transaction: TransactionEntity)

  @Query("DELETE FROM transactions WHERE id = :id")
  suspend fun deleteTransactionById(id: String)

  @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
  fun getTotalIncomeFlow(): Flow<Double?>

  @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
  fun getTotalExpenseFlow(): Flow<Double?>

  @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date >= :startEpoch AND date <= :endEpoch")
  fun getPeriodIncomeFlow(startEpoch: Long, endEpoch: Long): Flow<Double?>

  @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :startEpoch AND date <= :endEpoch")
  fun getPeriodExpenseFlow(startEpoch: Long, endEpoch: Long): Flow<Double?>
}

@Dao
interface SavingsGoalDao {
  @Query("SELECT * FROM savings_goals ORDER BY createdAt DESC")
  fun getAllGoals(): Flow<List<SavingsGoalEntity>>

  @Query("SELECT * FROM savings_goals WHERE id = :id LIMIT 1")
  suspend fun getGoalById(id: String): SavingsGoalEntity?

  @Query("SELECT SUM(currentAmount) FROM savings_goals")
  fun getTotalSavingsFlow(): Flow<Double?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGoal(goal: SavingsGoalEntity)

  @Update
  suspend fun updateGoal(goal: SavingsGoalEntity)

  @Query("DELETE FROM savings_goals WHERE id = :id")
  suspend fun deleteGoalById(id: String)
}

@Dao
interface SavingsContributionDao {
  @Query("SELECT * FROM savings_contributions WHERE goalId = :goalId ORDER BY date DESC")
  fun getContributionsForGoal(goalId: String): Flow<List<SavingsContributionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContribution(contribution: SavingsContributionEntity)
}

@Dao
interface MemoryEventDao {
  @Query("SELECT * FROM memory_events ORDER BY eventDate ASC")
  fun getAllEvents(): Flow<List<MemoryEventEntity>>

  @Query("SELECT * FROM memory_events WHERE id = :id LIMIT 1")
  suspend fun getEventById(id: String): MemoryEventEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: MemoryEventEntity)

  @Update
  suspend fun updateEvent(event: MemoryEventEntity)

  @Query("DELETE FROM memory_events WHERE id = :id")
  suspend fun deleteEventById(id: String)
}

@Dao
interface SyncOutboxDao {
  @Query("SELECT * FROM sync_outbox WHERE status = 'PENDING' ORDER BY createdAt ASC")
  fun getPendingQueue(): Flow<List<SyncOutboxEntity>>

  @Query("SELECT * FROM sync_outbox WHERE status = 'PENDING' ORDER BY createdAt ASC")
  suspend fun getPendingItems(): List<SyncOutboxEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun enqueue(item: SyncOutboxEntity)

  @Query("DELETE FROM sync_outbox WHERE id = :id")
  suspend fun removeFromQueue(id: Long)

  @Query("UPDATE sync_outbox SET status = :status, attempts = attempts + 1 WHERE id = :id")
  suspend fun updateStatus(id: Long, status: String)

  @Query("SELECT COUNT(*) FROM sync_outbox WHERE entityId = :entityId AND operation = :operation AND status = 'PENDING'")
  suspend fun countPendingForEntity(entityId: String, operation: String): Int
}
