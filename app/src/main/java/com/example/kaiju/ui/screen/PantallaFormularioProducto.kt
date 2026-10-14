package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kaiju.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioProducto(
    inventarioViewModel: InventarioViewModel,
    codigoAEditar: String?,
    onGuardado: () -> Unit,
    onCancelar: () -> Unit
) {
    LaunchedEffect(codigoAEditar) {
        if (codigoAEditar == null) {
            inventarioViewModel.nuevoProducto()
        } else {
            inventarioViewModel.editarProducto(codigoAEditar)
        }
    }

    val formulario by inventarioViewModel.formulario.collectAsState()
    val errores by inventarioViewModel.erroresFormulario.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(if (codigoAEditar == null) "Nuevo producto" else "Editar producto")
            },
            navigationIcon = {
                IconButton(onClick = onCancelar) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Cancelar"
                    )
                }
            },
            windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = formulario.codigo,
                onValueChange = inventarioViewModel::cambiarCodigo,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Código *") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                isError = errores.codigo != null,
                supportingText = { errores.codigo?.let { Text(it) } },
                singleLine = true
            )

            OutlinedTextField(
                value = formulario.nombre,
                onValueChange = inventarioViewModel::cambiarNombre,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre *") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                isError = errores.nombre != null,
                supportingText = { errores.nombre?.let { Text(it) } },
                singleLine = true
            )

            OutlinedTextField(
                value = formulario.descripcion,
                onValueChange = inventarioViewModel::cambiarDescripcion,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción") },
                leadingIcon = { Icon(Icons.Default.Create, contentDescription = null) },
                minLines = 2
            )

            SelectorDeOpcion(
                valor = formulario.categoria,
                etiqueta = "Categoría *",
                icono = Icons.Default.List,
                error = errores.categoria,
                opciones = inventarioViewModel.categoriasProducto,
                onElegir = inventarioViewModel::cambiarCategoria
            )

            SelectorDeOpcion(
                valor = formulario.tipo,
                etiqueta = "Tipo de detalle",
                icono = Icons.Default.Menu,
                error = null,
                opciones = inventarioViewModel.tiposDetalle,
                onElegir = inventarioViewModel::cambiarTipo
            )

            OutlinedTextField(
                value = formulario.precio,
                onValueChange = inventarioViewModel::cambiarPrecio,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Precio (CLP) *") },
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                isError = errores.precio != null,
                supportingText = { errores.precio?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = formulario.stockMinimo,
                onValueChange = inventarioViewModel::cambiarStockMinimo,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Stock mínimo *") },
                leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                isError = errores.stockMinimo != null,
                supportingText = { errores.stockMinimo?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = formulario.detalle,
                onValueChange = inventarioViewModel::cambiarDetalle,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Detalle (talla/color, lote o vencimiento)") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        if (inventarioViewModel.guardarProducto()) onGuardado()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Done, contentDescription = null)
                    Text(" Guardar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorDeOpcion(
    valor: String,
    etiqueta: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    error: String?,
    opciones: List<String>,
    onElegir: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            readOnly = true,
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onElegir(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
