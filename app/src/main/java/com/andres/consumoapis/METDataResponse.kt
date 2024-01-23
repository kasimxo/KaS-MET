package com.andres.consumoapis

import com.google.gson.annotations.SerializedName

data class METDataResponse (
    @SerializedName("total") val total: String,
    @SerializedName("objectIDs") val ids: List<String>
)

data class METItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("primaryImage") val imageBig: String,
    @SerializedName("primaryImageSmall") val imageSmall: String,
    @SerializedName("title") val title: String,
    @SerializedName("medium") val medio: String,
    @SerializedName("artistDisplayName") val autor: String,
    @SerializedName("objectDate") val fecha: String,
    @SerializedName("additionalImages") val imagenesAlternativas: List<String>


)


