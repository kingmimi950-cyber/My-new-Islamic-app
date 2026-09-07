package com.example.ui.prayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.R
import com.example.data.PrayerTimesHelper
import com.example.databinding.FragmentPrayerTimesBinding
import com.google.android.material.card.MaterialCardView

class PrayerTimesFragment : Fragment() {

    private var _binding: FragmentPrayerTimesBinding? = null
    private val binding get() = _binding!!

    private val countdownHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateNextPrayer()
            countdownHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrayerTimesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHijriDate.text = PrayerTimesHelper.getHijriDateFormatted()
        binding.tvGregorianDate.text = PrayerTimesHelper.getGregorianDateFormatted()

        populatePrayerTimes()
    }

    override fun onResume() {
        super.onResume()
        countdownHandler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        countdownHandler.removeCallbacks(updateRunnable)
    }

    private fun populatePrayerTimes() {
        val prayers = PrayerTimesHelper.getTodayPrayers()
        for (prayer in prayers) {
            when (prayer.nameArabic) {
                "الفجر" -> binding.tvFajrTime.text = prayer.formattedTime
                "الشروق" -> binding.tvSunriseTime.text = prayer.formattedTime
                "الظهر" -> binding.tvDhuhrTime.text = prayer.formattedTime
                "العصر" -> binding.tvAsrTime.text = prayer.formattedTime
                "المغرب" -> binding.tvMaghribTime.text = prayer.formattedTime
                "العشاء" -> binding.tvIshaTime.text = prayer.formattedTime
            }
        }
    }

    private fun updateNextPrayer() {
        if (_binding == null) return

        val nextPrayerInfo = PrayerTimesHelper.getNextPrayer()
        val nextPrayer = nextPrayerInfo.prayer

        binding.tvNextPrayerName.text = "صلاة ${nextPrayer.nameArabic}"
        binding.tvNextPrayerTime.text = nextPrayer.formattedTime
        binding.tvNextPrayerCountdown.text = "متبقي: ${nextPrayerInfo.countdownText}"
        binding.ivNextPrayerIcon.setImageResource(nextPrayer.iconRes)

        highlightNextPrayerCard(nextPrayer.nameArabic)
    }

    private fun highlightNextPrayerCard(nextPrayerName: String) {
        val cards = listOf(
            "الفجر" to binding.cardFajr,
            "الشروق" to binding.cardSunrise,
            "الظهر" to binding.cardDhuhr,
            "العصر" to binding.cardAsr,
            "المغرب" to binding.cardMaghrib,
            "العشاء" to binding.cardIsha
        )

        val activeBg = ContextCompat.getColor(requireContext(), R.color.card_active_bg)
        val normalBg = ContextCompat.getColor(requireContext(), R.color.card_surface)
        val activeStroke = ContextCompat.getColor(requireContext(), R.color.primary_emerald)
        val normalStroke = ContextCompat.getColor(requireContext(), R.color.card_stroke)

        for ((name, card) in cards) {
            if (name == nextPrayerName) {
                card.setCardBackgroundColor(activeBg)
                card.strokeColor = activeStroke
                card.strokeWidth = 3
            } else {
                card.setCardBackgroundColor(normalBg)
                card.strokeColor = normalStroke
                card.strokeWidth = 1
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
