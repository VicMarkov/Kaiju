package com.example.kaiju.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kaiju.model.Rol
import com.example.kaiju.model.Usuario
import com.example.kaiju.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SesionViewModel : ViewModel() {

    val usuarios: List<Usuario> = InventarioRepository.usuarios()

    private val _usuarioActivo = MutableStateFlow<Usuario?>(null)
    val usuarioActivo = _usuarioActivo.asStateFlow()

    private val _usuarioSeleccionado = MutableStateFlow<Usuario?>(null)
    val usuarioSeleccionado = _usuarioSeleccionado.asStateFlow()

    private val _errorSeleccion = MutableStateFlow<String?>(null)
    val errorSeleccion = _errorSeleccion.asStateFlow()

    fun seleccionarUsuario(usuario: Usuario) {
        _usuarioSeleccionado.value = usuario
        _errorSeleccion.value = null
    }

    fun ingresar(): Boolean {
        val seleccionado = _usuarioSeleccionado.value
        if (seleccionado == null) {
            _errorSeleccion.value = "Debes elegir un usuario para ingresar"
            return false
        }
        _usuarioActivo.value = seleccionado
        _errorSeleccion.value = null
        return true
    }

    fun cerrarSesion() {
        _usuarioActivo.value = null
        _usuarioSeleccionado.value = null
        _errorSeleccion.value = null
    }

    fun esAdministrador(): Boolean = _usuarioActivo.value?.rol == Rol.ADMINISTRADOR

    fun puedeVerAlertas(): Boolean =
        _usuarioActivo.value?.rol == Rol.ADMINISTRADOR || _usuarioActivo.value?.rol == Rol.ENCARGADO_INVENTARIO
}
