package com.example.kaiju.model

enum class Rol {
    ADMINISTRADOR,
    VENDEDOR,
    ENCARGADO_INVENTARIO
}

data class Usuario(
    val id: String,
    val nombre: String,
    val rol: Rol
)

val Rol.descripcion: String
    get() = when (this) {
        Rol.ADMINISTRADOR -> "Administrador"
        Rol.VENDEDOR -> "Vendedor"
        Rol.ENCARGADO_INVENTARIO -> "Encargado de inventario"
    }
