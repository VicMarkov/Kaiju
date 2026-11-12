package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.ConsejosFelinos
import com.example.kaiju.model.EasterEggEstado
import com.example.kaiju.repository.CatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EasterEggViewModel : ViewModel() {

    private val repository = CatRepository()

    private var contadorToques = 0

    private var trabajoCarga: Job? = null

    private val _estado = MutableStateFlow(
        EasterEggEstado()
    )

    val estado: StateFlow<EasterEggEstado> =
        _estado.asStateFlow()

    // Registra cada pulsación del botón Catálogo
    fun registrarToqueCatalogo() {

        // No contar mientras la ventana esté abierta
        if (_estado.value.visible) return

        contadorToques++

        if (contadorToques >= 10) {
            contadorToques = 0
            abrirEasterEgg()
        }
    }

    // Abre la ventana y solicita un gato
    private fun abrirEasterEgg() {

        val consejo = ConsejosFelinos.obtenerAleatorio()

        _estado.value = EasterEggEstado(
            visible = true,
            cargando = true,
            consejo = consejo
        )

        cargarGato()
    }

    // Obtiene una imagen y datos de raza desde la API
    fun cargarGato() {

        // Evita solicitudes simultáneas
        if (trabajoCarga?.isActive == true) return

        _estado.value = _estado.value.copy(
            cargando = true,
            error = null,
            gato = null
        )

        trabajoCarga = viewModelScope.launch {

            try {
                val gatoObtenido =
                    repository.obtenerGatoAleatorio()

                if (_estado.value.visible) {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        gato = gatoObtenido,
                        error = null
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                if (_estado.value.visible) {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        error = "No se pudo cargar el gato. " +
                                "Comprueba tu conexión a internet."
                    )
                }
            }
        }
    }

    // Permite solicitar otra fotografía
    fun obtenerOtroGato() {
        if (!_estado.value.visible) return

        cargarGato()
    }

    // Cierra la ventana y cancela una carga pendiente
    fun cerrarEasterEgg() {

        trabajoCarga?.cancel()
        trabajoCarga = null

        contadorToques = 0

        _estado.value = EasterEggEstado()
    }
}
