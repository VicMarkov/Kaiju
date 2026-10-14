package com.example.kaiju.model

data class FormularioProductoEstado(
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val tipo: String = "",
    val precio: String = "",
    val stockMinimo: String = "",
    val detalle: String = ""
)

data class FormularioProductoErrores(
    val codigo: String? = null,
    val nombre: String? = null,
    val categoria: String? = null,
    val precio: String? = null,
    val stockMinimo: String? = null
)
