package com.example.util

import com.example.model.ZodiacSign
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object CoupleCalculations {

  val DEFAULT_RELATIONSHIP_START_DATE: LocalDate = LocalDate.of(2025, 8, 15)

  private val currencyFormat = DecimalFormat("#,##0.00")
  private val compactCurrencyFormat = DecimalFormat("#,##0")
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
  val isoDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  /**
   * Formats an amount into Thai Baht currency (e.g., ฿1,250.00).
   */
  fun formatCurrency(amount: Double, includeDecimals: Boolean = true): String {
    val formatted = if (includeDecimals) {
      currencyFormat.format(amount)
    } else {
      compactCurrencyFormat.format(amount)
    }
    return "฿$formatted"
  }

  /**
   * Calculates Western Zodiac sign from month (1-12) and day (1-31).
   */
  fun calculateZodiacSign(month: Int, day: Int): ZodiacSign {
    return when (month) {
      1 -> if (day < 20) ZodiacSign.CAPRICORN else ZodiacSign.AQUARIUS
      2 -> if (day < 19) ZodiacSign.AQUARIUS else ZodiacSign.PISCES
      3 -> if (day < 21) ZodiacSign.PISCES else ZodiacSign.ARIES
      4 -> if (day < 20) ZodiacSign.ARIES else ZodiacSign.TAURUS
      5 -> if (day < 21) ZodiacSign.TAURUS else ZodiacSign.GEMINI
      6 -> if (day < 21) ZodiacSign.GEMINI else ZodiacSign.CANCER
      7 -> if (day < 23) ZodiacSign.CANCER else ZodiacSign.LEO
      8 -> if (day < 23) ZodiacSign.LEO else ZodiacSign.VIRGO
      9 -> if (day < 23) ZodiacSign.VIRGO else ZodiacSign.LIBRA
      10 -> if (day < 23) ZodiacSign.LIBRA else ZodiacSign.SCORPIO
      11 -> if (day < 22) ZodiacSign.SCORPIO else ZodiacSign.SAGITTARIUS
      12 -> if (day < 22) ZodiacSign.SAGITTARIUS else ZodiacSign.CAPRICORN
      else -> ZodiacSign.ARIES
    }
  }

  /**
   * Calculates total elapsed days together since relationship start date.
   */
  fun calculateDaysTogether(
    startDate: LocalDate = DEFAULT_RELATIONSHIP_START_DATE,
    currentDate: LocalDate = LocalDate.now()
  ): Long {
    val days = ChronoUnit.DAYS.between(startDate, currentDate)
    return if (days < 0) 0 else days
  }

  /**
   * Returns human-readable years and months elapsed together.
   */
  fun calculateYearsAndMonthsTogether(
    startDate: LocalDate = DEFAULT_RELATIONSHIP_START_DATE,
    currentDate: LocalDate = LocalDate.now()
  ): Pair<Int, Int> {
    if (currentDate.isBefore(startDate)) return Pair(0, 0)
    val period = Period.between(startDate, currentDate)
    return Pair(period.years, period.months)
  }

  /**
   * Calculates the next anniversary date for a given annual milestone.
   */
  fun calculateNextAnniversary(
    milestoneDate: LocalDate = DEFAULT_RELATIONSHIP_START_DATE,
    currentDate: LocalDate = LocalDate.now()
  ): LocalDate {
    val thisYearAnniversary = LocalDate.of(
      currentDate.year,
      milestoneDate.monthValue,
      milestoneDate.dayOfMonth
    )
    return if (!thisYearAnniversary.isBefore(currentDate)) {
      thisYearAnniversary
    } else {
      LocalDate.of(
        currentDate.year + 1,
        milestoneDate.monthValue,
        milestoneDate.dayOfMonth
      )
    }
  }

  /**
   * Calculates the remaining countdown days until the next anniversary.
   */
  fun calculateAnniversaryCountdown(
    milestoneDate: LocalDate = DEFAULT_RELATIONSHIP_START_DATE,
    currentDate: LocalDate = LocalDate.now()
  ): Long {
    val nextAnniversary = calculateNextAnniversary(milestoneDate, currentDate)
    return ChronoUnit.DAYS.between(currentDate, nextAnniversary)
  }

  /**
   * Calculates net financial balance given total incomes and total expenses.
   */
  fun calculateNetBalance(totalIncome: Double, totalExpense: Double): Double {
    return totalIncome - totalExpense
  }

  /**
   * Calculates savings goal progress percentage (0.0 to 100.0).
   */
  fun calculateGoalProgress(currentAmount: Double, targetAmount: Double): Double {
    if (targetAmount <= 0.0) return 0.0
    val progress = (currentAmount / targetAmount) * 100.0
    return progress.coerceIn(0.0, 100.0)
  }

  /**
   * Calculates remaining amount required to complete a savings goal.
   */
  fun calculateGoalRemaining(currentAmount: Double, targetAmount: Double): Double {
    val remaining = targetAmount - currentAmount
    return if (remaining < 0.0) 0.0 else remaining
  }
}
