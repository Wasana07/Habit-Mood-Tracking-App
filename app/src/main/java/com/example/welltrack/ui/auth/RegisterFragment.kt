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
import com.example.welltrack.data.models.User
import com.example.welltrack.data.preferences.SessionManager
import com.example.welltrack.data.preferences.UserPreferences
import com.example.welltrack.databinding.FragmentRegisterBinding
import java.util.UUID

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreferences: UserPreferences
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        userPreferences = UserPreferences(requireContext())
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            if (validateInput()) {
                performRegistration()
            }
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun performRegistration() {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val existingUser = userPreferences.getAllUsers().find { it.email == email }
        if (existingUser != null) {
            Toast.makeText(requireContext(), "An account with this email already exists.", Toast.LENGTH_SHORT).show()
            return
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            password = password
        )

        userPreferences.saveUser(newUser)
        sessionManager.saveUserSession(newUser.id)

        Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
    }

    private fun validateInput(): Boolean {
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}