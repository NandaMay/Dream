package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.OurDreamApp
import com.example.data.local.entity.CoupleEntity
import com.example.data.local.entity.MemoryEventEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.preferences.UserPreferences
import com.example.data.repository.CoupleRepository
import com.example.data.repository.FinanceRepository
import com.example.data.repository.MemoryRepository
import com.example.data.repository.SavingsRepository
import com.example.data.repository.SyncRepository
import com.example.model.TransactionScope
import com.example.model.TransactionType
import com.example.ui.theme.AppThemeSetting
import com.example.util.CoupleCalculations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

data class HomeUiState(
  val currentBalance: Double = 0.0,
  val savingsBalance: Double = 0.0,
  val monthlyIncome: Double = 0.0,
  val monthlyExpense: Double = 0.0,
  val daysTogether: Long = 0,
  val nextAnniversaryDate: String = "",
  val anniversaryCountdownDays: Long = 0,
  val relationshipStartDate: String = "2025-08-15",
  val savingsGoals: List<SavingsGoalEntity> = emptyList(),
  val recentTransactions: List<TransactionEntity> = emptyList(),
  val isCoupleConnected: Boolean = false,
  val partnerName: String? = null
)

class HomeViewModel(
  private val financeRepo: FinanceRepository,
  private val savingsRepo: SavingsRepository,
  private val coupleRepo: CoupleRepository
) : ViewModel() {

  val uiState: StateFlow<HomeUiState> = combine(
    financeRepo.allTransactions,
    financeRepo.totalIncome,
    financeRepo.totalExpense,
    savingsRepo.totalSaved,
    savingsRepo.allGoals,
    coupleRepo.couple,
    coupleRepo.partnerUser
  ) { args: Array<Any?> ->
    @Suppress("UNCHECKED_CAST")
    val transactions = args[0] as List<TransactionEntity>
    val totalInc = args[1] as Double
    val totalExp = args[2] as Double
    val totalSav = args[3] as Double
    @Suppress("UNCHECKED_CAST")
    val goals = args[4] as List<SavingsGoalEntity>
    val couple = args[5] as CoupleEntity?
    val partner = args[6] as UserEntity?

    val balance = CoupleCalculations.calculateNetBalance(totalInc, totalExp)

    // Calculate this month's stats
    val now = LocalDate.now()
    val startOfMonthEpoch = now.withDayOfMonth(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    val monthTransactions = transactions.filter { it.date >= startOfMonthEpoch }
    val monthInc = monthTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val monthExp = monthTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    val startDate = couple?.relationshipStartDate ?: "2025-08-15"
    val parsedStart = try {
      LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: Exception) {
      CoupleCalculations.DEFAULT_RELATIONSHIP_START_DATE
    }

    val daysTogether = CoupleCalculations.calculateDaysTogether(parsedStart, now)
    val nextAnniversary = CoupleCalculations.calculateNextAnniversary(parsedStart, now)
    val countdown = CoupleCalculations.calculateAnniversaryCountdown(parsedStart, now)

    HomeUiState(
      currentBalance = balance,
      savingsBalance = totalSav,
      monthlyIncome = monthInc,
      monthlyExpense = monthExp,
      daysTogether = daysTogether,
      nextAnniversaryDate = nextAnniversary.format(CoupleCalculations.dateFormatter),
      anniversaryCountdownDays = countdown,
      relationshipStartDate = startDate,
      savingsGoals = goals.take(3),
      recentTransactions = transactions.take(5),
      isCoupleConnected = couple?.isConnected ?: false,
      partnerName = partner?.displayName
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

  fun quickAddTransaction(type: TransactionType, amount: Double, category: String, description: String) {
    viewModelScope.launch {
      financeRepo.insertTransaction(
        TransactionEntity(
          id = UUID.randomUUID().toString(),
          type = type.name,
          amount = amount,
          category = category,
          date = System.currentTimeMillis(),
          description = description,
          payerOrRecipient = "Me",
          scope = "PERSONAL",
          userId = "current-user"
        )
      )
    }
  }
}

class FinanceViewModel(
  private val financeRepo: FinanceRepository
) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  private val _selectedScopeFilter = MutableStateFlow<TransactionScope?>(null) // null = ALL
  val selectedScopeFilter = _selectedScopeFilter.asStateFlow()

  private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
  val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

  val transactions: StateFlow<List<TransactionEntity>> = combine(
    financeRepo.allTransactions,
    _searchQuery,
    _selectedScopeFilter,
    _selectedCategoryFilter
  ) { all, query, scope, cat ->
    all.filter { tx ->
      val matchesQuery = query.isBlank() || tx.description.contains(query, ignoreCase = true) || tx.category.contains(query, ignoreCase = true)
      val matchesScope = scope == null || tx.scope.equals(scope.name, ignoreCase = true)
      val matchesCat = cat == null || tx.category.equals(cat, ignoreCase = true)
      matchesQuery && matchesScope && matchesCat
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalIncome: StateFlow<Double> = financeRepo.totalIncome
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val totalExpense: StateFlow<Double> = financeRepo.totalExpense
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  fun onSearchQueryChanged(query: String) {
    _searchQuery.value = query
  }

  fun onScopeFilterSelected(scope: TransactionScope?) {
    _selectedScopeFilter.value = scope
  }

  fun onCategoryFilterSelected(category: String?) {
    _selectedCategoryFilter.value = category
  }

  fun saveTransaction(
    id: String?,
    type: TransactionType,
    amount: Double,
    category: String,
    description: String,
    dateMillis: Long,
    payer: String,
    scope: TransactionScope
  ) {
    viewModelScope.launch {
      val txId = id ?: UUID.randomUUID().toString()
      val entity = TransactionEntity(
        id = txId,
        type = type.name,
        amount = amount,
        category = category,
        date = dateMillis,
        description = description,
        payerOrRecipient = payer,
        scope = scope.name,
        userId = "current-user",
        updatedAt = System.currentTimeMillis()
      )
      if (id == null) {
        financeRepo.insertTransaction(entity)
      } else {
        financeRepo.updateTransaction(entity)
      }
    }
  }

  fun deleteTransaction(id: String) {
    viewModelScope.launch {
      financeRepo.deleteTransaction(id)
    }
  }
}

class SavingsViewModel(
  private val savingsRepo: SavingsRepository
) : ViewModel() {

  val goals: StateFlow<List<SavingsGoalEntity>> = savingsRepo.allGoals
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalSaved: StateFlow<Double> = savingsRepo.totalSaved
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  fun saveGoal(
    id: String?,
    title: String,
    targetAmount: Double,
    currentAmount: Double,
    targetDate: String?,
    scope: TransactionScope
  ) {
    viewModelScope.launch {
      val goalId = id ?: UUID.randomUUID().toString()
      val entity = SavingsGoalEntity(
        id = goalId,
        title = title,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        targetDate = targetDate,
        scope = scope.name,
        userId = "current-user",
        updatedAt = System.currentTimeMillis()
      )
      if (id == null) {
        savingsRepo.insertGoal(entity)
      } else {
        savingsRepo.updateGoal(entity)
      }
    }
  }

  fun addContribution(goalId: String, amount: Double, note: String) {
    viewModelScope.launch {
      savingsRepo.addContribution(goalId, amount, note, "current-user")
    }
  }

  fun deleteGoal(id: String) {
    viewModelScope.launch {
      savingsRepo.deleteGoal(id)
    }
  }
}

class LoveViewModel(
  private val coupleRepo: CoupleRepository,
  private val memoryRepo: MemoryRepository
) : ViewModel() {

  val couple: StateFlow<CoupleEntity?> = coupleRepo.couple
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val partner: StateFlow<UserEntity?> = coupleRepo.partnerUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val events: StateFlow<List<MemoryEventEntity>> = memoryRepo.allEvents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun generateCoupleCode() {
    viewModelScope.launch {
      coupleRepo.generateCoupleCode()
    }
  }

  fun connectWithCode(code: String, onResult: (Boolean) -> Unit) {
    viewModelScope.launch {
      val success = coupleRepo.connectWithCoupleCode(code)
      onResult(success)
    }
  }

  fun saveEvent(
    id: String?,
    title: String,
    eventType: String,
    eventDate: String,
    description: String,
    photoUri: String? = null,
    isRecurringYearly: Boolean = true
  ) {
    viewModelScope.launch {
      val eventId = id ?: UUID.randomUUID().toString()
      val entity = MemoryEventEntity(
        id = eventId,
        title = title,
        eventType = eventType,
        eventDate = eventDate,
        description = description,
        photoUri = photoUri,
        isRecurringYearly = isRecurringYearly,
        userId = "current-user"
      )
      if (id == null) {
        memoryRepo.insertEvent(entity)
      } else {
        memoryRepo.updateEvent(entity)
      }
    }
  }

  fun deleteEvent(id: String) {
    viewModelScope.launch {
      memoryRepo.deleteEvent(id)
    }
  }

  fun updateRelationshipStartDate(newDate: String) {
    viewModelScope.launch {
      coupleRepo.updateRelationshipStartDate(newDate)
    }
  }
}

class ProfileViewModel(
  private val coupleRepo: CoupleRepository
) : ViewModel() {

  val currentUser: StateFlow<UserEntity?> = coupleRepo.currentUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val partnerUser: StateFlow<UserEntity?> = coupleRepo.partnerUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val couple: StateFlow<CoupleEntity?> = coupleRepo.couple
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  fun saveProfile(displayName: String, birthDate: String, bio: String, photoUri: String?) {
    viewModelScope.launch {
      coupleRepo.saveUserProfile(displayName, birthDate, bio, photoUri)
    }
  }
}

class SettingsViewModel(
  private val preferences: UserPreferences,
  private val coupleRepo: CoupleRepository,
  private val syncRepo: SyncRepository
) : ViewModel() {

  val theme: StateFlow<AppThemeSetting> = preferences.themeFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeSetting.LIGHT_ROMANTIC)

  val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabledFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

  val remind7Days: StateFlow<Boolean> = preferences.remind7DaysFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

  val remind3Days: StateFlow<Boolean> = preferences.remind3DaysFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

  val remind1Day: StateFlow<Boolean> = preferences.remind1DayFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

  val remindSameDay: StateFlow<Boolean> = preferences.remindSameDayFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

  val pendingSyncCount: StateFlow<Int> = syncRepo.pendingQueueCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val couple: StateFlow<CoupleEntity?> = coupleRepo.couple
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val supabaseUrl: StateFlow<String> = preferences.supabaseUrlFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

  val supabaseKey: StateFlow<String> = preferences.supabaseKeyFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

  fun setTheme(setting: AppThemeSetting) {
    viewModelScope.launch {
      preferences.setTheme(setting)
    }
  }

  fun setNotificationsEnabled(enabled: Boolean) {
    viewModelScope.launch {
      preferences.setNotificationsEnabled(enabled)
    }
  }

  fun setReminderIntervals(sevenDays: Boolean, threeDays: Boolean, oneDay: Boolean, sameDay: Boolean) {
    viewModelScope.launch {
      preferences.setReminderIntervals(sevenDays, threeDays, oneDay, sameDay)
    }
  }

  fun saveSupabaseCredentials(url: String, anonKey: String) {
    viewModelScope.launch {
      preferences.setSupabaseCredentials(url, anonKey)
    }
  }

  fun triggerSync(onResult: (Int) -> Unit) {
    viewModelScope.launch {
      val count = syncRepo.processOutbox()
      onResult(count)
    }
  }

  fun disconnectCouple() {
    viewModelScope.launch {
      coupleRepo.disconnectCouple()
    }
  }
}

class ViewModelFactory(private val app: OurDreamApp) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when {
      modelClass.isAssignableFrom(HomeViewModel::class.java) ->
        HomeViewModel(app.financeRepository, app.savingsRepository, app.coupleRepository) as T
      modelClass.isAssignableFrom(FinanceViewModel::class.java) ->
        FinanceViewModel(app.financeRepository) as T
      modelClass.isAssignableFrom(SavingsViewModel::class.java) ->
        SavingsViewModel(app.savingsRepository) as T
      modelClass.isAssignableFrom(LoveViewModel::class.java) ->
        LoveViewModel(app.coupleRepository, app.memoryRepository) as T
      modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
        ProfileViewModel(app.coupleRepository) as T
      modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
        SettingsViewModel(app.preferences, app.coupleRepository, app.syncRepository) as T
      else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
  }
}
