package com.ksh.daquote.utility

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

// api의 응답을 바탕으로 DTO 설정
data class DTO(
    @SerializedName("message") val message: String,
    @SerializedName("author") val author: String,
    @SerializedName("authorProfile") val authorProfile: String

)

//api를 사용하기위한 인터페이스 설정
interface api {
    @GET("/api/advice")
    fun getQuote(): Call<DTO>
}


