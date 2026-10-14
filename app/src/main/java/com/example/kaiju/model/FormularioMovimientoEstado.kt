package com.example.kaiju.model

data class FormularioMovimientoEstado(
    val codigoProducto: String = "",
    val tipo: String = "",
    val cantidad: String = "",
    val motivo: String = ""
)

data class FormularioMovimientoErrores(
    val codigoProducto: String? = null,
    val tipo: String? = null,
    val cantidad: String? = null,
    val motivo: String? = null
)
