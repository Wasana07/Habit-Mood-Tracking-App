package com.example.welltrack.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.welltrack.R
import com.example.welltrack.data.models.Habit
import com.example.welltrack.databinding.ItemHabitBinding

class HabitsAdapter(
    private val onEditHabitClicked: (Habit) -> Unit
) : ListAdapter<Habit, HabitsAdapter.HabitViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HabitViewHolder(private val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.tvHabitName.text = habit.name

            val displayDetails = if (habit.isCompleted) {
                "Completed"
            } else {
                when (habit.goalType) {
                    "glasses" -> "${habit.currentProgress}/${habit.goalNumber} Glasses"
                    "minutes" -> "${habit.currentProgress}/${habit.goalNumber} mins"
                    "steps" -> "${habit.currentProgress}/${habit.goalNumber} Steps"
                    else -> "${habit.currentProgress}/${habit.goalNumber} ${habit.goalType}"
                }
            }

            binding.tvHabitDetails.text = displayDetails

            binding.ivHabitIcon.setImageResource(
                when {
                    habit.name.contains("Meditate", ignoreCase = true) -> R.drawable.ic_meditation
                    habit.name.contains("Drink", ignoreCase = true) -> R.drawable.ic_water
                    habit.name.contains("Step", ignoreCase = true) -> R.drawable.ic_steps
                    habit.name.contains("Walk", ignoreCase = true) -> R.drawable.ic_steps
                    habit.name.contains("Exercise", ignoreCase = true) -> R.drawable.ic_exercise
                    else -> R.drawable.ic_meditation
                }
            )

            if (habit.isCompleted) {
                binding.ivStatus.setImageResource(R.drawable.ic_check_circle)
                binding.ivStatus.visibility = View.VISIBLE
            } else {
                binding.ivStatus.visibility = View.GONE
            }

            val color = if (habit.isCompleted) {
                ContextCompat.getColor(binding.root.context, R.color.green_light)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.white)
            }
            binding.root.setCardBackgroundColor(color)

            binding.root.setOnClickListener {
                onEditHabitClicked(habit)
            }
        }
    }

    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}