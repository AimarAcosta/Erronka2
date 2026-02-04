package com.example.irakasleakapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.irakasleakapp.R
import com.example.irakasleakapp.NavigationAcitvity
import com.example.irakasleakapp.databinding.ActivityHomeLoginBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeLogin : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeLoginBinding
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarDatosRecordados()

        binding.loginButton.setOnClickListener {
            ejecutarLogin()
        }

        setupObservers()
    }

    private fun cargarDatosRecordados() {
        val prefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("remember_email", "")
        val savedPass = prefs.getString("remember_password", "")
        val isChecked = prefs.getBoolean("remember_checked", false)

        if (isChecked) {
            binding.emailInput.setText(savedEmail)
            binding.passwordInput.setText(savedPass)
            binding.rememberme.isChecked = true
        }
    }

    private fun ejecutarLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            binding.errorTextView.text = "Por favor, completa todos los campos"
            binding.errorTextView.visibility = View.VISIBLE
            return
        }

        // Limpiamos mensaje anterior antes de intentar login
        binding.errorTextView.visibility = View.GONE
        viewModel.getLogin(email, password)
    }

    private fun setupObservers() {
        // 1️⃣ Usuario logueado correctamente
        lifecycleScope.launch {
            viewModel.userState.collect { user ->
                user?.let {
                    gestionarPersistencia()
                    val userJson = gson.toJson(user)
                    navigateToNavigationActivity(userJson)
                }
            }
        }

        // 2️⃣ Errores (Toast + mensaje debajo del EditText)
        viewModel.errorEvent
            .onEach { message ->
                Toast.makeText(this@HomeLogin, message, Toast.LENGTH_SHORT).show()
                binding.errorTextView.text = message
                binding.errorTextView.visibility = View.VISIBLE
            }
            .launchIn(lifecycleScope)
    }

    private fun gestionarPersistencia() {
        val prefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        if (binding.rememberme.isChecked) {
            editor.putString("remember_email", binding.emailInput.text.toString().trim())
            editor.putString("remember_password", binding.passwordInput.text.toString().trim())
            editor.putBoolean("remember_checked", true)
        } else {
            editor.clear()
        }
        editor.apply()
    }

    private fun navigateToNavigationActivity(userJson: String) {
        val intent = Intent(this, NavigationAcitvity::class.java).apply {
            putExtra("user", userJson)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}
