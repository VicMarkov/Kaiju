package com.example.kaiju.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.descripcion
import com.example.kaiju.ui.theme.LocalStockColores
import com.example.kaiju.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimiento(
    sesionViewModel: SesionViewModel
) {
    val usuarioActivo by sesionViewModel.usuarioActivo.collectAsState()
    val stock = LocalStockColores.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Movimiento", style = MaterialTheme.typography.titleLarge) },
            windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(44.dp)
                )
            }
            Text(
                text = "Registrar movimientos",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Entradas de mercadería, salidas por venta o consumo " +
                    "y ajustes de inventario en un solo lugar.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilaTipo(
                        color = stock.ok.barra,
                        titulo = "Entrada",
                        descripcion = "Llegada de mercadería al inventario"
                    )
                    FilaTipo(
                        color = stock.bajo.barra,
                        titulo = "Salida",
                        descripcion = "Venta o consumo que reduce el stock"
                    )
                    FilaTipo(
                        color = MaterialTheme.colorScheme.outline,
                        titulo = "Ajuste",
                        descripcion = "Corrección de existencias, exige motivo"
                    )
                }
            }

            Text(
                text = "El formulario con sus validaciones (RN-01: una salida nunca " +
                    "deja el stock bajo cero y RN-04: todo ajuste exige motivo) se " +
                    "completa en la próxima revisión de la base.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            usuarioActivo?.let { usuario ->
                Text(
                    text = "Sesión activa: ${usuario.nombre} · ${usuario.rol.descripcion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.size(8.dp))
        }
    }
}

@Composable
private fun FilaTipo(
    color: Color,
    titulo: String,
    descripcion: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
