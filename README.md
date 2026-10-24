# Kaiju Inventario

Aplicación Android de **control de inventario** para el caso **Kaiju**: catálogo de productos, registro de movimientos (entrada, salida y ajuste), trazabilidad y alertas de stock bajo.

- Asignatura: **DSY1105 · Desarrollo de Aplicaciones Móviles** · Duoc UC · 2026-2
- Base: Guía 10 · *Proyecto Kaiju: base del proyecto y avance*
- Stack: Kotlin · Jetpack Compose · Material3 · MVVM

## Equipo

|---|---|
Vicente Markov
|---|---|

## Descripción de la app

La app permite iniciar sesión con un usuario de prueba (administrador, vendedor o encargado de inventario) y muestra solo las opciones que le corresponden a ese perfil.

- **Catálogo**: lista de productos con buscador por nombre o código, filtro por categoría y marca de stock bajo.
- **Detalle de producto**: datos del producto e historial de movimientos, del más reciente al más antiguo.
- **Formulario de producto** (solo administrador): crear o editar productos con validación campo a campo.
- **Movimiento**: registro de entradas, salidas (venta o consumo) y ajustes de inventario *(formulario completo en la próxima revisión)*.
- **Alertas**: productos con stock igual o menor al stock mínimo; al tocar uno se abre su detalle.

## Estructura

Organización por capas (Guía 10, sección 3), bajo `app/src/main/java/com/example/kaiju/`:

```
MainActivity.kt          // solo llama a AppNavHost()
navigation/              // rutas (sealed) y NavHost con menú inferior
model/                   // Producto, Movimiento, Usuario, estados de formularios
repository/              // InventarioRepository (en memoria) y DatosDePrueba
viewmodel/               // SesionViewModel e InventarioViewModel (reglas RN)
ui/screen/               // las 6 pantallas
ui/components/           // TarjetaProducto e IndicadorStock
ui/theme/                // colores y tipografía
```

Las pantallas solo muestran estado; la validación y las reglas de negocio viven en el `InventarioViewModel`; el `InventarioRepository` es el único que guarda los datos (hoy en memoria; más tarde persistencia local, sin tocar las pantallas).

## Decisiones del equipo (sección 6 de la Guía 10)

Cada decisión debe justificarse con la necesidad del caso.

| Decisión | Decisión tomada | Justificación |
|---|---|---|
| Dos recursos nativos | *Pendiente* | *Pendiente* |
| Animaciones | *Pendiente* | *Pendiente* |
| Un módulo propio | *Pendiente* | *Pendiente* |
| Identidad visual | Paleta **verde bosque + ámbar** en `ui/theme/` (`Color.kt`, esquemas claro y oscuro completos, color dinámico desactivado), tipografía con títulos en negrita (`Type.kt`), formas redondeadas (`Shapes.kt`) y la mascota de `iconokaiju.png` como ícono de launcher e imagen de marca en el login | Verde = inventario saludable; ámbar = acento de alertas/reposición; rojo solo para stock crítico. Paleta fija (no Material You) para que la app tenga la misma identidad en cualquier dispositivo. El ícono se genera adaptativo (API 26+) con fondo verde `#C8E6C9` y PNG legacy para API 24-25, todo desde `iconokaiju.png` |

## Cómo compilar y probar

```powershell
cd Kaiju
.\gradlew.bat assembleDebug     # genera el APK de debug
.\gradlew.bat installDebug      # instala en el emulador/dispositivo conectado
```

## Datos

Todos los productos, usuarios y movimientos son **ficticios** (el caso prohíbe usar datos reales de Kaiju o de personas). Viven en `repository/DatosDePrueba.kt`.
