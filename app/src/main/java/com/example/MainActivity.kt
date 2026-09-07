package com.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.databinding.ActivityMainBinding
import com.example.ui.azkar.AzkarFragment
import com.example.ui.prayer.PrayerTimesFragment
import com.example.ui.quran.QuranFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val prayerTimesFragment by lazy { PrayerTimesFragment() }
    private val azkarFragment by lazy { AzkarFragment() }
    private val quranFragment by lazy { QuranFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(prayerTimesFragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_prayer_times -> {
                    loadFragment(prayerTimesFragment)
                    true
                }
                R.id.nav_azkar_tasbeeh -> {
                    loadFragment(azkarFragment)
                    true
                }
                R.id.nav_quran -> {
                    loadFragment(quranFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
