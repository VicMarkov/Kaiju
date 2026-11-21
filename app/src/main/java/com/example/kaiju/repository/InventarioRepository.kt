package com.example.kaiju.repository

import android.content.Context
import com.example.kaiju.database.KaijuDatabase
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
    private var base: KaijuDatabase? = null

    private val usuarios = mutableListOf<Usuario>()
    private val productos = mutableListOf<Producto>()
    private val movimientos = mutableListOf<Movimiento>()

    fun inicializar(contextoAplicacion: Context) {
        val contextoLimpio = contextoAplicacion.applicationContext
        runBlocking(Dispatchers.IO) {
            val db = KaijuDatabase.obtener(contextoLimpio)
            base = db
            if (db.productoDao().contar() == 0) {
                db.usuarioDao().insertarTodos(DatosDePrueba.usuarios)
                db.productoDao().insertarTodos(DatosDePrueba.productos)
                db.movimientoDao().insertarTodos(DatosDePrueba.movimientos)
            }
            usuarios.clear()
            usuarios.addAll(db.usuarioDao().todos())
            productos.clear()
            productos.addAll(db.productoDao().todos())
            movimientos.clear()
            movimientos.addAll(db.movimientoDao().todos())
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
        val db = base ?: return
        alcance.launch { db.productoDao().guardar(producto) }
    }

    fun asignarFoto(codigo: String, ruta: String) {
        val posicion = productos.indexOfFirst { it.codigo == codigo }
        if (posicion >= 0) {
            val actualizado = productos[posicion].copy(fotoPath = ruta)
            productos[posicion] = actualizado
            val db = base ?: return
            alcance.launch { db.productoDao().guardar(actualizado) }
        }
    }

    fun historialDe(codigo: String): List<Movimiento> =
        movimientos.filter { it.codigoProducto == codigo }.reversed()
}
