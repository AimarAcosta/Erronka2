package com.example.irakasleakapp.data
data class Metting(
    val estado: String,
    val alumno: String,
    val profesor: String,
    val fecha: String,    // Enviaremos la fecha real calculada: "2025-10-12 10:00:00"
    val id: Int
)