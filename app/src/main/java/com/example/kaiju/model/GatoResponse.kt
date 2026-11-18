
package com.example.kaiju.model

import com.google.gson.annotations.SerializedName

data class GatoResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("breeds")
    val razas: List<RazaResponse>? = null
)

data class RazaResponse(
    @SerializedName("name")
    val nombre: String? = null,

    @SerializedName("origin")
    val origen: String? = null,

    @SerializedName("temperament")
    val temperamento: String? = null,

    @SerializedName("life_span")
    val esperanzaVida: String? = null
)
