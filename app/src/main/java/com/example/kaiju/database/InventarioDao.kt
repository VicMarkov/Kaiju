package com.example.kaiju.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Usuario

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY rowid")
    suspend fun todos(): List<Producto>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int

    @Insert
    suspend fun insertarTodos(productos: List<Producto>)

    @Upsert
    suspend fun guardar(producto: Producto)
}

@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY rowid")
    suspend fun todos(): List<Movimiento>

    @Query("SELECT COUNT(*) FROM movimientos")
    suspend fun contar(): Int

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
