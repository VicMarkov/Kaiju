
package com.example.kaiju.repository

import com.example.kaiju.BuildConfig
import com.example.kaiju.model.GatoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class CatRepository(
    private val servicio: CatApiService = servicioCompartido,
    private val apiKey: String = BuildConfig.CAT_API_KEY
) {

    companion object {

        private val servicioCompartido: CatApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(CatApiService::class.java)
        }
    }

    suspend fun obtenerGatoAleatorio(): GatoApi {

        if (apiKey.isBlank()) {
            throw IllegalStateException(
                "No se configuró la clave de The Cat API"
            )
        }

        val respuesta = servicio.obtenerGatos(
            apiKey = apiKey
        )

        if (!respuesta.isSuccessful) {
            throw IOException(
                "La API respondió con HTTP ${respuesta.code()}"
            )
        }

        val gato = respuesta.body()
            ?.firstOrNull()
            ?: throw IOException(
                "La API no devolvió fotografías"
            )

        val imagenUrl = gato.url.orEmpty()

        if (!imagenUrl.startsWith("https://")) {
            throw IOException(
                "La fotografía no tiene una URL válida"
            )
        }

        val raza = gato.razas?.firstOrNull()

        return GatoApi(
            id = gato.id.orEmpty(),
            imagenUrl = imagenUrl,
            nombreRaza = raza?.nombre
                ?.takeIf { it.isNotBlank() }
                ?: "Raza desconocida",
            origen = raza?.origen
                ?.takeIf { it.isNotBlank() }
                ?: "No disponible",
            temperamento = raza?.temperamento
                ?.takeIf { it.isNotBlank() }
                ?: "No disponible",
            esperanzaVida = raza?.esperanzaVida
                ?.takeIf { it.isNotBlank() }
                ?: "No disponible"
        )
    }

    // Permite comprobar respuestas de entrada inválida
    suspend fun comprobarIdInvalido(id: String): Int {

        require(id.isNotBlank()) {
            "El identificador no puede estar vacío"
        }

        val respuesta = servicio.obtenerGatoPorId(
            apiKey = apiKey,
            id = id
        )

        return respuesta.code()
    }
}
