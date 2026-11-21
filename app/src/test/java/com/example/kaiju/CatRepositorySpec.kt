package com.example.kaiju

import com.example.kaiju.model.GatoResponse
import com.example.kaiju.model.RazaResponse
import com.example.kaiju.repository.CatApiService
import com.example.kaiju.repository.CatRepository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

import java.io.IOException
import retrofit2.Response

class CatRepositorySpec : FunSpec({

    // Datos de ejemplo para las pruebas.
    // No provienen de internet.
    val gatoSimulado = GatoResponse(
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

    test("consulta exitosa devuelve un gato con sus datos") {

        // Dado: una API simulada con respuesta HTTP 200
        val api = mockk<CatApiService>()

        coEvery {
            api.obtenerGatos(any(), any(), any(), any())
        } returns Response.success(listOf(gatoSimulado))

        val repository = CatRepository(
            servicio = api,
            apiKey = "CLAVE_DE_PRUEBA"
        )

        // Cuando: solicitamos un gato
        val gato = repository.obtenerGatoAleatorio()

        // Entonces: comprobamos los datos
        gato.id shouldBe "gato123"
        gato.nombreRaza shouldBe "Siamese"
        gato.origen shouldBe "Thailand"
        gato.temperamento shouldBe "Active, Social"
        gato.esperanzaVida shouldBe "12 - 15"

        // La consulta se realizó una sola vez
        coVerify(exactly = 1) {
            api.obtenerGatos(any(), any(), any(), any())
        }
    }

    test("un identificador vacio se rechaza sin consultar la API") {

        val api = mockk<CatApiService>()

        val repository = CatRepository(
            servicio = api,
            apiKey = "CLAVE_DE_PRUEBA"
        )

        // Cuando: se utiliza un ID vacío
        shouldThrow<IllegalArgumentException> {
            repository.comprobarIdInvalido("")
        }

        // Entonces: la API nunca fue consultada
        coVerify(exactly = 0) {
            api.obtenerGatoPorId(any(), any())
        }
    }

    test("una falla de conexion produce IOException") {

        val api = mockk<CatApiService>()

        coEvery {
            api.obtenerGatos(any(), any(), any(), any())
        } throws IOException("Sin conexión")

        val repository = CatRepository(
            servicio = api,
            apiKey = "CLAVE_DE_PRUEBA"
        )

        // Cuando: la API falla
        val excepcion = shouldThrow<IOException> {
            repository.obtenerGatoAleatorio()
        }

        // Entonces: se propaga el error
        excepcion.message shouldBe "Sin conexión"

        coVerify(exactly = 1) {
            api.obtenerGatos(any(), any(), any(), any())
        }
    }

    test("una respuesta vacia es rechazada") {

        val api = mockk<CatApiService>()

        coEvery {
            api.obtenerGatos(any(), any(), any(), any())
        } returns Response.success(emptyList())

        val repository = CatRepository(
            servicio = api,
            apiKey = "CLAVE_DE_PRUEBA"
        )

        // Cuando: el servidor responde sin fotografías
        val excepcion = shouldThrow<IOException> {
            repository.obtenerGatoAleatorio()
        }

        // Entonces: se informa el problema
        excepcion.message.orEmpty() shouldContain
                "no devolvió fotografías"

        coVerify(exactly = 1) {
            api.obtenerGatos(any(), any(), any(), any())
        }
    }

    test("una clave vacia evita realizar solicitudes") {

        val api = mockk<CatApiService>()

        val repository = CatRepository(
            servicio = api,
            apiKey = ""
        )

        // Se rechaza antes de utilizar el servicio
        shouldThrow<IllegalStateException> {
            repository.obtenerGatoAleatorio()
        }

        coVerify(exactly = 0) {
            api.obtenerGatos(any(), any(), any(), any())
        }
    }
})
