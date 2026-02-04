package com.example.irakasleakapp.data.api

import com.example.irakasleakapp.data.Horarios
import com.example.irakasleakapp.data.Metting
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.data.api.model.ReunionPostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/android/login/{usuario}/{password}")
    suspend fun getUserByUsernameAndPassword(
        @Path("usuario") user: String,
        @Path("password") pass: String
    ): Response<Person>

    @GET("api/android/usuarios")
    suspend fun getAllUsers(): Response<List<Person>>

    @GET("api/android/horario/{username}")
    suspend fun getHorarios(
        @Path("username") username: String
    ): List<Horarios>

    @GET("api/android/reuniones/{id}")
    suspend fun getReuniones(
        @Path("id") idUsuario: Int
    ): List<Metting>

    @POST("api/android/reuniones/crear")
    suspend fun crearReunion(
        @Body reunion: ReunionPostRequest
    ): Response<Unit>

    @POST("api/android/reuniones/estado")
    suspend fun putReunion(
        @Body body: Map<String, String>
    ): Response<Unit>
}