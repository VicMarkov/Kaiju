package com.example.kaiju.navigation

sealed class Rutas(val ruta: String) {

    data object Login : Rutas("login")

    data object Catalogo : Rutas("catalogo")

    data object Detalle : Rutas("detalle/{codigo}") {
        fun de(codigo: String) = "detalle/$codigo"
    }

    data object Formulario : Rutas("formulario?codigo={codigo}") {
        fun nuevo() = "formulario"
        fun editar(codigo: String) = "formulario?codigo=$codigo"
    }

    data object Movimiento : Rutas("movimiento")

    data object Alertas : Rutas("alertas")
}
