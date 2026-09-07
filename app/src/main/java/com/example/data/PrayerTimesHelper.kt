package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PrayerTime(
    val nameArabic: String,
    val hour: Int,
    val minute: Int,
    val iconRes: Int,
    val isMainPrayer: Boolean = true
) {
    val formattedTime: String
        get() {
            val period = if (hour < 12) "ص" else "م"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, period)
        }

    fun toCalendar(baseDate: Calendar = Calendar.getInstance()): Calendar {
        val cal = baseDate.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }
}

object PrayerTimesHelper {

    fun getTodayPrayers(): List<PrayerTime> {
        return listOf(
            PrayerTime("الفجر", 4, 52, com.example.R.drawable.ic_prayer_fajr),
            PrayerTime("الشروق", 6, 10, com.example.R.drawable.ic_prayer_sunrise, isMainPrayer = false),
            PrayerTime("الظهر", 12, 15, com.example.R.drawable.ic_prayer_dhuhr),
            PrayerTime("العصر", 15, 41, com.example.R.drawable.ic_prayer_asr),
            PrayerTime("المغرب", 18, 23, com.example.R.drawable.ic_prayer_maghrib),
            PrayerTime("العشاء", 19, 53, com.example.R.drawable.ic_prayer_isha)
        )
    }

    data class NextPrayerInfo(
        val prayer: PrayerTime,
        val remainingMillis: Long,
        val countdownText: String
    )

    fun getNextPrayer(currentTime: Calendar = Calendar.getInstance()): NextPrayerInfo {
        val prayers = getTodayPrayers()
        for (prayer in prayers) {
            val prayerCal = prayer.toCalendar(currentTime)
            if (prayerCal.after(currentTime)) {
                val diff = prayerCal.timeInMillis - currentTime.timeInMillis
                return NextPrayerInfo(prayer, diff, formatCountdown(diff))
            }
        }
        // If all today's prayers have passed, next is tomorrow's Fajr
        val fajr = prayers.first()
        val tomorrowFajrCal = fajr.toCalendar(currentTime)
        tomorrowFajrCal.add(Calendar.DAY_OF_YEAR, 1)
        val diff = tomorrowFajrCal.timeInMillis - currentTime.timeInMillis
        return NextPrayerInfo(fajr, diff, formatCountdown(diff))
    }

    private fun formatCountdown(diffMillis: Long): String {
        val seconds = (diffMillis / 1000) % 60
        val minutes = (diffMillis / (1000 * 60)) % 60
        val hours = (diffMillis / (1000 * 60 * 60))
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun getHijriDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        // Hijri date approximation for the period: 1448 H / Rabi' al-Awwal
        val hijriDay = (day % 30) + 1
        return "$hijriDay ربيع الأول ١٤٤٨ هـ"
    }

    fun getGregorianDateFormatted(): String {
        val sdf = SimpleDateFormat("EEEE، d MMMM yyyy م", Locale("ar"))
        return sdf.format(Date())
    }
}
