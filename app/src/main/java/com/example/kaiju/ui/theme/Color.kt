package com.example.kaiju.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val VerdeBosque = Color(0xFF1B5E20)
val VerdeBosqueMedio = Color(0xFF285F2C)
val VerdeBosqueClaro = Color(0xFF81C784)
val VerdeBosqueHondo = Color(0xFF0B3D12)
val VerdeNiebla = Color(0xFFC8E6C9)
val VerdeNieblaHondo = Color(0xFFE8F5E9)

val Ambar = Color(0xFFA25E00)
val AmbarClaro = Color(0xFFFFB74D)
val AmbarNiebla = Color(0xFFFFE7C2)
val AmbarHondo = Color(0xFF3E2A00)
val AmbarFondo = Color(0xFF7A4E00)

val Teal = Color(0xFF00695C)
val TealClaro = Color(0xFF4DB6AC)
val TealNiebla = Color(0xFFB2DFDB)
val TealHondo = Color(0xFF00201B)
val TealFondo = Color(0xFF00504A)

val NeutroClaroFondo = Color(0xFFF7FAF6)
val NeutroClaroSuperficie = Color(0xFFFCFEFB)
val NeutroClaroTexto = Color(0xFF1A1C19)
val NeutroClaroVariant = Color(0xFFE4EDE4)
val NeutroClaroVariantTexto = Color(0xFF414941)
val NeutroClaroBorde = Color(0xFF737973)
val NeutroClaroBordeSuave = Color(0xFFC3C9C3)

val NeutroOscuroFondo = Color(0xFF101410)
val NeutroOscuroSuperficie = Color(0xFF151A15)
val NeutroOscuroTexto = Color(0xFFE2E4E0)
val NeutroOscuroVariant = Color(0xFF202720)
val NeutroOscuroVariantTexto = Color(0xFFC3C9C3)
val NeutroOscuroBorde = Color(0xFF8D938D)
val NeutroOscuroBordeSuave = Color(0xFF414941)

val RojoError = Color(0xFFB3261E)
val RojoErrorOscuro = Color(0xFFFFB4AB)

data class ColorEstadoStock(
    val barra: Color,
    val fondo: Color,
    val texto: Color
)

data class StockColores(
    val ok: ColorEstadoStock,
    val bajo: ColorEstadoStock,
    val critico: ColorEstadoStock
)

val StockClaro = StockColores(
    ok = ColorEstadoStock(
        barra = Color(0xFF2E7D32),
        fondo = Color(0xFFD8EFDA),
        texto = Color(0xFF14532D)
    ),
    bajo = ColorEstadoStock(
        barra = Color(0xFFE08A00),
        fondo = AmbarNiebla,
        texto = AmbarFondo
    ),
    critico = ColorEstadoStock(
        barra = Color(0xFFC62828),
        fondo = Color(0xFFF9DEDC),
        texto = Color(0xFF8C1D18)
    )
)

val StockOscuro = StockColores(
    ok = ColorEstadoStock(
        barra = Color(0xFF66BB6A),
        fondo = Color(0xFF1B3D22),
        texto = Color(0xFFA5D6A7)
    ),
    bajo = ColorEstadoStock(
        barra = Color(0xFFFFA726),
        fondo = Color(0xFF4A3200),
        texto = Color(0xFFFFCC80)
    ),
    critico = ColorEstadoStock(
        barra = Color(0xFFEF5350),
        fondo = Color(0xFF4E1512),
        texto = Color(0xFFFFC9C4)
    )
)

val LocalStockColores = staticCompositionLocalOf { StockClaro }
