package com.example.welltrack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.welltrack.R
import com.example.welltrack.data.models.Habit
import com.example.welltrack.data.preferences.HabitPreferences
import com.example.welltrack.data.preferences.SessionManager
import com.example.welltrack.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var habitPreferences: HabitPreferences
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        habitPreferences = HabitPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateTime()
        setupRecyclerView()
        loadHabits()
        setupLogout()

        setFragmentResultListener("addEditRequest") { _, bundle ->
            when (bundle.getString("action")) {
                "add", "edit", "delete" -> loadHabits()
            }
        }

        binding.footerView.cvAddHabits.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addEditHabitFragment)
        }

        binding.footerView.cvHabitHistory.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_habitHistoryFragment)
        }

        binding.footerView.cvStatistics.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_statisticsFragment)
        }

        binding.footerView.cvMood.setOnClickListener {
            findNavController().navigate(R.id.action_to_mood_tracker_from_dashboard)
        }
    }

    private fun setupDateTime() {
        val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        binding.tvDate.text = currentDate
        binding.tvTime.text = currentTime
    }

    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter { habit ->
            onEditHabitClicked(habit)
        }
        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }
    }

    /**
     * Handles the click event on a habit item.
     * Navigates to the Add/Edit screen for the selected habit.
     */
    private fun onEditHabitClicked(habit: Habit) {
        val action = DashboardFragmentDirections.actionDashboardFragmentToAddEditHabitFragment(habitId = habit.id)
        findNavController().navigate(action)
    }

    private fun loadHabits() {
        val userId = sessionManager.getLoggedInUserId()!!
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val allHabits = habitPreferences.loadHabits()
        val todayHabits = allHabits.filter { it.userId == userId && it.date == today }

        habitsAdapter.submitList(todayHabits)

        var totalProgressPercentage = 0.0

        if (todayHabits.isNotEmpty()) {
            for (habit in todayHabits) {
                val individualProgress = (habit.currentProgress.toFloat() / habit.goalNumber) * 100
                val cappedProgress = minOf(individualProgress, 100f)

                totalProgressPercentage += cappedProgress
            }
            totalProgressPercentage /= todayHabits.size
        }

        val completionPercentage = totalProgressPercentage.toInt()


        binding.tvCompletion.text = "$completionPercentage%"
        binding.progressBar.setProgressWithAnimation(completionPercentage.toFloat(), 1000)
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            findNavController().navigate(R.id.action_dashboardFragment_to_loginFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}