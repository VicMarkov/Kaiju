package com.example.kaiju.model

data class EasterEggEstado(
    val visible: Boolean = false,
    val cargando: Boolean = false,
    val gato: GatoApi? = null,
    val consejo: ConsejoSalud? = null,
    val error: String? = null
)
