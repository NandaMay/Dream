package com.example

import android.app.Application
import com.example.data.local.OurDreamDatabase
import com.example.data.local.entity.CoupleEntity
import com.example.data.local.entity.MemoryEventEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.preferences.UserPreferences
import com.example.data.remote.SupabaseClient
import com.example.data.repository.CoupleRepository
import com.example.data.repository.FinanceRepository
import com.example.data.repository.MemoryRepository
import com.example.data.repository.SavingsRepository
import com.example.data.repository.SyncRepository
import com.example.notification.ReminderAlarmReceiver
import com.example.util.CoupleCalculations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class OurDreamApp : Application() {

  lateinit var database: OurDreamDatabase
    private set
  lateinit var preferences: UserPreferences
    private set
  lateinit var supabaseClient: SupabaseClient
    private set

  lateinit var financeRepository: FinanceRepository
    private set
  lateinit var savingsRepository: SavingsRepository
    private set
  lateinit var coupleRepository: CoupleRepository
    private set
  lateinit var memoryRepository: MemoryRepository
    private set
  lateinit var syncRepository: SyncRepository
    private set

  override fun onCreate() {
    super.onCreate()
    database = OurDreamDatabase.getDatabase(this)
    preferences = UserPreferences(this)
    supabaseClient = SupabaseClient()

    financeRepository = FinanceRepository(database, preferences)
    savingsRepository = SavingsRepository(database)
    coupleRepository = CoupleRepository(database, preferences)
    memoryRepository = MemoryRepository(this, database, preferences)
    syncRepository = SyncRepository(database, supabaseClient)

    ReminderAlarmReceiver.createNotificationChannel(this)

    // Seed default state if fresh launch
    CoroutineScope(Dispatchers.IO).launch {
      seedInitialDataIfNeeded()
    }
  }

  private suspend fun seedInitialDataIfNeeded() {
    val existingUser = database.userDao().getCurrentUser()
    if (existingUser == null) {
      val userId = UUID.randomUUID().toString()
      val coupleId = UUID.randomUUID().toString()
      val partnerId = UUID.randomUUID().toString()

      // Default user
      val user = UserEntity(
        id = userId,
        email = "sweetheart@ourdream.app",
        displayName = "Min Min",
        birthDate = "1999-11-12",
        zodiacSign = CoupleCalculations.calculateZodiacSign(11, 12).signName,
        bio = "Building our dreams together step by step 🌸",
        photoUri = null,
        isCurrentDeviceUser = true,
        partnerId = partnerId,
        coupleId = coupleId
      )
      database.userDao().insertOrUpdateUser(user)

      // Partner user
      val partner = UserEntity(
        id = partnerId,
        email = "darling@ourdream.app",
        displayName = "Hnin Hnin",
        birthDate = "2001-08-15",
        zodiacSign = CoupleCalculations.calculateZodiacSign(8, 15).signName,
        bio = "Forever and always with my favorite person 💕",
        photoUri = null,
        isCurrentDeviceUser = false,
        partnerId = userId,
        coupleId = coupleId
      )
      database.userDao().insertOrUpdateUser(partner)

      // Default Couple
      val couple = CoupleEntity(
        id = coupleId,
        coupleCode = "DREAM-2025",
        partner1Id = userId,
        partner2Id = partnerId,
        relationshipStartDate = "2025-08-15",
        isConnected = true
      )
      database.coupleDao().insertOrUpdateCouple(couple)

      // Default Memories
      val anniversaryEvent = MemoryEventEntity(
        id = UUID.randomUUID().toString(),
        title = "Our Relationship Anniversary",
        eventType = "ANNIVERSARY",
        eventDate = "2025-08-15",
        description = "The beautiful day our love story officially started ❤️",
        isRecurringYearly = true,
        coupleId = coupleId,
        userId = userId
      )
      database.memoryEventDao().insertEvent(anniversaryEvent)

      val firstDateEvent = MemoryEventEntity(
        id = UUID.randomUUID().toString(),
        title = "First Coffee Date",
        eventType = "FIRST_DATE",
        eventDate = "2025-08-01",
        description = "Sharing iced latte and endless smiles together ☕",
        isRecurringYearly = false,
        coupleId = coupleId,
        userId = userId
      )
      database.memoryEventDao().insertEvent(firstDateEvent)

      // Default Savings Goal
      val savingsGoal1 = SavingsGoalEntity(
        id = UUID.randomUUID().toString(),
        title = "Wedding & Future Home 🏡",
        targetAmount = 150000.0,
        currentAmount = 45000.0,
        targetDate = "2027-12-31",
        scope = "SHARED",
        userId = userId,
        coupleId = coupleId
      )
      database.savingsGoalDao().insertGoal(savingsGoal1)

      val savingsGoal2 = SavingsGoalEntity(
        id = UUID.randomUUID().toString(),
        title = "Japan Couple Trip 🌸",
        targetAmount = 50000.0,
        currentAmount = 18500.0,
        targetDate = "2026-11-20",
        scope = "SHARED",
        userId = userId,
        coupleId = coupleId
      )
      database.savingsGoalDao().insertGoal(savingsGoal2)

      // Default Transactions
      val now = System.currentTimeMillis()
      val oneDayAgo = now - 86400000L
      val twoDaysAgo = now - 172800000L

      database.transactionDao().insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = "INCOME",
          amount = 45000.0,
          category = "SALARY",
          date = twoDaysAgo,
          description = "Monthly Salary (ဝင်ငွေ)",
          payerOrRecipient = "Min Min",
          scope = "PERSONAL",
          userId = userId,
          coupleId = coupleId
        )
      )

      database.transactionDao().insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = "EXPENSE",
          amount = 1250.0,
          category = "FOOD",
          date = oneDayAgo,
          description = "Romantic Weekend Dinner (ညစာစားခြင်း)",
          payerOrRecipient = "Both",
          scope = "SHARED",
          userId = userId,
          coupleId = coupleId
        )
      )

      database.transactionDao().insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = "EXPENSE",
          amount = 8500.0,
          category = "RENT",
          date = now,
          description = "Apartment Rent & Utilities (အခန်းခ)",
          payerOrRecipient = "Min Min",
          scope = "SHARED",
          userId = userId,
          coupleId = coupleId
        )
      )
    }
  }
}
