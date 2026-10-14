package com.example.kaiju.repository

import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Rol
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.model.Usuario

val CATEGORIAS = listOf("Ropa", "Calzado", "Accesorio", "Alimentos", "Bebidas")

val TIPOS_DETALLE = listOf("Talla y color", "Lote", "Fecha de vencimiento", "Sin detalle")

object DatosDePrueba {

    val usuarios = listOf(
        Usuario("U-01", "Camila Fuentes", Rol.ADMINISTRADOR),
        Usuario("U-02", "Diego Muñoz", Rol.VENDEDOR),
        Usuario("U-03", "Fernanda Soto", Rol.ENCARGADO_INVENTARIO)
    )

    val productos = listOf(
        Producto("P-001", "Polera básica", "Polera de algodón para uso diario", "Ropa", "Talla y color", 9990, 25, 10, "M · Azul"),
        Producto("P-002", "Pantalón mezclilla", "Pantalón clásico de mezclilla", "Ropa", "Talla y color", 19990, 8, 10, "40 · Azul"),
        Producto("P-003", "Zapatillas running", "Zapatillas livianas para correr", "Calzado", "Talla y color", 29990, 12, 5, "42 · Negro"),
        Producto("P-004", "Casco de seguridad", "Casco certificado para trabajar en bodega", "Accesorio", "Sin detalle", 12990, 4, 6, ""),
        Producto("P-005", "Agua mineral 1 L", "Agua mineral sin gas", "Bebidas", "Fecha de vencimiento", 1290, 120, 30, "31/12/2026"),
        Producto("P-006", "Café en grano 500 g", "Café tostado de origen nacional", "Alimentos", "Lote", 7490, 18, 8, "L-2026-04"),
        Producto("P-007", "Barra de cereal", "Barra de cereal con frutos secos", "Alimentos", "Fecha de vencimiento", 1990, 30, 40, "15/11/2026"),
        Producto("P-008", "Guantes de trabajo", "Guantes de nitrilo para bodega", "Accesorio", "Talla y color", 4990, 45, 15, "L · Blanco"),
        Producto("P-009", "Botella térmica 750 ml", "Botella de acero inoxidable", "Accesorio", "Sin detalle", 14990, 6, 6, ""),
        Producto("P-010", "Polera manga larga", "Polera manga larga unisex", "Ropa", "Talla y color", 12990, 40, 12, "S · Verde"),
        Producto("P-011", "Jugo de naranja 1 L", "Jugo de naranja sin azúcar", "Bebidas", "Fecha de vencimiento", 2490, 50, 20, "31/01/2027")
    )

    val movimientos = listOf(
        Movimiento("M-001", "P-001", TipoMovimiento.ENTRADA, 20, "Reposición semanal", "06/10/2026 10:15", "Fernanda Soto"),
        Movimiento("M-002", "P-001", TipoMovimiento.SALIDA_VENTA, 5, "Venta de mostrador", "08/10/2026 16:40", "Diego Muñoz"),
        Movimiento("M-003", "P-005", TipoMovimiento.ENTRADA, 100, "Llegada de mercadería", "07/10/2026 09:05", "Fernanda Soto"),
        Movimiento("M-004", "P-007", TipoMovimiento.SALIDA_CONSUMO, 4, "Uso interno en oficina", "09/10/2026 11:20", "Fernanda Soto"),
        Movimiento("M-005", "P-004", TipoMovimiento.AJUSTE, 2, "Dos unidades dañadas en bodega", "10/10/2026 08:50", "Camila Fuentes")
    )
}
