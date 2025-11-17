package com.example.welltrack.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.welltrack.R
import com.example.welltrack.data.models.Mood
import com.example.welltrack.data.preferences.MoodPreferences
import com.example.welltrack.data.preferences.SessionManager
import com.example.welltrack.databinding.FragmentMoodTrackerBinding
import java.text.SimpleDateFormat
import java.util.*

class MoodTrackerFragment : Fragment() {

    private var _binding: FragmentMoodTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var moodPreferences: MoodPreferences
    private lateinit var moodHistoryAdapter: MoodHistoryAdapter
    private val calendar = Calendar.getInstance()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodTrackerBinding.inflate(inflater, container, false)
        moodPreferences = MoodPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupMoodButtons()
        loadMoodHistory()
        setupBack()
    }

    private fun setupRecyclerView() {
        moodHistoryAdapter = MoodHistoryAdapter()
        binding.rvPastMoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodHistoryAdapter
        }
    }

    private fun setupMoodButtons() {
        val moodButtons = listOf(
            binding.cvHappy to "Happy",
            binding.cvNeutral to "Neutral",
            binding.cvAngry to "Angry",
            binding.cvCool to "Cool"
        )

        moodButtons.forEach { (cardView, moodType) ->
            cardView.setOnClickListener {
                saveCurrentMood(moodType)
            }
        }
    }

    private fun saveCurrentMood(moodType: String) {
        val now = System.currentTimeMillis()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))

        val todayMoods = moodPreferences.getMoodsForDate(today, sessionManager.getLoggedInUserId()!!)
        val existingMood = todayMoods.firstOrNull()

        val moodToSave = Mood(
            id = existingMood?.id ?: UUID.randomUUID().toString(),
            userId = sessionManager.getLoggedInUserId()!!,
            moodType = moodType,
            timestamp = now,
            date = today
        )

        moodPreferences.addOrUpdateMood(moodToSave)
        Toast.makeText(requireContext(), "Mood saved: $moodType", Toast.LENGTH_SHORT).show()

        loadMoodHistory()

        setFragmentResult("mood_saved", Bundle().apply {
            putString("moodType", moodType)
            putLong("timestamp", now)
        })
    }

    private fun loadMoodHistory() {
        val allMoods = moodPreferences.loadMoods()
            .filter { it.userId == sessionManager.getLoggedInUserId()!! }
            .sortedByDescending { it.timestamp }

        moodHistoryAdapter.submitList(allMoods)
    }

    private fun setupBack() {
        binding.imgBackMood.setOnClickListener {
            findNavController().navigate(R.id.action_moodTrackerFragment_to_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}