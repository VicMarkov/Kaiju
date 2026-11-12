
package com.example.kaiju.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.SubcomposeAsyncImage
import com.example.kaiju.model.EasterEggEstado

@Composable
fun DialogoEasterEgg(
    estado: EasterEggEstado,
    onOtroGato: () -> Unit,
    onCerrar: () -> Unit
) {

    if (!estado.visible) return

    Dialog(
        onDismissRequest = onCerrar
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Sorpresa felina 🐱",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "¡Haz encontrado un gato salvaje!",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    HorizontalDivider()

                    // Estado de carga de la API
                    if (estado.cargando) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }

                        Text(
                            text = "Buscando un gato...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Error de conexión
                    else if (estado.error != null) {

                        Text(
                            text = estado.error,
                            color = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "Puedes intentar nuevamente.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Gato recibido desde The Cat API
                    else {

                        estado.gato?.let { gato ->

                            SubcomposeAsyncImage(
                                model = gato.imagenUrl,
                                contentDescription =
                                    "Fotografía de un gato de raza ${gato.nombreRaza}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                },
                                error = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No se pudo cargar la fotografía"
                                        )
                                    }
                                }
                            )

                            Text(
                                text = gato.nombreRaza,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            DatoFelino(
                                titulo = "Origen",
                                contenido = gato.origen
                            )

                            DatoFelino(
                                titulo = "Esperanza de vida",
                                contenido = "${gato.esperanzaVida} años"
                            )

                            DatoFelino(
                                titulo = "Temperamento",
                                contenido = gato.temperamento
                            )
                        }
                    }

                    HorizontalDivider()

                    // Consejo de salud local
                    estado.consejo?.let { consejo ->

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = "Consejo de salud felina",
                                    style = MaterialTheme.typography.titleMedium,
                                    color =
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Text(
                                    text = consejo.titulo,
                                    fontWeight = FontWeight.Bold,
                                    color =
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Text(
                                    text = consejo.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color =
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Botones del diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onOtroGato,
                        enabled = !estado.cargando,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Otro gato")
                    }

                    Button(
                        onClick = onCerrar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoFelino(
    titulo: String,
    contenido: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = contenido,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
