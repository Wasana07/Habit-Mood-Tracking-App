package com.example.welltrack.ui.mood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.welltrack.data.models.Mood
import com.example.welltrack.databinding.ItemMoodHistoryBinding
import java.text.SimpleDateFormat
import java.util.*
import com.example.welltrack.R

class MoodHistoryAdapter : ListAdapter<Mood, MoodHistoryAdapter.MoodHistoryViewHolder>(MoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodHistoryViewHolder {
        val binding = ItemMoodHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MoodHistoryViewHolder(private val binding: ItemMoodHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mood: Mood) {
            val dateFormat = SimpleDateFormat("MMMM d, yyyy - h:mm a", Locale.getDefault())
            binding.tvDateTime.text = dateFormat.format(Date(mood.timestamp))
            binding.tvMoodType.text = mood.moodType

            val iconRes = when (mood.moodType) {
                "Happy" -> R.drawable.ic_happy
                "Neutral" -> R.drawable.ic_neutral
                "Angry" -> R.drawable.ic_angry
                "Cool" -> R.drawable.ic_cool
                else -> R.drawable.ic_neutral
            }
            binding.ivMoodIcon.setImageResource(iconRes)
        }
    }

    class MoodDiffCallback : DiffUtil.ItemCallback<Mood>() {
        override fun areItemsTheSame(oldItem: Mood, newItem: Mood): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Mood, newItem: Mood): Boolean = oldItem == newItem
    }
}