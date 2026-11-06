package com.example.kaiju.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "inventario")

object InventarioDataStore {

    private val claveProductos = stringPreferencesKey("productos_json")
    private val claveMovimientos = stringPreferencesKey("movimientos_json")

    suspend fun cargar(context: Context): Pair<List<Producto>, List<Movimiento>>? {
        return try {
            val prefs = context.dataStore.data.first()
            val productosJson = prefs[claveProductos] ?: return null
            val movimientosJson = prefs[claveMovimientos] ?: return null
            leerProductos(productosJson) to leerMovimientos(movimientosJson)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun guardar(
        context: Context,
        productos: List<Producto>,
        movimientos: List<Movimiento>
    ) {
        context.dataStore.edit { prefs ->
            prefs[claveProductos] = escribirProductos(productos)
            prefs[claveMovimientos] = escribirMovimientos(movimientos)
        }
    }

    private fun escribirProductos(productos: List<Producto>): String {
        val array = JSONArray()
        productos.forEach { producto ->
            array.put(JSONObject().apply {
                put("codigo", producto.codigo)
                put("nombre", producto.nombre)
                put("descripcion", producto.descripcion)
                put("categoria", producto.categoria)
                put("tipo", producto.tipo)
                put("precio", producto.precio)
                put("stockActual", producto.stockActual)
                put("stockMinimo", producto.stockMinimo)
                put("detalle", producto.detalle)
                put("fotoPath", producto.fotoPath)
            })
        }
        return array.toString()
    }

    private fun leerProductos(json: String): List<Producto> {
        val array = JSONArray(json)
        return (0 until array.length()).map { indice ->
            val objeto = array.getJSONObject(indice)
            Producto(
                codigo = objeto.getString("codigo"),
                nombre = objeto.getString("nombre"),
                descripcion = objeto.getString("descripcion"),
                categoria = objeto.getString("categoria"),
                tipo = objeto.getString("tipo"),
                precio = objeto.getInt("precio"),
                stockActual = objeto.getInt("stockActual"),
                stockMinimo = objeto.getInt("stockMinimo"),
                detalle = objeto.optString("detalle"),
                fotoPath = objeto.optString("fotoPath")
            )
        }
    }

    private fun escribirMovimientos(movimientos: List<Movimiento>): String {
        val array = JSONArray()
        movimientos.forEach { movimiento ->
            array.put(JSONObject().apply {
                put("id", movimiento.id)
                put("codigoProducto", movimiento.codigoProducto)
                put("tipo", movimiento.tipo.name)
                put("cantidad", movimiento.cantidad)
                put("motivo", movimiento.motivo)
                put("fecha", movimiento.fecha)
                put("usuario", movimiento.usuario)
            })
        }
        return array.toString()
    }

    private fun leerMovimientos(json: String): List<Movimiento> {
        val array = JSONArray(json)
        return (0 until array.length()).map { indice ->
            val objeto = array.getJSONObject(indice)
            Movimiento(
                id = objeto.getString("id"),
                codigoProducto = objeto.getString("codigoProducto"),
                tipo = com.example.kaiju.model.TipoMovimiento.valueOf(objeto.getString("tipo")),
                cantidad = objeto.getInt("cantidad"),
                motivo = objeto.getString("motivo"),
                fecha = objeto.getString("fecha"),
                usuario = objeto.getString("usuario")
            )
        }
    }
}
