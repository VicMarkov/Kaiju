package com.example.kaiju.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Rol
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.model.Usuario

class Convertidores {

    @TypeConverter
    fun desdeTipoMovimiento(valor: TipoMovimiento): String = valor.name

    @TypeConverter
    fun haciaTipoMovimiento(valor: String): TipoMovimiento =
        TipoMovimiento.valueOf(valor)

    @TypeConverter
    fun desdeRol(valor: Rol): String = valor.name

    @TypeConverter
    fun haciaRol(valor: String): Rol = Rol.valueOf(valor)
}

@Database(
    entities = [Producto::class, Movimiento::class, Usuario::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertidores::class)
abstract class KaijuDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var instancia: KaijuDatabase? = null

        fun obtener(contexto: Context): KaijuDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    KaijuDatabase::class.java,
                    "kaiju.db"
                ).build().also { instancia = it }
            }
        }
    }
}
