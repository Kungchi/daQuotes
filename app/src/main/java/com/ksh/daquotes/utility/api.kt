package com.ksh.daquotes.utility

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

data class DTO(
    @SerializedName("message") val message: String,
    @SerializedName("author") val author: String,
    @SerializedName("authorProfile") val authorProfile: String

)

interface api {
    @GET("/api/advice")
    fun getQuote(): Call<DTO>
}


