package com.example.kaiju.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoMovimiento {
    ENTRADA,
    SALIDA_VENTA,
    SALIDA_CONSUMO,
    AJUSTE
}

@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey
    val id: String,
    val codigoProducto: String,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: String,
    val fecha: String,
    val usuario: String
)
