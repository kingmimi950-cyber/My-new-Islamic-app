package com.example.ui.azkar

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.fragment.app.Fragment
import com.example.R
import com.example.data.AzkarCategory
import com.example.data.AzkarRepository
import com.example.databinding.DialogAzkarReaderBinding
import com.example.databinding.FragmentAzkarBinding
import com.google.android.material.chip.Chip

class AzkarFragment : Fragment() {

    private var _binding: FragmentAzkarBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: SharedPreferences
    private var currentCount = 0
    private var totalCount = 0
    private var targetGoal = 33

    private val targets = listOf(33, 100, 1000)
    private var targetIndex = 0

    companion object {
        private const val PREFS_NAME = "tasbeeh_prefs"
        private const val KEY_TOTAL_COUNT = "key_total_count"
        private const val KEY_CURRENT_COUNT = "key_current_count"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAzkarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalCount = prefs.getInt(KEY_TOTAL_COUNT, 0)
        currentCount = prefs.getInt(KEY_CURRENT_COUNT, 0)

        updateDisplay()
        setupListeners()
        setupAzkarCards()
    }

    private fun setupListeners() {
        // Increment Tasbeeh Button
        binding.btnIncrementTasbeeh.setOnClickListener {
            currentCount++
            totalCount++

            // Save to prefs
            prefs.edit()
                .putInt(KEY_CURRENT_COUNT, currentCount)
                .putInt(KEY_TOTAL_COUNT, totalCount)
                .apply()

            // Visual tap feedback
            animateCounterTap()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            updateDisplay()

            // Target reached alert
            if (targetGoal > 0 && currentCount == targetGoal) {
                it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                binding.tvActiveDhikr.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150)
                    .withEndAction {
                        binding.tvActiveDhikr.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                    }.start()
            }
        }

        // Reset Current Counter
        binding.btnResetTasbeeh.setOnClickListener {
            currentCount = 0
            prefs.edit().putInt(KEY_CURRENT_COUNT, 0).apply()
            updateDisplay()
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        // Target Toggle (33 -> 100 -> 1000)
        binding.btnTargetToggle.setOnClickListener {
            targetIndex = (targetIndex + 1) % targets.size
            targetGoal = targets[targetIndex]
            binding.btnTargetToggle.text = "الهدف: $targetGoal"
            binding.tvCounterTarget.text = "الهدف: $targetGoal"
        }

        // Chip selection for Dhikr
        binding.chipGroupDhikr.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds.first())
                val dhikrName = chip?.text?.toString() ?: "سبحان الله"
                binding.tvActiveDhikr.text = getFullDhikrText(dhikrName)
                binding.tvDhikrVirtue.text = getDhikrVirtue(dhikrName)
            }
        }
    }

    private fun animateCounterTap() {
        val anim = ScaleAnimation(
            0.96f, 1.0f,
            0.96f, 1.0f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 100
        }
        binding.btnIncrementTasbeeh.startAnimation(anim)
    }

    private fun updateDisplay() {
        binding.tvCounterValue.text = currentCount.toString()
        binding.tvTotalTasbeeh.text = String.format("%,d", totalCount)
        binding.tvCounterTarget.text = "الهدف: $targetGoal"
        binding.btnTargetToggle.text = "الهدف: $targetGoal"
    }

    private fun getFullDhikrText(chipName: String): String {
        return when (chipName) {
            "سبحان الله" -> "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ"
            "الحمد لله" -> "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ"
            "الله أكبر" -> "اللَّهُ أَكْبَرُ كَبِيرًا"
            "أستغفر الله" -> "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ"
            "اللهم صلِّ على محمد" -> "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ"
            "لا إله إلا الله" -> "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ"
            else -> chipName
        }
    }

    private fun getDhikrVirtue(chipName: String): String {
        return when (chipName) {
            "سبحان الله" -> "فضل الذكر: كلمتان خفيفتان على اللسان ثقيلتان في الميزان"
            "الحمد لله" -> "فضل الذكر: الحمد لله تملأ الميزان"
            "الله أكبر" -> "فضل الذكر: من الباقيات الصالحات وخير مما طلعت عليه الشمس"
            "أستغفر الله" -> "فضل الذكر: من لزم الاستغفار جعل الله له من كل هم فرجاً"
            "اللهم صلِّ على محمد" -> "فضل الذكر: من صلى عليّ صلاة صلى الله عليه بها عشراً"
            "لا إله إلا الله" -> "فضل الذكر: أفضل الذكر لا إله إلا الله"
            else -> "ألا بذكر الله تطمئن القلوب"
        }
    }

    private fun setupAzkarCards() {
        binding.btnReadSabah.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.morningAzkar)
        }
        binding.cardAzkarSabah.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.morningAzkar)
        }

        binding.btnReadMasaa.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.eveningAzkar)
        }
        binding.cardAzkarMasaa.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.eveningAzkar)
        }

        binding.btnReadSalah.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.afterPrayerAzkar)
        }
        binding.cardAzkarSalah.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.afterPrayerAzkar)
        }

        binding.btnReadSleep.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.sleepAzkar)
        }
        binding.cardAzkarSleep.setOnClickListener {
            showAzkarReaderDialog(AzkarRepository.sleepAzkar)
        }
    }

    private fun showAzkarReaderDialog(category: AzkarCategory) {
        val dialogBinding = DialogAzkarReaderBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var currentIndex = 0
        var itemCounter = 0
        val items = category.items

        fun updateItemView() {
            val item = items[currentIndex]
            dialogBinding.dialogAzkarCategoryTitle.text = category.title
            dialogBinding.dialogAzkarProgress.text = "الذكر ${currentIndex + 1} من ${items.size}"
            dialogBinding.dialogAzkarText.text = item.text
            dialogBinding.dialogAzkarBtnCount.text = "التكرار: $itemCounter / ${item.targetCount}"

            dialogBinding.dialogAzkarBtnPrev.isEnabled = currentIndex > 0
            dialogBinding.dialogAzkarBtnNext.isEnabled = currentIndex < items.size - 1
        }

        dialogBinding.dialogAzkarBtnCount.setOnClickListener {
            val item = items[currentIndex]
            if (itemCounter < item.targetCount) {
                itemCounter++
                totalCount++
                prefs.edit().putInt(KEY_TOTAL_COUNT, totalCount).apply()
                updateDisplay()
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
            if (itemCounter >= item.targetCount) {
                dialogBinding.dialogAzkarBtnCount.text = "اكتمل ✓"
                it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                dialogBinding.dialogAzkarBtnCount.text = "التكرار: $itemCounter / ${item.targetCount}"
            }
        }

        dialogBinding.dialogAzkarBtnNext.setOnClickListener {
            if (currentIndex < items.size - 1) {
                currentIndex++
                itemCounter = 0
                updateItemView()
            }
        }

        dialogBinding.dialogAzkarBtnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                itemCounter = 0
                updateItemView()
            }
        }

        dialogBinding.dialogAzkarBtnClose.setOnClickListener {
            dialog.dismiss()
        }

        updateItemView()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
