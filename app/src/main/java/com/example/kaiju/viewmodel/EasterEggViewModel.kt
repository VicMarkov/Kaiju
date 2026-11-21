
package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.ConsejosFelinos
import com.example.kaiju.model.EasterEggEstado
import com.example.kaiju.model.GatoApi
import com.example.kaiju.repository.CatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class EasterEggViewModel(
    private val repository: CatRepository = CatRepository()
) : ViewModel() {

    private var contadorToques = 0

    private var trabajoCarga: Job? = null

    // Caché durante la vida de este ViewModel
    private var ultimoGato: GatoApi? = null

    private val _estado = MutableStateFlow(
        EasterEggEstado()
    )

    val estado = _estado.asStateFlow()

    fun registrarToqueCatalogo() {

        if (_estado.value.visible) return

        contadorToques++

        if (contadorToques >= 10) {
            contadorToques = 0
            abrirEasterEgg()
        }
    }

    private fun abrirEasterEgg() {

        _estado.value = EasterEggEstado(
            visible = true,
            cargando = ultimoGato == null,
            gato = ultimoGato,
            consejo = ConsejosFelinos.obtenerAleatorio()
        )

        // Solo consultar si todavía no tenemos un gato
        if (ultimoGato == null) {
            cargarGato()
        }
    }

    fun cargarGato() {

        if (!_estado.value.visible) return
        if (trabajoCarga?.isActive == true) return

        _estado.value = _estado.value.copy(
            cargando = true,
            error = null,
            gato = null
        )

        trabajoCarga = viewModelScope.launch {

            try {
                val nuevoGato = repository.obtenerGatoAleatorio()

                ultimoGato = nuevoGato

                if (_estado.value.visible) {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        gato = nuevoGato,
                        error = null
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                if (_estado.value.visible) {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        error = when (e) {
                            is IllegalStateException ->
                                "No se configuró la API key."
                            else ->
                                "No se pudo cargar el gato. " +
                                        "Revisa tu conexión e inténtalo otra vez."
                        }
                    )
                }

            } finally {
                trabajoCarga = null
            }
        }
    }

    fun obtenerOtroGato() {
        cargarGato()
    }

    fun cerrarEasterEgg() {

        trabajoCarga?.cancel()
        trabajoCarga = null

        contadorToques = 0

        _estado.value = EasterEggEstado()

        // No borramos ultimoGato para reutilizarlo
    }
}
