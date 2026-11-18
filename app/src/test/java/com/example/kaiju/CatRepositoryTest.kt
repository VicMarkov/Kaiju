
package com.example.kaiju

import com.example.kaiju.model.GatoResponse
import com.example.kaiju.model.RazaResponse
import com.example.kaiju.repository.CatApiService
import com.example.kaiju.repository.CatRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class CatRepositoryTest {

    // Servicio falso que podemos configurar en cada prueba
    private class ServicioFalso(
        private val respuesta: Response<List<GatoResponse>>? = null,
        private val errorConexion: IOException? = null
    ) : CatApiService {

        override suspend fun obtenerGatos(
            apiKey: String,
            limite: Int,
            conRaza: Int,
            incluirRazas: Int
        ): Response<List<GatoResponse>> {

            errorConexion?.let { throw it }

            return respuesta ?: Response.success(emptyList())
        }

        override suspend fun obtenerGatoPorId(
            apiKey: String,
            id: String
        ): Response<GatoResponse> {
            error("No se esperaba consultar un gato por ID")
        }
    }

    @Test
    fun idVacioEsRechazado() = runBlocking {

        val repository = CatRepository(
            servicio = ServicioFalso(),
            apiKey = "CLAVE_DE_PRUEBA"
        )

        try {
            repository.comprobarIdInvalido("")
            fail("Debía rechazar el identificador vacío")

        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("vacío"))
        }
    }

    @Test
    fun consultaExitosaDevuelveGato() = runBlocking {

        val respuestaSimulada = GatoResponse(
            id = "gato123",
            url = "https://ejemplo.com/gato.jpg",
            razas = listOf(
                RazaResponse(
                    nombre = "Siamese",
                    origen = "Thailand",
                    temperamento = "Active, Social",
                    esperanzaVida = "12 - 15"
                )
            )
        )

        val repository = CatRepository(
            servicio = ServicioFalso(
                respuesta = Response.success(
                    listOf(respuestaSimulada)
                )
            ),
            apiKey = "CLAVE_DE_PRUEBA"
        )

        val gato = repository.obtenerGatoAleatorio()

        assertEquals("gato123", gato.id)
        assertEquals(
            "https://ejemplo.com/gato.jpg",
            gato.imagenUrl
        )
        assertEquals("Siamese", gato.nombreRaza)
        assertEquals("Thailand", gato.origen)
        assertEquals("Active, Social", gato.temperamento)
        assertEquals("12 - 15", gato.esperanzaVida)
    }

    @Test
    fun falloConexionEsPropagado() = runBlocking {

        val repository = CatRepository(
            servicio = ServicioFalso(
                errorConexion = IOException("Sin conexión")
            ),
            apiKey = "CLAVE_DE_PRUEBA"
        )

        try {
            repository.obtenerGatoAleatorio()
            fail("Debía ocurrir una falla de conexión")

        } catch (e: IOException) {
            assertEquals("Sin conexión", e.message)
        }
    }

    @Test
    fun respuestaVaciaEsRechazada() = runBlocking {

        val repository = CatRepository(
            servicio = ServicioFalso(
                respuesta = Response.success(emptyList())
            ),
            apiKey = "CLAVE_DE_PRUEBA"
        )

        try {
            repository.obtenerGatoAleatorio()
            fail("Una respuesta vacía debe rechazarse")

        } catch (e: IOException) {
            assertTrue(
                e.message!!.contains("no devolvió fotografías")
            )
        }
    }
}
