package com.example.sleepdog.Retrofit

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {
    @GET("/PetinfoAPI/{Petname}/")
    fun getPetInfo(): Call<JsonElement>


    //콜값과 콜백값은 petInfoApi.kt에 data class로 정의 해놓은 형식으로 주고 받는다
    @FormUrlEncoded
    @POST("/PetinfoAPI/")
    fun postPetInfo(
        @Field("Petname") Petname: String,
        @Field("Happy") Happy: String,
        @Field("Kind") Kind: String,
        @Field("Gender") Gender: String,
        @Field("Weight") Weight: Int
    ): Call<petInfosetting>
}