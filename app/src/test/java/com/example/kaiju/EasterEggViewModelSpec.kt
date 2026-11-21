package com.example.kaiju

import com.example.kaiju.model.GatoApi
import com.example.kaiju.repository.CatRepository
import com.example.kaiju.viewmodel.EasterEggViewModel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class EasterEggViewModelSpec : FunSpec({

    lateinit var dispatcher: TestDispatcher

    // Sustituir el hilo principal de Android
    // antes de cada prueba.
    beforeTest {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    // Restaurar el hilo principal al terminar.
    afterTest {
        Dispatchers.resetMain()
    }

    val gatoSimulado = GatoApi(
        id = "gato123",
        imagenUrl = "https://ejemplo.com/gato.jpg",
        nombreRaza = "Siamese",
        origen = "Thailand",
        temperamento = "Active, Social",
        esperanzaVida = "12 - 15"
    )

    test("nueve pulsaciones no activan el Easter Egg") {
        runTest(dispatcher) {

            val repository = mockk<CatRepository>()
            val vm = EasterEggViewModel(repository)

            repeat(9) {
                vm.registrarToqueCatalogo()
            }

            vm.estado.value.visible shouldBe false
            vm.estado.value.cargando shouldBe false

            coVerify(exactly = 0) {
                repository.obtenerGatoAleatorio()
            }
        }
    }

    test("diez pulsaciones activan el dialogo y cargan el gato") {
        runTest(dispatcher) {

            val repository = mockk<CatRepository>()

            coEvery {
                repository.obtenerGatoAleatorio()
            } returns gatoSimulado

            val vm = EasterEggViewModel(repository)

            repeat(10) {
                vm.registrarToqueCatalogo()
            }

            // Estado inicial, antes de ejecutar
            // la corrutina de carga.
            vm.estado.value.visible shouldBe true
            vm.estado.value.cargando shouldBe true
            vm.estado.value.gato shouldBe null

            // Ejecutar la corrutina pendiente.
            advanceUntilIdle()

            // Estado final.
            vm.estado.value.cargando shouldBe false
            vm.estado.value.gato shouldBe gatoSimulado
            vm.estado.value.error shouldBe null

            coVerify(exactly = 1) {
                repository.obtenerGatoAleatorio()
            }
        }
    }

    test("una falla de conexion muestra un error") {
        runTest(dispatcher) {

            val repository = mockk<CatRepository>()

            coEvery {
                repository.obtenerGatoAleatorio()
            } throws IOException("Sin internet")

            val vm = EasterEggViewModel(repository)

            repeat(10) {
                vm.registrarToqueCatalogo()
            }

            advanceUntilIdle()

            vm.estado.value.visible shouldBe true
            vm.estado.value.cargando shouldBe false
            vm.estado.value.gato shouldBe null

            vm.estado.value.error.orEmpty() shouldContain
                    "Revisa tu conexión"

            coVerify(exactly = 1) {
                repository.obtenerGatoAleatorio()
            }
        }
    }

    test("despues de un error el reintento funciona") {
        runTest(dispatcher) {

            val repository = mockk<CatRepository>()

            coEvery {
                repository.obtenerGatoAleatorio()
            } throws IOException("Sin internet") andThen gatoSimulado

            val vm = EasterEggViewModel(repository)

            repeat(10) {
                vm.registrarToqueCatalogo()
            }

            // Primera solicitud: falla.
            advanceUntilIdle()

            vm.estado.value.error shouldBe
                    "No se pudo cargar el gato. " +
                    "Revisa tu conexión e inténtalo otra vez."

            // Segunda solicitud: funciona.
            vm.obtenerOtroGato()

            advanceUntilIdle()

            vm.estado.value.error shouldBe null
            vm.estado.value.gato shouldBe gatoSimulado
            vm.estado.value.cargando shouldBe false

            coVerify(exactly = 2) {
                repository.obtenerGatoAleatorio()
            }
        }
    }

    test("la cache evita repetir solicitudes al reabrir") {
        runTest(dispatcher) {

            val repository = mockk<CatRepository>()

            coEvery {
                repository.obtenerGatoAleatorio()
            } returns gatoSimulado

            val vm = EasterEggViewModel(repository)

            // Primera apertura.
            repeat(10) {
                vm.registrarToqueCatalogo()
            }

            advanceUntilIdle()

            vm.estado.value.gato shouldBe gatoSimulado

            // Cerrar la ventana.
            vm.cerrarEasterEgg()

            vm.estado.value.visible shouldBe false

            // Volver a abrir con diez pulsaciones.
            repeat(10) {
                vm.registrarToqueCatalogo()
            }

            advanceUntilIdle()

            vm.estado.value.visible shouldBe true
            vm.estado.value.gato shouldBe gatoSimulado
            vm.estado.value.cargando shouldBe false

            // La API solo debió consultarse una vez.
            coVerify(exactly = 1) {
                repository.obtenerGatoAleatorio()
            }
        }
    }

    test("otro gato realiza una nueva solicitud") {
        runTest(dispatcher) {

            val repository = mockk<CatRepository>()

            val segundoGato = gatoSimulado.copy(
                id = "gato456",
                nombreRaza = "Bengal"
            )

            coEvery {
                repository.obtenerGatoAleatorio()
            } returns gatoSimulado andThen segundoGato

            val vm = EasterEggViewModel(repository)

            repeat(10) {
                vm.registrarToqueCatalogo()
            }

            advanceUntilIdle()

            vm.estado.value.gato?.id shouldBe "gato123"

            vm.obtenerOtroGato()

            advanceUntilIdle()

            vm.estado.value.gato?.id shouldBe "gato456"
            vm.estado.value.gato?.nombreRaza shouldBe "Bengal"

            coVerify(exactly = 2) {
                repository.obtenerGatoAleatorio()
            }
        }
    }

    test("cerrar durante la carga oculta el dialogo") {
        runTest(dispatcher) {

            val repository = mockk<CatRepository>()

            coEvery {
                repository.obtenerGatoAleatorio()
            } returns gatoSimulado

            val vm = EasterEggViewModel(repository)

            repeat(10) {
                vm.registrarToqueCatalogo()
            }

            // No avanzamos el dispatcher:
            // la carga sigue pendiente.
            vm.estado.value.visible shouldBe true
            vm.estado.value.cargando shouldBe true

            vm.cerrarEasterEgg()

            advanceUntilIdle()

            vm.estado.value.visible shouldBe false
            vm.estado.value.gato shouldBe null
            vm.estado.value.cargando shouldBe false

            // La carga fue cancelada antes
            // de ejecutarse la consulta.
            coVerify(exactly = 0) {
                repository.obtenerGatoAleatorio()
            }
        }
    }
})
