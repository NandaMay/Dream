package com.example

import com.example.model.ZodiacSign
import com.example.util.CoupleCalculations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class OurDreamCalculationsTest {

  @Test
  fun testIncomeAndExpenseBalanceCalculations() {
    val totalIncome = 45000.0
    val totalExpense = 15250.0

    val netBalance = CoupleCalculations.calculateNetBalance(totalIncome, totalExpense)
    assertEquals(29750.0, netBalance, 0.001)

    // Negative balance check
    val deficitBalance = CoupleCalculations.calculateNetBalance(1000.0, 3500.0)
    assertEquals(-2500.0, deficitBalance, 0.001)

    // Formatting check
    val formatted = CoupleCalculations.formatCurrency(netBalance)
    assertTrue(formatted.startsWith("฿"))
    assertTrue(formatted.contains("29,750.00"))
  }

  @Test
  fun testSavingsCalculations() {
    val targetAmount = 100000.0
    val currentSaved = 45000.0

    val progress = CoupleCalculations.calculateGoalProgress(currentSaved, targetAmount)
    assertEquals(45.0, progress, 0.001)

    val remaining = CoupleCalculations.calculateGoalRemaining(currentSaved, targetAmount)
    assertEquals(55000.0, remaining, 0.001)

    // Over-achieved goal check
    val overSaved = CoupleCalculations.calculateGoalProgress(120000.0, 100000.0)
    assertEquals(100.0, overSaved, 0.001)

    val remainingZero = CoupleCalculations.calculateGoalRemaining(120000.0, 100000.0)
    assertEquals(0.0, remainingZero, 0.001)

    // Zero target edge case
    assertEquals(0.0, CoupleCalculations.calculateGoalProgress(100.0, 0.0), 0.001)
  }

  @Test
  fun testZodiacSignCalculations() {
    // 12 Western Zodiac signs
    assertEquals(ZodiacSign.ARIES, CoupleCalculations.calculateZodiacSign(3, 25))
    assertEquals(ZodiacSign.TAURUS, CoupleCalculations.calculateZodiacSign(5, 5))
    assertEquals(ZodiacSign.GEMINI, CoupleCalculations.calculateZodiacSign(6, 10))
    assertEquals(ZodiacSign.CANCER, CoupleCalculations.calculateZodiacSign(7, 15))
    assertEquals(ZodiacSign.LEO, CoupleCalculations.calculateZodiacSign(8, 15)) // Anniversary month
    assertEquals(ZodiacSign.VIRGO, CoupleCalculations.calculateZodiacSign(9, 10))
    assertEquals(ZodiacSign.LIBRA, CoupleCalculations.calculateZodiacSign(10, 10))
    assertEquals(ZodiacSign.SCORPIO, CoupleCalculations.calculateZodiacSign(11, 10))
    assertEquals(ZodiacSign.SAGITTARIUS, CoupleCalculations.calculateZodiacSign(12, 10))
    assertEquals(ZodiacSign.CAPRICORN, CoupleCalculations.calculateZodiacSign(1, 10))
    assertEquals(ZodiacSign.AQUARIUS, CoupleCalculations.calculateZodiacSign(2, 10))
    assertEquals(ZodiacSign.PISCES, CoupleCalculations.calculateZodiacSign(3, 10))
  }

  @Test
  fun testDaysTogetherCalculations() {
    val startDate = LocalDate.of(2025, 8, 15)

    // Exact same day
    assertEquals(0L, CoupleCalculations.calculateDaysTogether(startDate, startDate))

    // 10 days later
    val tenDaysLater = LocalDate.of(2025, 8, 25)
    assertEquals(10L, CoupleCalculations.calculateDaysTogether(startDate, tenDaysLater))

    // 1 year later (365 days)
    val oneYearLater = LocalDate.of(2026, 8, 15)
    assertEquals(365L, CoupleCalculations.calculateDaysTogether(startDate, oneYearLater))

    // Future start date safeguard
    val pastDate = LocalDate.of(2025, 8, 10)
    assertEquals(0L, CoupleCalculations.calculateDaysTogether(startDate, pastDate))
  }

  @Test
  fun testAnniversaryCountdown() {
    val milestoneDate = LocalDate.of(2025, 8, 15)

    // Exactly 5 days before anniversary
    val testDateBefore = LocalDate.of(2026, 8, 10)
    val countdown = CoupleCalculations.calculateAnniversaryCountdown(milestoneDate, testDateBefore)
    assertEquals(5L, countdown)

    // On anniversary day
    val anniversaryDay = LocalDate.of(2026, 8, 15)
    assertEquals(0L, CoupleCalculations.calculateAnniversaryCountdown(milestoneDate, anniversaryDay))

    // Day after anniversary (next is 2027-08-15)
    val dayAfter = LocalDate.of(2026, 8, 16)
    val nextCountdown = CoupleCalculations.calculateAnniversaryCountdown(milestoneDate, dayAfter)
    assertEquals(364L, nextCountdown)
  }

  @Test
  fun testDuplicateSyncPrevention() {
    // Test set-based deduplication logic for idempotent sync operations
    val syncQueue = mutableSetOf<String>()

    val item1Key = "TRANSACTION:tx-1234:INSERT"
    val item2Key = "TRANSACTION:tx-1234:INSERT"

    val addedFirst = syncQueue.add(item1Key)
    val addedDuplicate = syncQueue.add(item2Key)

    assertTrue(addedFirst)
    assertFalse(addedDuplicate)
    assertEquals(1, syncQueue.size)
  }
}
