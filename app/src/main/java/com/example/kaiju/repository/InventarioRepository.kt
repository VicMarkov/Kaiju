package com.example.kaiju.repository

import android.content.Context
import com.example.kaiju.database.KaijuDatabase
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.Flow
import androidx.room.withTransaction
import com.example.kaiju.model.ReglasMovimiento
import com.example.kaiju.model.Rol
import com.example.kaiju.model.TipoMovimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object InventarioRepository {

    private var base: KaijuDatabase? = null

    private val usuarios = mutableListOf<Usuario>()

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
        }
    }


    fun observarProducto(codigo: String): Flow<Producto?> {
        val db = checkNotNull(base) {
            "La base de datos no está inicializada"
        }

        return db.productoDao().observarPorCodigo(codigo)
    }

    fun observarHistorial(codigo: String): Flow<List<Movimiento>> {
        val db = checkNotNull(base) {
            "La base de datos no está inicializada"
        }

        return db.movimientoDao().observarHistorial(codigo)
    }



    fun observarProductos(): Flow<List<Producto>> {
        val db = checkNotNull(base) {
            "InventarioRepository debe inicializarse antes de observar productos"
        }

        return db.productoDao().observarTodos()
    }


    suspend fun buscarProducto(codigo: String): Producto? {
        val db = checkNotNull(base) {
            "La base de datos no está inicializada"
        }

        return db.productoDao().buscarPorCodigo(codigo)
    }

    suspend fun codigoExiste(codigo: String): Boolean {
        val db = checkNotNull(base) {
            "La base de datos no está inicializada"
        }

        return db.productoDao().contarPorCodigo(codigo) > 0
    }



    fun usuarios(): List<Usuario> = usuarios.toList()

    fun usuario(id: String): Usuario? = usuarios.find { it.id == id }


    suspend fun guardarProducto(
        producto: Producto,
        codigoOriginal: String? = null
    ) {
        val db = checkNotNull(base) {
            "La base de datos no está inicializada"
        }

        val dao = db.productoDao()

        if (codigoOriginal == null) {
            require(!codigoExiste(producto.codigo)) {
                "El código ya existe"
            }

            dao.insertar(producto)
        } else {
            require(producto.codigo == codigoOriginal) {
                "No se permite modificar el código del producto"
            }

            val anterior = dao.buscarPorCodigo(codigoOriginal)
                ?: throw IllegalArgumentException(
                    "El producto no existe"
                )

            val actualizado = producto.copy(
                stockActual = anterior.stockActual,
                fotoPath = anterior.fotoPath
            )

            val filas = dao.actualizar(actualizado)

            check(filas == 1) {
                "No se pudo actualizar el producto"
            }
        }
    }

    suspend fun registrarMovimiento(
        codigoProducto: String,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: String,
        usuario: Usuario
    ) {
        val error = ReglasMovimiento.validar(
            tipo = tipo,
            cantidad = cantidad,
            motivo = motivo,
            rol = usuario.rol
        )

        require(error == null) {
            error ?: "Movimiento inválido"
        }

        val db = checkNotNull(base) {
            "La base de datos no está inicializada"
        }

        val variacion = ReglasMovimiento.calcularVariacion(
            tipo = tipo,
            cantidad = cantidad
        )

        val fecha = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        val movimiento = Movimiento(
            id = UUID.randomUUID().toString(),
            codigoProducto = codigoProducto,
            tipo = tipo,
            cantidad = cantidad,
            motivo = motivo.trim(),
            fecha = fecha,
            usuario = usuario.nombre
        )

        db.withTransaction {
            val filasActualizadas = db.productoDao().modificarStock(
                codigo = codigoProducto,
                variacion = variacion
            )

            require(filasActualizadas == 1) {
                "El producto no existe o el stock es insuficiente"
            }

            db.movimientoDao().insertar(movimiento)
        }
    }

    suspend fun asignarFoto(
        codigo: String,
        ruta: String
    ) {
        val db = checkNotNull(base) {
            "La base de datos no está inicializada"
        }

        val filas = db.productoDao().actualizarFoto(
            codigo = codigo,
            ruta = ruta
        )

        check(filas == 1) {
            "No se pudo guardar la fotografía"
        }
    }
}
