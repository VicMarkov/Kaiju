package com.example.kaiju.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto
import com.example.kaiju.ui.theme.ColorEstadoStock
import com.example.kaiju.ui.theme.LocalStockColores

enum class EstadoStock { OK, BAJO, CRITICO }

fun Producto.estadoStock(): EstadoStock = when {
    stockActual <= 0 -> EstadoStock.CRITICO
    estaEnAlerta() -> EstadoStock.BAJO
    else -> EstadoStock.OK
}

@Composable
private fun coloresDeEstado(estado: EstadoStock): ColorEstadoStock {
    val stock = LocalStockColores.current
    return when (estado) {
        EstadoStock.OK -> stock.ok
        EstadoStock.BAJO -> stock.bajo
        EstadoStock.CRITICO -> stock.critico
    }
}

@Composable
fun BarraStock(
    producto: Producto,
    modifier: Modifier = Modifier
) {
    val estado = producto.estadoStock()
    val progreso = when {
        producto.stockActual <= 0 -> 0f
        producto.stockMinimo <= 0 -> 1f
        else -> (producto.stockActual.toFloat() / (producto.stockMinimo * 2)).coerceIn(0f, 1f)
    }

    LinearProgressIndicator(
        progress = { progreso },
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(MaterialTheme.shapes.extraSmall),
        color = coloresDeEstado(estado).barra,
        trackColor = coloresDeEstado(estado).barra.copy(alpha = 0.16f),
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun IndicadorStock(producto: Producto, modifier: Modifier = Modifier) {
    val estado = producto.estadoStock()
    val colores = coloresDeEstado(estado)
    val texto = when (estado) {
        EstadoStock.OK -> "Disponible"
        EstadoStock.BAJO -> "Stock bajo"
        EstadoStock.CRITICO -> "Sin stock"
    }

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(colores.fondo)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (estado == EstadoStock.OK) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = colores.texto,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = colores.texto
        )
    }
}
