package com.example.welltrack.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.welltrack.R
import com.example.welltrack.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OnboardingAdapter()
        binding.viewPagerOnboarding.adapter = adapter

        TabLayoutMediator(binding.tabLayoutDots, binding.viewPagerOnboarding) { _, _ -> }.attach()

        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) {
                    binding.btnGetStarted.text = "Get Started"
                } else {
                    binding.btnGetStarted.text = "Next"
                }
            }
        })

        binding.btnGetStarted.setOnClickListener {
            val currentItem = binding.viewPagerOnboarding.currentItem
            if (currentItem < adapter.itemCount - 1) {
                binding.viewPagerOnboarding.currentItem = currentItem + 1
            } else {
                findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}