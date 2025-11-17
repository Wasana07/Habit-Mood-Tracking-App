package com.example.welltrack.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.welltrack.R
import com.example.welltrack.databinding.ItemOnboardingBinding

class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private val onboardingItems = listOf(
        OnboardingItem(
            title = "Track Your Wellness",
            description = "Monitor your daily health activities and achieve your wellness goals.",
            imageRes = R.drawable.ic_onboarding_1
        ),
        OnboardingItem(
            title = "Stay Healthy",
            description = "Get personalized recommendations to improve your health and well-being.",
            imageRes = R.drawable.ic_onboarding_2
        ),
        OnboardingItem(
            title = "Connect with Community",
            description = "Join a community of like-minded individuals on their wellness journey.",
            imageRes = R.drawable.ic_onboarding_3
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int = onboardingItems.size

    inner class OnboardingViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OnboardingItem) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.ivOnboarding.setImageResource(item.imageRes)
        }
    }

    data class OnboardingItem(
        val title: String,
        val description: String,
        val imageRes: Int
    )
}