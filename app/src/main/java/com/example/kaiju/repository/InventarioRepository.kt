package com.example.kaiju.repository

import android.content.Context
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object InventarioRepository {

    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var contexto: Context? = null

    private val usuarios = DatosDePrueba.usuarios.toMutableList()
    private val productos = DatosDePrueba.productos.toMutableList()
    private val movimientos = DatosDePrueba.movimientos.toMutableList()

    fun inicializar(contextoAplicacion: Context) {
        val contextoAplicacionLimpia = contextoAplicacion.applicationContext
        contexto = contextoAplicacionLimpia
        val guardado = runBlocking { InventarioDataStore.cargar(contextoAplicacionLimpia) }
        if (guardado != null) {
            productos.clear()
            productos.addAll(guardado.first)
            movimientos.clear()
            movimientos.addAll(guardado.second)
        } else {
            persistir()
        }
    }

    private fun persistir() {
        val ctx = contexto ?: return
        val copiaProductos = productos.toList()
        val copiaMovimientos = movimientos.toList()
        alcance.launch {
            InventarioDataStore.guardar(ctx, copiaProductos, copiaMovimientos)
        }
    }

    fun usuarios(): List<Usuario> = usuarios.toList()

    fun usuario(id: String): Usuario? = usuarios.find { it.id == id }

    fun productos(): List<Producto> = productos.toList()

    fun producto(codigo: String): Producto? = productos.find { it.codigo == codigo }

    fun productosEnAlerta(): List<Producto> = productos.filter { it.estaEnAlerta() }

    fun codigoExiste(codigo: String, codigoOriginal: String? = null): Boolean =
        productos.any { it.codigo == codigo && it.codigo != codigoOriginal }

    fun guardarProducto(producto: Producto) {
        val posicion = productos.indexOfFirst { it.codigo == producto.codigo }
        if (posicion >= 0) {
            productos[posicion] = producto
        } else {
            productos.add(producto)
        }
        persistir()
    }

    fun asignarFoto(codigo: String, ruta: String) {
        val posicion = productos.indexOfFirst { it.codigo == codigo }
        if (posicion >= 0) {
            productos[posicion] = productos[posicion].copy(fotoPath = ruta)
            persistir()
        }
    }

    fun historialDe(codigo: String): List<Movimiento> =
        movimientos.filter { it.codigoProducto == codigo }.reversed()
}
