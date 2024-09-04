package com.ksh.daquotes.utility

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

data class DTO(
    @SerializedName("_id") val id: String,
    @SerializedName("content") val content: String,
    @SerializedName("author") val author: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("authorSlug") val authorSlug: String,
    @SerializedName("length") val length: Int,
    @SerializedName("dateAdded") val dateAdded: String,
    @SerializedName("dateModified") val dateModified: String

)

interface api {
    @GET("/quotes/random")
    fun getQuote(): Call<List<DTO>>
}


