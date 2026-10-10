package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CoupleDao
import com.example.data.local.dao.MemoryEventDao
import com.example.data.local.dao.SavingsContributionDao
import com.example.data.local.dao.SavingsGoalDao
import com.example.data.local.dao.SyncOutboxDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.CoupleEntity
import com.example.data.local.entity.MemoryEventEntity
import com.example.data.local.entity.SavingsContributionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.SyncOutboxEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity

@Database(
  entities = [
    UserEntity::class,
    CoupleEntity::class,
    TransactionEntity::class,
    SavingsGoalEntity::class,
    SavingsContributionEntity::class,
    MemoryEventEntity::class,
    SyncOutboxEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class OurDreamDatabase : RoomDatabase() {

  abstract fun userDao(): UserDao
  abstract fun coupleDao(): CoupleDao
  abstract fun transactionDao(): TransactionDao
  abstract fun savingsGoalDao(): SavingsGoalDao
  abstract fun savingsContributionDao(): SavingsContributionDao
  abstract fun memoryEventDao(): MemoryEventDao
  abstract fun syncOutboxDao(): SyncOutboxDao

  companion object {
    @Volatile
    private var INSTANCE: OurDreamDatabase? = null

    fun getDatabase(context: Context): OurDreamDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          OurDreamDatabase::class.java,
          "our_dream_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
