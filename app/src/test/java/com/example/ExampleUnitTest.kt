package com.example

import com.example.data.PrayerTimesHelper
import com.example.data.QuranRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun quranRepository_containsAll114Surahs() {
    assertEquals(114, QuranRepository.surahs.size)
    assertEquals("الفَاتِحَة", QuranRepository.surahs.first().nameArabic)
    assertEquals("النَّاس", QuranRepository.surahs.last().nameArabic)
  }

  @Test
  fun prayerTimes_providesAllDailyPrayers() {
    val prayers = PrayerTimesHelper.getTodayPrayers()
    assertEquals(6, prayers.size)
    val nextPrayer = PrayerTimesHelper.getNextPrayer()
    assertTrue(nextPrayer.remainingMillis > 0)
  }
}

