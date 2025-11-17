package com.example.welltrack.ui.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.welltrack.R
import com.example.welltrack.data.preferences.HabitPreferences
import com.example.welltrack.databinding.FragmentHabitHistoryBinding
import java.text.SimpleDateFormat
import java.util.*
import com.example.welltrack.data.preferences.SessionManager
class HabitHistoryFragment : Fragment() {

    private var _binding: FragmentHabitHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: HabitHistoryAdapter
    private lateinit var habitPreferences: HabitPreferences
    private val calendar = Calendar.getInstance()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitHistoryBinding.inflate(inflater, container, false)
        habitPreferences = HabitPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCalendar()
        loadHistoryData()
        setupBack()

        binding.btnPreviousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }
        binding.btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HabitHistoryAdapter()
        binding.rvCompletedHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun setupCalendar() {
        updateCalendar()
    }

    private fun updateCalendar() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.tvMonthYear.text = monthFormat.format(calendar.time)
        loadHistoryData()
    }

    private fun loadHistoryData() {
        val yearMonthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val currentYearMonth = yearMonthFormat.format(calendar.time)

        val allHabits = habitPreferences.loadHabits()
        val monthlyHabits = allHabits.filter { habit ->
            habit.userId == sessionManager.getLoggedInUserId()!! && habit.date.startsWith(currentYearMonth)
        }

        val completedMonthlyHabits = monthlyHabits.filter { it.isCompleted }

        historyAdapter.submitList(completedMonthlyHabits)

        val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
        binding.tvCompletedToday.text = "Completed Habits - ${dateFormat.format(calendar.time)}"
    }

    private fun setupBack() {
        binding.imgBackHabitHistory.setOnClickListener {
            findNavController().navigate(R.id.action_habitHistoryFragment_to_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}