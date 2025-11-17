package com.example.welltrack.ui.habit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.welltrack.R
import com.example.welltrack.data.models.Habit
import com.example.welltrack.databinding.ItemHabitHistoryBinding

class HabitHistoryAdapter : ListAdapter<Habit, HabitHistoryAdapter.HabitHistoryViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHistoryViewHolder {
        val binding = ItemHabitHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HabitHistoryViewHolder(private val binding: ItemHabitHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: Habit) {
            binding.tvHabitName.text = habit.name
            val details = when (habit.goalType) {
                "glasses" -> "${habit.goalNumber}/${habit.goalNumber} Glasses"
                "minutes" -> "${habit.goalNumber} mins"
                "steps" -> "${habit.goalNumber}/${habit.goalNumber} Steps"
                else -> "${habit.goalNumber} ${habit.goalType}"
            }
            binding.tvHabitDetails.text = details

            binding.ivHabitIcon.setImageResource(
                when (habit.name) {
                    "Meditate" -> R.drawable.ic_meditation
                    "Drink Water" -> R.drawable.ic_water
                    "Take 10,000 Steps" -> R.drawable.ic_steps
                    else -> R.drawable.ic_meditation
                }
            )

            val color = when (habit.name) {
                "Meditate" -> R.color.green_light
                "Drink Water" -> R.color.blue_light
                "Take 10,000 Steps" -> R.color.purple_light
                else -> R.color.green_light
            }
            binding.cardHabit.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, color))
        }
    }

    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean = oldItem == newItem
    }
}