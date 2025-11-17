package com.example.welltrack.ui.habit

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.welltrack.R
import com.example.welltrack.data.models.Habit
import com.example.welltrack.data.preferences.HabitPreferences
import com.example.welltrack.databinding.FragmentAddEditHabitBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import com.example.welltrack.workers.HabitReminderScheduler
import com.example.welltrack.data.preferences.SessionManager
class AddEditHabitFragment : Fragment() {

    private var _binding: FragmentAddEditHabitBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditHabitFragmentArgs by navArgs()
    private lateinit var habitPreferences: HabitPreferences
    private var currentHabit: Habit? = null

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditHabitBinding.inflate(inflater, container, false)
        habitPreferences = HabitPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGoalTypeDropdown()
        setupReminderPicker()
        setupClickListeners()
        setupBack()

        if (!args.habitId.isNullOrEmpty()) {
            currentHabit = habitPreferences.getHabitById(args.habitId!!, sessionManager.getLoggedInUserId()!!)
            currentHabit?.let { habit ->
                populateFields(habit)
                binding.tvTitle.text = "Edit Habit"
                binding.btnDelete.visibility = View.VISIBLE

                binding.tilCurrentProgress.visibility = View.VISIBLE
            }
        } else {
            binding.tvTitle.text = "Add Habit"
            binding.btnDelete.visibility = View.GONE
        }
    }

    private fun setupGoalTypeDropdown() {
        val goalTypes = arrayOf("glasses", "minutes", "steps", "hours", "reps")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, goalTypes)
        binding.actGoalType.setAdapter(adapter)
    }

    private fun setupReminderPicker() {
        binding.tvReminderTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Select Reminder Time")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val selectedHour = timePicker.hour
                val selectedMinute = timePicker.minute
                val time = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }
                binding.tvReminderTime.text = DateFormat.getTimeFormat(requireContext()).format(time.time)
            }
            timePicker.show(parentFragmentManager, "timePicker")
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener { saveHabit() }
        binding.btnDelete.setOnClickListener { deleteHabit() }
    }

    private fun populateFields(habit: Habit) {
        binding.etHabitName.setText(habit.name)
        binding.actGoalType.setText(habit.goalType, false)
        binding.etGoalNumber.setText(habit.goalNumber.toString())
        binding.etCurrentProgress.setText(habit.currentProgress.toString())
        binding.tvReminderTime.text = formatTime(habit.reminderTime)
    }

    private fun saveHabit() {
        val name = binding.etHabitName.text.toString().trim()
        val goalType = binding.actGoalType.text.toString().trim()
        val goalNumberStr = binding.etGoalNumber.text.toString().trim()
        val currentProgressStr = binding.etCurrentProgress.text.toString().trim() // NEW
        val reminderTime = binding.tvReminderTime.text.toString().trim()

        if (name.isEmpty() || goalType.isEmpty() || goalNumberStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val goalNumber = goalNumberStr.toIntOrNull() ?: run {
            Toast.makeText(requireContext(), "Invalid goal number", Toast.LENGTH_SHORT).show()
            return
        }

        val currentProgress = currentProgressStr.toIntOrNull() ?: 0

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val reminderTimeFormatted = timeFormat.format(DateFormat.getTimeFormat(requireContext()).parse(reminderTime) ?: Date())

        val habitToSave = Habit(
            id = currentHabit?.id ?: UUID.randomUUID().toString(),
            userId = sessionManager.getLoggedInUserId()!!,
            name = name,
            goalType = goalType,
            goalNumber = goalNumber,
            currentProgress = currentProgress,
            reminderTime = reminderTimeFormatted,
            date = today,
            creationDate = currentHabit?.creationDate ?: today,
            isCompleted = currentHabit?.isCompleted ?: false
        )

        habitPreferences.addOrUpdateHabit(habitToSave)

        HabitReminderScheduler.schedule(habitToSave.id, habitToSave.name, habitToSave.reminderTime, requireContext())
        Toast.makeText(requireContext(), "Habit Saved!", Toast.LENGTH_SHORT).show()
        setFragmentResult("addEditRequest", Bundle().apply {
            putString("action", if (currentHabit == null) "add" else "edit")
        })
        findNavController().navigateUp()
    }

    private fun deleteHabit() {
        currentHabit?.let { habit ->
            HabitReminderScheduler.cancel(habit.id, requireContext())
            habitPreferences.deleteHabit(habit.id, sessionManager.getLoggedInUserId()!!)
            Toast.makeText(requireContext(), "Habit Deleted!", Toast.LENGTH_SHORT).show()
            setFragmentResult("addEditRequest", Bundle().apply {
                putString("action", "delete")
            })
            findNavController().navigateUp()
        }
    }

    private fun formatTime(time: String): String {
        val parts = time.split(":")
        if (parts.size != 2) return time
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return DateFormat.getTimeFormat(requireContext()).format(calendar.time)
    }

    private fun setupBack() {
        binding.imgBackHabit.setOnClickListener {
            findNavController().navigate(R.id.action_addEditHabitFragment_to_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}