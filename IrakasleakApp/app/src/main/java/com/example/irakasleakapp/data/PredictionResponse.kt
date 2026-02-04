package com.example.irakasleakapp.data

import com.google.gson.annotations.SerializedName
class PredictionResponse {
    @SerializedName("username") // En minúscula como en Postman
    val username: String = ""

    @SerializedName("nombre") // Añadido porque viene en el JSON
    val nombre: String = ""

    @SerializedName("apellidos") // En minúscula
    val apellidos: String = ""

    @SerializedName("direccion") // En minúscula
    val direccion: String = ""

    @SerializedName("password") // En minúscula
    val password: String = ""

    @SerializedName("email") // En minúscula
    val email: String = ""

    @SerializedName("tipos") // En minúscula
    val tipos: TipoDetalle = TipoDetalle(
        id = 0,
        name = ""
    )



    @SerializedName("argazkiaUrl") // Este está perfecto, es la URL de la foto
    val argazkiaUrl: String = ""

    @SerializedName("telefono1") // En Postman es telefono1, no Telefono
    val telefono: String = ""

    @SerializedName("ciclo")
    val ciclo: String = ""

    @SerializedName("id")
    val id: Int = 0


    @SerializedName("curso")
    val curso: String = ""
}