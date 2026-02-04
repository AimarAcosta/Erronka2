// Kotlin
// File: app/src/main/java/com/example/irakasleakapp/NavigationAcitvity.kt
package com.example.irakasleakapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.databinding.ActivityNavigationBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationAcitvity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding
    private lateinit var navController: NavController

    // Hacerla nullable evita excepciones si no se inicializa por alguna razón
    var user: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userJson = intent.getStringExtra("user")
        if (userJson != null) {
            val gson = Gson()
            // Asignar a la propiedad de la clase (no usar "val user" que la sombrea)
            user = gson.fromJson(userJson, Person::class.java)
            user?.let {
                Log.d("Javi", "Usuario recibido: ${it.username}")
                Log.d("Javi", "Usuario recibido: ${it.email}")
                Log.d("Javi", "Usuario recibido: ${it.password}")
                Log.d("Javi", "Usuario recibido: ${it.apellidos}")
                Log.d("Javi", "Usuario recibido: ${it.direccion}")
                Log.d("Javi", "Usuario recibido: ${it.argazkiaUrl}")
            }
        } else {
            Log.d("Javi", "No se recibió usuario")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController

        binding.bottomBar.setupWithNavController(navController)
    }
}
