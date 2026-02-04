package com.example.irakasleakapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.data.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    // Estado del usuario logueado
    private val _userState = MutableStateFlow<Person?>(null)
    val userState: StateFlow<Person?> = _userState

    // Evento de error único (SharedFlow)
    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun getLogin(email: String, pass: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getUserByUsernameAndPassword(email, pass)
                }

                if (response.isSuccessful) {
                    val p = response.body()
                    if (p != null) {
                        _userState.value = p
                    } else {
                        _userState.value = null
                        _errorEvent.emit("Usuario o contraseña incorrectos")
                    }
                } else {
                    _userState.value = null
                    _errorEvent.emit("Usuario o contraseña incorrectos")
                }

            } catch (e: Exception) {
                _userState.value = null
                _errorEvent.emit("Error de conexión")
                Log.e("HomeViewModel", "Error de conexión", e)
            }
        }
    }
}
