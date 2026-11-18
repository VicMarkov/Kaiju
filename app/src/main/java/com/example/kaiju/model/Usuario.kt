package com.example.kaiju.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Rol {
    ADMINISTRADOR,
    VENDEDOR,
    ENCARGADO_INVENTARIO
}

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
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
