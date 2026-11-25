package com.example.kaiju.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Usuario
import androidx.room.Update
import kotlinx.coroutines.flow.Flow



@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY rowid")
    suspend fun todos(): List<Producto>

    @Query("SELECT * FROM productos ORDER BY rowid")
    fun observarTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    suspend fun buscarPorCodigo(codigo: String): Producto?

    @Query("""
        SELECT COUNT(*) FROM productos
        WHERE codigo = :codigo COLLATE NOCASE
    """)
    suspend fun contarPorCodigo(codigo: String): Int

    @Query("""
    UPDATE productos
    SET stockActual = stockActual + :variacion
    WHERE codigo = :codigo
    AND stockActual + :variacion >= 0
""")
    suspend fun modificarStock(
        codigo: String,
        variacion: Int
    ): Int


    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int


    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    fun observarPorCodigo(codigo: String): Flow<Producto?>


    @Insert
    suspend fun insertarTodos(productos: List<Producto>)

    @Insert
    suspend fun insertar(producto: Producto)

    @Update
    suspend fun actualizar(producto: Producto): Int


    @Query("""
        UPDATE productos
        SET fotoPath = :ruta
        WHERE codigo = :codigo
    """)
    suspend fun actualizarFoto(
        codigo: String,
        ruta: String
    ): Int
}


@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY rowid")
    suspend fun todos(): List<Movimiento>

    @Query("SELECT COUNT(*) FROM movimientos")
    suspend fun contar(): Int

    @Query("""
    SELECT * FROM movimientos
    WHERE codigoProducto = :codigo
    ORDER BY rowid DESC
            """)
    fun observarHistorial(codigo: String): Flow<List<Movimiento>>

    @Insert
    suspend fun insertar(movimiento: Movimiento)

    @Insert
    suspend fun insertarTodos(movimientos: List<Movimiento>)
}

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios ORDER BY rowid")
    suspend fun todos(): List<Usuario>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Insert
    suspend fun insertarTodos(usuarios: List<Usuario>)
}
