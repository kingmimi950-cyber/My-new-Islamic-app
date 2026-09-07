package com.example.ui.quran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.data.Surah
import com.example.databinding.ItemSurahBinding

class SurahAdapter(
    private var surahs: List<Surah>,
    private val onSurahClick: (Surah) -> Unit
) : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    fun updateList(newList: List<Surah>) {
        surahs = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val binding = ItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SurahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        holder.bind(surahs[position])
    }

    override fun getItemCount(): Int = surahs.size

    inner class SurahViewHolder(private val binding: ItemSurahBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(surah: Surah) {
            binding.tvSurahNumber.text = surah.number.toString()
            binding.tvSurahNameArabic.text = "سورة ${surah.nameArabic}"
            binding.tvSurahNameEnglish.text = surah.nameEnglish
            binding.tvSurahVersesCount.text = "${surah.versesCount} آيات"
            binding.tvSurahType.text = surah.revelationType

            binding.cardSurah.setOnClickListener {
                onSurahClick(surah)
            }
        }
    }
}
