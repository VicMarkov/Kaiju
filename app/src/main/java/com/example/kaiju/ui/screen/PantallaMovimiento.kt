package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.ReglasMovimiento
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.viewmodel.InventarioViewModel
import com.example.kaiju.viewmodel.SesionViewModel

@Composable
fun PantallaMovimiento(
    inventarioViewModel: InventarioViewModel,
    sesionViewModel: SesionViewModel,
    codigoProductoInicial: String? = null,
    onVolver: () -> Unit = {}
) {
    val usuario by sesionViewModel.usuarioActivo.collectAsState()
    val productos by inventarioViewModel.productos.collectAsState()
    val formulario by inventarioViewModel.formularioMovimiento.collectAsState()
    val errores by inventarioViewModel.erroresMovimiento.collectAsState()
    val guardando by inventarioViewModel.guardandoMovimiento.collectAsState()
    val errorGuardado by inventarioViewModel.errorMovimiento.collectAsState()

    var menuProductosAbierto by remember { mutableStateOf(false) }
    var registroExitoso by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(codigoProductoInicial) {
        inventarioViewModel.nuevoMovimiento(
            codigoProducto = codigoProductoInicial.orEmpty()
        )
        registroExitoso = false
    }

    val tiposPermitidos = usuario?.let { usuarioActivo ->
        TipoMovimiento.entries.filter { tipo ->
            ReglasMovimiento.puedeRegistrar(tipo, usuarioActivo.rol)
        }
    }.orEmpty()

    val productoSeleccionado = productos.find {
        it.codigo == formulario.codigoProducto
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (codigoProductoInicial != null) {
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al detalle")
            }
        }

        Text(
            text = "Registrar movimiento",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Selecciona un producto y registra un cambio de inventario.",
            style = MaterialTheme.typography.bodyMedium
        )

        usuario?.let {
            Text(
                text = "Responsable: ${it.nombre}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Producto",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(
                onClick = { menuProductosAbierto = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !guardando && codigoProductoInicial == null
            ) {
                Text(
                    text = productoSeleccionado?.let {
                        "${it.nombre} (${it.codigo})"
                    } ?: "Seleccionar producto"
                )
            }

            DropdownMenu(
                expanded = menuProductosAbierto,
                onDismissRequest = { menuProductosAbierto = false }
            ) {
                productos.forEach { producto ->
                    DropdownMenuItem(
                        text = {
                            Text("${producto.nombre} (${producto.codigo})")
                        },
                        onClick = {
                            inventarioViewModel.cambiarProductoMovimiento(
                                producto.codigo
                            )
                            menuProductosAbierto = false
                            registroExitoso = false
                        }
                    )
                }
            }

            errores.codigoProducto?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            productoSeleccionado?.let {
                Text(
                    text = "Stock disponible: ${it.stockActual}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tipo de movimiento",
                style = MaterialTheme.typography.titleMedium
            )

            tiposPermitidos.forEach { tipo ->
                FilterChip(
                    selected = formulario.tipo == tipo.name,
                    onClick = {
                        inventarioViewModel.cambiarTipoMovimiento(tipo.name)
                        registroExitoso = false
                    },
                    label = {
                        Text(nombreTipoMovimiento(tipo))
                    },
                    enabled = !guardando
                )
            }

            errores.tipo?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        OutlinedTextField(
            value = formulario.cantidad,
            onValueChange = {
                inventarioViewModel.cambiarCantidadMovimiento(it)
                registroExitoso = false
            },
            label = { Text("Cantidad") },
            supportingText = {
                Text(
                    if (formulario.tipo == TipoMovimiento.AJUSTE.name) {
                        "Usa + para agregar o - para descontar unidades"
                    } else {
                        "Ingresa una cantidad mayor que cero"
                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (
                    formulario.tipo == TipoMovimiento.AJUSTE.name
                ) {
                    KeyboardType.Text
                } else {
                    KeyboardType.Number
                }
            ),
            isError = errores.cantidad != null,
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        errores.cantidad?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = formulario.motivo,
            onValueChange = {
                inventarioViewModel.cambiarMotivoMovimiento(it)
                registroExitoso = false
            },
            label = {
                Text(
                    if (formulario.tipo == TipoMovimiento.AJUSTE.name) {
                        "Motivo del ajuste"
                    } else {
                        "Motivo (opcional)"
                    }
                )
            },
            isError = errores.motivo != null,
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        errores.motivo?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        errorGuardado?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (registroExitoso) {
            Text(
                text = "Movimiento registrado correctamente",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                inventarioViewModel.guardarMovimiento(usuario) { exitoso ->
                    if (exitoso) {
                        if (codigoProductoInicial != null) {
                            onVolver()
                        } else {
                            registroExitoso = true
                        }
                    } else {
                        registroExitoso = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !guardando && usuario != null
        ) {
            if (guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(2.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Registrar movimiento")
            }
        }
    }
}

private fun nombreTipoMovimiento(tipo: TipoMovimiento): String {
    return when (tipo) {
        TipoMovimiento.ENTRADA -> "Entrada de mercadería"
        TipoMovimiento.SALIDA_VENTA -> "Salida por venta"
        TipoMovimiento.SALIDA_CONSUMO -> "Salida por consumo"
        TipoMovimiento.AJUSTE -> "Ajuste de inventario"
    }
}