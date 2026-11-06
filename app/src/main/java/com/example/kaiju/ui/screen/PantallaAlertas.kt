package com.example.kaiju.ui.screen

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.kaiju.MainActivity
import com.example.kaiju.R
import com.example.kaiju.ui.components.TarjetaProducto
import com.example.kaiju.viewmodel.InventarioViewModel

private const val ID_NOTIFICACION = 1001
private const val CANAL_ALERTAS = "alertas"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAlertas(
    inventarioViewModel: InventarioViewModel,
    onProducto: (String) -> Unit
) {
    val alertas by inventarioViewModel.alertas.collectAsState()
    val contexto = LocalContext.current
    var aviso by remember { mutableStateOf<String?>(null) }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            enviarNotificacion(contexto, alertas.size)
            aviso = "Notificación enviada."
        } else {
            aviso = "Permiso de notificaciones denegado."
        }
    }

    fun avisar() {
        if (alertas.isEmpty()) {
            aviso = "No hay productos en alerta."
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val estado = ContextCompat.checkSelfPermission(
                contexto, Manifest.permission.POST_NOTIFICATIONS
            )
            if (estado != PackageManager.PERMISSION_GRANTED) {
                lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        enviarNotificacion(contexto, alertas.size)
        aviso = "Notificación enviada: ${alertas.size} productos en alerta."
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text("Alertas de stock", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (alertas.isEmpty()) {
                            "Sin productos por debajo del mínimo"
                        } else {
                            "${alertas.size} productos requieren reposición"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = ::avisar) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Avisar por notificación"
                    )
                }
            },
            windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        )

        aviso?.let { mensaje ->
            Text(
                text = mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (alertas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Todo en orden: no hay productos en alerta.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alertas, key = { it.codigo }) { producto ->
                    TarjetaProducto(
                        producto = producto,
                        onClick = { onProducto(producto.codigo) }
                    )
                }
            }
        }
    }
}

private fun enviarNotificacion(contexto: Context, cantidad: Int) {
    val manager = contexto.getSystemService(NotificationManager::class.java) ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            CANAL_ALERTAS,
            "Alertas de stock",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos de productos por debajo del stock mínimo"
        }
        manager.createNotificationChannel(canal)
    }

    val intencion = Intent(contexto, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendiente = android.app.PendingIntent.getActivity(
        contexto, 0, intencion,
        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
    )

    val notificacion = NotificationCompat.Builder(contexto, CANAL_ALERTAS)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Kaiju · Alertas de stock")
        .setContentText("$cantidad productos requieren reposición")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("$cantidad productos están por debajo del stock mínimo. Revisa el catálogo y registra una entrada.")
        )
        .setContentIntent(pendiente)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(contexto).notify(ID_NOTIFICACION, notificacion)
}
