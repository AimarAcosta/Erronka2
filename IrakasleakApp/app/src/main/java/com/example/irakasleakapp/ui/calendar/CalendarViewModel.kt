package com.example.irakasleakapp.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irakasleakapp.NavigationAcitvity
import com.example.irakasleakapp.data.Horarios
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.data.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _horariosState = MutableStateFlow<List<Horarios>>(emptyList())
    val horariosState: StateFlow<List<Horarios>> = _horariosState

    fun getHorarios(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = apiService.getHorarios(username)
                // LOG CRUCIAL:
                Log.d("javi", "API Response: $result - Count: ${result.size}")

                _horariosState.value = result
            } catch (e: Exception) {
                Log.e("javi", "Error en la petición: ${e.message}")
            }
        }
    }


}





