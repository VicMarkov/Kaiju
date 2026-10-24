package com.example.kaiju.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.ui.components.BarraStock
import com.example.kaiju.ui.components.IndicadorStock
import com.example.kaiju.ui.components.formatoPesos
import com.example.kaiju.ui.theme.LocalStockColores
import com.example.kaiju.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleProducto(
    inventarioViewModel: InventarioViewModel,
    codigo: String,
    onVolver: () -> Unit,
    onRegistrarMovimiento: () -> Unit
) {
    LaunchedEffect(codigo) {
        inventarioViewModel.abrirDetalle(codigo)
    }

    val producto by inventarioViewModel.productoDetalle.collectAsState()
    val historial by inventarioViewModel.historial.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(producto?.nombre ?: "Detalle del producto") },
            navigationIcon = {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        )

        val actual = producto
        if (actual == null) {
            Text(
                text = "No se encontró el producto.",
                modifier = Modifier.padding(24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Stock actual",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${actual.stockActual} unidades",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            BarraStock(producto = actual, modifier = Modifier.fillMaxWidth())
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IndicadorStock(producto = actual)
                                Text(
                                    text = "Mínimo requerido: ${actual.stockMinimo}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                            DatoFila("Código", actual.codigo)
                            DatoFila("Categoría", actual.categoria)
                            DatoFila("Tipo de detalle", actual.tipo)
                            DatoFila("Precio", formatoPesos(actual.precio))
                            if (actual.detalle.isNotBlank()) {
                                DatoFila("Detalle", actual.detalle)
                            }
                            if (actual.descripcion.isNotBlank()) {
                                DatoFila("Descripción", actual.descripcion)
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onRegistrarMovimiento,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Registrar movimiento de este producto")
                    }
                }

                item {
                    Text(
                        text = "Historial de movimientos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (historial.isEmpty()) {
                    item {
                        Text(
                            text = "Este producto aún no tiene movimientos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(historial, key = { it.id }) { movimiento ->
                        TarjetaMovimiento(movimiento = movimiento)
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun tituloTipo(tipo: TipoMovimiento): String = when (tipo) {
    TipoMovimiento.ENTRADA -> "Entrada"
    TipoMovimiento.SALIDA_VENTA -> "Salida por venta"
    TipoMovimiento.SALIDA_CONSUMO -> "Salida por consumo"
    TipoMovimiento.AJUSTE -> "Ajuste"
}

@Composable
private fun TarjetaMovimiento(movimiento: Movimiento) {
    val stock = LocalStockColores.current
    val fondo: Color
    val texto: Color
    when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> {
            fondo = stock.ok.fondo
            texto = stock.ok.texto
        }
        TipoMovimiento.SALIDA_VENTA, TipoMovimiento.SALIDA_CONSUMO -> {
            fondo = stock.bajo.fondo
            texto = stock.bajo.texto
        }
        TipoMovimiento.AJUSTE -> {
            fondo = MaterialTheme.colorScheme.surfaceVariant
            texto = MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tituloTipo(movimiento.tipo),
                    style = MaterialTheme.typography.labelLarge,
                    color = texto,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(fondo)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    text = movimiento.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Cantidad: ${movimiento.cantidad}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (movimiento.motivo.isNotBlank()) {
                Text(
                    text = "Motivo: ${movimiento.motivo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Registró: ${movimiento.usuario}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
