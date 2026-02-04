package com.example.irakasleakapp.data



class Person(
    val username: String,
    val apellidos: String,
    val direccion: String,
    val argazkiaUrl : String,
    val curso  : String,
    val ciclo  : String,
    val password: String,
    val email: String,
    val tipos: TipoDetalle,
    val telefono: String,
    val id: Int
)

data class TipoDetalle(
    val id: Int,
    val name: String
)