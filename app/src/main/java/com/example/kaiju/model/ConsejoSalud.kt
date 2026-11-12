
package com.example.kaiju.model

data class ConsejoSalud(
    val titulo: String,
    val descripcion: String
)

object ConsejosFelinos {

    val consejos = listOf(
        ConsejoSalud(
            titulo = "Hidratación",
            descripcion = "Mantén agua fresca y limpia disponible todos los días."
        ),
        ConsejoSalud(
            titulo = "Alimentación",
            descripcion = "Ofrece alimento adecuado para la edad y necesidades de tu gato."
        ),
        ConsejoSalud(
            titulo = "Actividad física",
            descripcion = "Dedica tiempo diario al juego para estimular su actividad y bienestar."
        ),
        ConsejoSalud(
            titulo = "Higiene",
            descripcion = "Limpia regularmente su caja de arena y mantenla en un lugar tranquilo."
        ),
        ConsejoSalud(
            titulo = "Controles veterinarios",
            descripcion = "Mantén sus vacunas, controles y cuidados preventivos según indicación veterinaria."
        ),
        ConsejoSalud(
            titulo = "Descanso",
            descripcion = "Proporciona lugares seguros, cómodos y tranquilos para descansar."
        )
    )

    fun obtenerAleatorio(): ConsejoSalud {
        return consejos.random()
    }
}
