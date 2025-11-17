package com.example.welltrack.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.welltrack.R
import com.example.welltrack.data.preferences.SessionManager
import com.example.welltrack.data.preferences.UserPreferences
import com.example.welltrack.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreferences: UserPreferences
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        userPreferences = UserPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                performLogin()
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val user = userPreferences.login(email, password)
        if (user != null) {
            sessionManager.saveUserSession(user.id)
            Toast.makeText(requireContext(), "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
        } else {
            Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}