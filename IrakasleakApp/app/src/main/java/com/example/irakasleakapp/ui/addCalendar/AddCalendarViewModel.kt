package com.example.irakasleakapp.ui.addCalendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irakasleakapp.data.Metting
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.data.api.ApiService
import com.example.irakasleakapp.data.api.model.ReunionPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.http.Body
import javax.inject.Inject

@HiltViewModel
class AddCalendarViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _reunionesState = MutableStateFlow<List<Metting>>(emptyList())
    val reunionesState: StateFlow<List<Metting>> = _reunionesState

    private val _userListState = MutableStateFlow<List<Person>?>(null)
    val userListState: StateFlow<List<Person>?> = _userListState

    fun getReuniones(idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = apiService.getReuniones(idUsuario)
                _reunionesState.value = result
            } catch (e: Exception) {
                Log.e("javi72", "Error GET: ${e.message}")
            }
        }
    }

    fun getUsersSearch() {
        if (!_userListState.value.isNullOrEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getAllUsers()
                if (response.isSuccessful) {
                    _userListState.value = response.body()
                }
            } catch (e: Exception) {
                Log.e("javi72", "Error Users: ${e.message}")
            }
        }
    }



    fun crearNuevaReunion(reunion: ReunionPostRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("javi72", "Enviando al POST: $reunion")

                val response = apiService.crearReunion(reunion)

                if (response.isSuccessful) {
                    Log.d("javi72", "Éxito en el servidor: ${response.code()}")

                    getReuniones(reunion.profesorId)
                } else {
                    Log.e("javi72", "Error del servidor: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                // Esto captura errores de red (sin internet, timeout, etc.)
                Log.e("javi72", "Error de conexión: ${e.message}")
            }
        }
    }

    fun updateReunion(body: Map<String, String>, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.putReunion(body)
                if (response.isSuccessful) {
                    Log.d("javi72", "Actualizado con éxito")

                    getReuniones(userId)
                } else {
                    Log.e("javi72", "Error al actualizar: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("javi72", "Error: ${e.message}")
            }
        }
    }
}
