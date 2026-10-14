package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.FormularioProductoErrores
import com.example.kaiju.model.FormularioProductoEstado
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.repository.CATEGORIAS
import com.example.kaiju.repository.InventarioRepository
import com.example.kaiju.repository.TIPOS_DETALLE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class InventarioViewModel : ViewModel() {

    private val _productos = MutableStateFlow(InventarioRepository.productos())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow("Todas")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada.asStateFlow()

    val categorias: List<String> = listOf("Todas") + CATEGORIAS

    val categoriasProducto: List<String> = CATEGORIAS

    val tiposDetalle: List<String> = TIPOS_DETALLE

    val catalogo: StateFlow<List<Producto>> =
        combine(_productos, _busqueda, _categoriaSeleccionada) { productos, busqueda, categoria ->
            productos.filter { producto ->
                val coincideBusqueda = busqueda.isBlank() ||
                    producto.nombre.contains(busqueda, ignoreCase = true) ||
                    producto.codigo.contains(busqueda, ignoreCase = true)
                val coincideCategoria = categoria == "Todas" || producto.categoria == categoria
                coincideBusqueda && coincideCategoria
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val alertas: StateFlow<List<Producto>> = _productos
        .map { lista -> lista.filter { it.estaEnAlerta() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun filtrarPorCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }

    fun productoDe(codigo: String): Producto? = InventarioRepository.producto(codigo)

    private val _productoDetalle = MutableStateFlow<Producto?>(null)
    val productoDetalle: StateFlow<Producto?> = _productoDetalle.asStateFlow()

    private val _historial = MutableStateFlow<List<Movimiento>>(emptyList())
    val historial: StateFlow<List<Movimiento>> = _historial.asStateFlow()

    fun abrirDetalle(codigo: String) {
        _productoDetalle.value = InventarioRepository.producto(codigo)
        _historial.value = InventarioRepository.historialDe(codigo)
    }

    private val _formulario = MutableStateFlow(FormularioProductoEstado())
    val formulario: StateFlow<FormularioProductoEstado> = _formulario.asStateFlow()

    private val _erroresFormulario = MutableStateFlow(FormularioProductoErrores())
    val erroresFormulario: StateFlow<FormularioProductoErrores> = _erroresFormulario.asStateFlow()

    private var codigoEditando: String? = null

    fun nuevoProducto() {
        codigoEditando = null
        _formulario.value = FormularioProductoEstado()
        _erroresFormulario.value = FormularioProductoErrores()
    }

    fun editarProducto(codigo: String) {
        val producto = InventarioRepository.producto(codigo) ?: return
        codigoEditando = producto.codigo
        _erroresFormulario.value = FormularioProductoErrores()
        _formulario.value = FormularioProductoEstado(
            codigo = producto.codigo,
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            categoria = producto.categoria,
            tipo = producto.tipo,
            precio = producto.precio.toString(),
            stockMinimo = producto.stockMinimo.toString(),
            detalle = producto.detalle
        )
    }

    fun cambiarCodigo(valor: String) {
        _formulario.value = _formulario.value.copy(codigo = valor)
        limpiarErrorDe("codigo")
    }

    fun cambiarNombre(valor: String) {
        _formulario.value = _formulario.value.copy(nombre = valor)
        limpiarErrorDe("nombre")
    }

    fun cambiarDescripcion(valor: String) {
        _formulario.value = _formulario.value.copy(descripcion = valor)
    }

    fun cambiarCategoria(valor: String) {
        _formulario.value = _formulario.value.copy(categoria = valor)
        limpiarErrorDe("categoria")
    }

    fun cambiarTipo(valor: String) {
        _formulario.value = _formulario.value.copy(tipo = valor)
    }

    fun cambiarPrecio(valor: String) {
        _formulario.value = _formulario.value.copy(precio = valor)
        limpiarErrorDe("precio")
    }

    fun cambiarStockMinimo(valor: String) {
        _formulario.value = _formulario.value.copy(stockMinimo = valor)
        limpiarErrorDe("stockMinimo")
    }

    fun cambiarDetalle(valor: String) {
        _formulario.value = _formulario.value.copy(detalle = valor)
    }

    private fun limpiarErrorDe(campo: String) {
        val errores = _erroresFormulario.value
        _erroresFormulario.value = when (campo) {
            "codigo" -> errores.copy(codigo = null)
            "nombre" -> errores.copy(nombre = null)
            "categoria" -> errores.copy(categoria = null)
            "precio" -> errores.copy(precio = null)
            "stockMinimo" -> errores.copy(stockMinimo = null)
            else -> errores
        }
    }

    fun guardarProducto(): Boolean {
        val estado = _formulario.value
        val errores = validarFormularioProducto(estado, codigoEditando)
        _erroresFormulario.value = errores
        if (errores != FormularioProductoErrores()) return false

        val original = codigoEditando?.let { InventarioRepository.producto(it) }
        val producto = Producto(
            codigo = estado.codigo.trim(),
            nombre = estado.nombre.trim(),
            descripcion = estado.descripcion.trim(),
            categoria = estado.categoria,
            tipo = estado.tipo.ifBlank { "Sin detalle" },
            precio = estado.precio.trim().toInt(),
            stockActual = original?.stockActual ?: 0,
            stockMinimo = estado.stockMinimo.trim().toInt(),
            detalle = estado.detalle.trim()
        )
        InventarioRepository.guardarProducto(producto)
        _productos.value = InventarioRepository.productos()
        return true
    }

    private fun validarFormularioProducto(
        estado: FormularioProductoEstado,
        codigoOriginal: String?
    ): FormularioProductoErrores {
        val codigo = estado.codigo.trim()

        val errorCodigo = when {
            codigo.isEmpty() -> "El código es obligatorio"
            InventarioRepository.codigoExiste(codigo, codigoOriginal) -> "Ese código ya existe en el catálogo"
            else -> null
        }

        val nombre = estado.nombre.trim()
        val errorNombre = when {
            nombre.isEmpty() -> "El nombre es obligatorio"
            nombre.length < 3 -> "Debe tener al menos 3 caracteres"
            else -> null
        }

        val errorCategoria = if (estado.categoria.isBlank()) "Elige una categoría" else null

        val precio = estado.precio.trim().toIntOrNull()
        val errorPrecio = when {
            estado.precio.isBlank() -> "El precio es obligatorio"
            precio == null -> "Escribe un número entero"
            precio <= 0 -> "Debe ser mayor que cero"
            else -> null
        }

        val stockMinimo = estado.stockMinimo.trim().toIntOrNull()
        val errorStockMinimo = when {
            estado.stockMinimo.isBlank() -> "El stock mínimo es obligatorio"
            stockMinimo == null -> "Escribe un número entero"
            stockMinimo < 0 -> "No puede ser negativo"
            else -> null
        }

        return FormularioProductoErrores(
            codigo = errorCodigo,
            nombre = errorNombre,
            categoria = errorCategoria,
            precio = errorPrecio,
            stockMinimo = errorStockMinimo
        )
    }
}
