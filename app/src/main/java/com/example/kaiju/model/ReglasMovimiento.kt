package com.example.kaiju.model

object ReglasMovimiento {

    fun validar(
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: String,
        rol: Rol
    ): String? {
        if (!puedeRegistrar(tipo, rol)) {
            return "Tu perfil no permite registrar este movimiento"
        }

        if (cantidad == 0) {
            return "La cantidad debe ser distinta de cero"
        }

        if (cantidad == Int.MIN_VALUE) {
            return "La cantidad está fuera del rango permitido"
        }

        if (tipo != TipoMovimiento.AJUSTE && cantidad < 0) {
            return "La cantidad debe ser mayor que cero"
        }

        if (tipo == TipoMovimiento.AJUSTE && motivo.isBlank()) {
            return "Debes indicar el motivo del ajuste"
        }

        return null
    }

    fun puedeRegistrar(
        tipo: TipoMovimiento,
        rol: Rol
    ): Boolean {
        return when (rol) {
            Rol.ADMINISTRADOR ->
                tipo == TipoMovimiento.ENTRADA ||
                        tipo == TipoMovimiento.AJUSTE

            Rol.ENCARGADO_INVENTARIO -> true

            Rol.VENDEDOR ->
                tipo == TipoMovimiento.SALIDA_VENTA
        }
    }

    fun calcularVariacion(
        tipo: TipoMovimiento,
        cantidad: Int
    ): Int {
        return when (tipo) {
            TipoMovimiento.ENTRADA -> cantidad
            TipoMovimiento.SALIDA_VENTA,
            TipoMovimiento.SALIDA_CONSUMO -> -cantidad
            TipoMovimiento.AJUSTE -> cantidad
        }
    }
}