package com.example.welltrack.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.welltrack.R
import com.example.welltrack.data.preferences.HabitPreferences
import com.example.welltrack.data.preferences.MoodPreferences
import com.example.welltrack.data.preferences.SessionManager
import com.example.welltrack.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitPreferences: HabitPreferences
    private lateinit var moodPreferences: MoodPreferences
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        habitPreferences = HabitPreferences(requireContext())
        moodPreferences = MoodPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWeeklyHabitChart()
        setupMoodDistributionChart()
        setupBack()
    }

    private fun setupWeeklyHabitChart() {
        val barChart: BarChart = binding.barChartWeeklyHabits
        val lastSevenDays = getLastSevenDays()
        val barEntries = mutableListOf<BarEntry>()

        val userId = sessionManager.getLoggedInUserId() ?: return

        lastSevenDays.forEachIndexed { index, date ->
            val habitsOnDate = habitPreferences.loadHabits()
                .filter { it.userId == userId && it.date == date }
            val completedCount = habitsOnDate.count { it.isCompleted }
            barEntries.add(BarEntry(index.toFloat(), completedCount.toFloat()))
        }

        val dataSet = BarDataSet(barEntries, "Completed Habits").apply {
            color = Color.parseColor("#4CAF50")
            setDrawValues(true)
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)
        barChart.data = barData

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)

        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            valueFormatter = IndexAxisValueFormatter(lastSevenDays.map { getDayAbbreviation(it) })
        }

        barChart.axisLeft.apply {
            setDrawGridLines(true)
            axisMinimum = 0f
        }
        barChart.axisRight.isEnabled = false

        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun setupMoodDistributionChart() {
        val pieChart: PieChart = binding.pieChartMoodDistribution
        val userId = sessionManager.getLoggedInUserId() ?: return

        val allMoods = moodPreferences.loadMoods().filter { it.userId == userId }

        if (allMoods.isEmpty()) {
            pieChart.setNoDataText("No mood data available")
            return
        }

        val moodCounts = allMoods.groupingBy { it.moodType }.eachCount()
        val pieEntries = moodCounts.map { (mood, count) ->
            PieEntry(count.toFloat(), mood)
        }

        val dataSet = PieDataSet(pieEntries, "").apply {
            colors = listOf(
                Color.parseColor("#FFD54F"), // Happy
                Color.parseColor("#FFEE58"), // Neutral
                Color.parseColor("#FF7043"), // Angry
                Color.parseColor("#4FC3F7")  // Cool
            )
            setDrawValues(true)
            valueTextSize = 12f
            valueTextColor = Color.BLACK
        }

        val pieData = PieData(dataSet)
        pieChart.data = pieData

        pieChart.description.isEnabled = false
        pieChart.centerText = "Mood Distribution"
        pieChart.setCenterTextSize(16f)
        pieChart.legend.isEnabled = true
        pieChart.legend.orientation =
            com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
        pieChart.legend.setDrawInside(false)

        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun getLastSevenDays(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 6 downTo 0) {
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        return dates.reversed()
    }

    private fun getDayAbbreviation(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        return dayFormat.format(date ?: Date())
    }

    private fun setupBack() {
        binding.imgBackHabitStatistics.setOnClickListener {
            findNavController().navigate(R.id.action_statisticsFragment_to_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
