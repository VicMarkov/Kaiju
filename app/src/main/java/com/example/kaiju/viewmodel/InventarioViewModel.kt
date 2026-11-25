package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaiju.model.FormularioProductoErrores
import com.example.kaiju.model.FormularioProductoEstado
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.FormularioMovimientoEstado
import com.example.kaiju.model.FormularioMovimientoErrores
import com.example.kaiju.model.ReglasMovimiento
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.model.Usuario
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class InventarioViewModel : ViewModel() {


    val productos: StateFlow<List<Producto>> =
        InventarioRepository.observarProductos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow("Todas")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada.asStateFlow()

    val categorias: List<String> = listOf("Todas") + CATEGORIAS

    val categoriasProducto: List<String> = CATEGORIAS

    val tiposDetalle: List<String> = TIPOS_DETALLE

    val catalogo: StateFlow<List<Producto>> =
        combine(productos, _busqueda, _categoriaSeleccionada) { lista, busqueda, categoria ->
            lista.filter { producto ->
                val coincideBusqueda = busqueda.isBlank() ||
                    producto.nombre.contains(busqueda, ignoreCase = true) ||
                    producto.codigo.contains(busqueda, ignoreCase = true)
                val coincideCategoria = categoria == "Todas" || producto.categoria == categoria
                coincideBusqueda && coincideCategoria
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val alertas: StateFlow<List<Producto>> = productos
        .map { lista -> lista.filter { it.estaEnAlerta() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun filtrarPorCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }

    fun productoDe(codigo: String): Producto? =
        productos.value.find { it.codigo == codigo }

    private val _productoDetalle = MutableStateFlow<Producto?>(null)
    val productoDetalle: StateFlow<Producto?> = _productoDetalle.asStateFlow()

    private val _historial = MutableStateFlow<List<Movimiento>>(emptyList())
    val historial: StateFlow<List<Movimiento>> = _historial.asStateFlow()

    private var detalleJob: Job? = null


    fun abrirDetalle(codigo: String) {
        detalleJob?.cancel()

        _productoDetalle.value = null
        _historial.value = emptyList()

        detalleJob = viewModelScope.launch {
            launch {
                InventarioRepository.observarProducto(codigo)
                    .collect { producto ->
                        _productoDetalle.value = producto
                    }
            }

            launch {
                InventarioRepository.observarHistorial(codigo)
                    .collect { movimientos ->
                        _historial.value = movimientos
                    }
            }
        }
    }

    fun asignarFoto(
        codigo: String,
        ruta: String
    ) {
        viewModelScope.launch {
            try {
                InventarioRepository.asignarFoto(codigo, ruta)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e(
                    "KaijuInventario",
                    "Error al guardar la fotografía",
                    e
                )
            }
        }
    }



    private val _formulario = MutableStateFlow(FormularioProductoEstado())
    val formulario: StateFlow<FormularioProductoEstado> = _formulario.asStateFlow()

    private val _erroresFormulario = MutableStateFlow(FormularioProductoErrores())
    val erroresFormulario: StateFlow<FormularioProductoErrores> = _erroresFormulario.asStateFlow()

    private val _formularioMovimiento =
        MutableStateFlow(FormularioMovimientoEstado())

    val formularioMovimiento: StateFlow<FormularioMovimientoEstado> =
        _formularioMovimiento.asStateFlow()

    private val _erroresMovimiento =
        MutableStateFlow(FormularioMovimientoErrores())

    val erroresMovimiento: StateFlow<FormularioMovimientoErrores> =
        _erroresMovimiento.asStateFlow()

    private val _guardandoMovimiento = MutableStateFlow(false)

    val guardandoMovimiento: StateFlow<Boolean> =
        _guardandoMovimiento.asStateFlow()

    private val _errorMovimiento = MutableStateFlow<String?>(null)

    val errorMovimiento: StateFlow<String?> =
        _errorMovimiento.asStateFlow()


    private val _guardandoProducto = MutableStateFlow(false)
    val guardandoProducto: StateFlow<Boolean> =
        _guardandoProducto.asStateFlow()

    private val _errorGuardado = MutableStateFlow<String?>(null)
    val errorGuardado: StateFlow<String?> =
        _errorGuardado.asStateFlow()


    private var codigoEditando: String? = null

    fun nuevoProducto() {
        codigoEditando = null
        _formulario.value = FormularioProductoEstado()
        _erroresFormulario.value = FormularioProductoErrores()
        _errorGuardado.value = null
    }


    fun editarProducto(codigo: String) {
        codigoEditando = codigo
        _formulario.value = FormularioProductoEstado()
        _erroresFormulario.value = FormularioProductoErrores()
        _errorGuardado.value = null

        viewModelScope.launch {
            val producto =
                InventarioRepository.buscarProducto(codigo) ?: return@launch

            if (codigoEditando != codigo) return@launch

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


    fun guardarProducto(
        onResultado: (Boolean) -> Unit
    ) {
        // Evitar dos guardados simultáneos
        if (_guardandoProducto.value) return

        val estado = _formulario.value

        val errores = validarFormularioProducto(
            estado,
            codigoEditando
        )

        _erroresFormulario.value = errores
        _errorGuardado.value = null

        // Los errores de formulario siguen mostrándose
        // junto a los campos correspondientes.
        if (errores != FormularioProductoErrores()) {
            onResultado(false)
            return
        }


        val producto = Producto(
            codigo = estado.codigo.trim(),
            nombre = estado.nombre.trim(),
            descripcion = estado.descripcion.trim(),
            categoria = estado.categoria,
            tipo = estado.tipo.ifBlank { "Sin detalle" },
            precio = estado.precio.trim().toInt(),
            stockActual = 0,
            stockMinimo = estado.stockMinimo.trim().toInt(),
            detalle = estado.detalle.trim(),
            fotoPath = ""
        )

        _guardandoProducto.value = true

        viewModelScope.launch {

            try {
                // Esperar el guardado real en Room
                InventarioRepository.guardarProducto(
                    producto = producto,
                    codigoOriginal = codigoEditando
                )

                // La observación de Room actualizará el catálogo automáticamente.
                onResultado(true)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _errorGuardado.value =
                    "No se pudo guardar el producto. " +
                            "Inténtalo nuevamente."

                onResultado(false)

            } finally {
                _guardandoProducto.value = false
            }
        }
    }


    private fun validarFormularioProducto(
        estado: FormularioProductoEstado,
        codigoOriginal: String?
    ): FormularioProductoErrores {
        val codigo = estado.codigo.trim()


        val errorCodigo = when {
            codigo.isEmpty() ->
                "El código es obligatorio"

            codigoOriginal != null && codigo != codigoOriginal ->
                "No se puede modificar el código de un producto"

            productos.value.any {
                it.codigo.equals(codigo, ignoreCase = true) &&
                        it.codigo != codigoOriginal
            } ->
                "Ese código ya existe en el catálogo"

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

    private fun validarFormularioMovimiento(
        estado: FormularioMovimientoEstado,
        usuario: Usuario?
    ): FormularioMovimientoErrores {

        val codigo = estado.codigoProducto.trim()
        val tipo = TipoMovimiento.entries.find {
            it.name == estado.tipo
        }
        val cantidad = estado.cantidad.trim().toIntOrNull()

        val errorProducto = when {
            codigo.isBlank() ->
                "Selecciona un producto"

            else -> null
        }

        val errorTipo = if (tipo == null) {
            "Selecciona un tipo de movimiento"
        } else {
            null
        }

        val errorCantidad = when {
            estado.cantidad.isBlank() ->
                "Ingresa una cantidad"

            cantidad == null ->
                "Ingresa un número entero válido"

            cantidad == 0 ->
                "La cantidad debe ser distinta de cero"

            cantidad == Int.MIN_VALUE ->
                "La cantidad está fuera del rango permitido"

            tipo != null &&
                    tipo != TipoMovimiento.AJUSTE &&
                    cantidad < 0 ->
                "La cantidad debe ser mayor que cero"

            else -> null
        }

        val errorMotivo = if (
            tipo == TipoMovimiento.AJUSTE &&
            estado.motivo.isBlank()
        ) {
            "Debes indicar el motivo del ajuste"
        } else {
            null
        }

        return FormularioMovimientoErrores(
            codigoProducto = errorProducto,
            tipo = errorTipo,
            cantidad = errorCantidad,
            motivo = errorMotivo
        )
    }

    fun guardarMovimiento(
        usuario: Usuario?,
        onResultado: (Boolean) -> Unit
    ) {
        if (_guardandoMovimiento.value) return

        val estado = _formularioMovimiento.value

        _errorMovimiento.value = null

        val errores = validarFormularioMovimiento(
            estado = estado,
            usuario = usuario
        )

        _erroresMovimiento.value = errores

        if (errores != FormularioMovimientoErrores()) {
            onResultado(false)
            return
        }

        if (usuario == null) {
            _errorMovimiento.value = "Debes iniciar sesión"
            onResultado(false)
            return
        }

        val tipo = TipoMovimiento.valueOf(estado.tipo)
        val cantidad = estado.cantidad.trim().toInt()

        val errorReglas = ReglasMovimiento.validar(
            tipo = tipo,
            cantidad = cantidad,
            motivo = estado.motivo,
            rol = usuario.rol
        )

        if (errorReglas != null) {
            _errorMovimiento.value = errorReglas
            onResultado(false)
            return
        }

        _guardandoMovimiento.value = true

        viewModelScope.launch {
            try {
                InventarioRepository.registrarMovimiento(
                    codigoProducto = estado.codigoProducto.trim(),
                    tipo = tipo,
                    cantidad = cantidad,
                    motivo = estado.motivo.trim(),
                    usuario = usuario
                )

                _formularioMovimiento.value =
                    FormularioMovimientoEstado()

                _erroresMovimiento.value =
                    FormularioMovimientoErrores()

                onResultado(true)

            } catch (e: CancellationException) {
                throw e

            } catch (e: IllegalArgumentException) {
                _errorMovimiento.value =
                    e.message ?: "Movimiento inválido"

                onResultado(false)

            } catch (e: Exception) {
                _errorMovimiento.value =
                    "No se pudo registrar el movimiento"

                onResultado(false)

            } finally {
                _guardandoMovimiento.value = false
            }
        }
    }

    fun nuevoMovimiento(codigoProducto: String = "") {
        if (_guardandoMovimiento.value) return

        _formularioMovimiento.value = FormularioMovimientoEstado(
            codigoProducto = codigoProducto
        )
        _erroresMovimiento.value = FormularioMovimientoErrores()
        _errorMovimiento.value = null
    }

    fun cambiarProductoMovimiento(valor: String) {
        _formularioMovimiento.value =
            _formularioMovimiento.value.copy(codigoProducto = valor)

        _erroresMovimiento.value =
            _erroresMovimiento.value.copy(codigoProducto = null)
    }

    fun cambiarTipoMovimiento(valor: String) {
        _formularioMovimiento.value =
            _formularioMovimiento.value.copy(tipo = valor)

        _erroresMovimiento.value =
            _erroresMovimiento.value.copy(tipo = null)

        _errorMovimiento.value = null
    }

    fun cambiarCantidadMovimiento(valor: String) {
        _formularioMovimiento.value =
            _formularioMovimiento.value.copy(cantidad = valor)

        _erroresMovimiento.value =
            _erroresMovimiento.value.copy(cantidad = null)

        _errorMovimiento.value = null
    }

    fun cambiarMotivoMovimiento(valor: String) {
        _formularioMovimiento.value =
            _formularioMovimiento.value.copy(motivo = valor)

        _erroresMovimiento.value =
            _erroresMovimiento.value.copy(motivo = null)

        _errorMovimiento.value = null
    }
}
