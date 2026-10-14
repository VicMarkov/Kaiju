package com.example.kaiju.repository

import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Usuario

object InventarioRepository {

    private val usuarios = DatosDePrueba.usuarios.toMutableList()
    private val productos = DatosDePrueba.productos.toMutableList()
    private val movimientos = DatosDePrueba.movimientos.toMutableList()

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
    }

    fun historialDe(codigo: String): List<Movimiento> =
        movimientos.filter { it.codigoProducto == codigo }.reversed()
}
