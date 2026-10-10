package com.example.model

enum class TransactionType {
  INCOME,
  EXPENSE
}

enum class TransactionScope {
  PERSONAL,
  SHARED
}

enum class TransactionCategory(val displayName: String, val iconName: String) {
  SALARY("Salary", "payments"),
  FOOD("Food & Dining", "restaurant"),
  TRANSPORT("Transport", "directions_car"),
  RENT("Rent & Home", "home"),
  SHOPPING("Shopping", "shopping_bag"),
  BILLS("Bills & Utilities", "receipt_long"),
  ENTERTAINMENT("Entertainment", "movie"),
  HEALTH("Health & Medical", "local_hospital"),
  GIFTS("Gifts & Love", "favorite"),
  OTHER("Other", "category");

  companion object {
    fun fromName(name: String): TransactionCategory {
      return entries.find { it.name.equals(name, ignoreCase = true) } ?: OTHER
    }
  }
}

enum class EventType(val displayName: String, val defaultTitle: String) {
  ANNIVERSARY("Anniversary", "Relationship Anniversary"),
  BIRTHDAY("Birthday", "Birthday Celebration"),
  FIRST_MEET("First Meeting", "Day We First Met"),
  FIRST_DATE("First Date", "Our Very First Date"),
  SPECIAL_MEMORY("Special Memory", "Precious Memory"),
  CUSTOM("Custom Date", "Special Day");

  companion object {
    fun fromName(name: String): EventType {
      return entries.find { it.name.equals(name, ignoreCase = true) } ?: SPECIAL_MEMORY
    }
  }
}

enum class SyncStatus {
  PENDING,
  SYNCED,
  FAILED
}

enum class ZodiacSign(
  val signName: String,
  val symbol: String,
  val burmeseName: String,
  val dateRange: String,
  val romanticTrait: String
) {
  ARIES("Aries", "♈", "မိဿရာသီ", "Mar 21 - Apr 19", "Passionate, energetic, and fearlessly devoted"),
  TAURUS("Taurus", "♉", "ပြိဿရာသီ", "Apr 20 - May 20", "Faithful, romantic, and deeply grounding"),
  GEMINI("Gemini", "♊", "မေထုန်ရာသီ", "May 21 - Jun 20", "Playful, charming, and great conversation partner"),
  CANCER("Cancer", "♋", "ကရကဋ်ရာသီ", "Jun 21 - Jul 22", "Nurturing, compassionate, and forever loyal"),
  LEO("Leo", "♌", "သိဟ်ရာသီ", "Jul 23 - Aug 22", "Generous, protective, and radiant lover"),
  VIRGO("Virgo", "♍", "ကန်ရာသီ", "Aug 23 - Sep 22", "Attentive, thoughtful, and tenderly supportive"),
  LIBRA("Libra", "♎", "တူရာသီ", "Sep 23 - Oct 22", "Harmonious, graceful, and deeply romantic"),
  SCORPIO("Scorpio", "♏", "ဗြိစ္ဆာရာသီ", "Oct 23 - Nov 21", "Intense, deeply loyal, and soulful connection"),
  SAGITTARIUS("Sagittarius", "♐", "ဓနုရာသီ", "Nov 22 - Dec 21", "Adventurous, joyful, and free-spirited companion"),
  CAPRICORN("Capricorn", "♑", "မကာရရာသီ", "Dec 22 - Jan 19", "Steadfast, trustworthy, and long-term committed"),
  AQUARIUS("Aquarius", "♒", "ကုမ်ရာသီ", "Jan 20 - Feb 18", "Unique, understanding, and best friend at heart"),
  PISCES("Pisces", "♓", "မိန်ရာသီ", "Feb 19 - Mar 20", "Dreamy, soulful, and boundless love")
}
