package com.example.irakasleakapp.ui.searchUsers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.data.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception
@HiltViewModel
class SearchfragmentViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {


    private val _userListState = MutableStateFlow<List<Person>?>(null)

    val userListState: StateFlow<List<Person>?> = _userListState
    fun getUsersSearch() {
        viewModelScope.launch {
            try {

                val response = apiService.getAllUsers()

                if (response.isSuccessful) {
                    // 2. Extraemos la lista del cuerpo de la respuesta
                    val newUsers = response.body() ?: emptyList()

                    // 3. Obtenemos lo que ya teníamos y le sumamos lo nuevo
                    val currentList = _userListState.value ?: emptyList()
                    _userListState.value = currentList + newUsers

                    Log.d("JAVI", "Éxito: Se han añadido ${newUsers.size} usuarios.")
                } else {
                    Log.e("JAVI", "Error en la respuesta: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("JAVI", "Fallo de red o servidor", e)
            }
        }
    }
}