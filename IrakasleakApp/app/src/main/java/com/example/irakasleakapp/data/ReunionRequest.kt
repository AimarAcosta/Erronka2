package com.example.irakasleakapp.data.api.model

data class ReunionPostRequest(
    val titulo: String,
    val asunto: String,
    val aula: Int,
    val fecha: String, // Formato "yyyy-MM-dd HH:mm:ss"
    val alumnoId: Int,
    val profesorId: Int
)