package com.example.kaiju.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Rol
import com.example.kaiju.ui.components.TarjetaProducto
import com.example.kaiju.viewmodel.InventarioViewModel
import com.example.kaiju.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    inventarioViewModel: InventarioViewModel,
    sesionViewModel: SesionViewModel,
    onProducto: (String) -> Unit,
    onNuevoProducto: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val productosTotales by inventarioViewModel.productos.collectAsState()
    val productos by inventarioViewModel.catalogo.collectAsState()
    val busqueda by inventarioViewModel.busqueda.collectAsState()
    val categoria by inventarioViewModel.categoriaSeleccionada.collectAsState()
    val usuarioActivo by sesionViewModel.usuarioActivo.collectAsState()

    val hayFiltros = busqueda.isNotBlank() || categoria != "Todas"
    val subtitulo = when {
        hayFiltros -> "${productos.size} de ${productosTotales.size} productos"
        else -> "${productosTotales.size} productos en catálogo"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text("Catálogo", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = subtitulo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
            )

            OutlinedTextField(
                value = busqueda,
                onValueChange = inventarioViewModel::buscar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("Buscar por nombre o código") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (busqueda.isNotBlank()) {
                        IconButton(onClick = { inventarioViewModel.buscar("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                shape = MaterialTheme.shapes.small,
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inventarioViewModel.categorias) { opcion ->
                        FilterChip(
                            selected = categoria == opcion,
                            onClick = { inventarioViewModel.filtrarPorCategoria(opcion) },
                            label = { Text(opcion) },
                            leadingIcon = if (categoria == opcion) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                null
                            }
                        )
                    }
                }
                if (hayFiltros) {
                    TextButton(onClick = {
                        inventarioViewModel.buscar("")
                        inventarioViewModel.filtrarPorCategoria("Todas")
                    }) {
                        Text("Limpiar")
                    }
                }
            }

            if (productos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "No hay productos que coincidan con la búsqueda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (hayFiltros) {
                            TextButton(onClick = {
                                inventarioViewModel.buscar("")
                                inventarioViewModel.filtrarPorCategoria("Todas")
                            }) {
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos, key = { it.codigo }) { producto ->
                        TarjetaProducto(
                            producto = producto,
                            onClick = { onProducto(producto.codigo) }
                        )
                    }
                }
            }
        }

        if (usuarioActivo?.rol == Rol.ADMINISTRADOR) {
            ExtendedFloatingActionButton(
                onClick = onNuevoProducto,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text("Nuevo") }
            )
        }
    }
}
