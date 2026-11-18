
package com.example.kaiju.repository

import com.example.kaiju.model.GatoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CatApiService {

    @GET("v1/images/search")
    suspend fun obtenerGatos(
        @Header("x-api-key") apiKey: String,
        @Query("limit") limite: Int = 1,
        @Query("has_breeds") conRaza: Int = 1,
        @Query("include_breeds") incluirRazas: Int = 1
    ): Response<List<GatoResponse>>

    @GET("v1/images/{id}")
    suspend fun obtenerGatoPorId(
        @Header("x-api-key") apiKey: String,
        @Path("id") id: String
    ): Response<GatoResponse>
}
