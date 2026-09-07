package com.example.ui.quran

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.QuranRepository
import com.example.data.Surah
import com.example.databinding.DialogSurahDetailBinding
import com.example.databinding.FragmentQuranBinding

class QuranFragment : Fragment() {

    private var _binding: FragmentQuranBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SurahAdapter
    private val allSurahs = QuranRepository.surahs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
    }

    private fun setupRecyclerView() {
        adapter = SurahAdapter(allSurahs) { surah ->
            showSurahDetailDialog(surah)
        }
        binding.rvSurahs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSurahs.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchSurah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSurahs(s?.toString()?.trim() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearchSurah.text?.clear()
        }
    }

    private fun filterSurahs(query: String) {
        if (query.isEmpty()) {
            binding.btnClearSearch.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvSurahs.visibility = View.VISIBLE
            binding.tvSurahTotalBadge.text = "${allSurahs.size} سورة"
            adapter.updateList(allSurahs)
            return
        }

        binding.btnClearSearch.visibility = View.VISIBLE
        val normalizedQuery = query.lowercase().replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")

        val filtered = allSurahs.filter { surah ->
            val normalizedArabic = surah.nameArabic.replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")
            normalizedArabic.contains(normalizedQuery, ignoreCase = true) ||
                    surah.nameEnglish.lowercase().contains(normalizedQuery) ||
                    surah.number.toString() == query
        }

        if (filtered.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvSurahs.visibility = View.GONE
            binding.tvSurahTotalBadge.text = "0 سورة"
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvSurahs.visibility = View.VISIBLE
            binding.tvSurahTotalBadge.text = "${filtered.size} سورة"
        }

        adapter.updateList(filtered)
    }

    private fun showSurahDetailDialog(surah: Surah) {
        val dialogBinding = DialogSurahDetailBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.dialogTvSurahNumber.text = surah.number.toString()
        dialogBinding.dialogTvSurahTitle.text = "سورة ${surah.nameArabic}"
        dialogBinding.dialogTvSurahSubtitle.text =
            "${surah.revelationType} • ${surah.versesCount} آيات • الاسم بالإنجليزية: ${surah.nameEnglish}"

        // Bismillah is not recited at the start of Surah At-Tawbah (Surah 9)
        if (surah.number == 9) {
            dialogBinding.dialogTvBismillah.visibility = View.GONE
        } else {
            dialogBinding.dialogTvBismillah.visibility = View.VISIBLE
        }

        if (surah.openingVerses.isNotEmpty()) {
            dialogBinding.dialogTvVersesContent.text = surah.openingVerses
        } else {
            dialogBinding.dialogTvVersesContent.text = "مِنْ آيَاتِ سُورَةِ ${surah.nameArabic} الْمُبَارَكَةِ."
        }

        if (surah.virtue.isNotEmpty()) {
            dialogBinding.dialogTvSurahFadl.visibility = View.VISIBLE
            dialogBinding.dialogTvSurahFadl.text = surah.virtue
        } else {
            dialogBinding.dialogTvSurahFadl.visibility = View.GONE
        }

        dialogBinding.dialogBtnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
