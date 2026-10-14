package com.example.kaiju.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kaiju.ui.screen.PantallaAlertas
import com.example.kaiju.ui.screen.PantallaCatalogo
import com.example.kaiju.ui.screen.PantallaDetalleProducto
import com.example.kaiju.ui.screen.PantallaFormularioProducto
import com.example.kaiju.ui.screen.PantallaLogin
import com.example.kaiju.ui.screen.PantallaMovimiento
import com.example.kaiju.viewmodel.InventarioViewModel
import com.example.kaiju.viewmodel.SesionViewModel

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel()
    val inventarioViewModel: InventarioViewModel = viewModel()

    val usuarioActivo by sesionViewModel.usuarioActivo.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val destinosMenu = buildList {
        add(Rutas.Catalogo.ruta)
        add(Rutas.Movimiento.ruta)
        if (sesionViewModel.puedeVerAlertas()) add(Rutas.Alertas.ruta)
    }

    fun irA(destino: String) {
        navController.navigate(destino) {
            popUpTo(Rutas.Catalogo.ruta) { inclusive = false }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            if (usuarioActivo != null && rutaActual != null && rutaActual in destinosMenu) {
                NavigationBar {
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Catalogo.ruta,
                        onClick = { irA(Rutas.Catalogo.ruta) },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("Catálogo") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Movimiento.ruta,
                        onClick = { irA(Rutas.Movimiento.ruta) },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text("Movimiento") }
                    )
                    if (sesionViewModel.puedeVerAlertas()) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.Alertas.ruta,
                            onClick = { irA(Rutas.Alertas.ruta) },
                            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                            label = { Text("Alertas") }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Rutas.Login.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Rutas.Login.ruta) {
                PantallaLogin(
                    sesionViewModel = sesionViewModel,
                    onIngresar = {
                        navController.navigate(Rutas.Catalogo.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.Catalogo.ruta) {
                PantallaCatalogo(
                    inventarioViewModel = inventarioViewModel,
                    sesionViewModel = sesionViewModel,
                    onProducto = { codigo -> navController.navigate(Rutas.Detalle.de(codigo)) },
                    onNuevoProducto = { navController.navigate(Rutas.Formulario.nuevo()) },
                    onCerrarSesion = {
                        sesionViewModel.cerrarSesion()
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Catalogo.ruta) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Rutas.Detalle.ruta,
                arguments = listOf(navArgument("codigo") { type = NavType.StringType })
            ) { entrada ->
                val codigo = entrada.arguments?.getString("codigo").orEmpty()
                PantallaDetalleProducto(
                    inventarioViewModel = inventarioViewModel,
                    codigo = codigo,
                    onVolver = { navController.popBackStack() },
                    onRegistrarMovimiento = { irA(Rutas.Movimiento.ruta) }
                )
            }

            composable(
                route = Rutas.Formulario.ruta,
                arguments = listOf(
                    navArgument("codigo") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entrada ->
                val codigo = entrada.arguments?.getString("codigo")
                PantallaFormularioProducto(
                    inventarioViewModel = inventarioViewModel,
                    codigoAEditar = codigo,
                    onGuardado = { navController.popBackStack() },
                    onCancelar = { navController.popBackStack() }
                )
            }

            composable(Rutas.Movimiento.ruta) {
                PantallaMovimiento(sesionViewModel = sesionViewModel)
            }

            composable(Rutas.Alertas.ruta) {
                PantallaAlertas(
                    inventarioViewModel = inventarioViewModel,
                    onProducto = { codigo -> navController.navigate(Rutas.Detalle.de(codigo)) }
                )
            }
        }
    }
}
