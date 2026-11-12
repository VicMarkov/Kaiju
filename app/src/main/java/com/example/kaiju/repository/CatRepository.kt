package com.example.kaiju.repository

import com.example.kaiju.BuildConfig
import com.example.kaiju.model.GatoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class CatRepository {

    companion object {
        private const val URL_API =
            "https://api.thecatapi.com/v1/images/search" +
                    "?limit=1&has_breeds=1&include_breeds=1&order=RAND"
    }

    suspend fun obtenerGatoAleatorio(): GatoApi =
        withContext(Dispatchers.IO) {

            if (BuildConfig.CAT_API_KEY.isBlank()) {
                throw IllegalStateException(
                    "No se configuró la clave de The Cat API"
                )
            }

            val conexion = URL(URL_API)
                .openConnection() as HttpURLConnection

            try {
                conexion.requestMethod = "GET"
                conexion.connectTimeout = 10000
                conexion.readTimeout = 10000

                conexion.setRequestProperty(
                    "x-api-key",
                    BuildConfig.CAT_API_KEY
                )

                conexion.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val codigoRespuesta = conexion.responseCode

                if (codigoRespuesta !in 200..299) {
                    throw Exception(
                        "Error HTTP: $codigoRespuesta"
                    )
                }

                val respuesta = conexion.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val gatos = JSONArray(respuesta)

                if (gatos.length() == 0) {
                    throw Exception(
                        "La API no devolvió imágenes"
                    )
                }

                val gatoJson = gatos.getJSONObject(0)
                val razas = gatoJson.optJSONArray("breeds")

                val raza = if (
                    razas != null && razas.length() > 0
                ) {
                    razas.getJSONObject(0)
                } else {
                    null
                }

                val imagenUrl = gatoJson.optString("url")

                if (!imagenUrl.startsWith("https://")) {
                    throw Exception(
                        "La API devolvió una URL inválida"
                    )
                }

                GatoApi(
                    id = gatoJson.getString("id"),
                    imagenUrl = imagenUrl,
                    nombreRaza = raza?.optString(
                        "name",
                        "Raza desconocida"
                    ) ?: "Raza desconocida",
                    origen = raza?.optString(
                        "origin",
                        "No disponible"
                    ) ?: "No disponible",
                    temperamento = raza?.optString(
                        "temperament",
                        "No disponible"
                    ) ?: "No disponible",
                    esperanzaVida = raza?.optString(
                        "life_span",
                        "No disponible"
                    ) ?: "No disponible"
                )

            } finally {
                conexion.disconnect()
            }
        }
}
