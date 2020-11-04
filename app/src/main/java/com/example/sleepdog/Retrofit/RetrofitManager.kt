package com.example.sleepdog.Retrofit

import android.content.ContentValues.TAG
import android.util.Log
import com.example.sleepdog.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }
    //레트로핏 인테페이스 가져오기
    private val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    //api호출
    fun getToDo() {
        val call = iRetrofit?.getPetInfo().let{
            it
        }?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager -getToDo - onFailure() called / :t: ${t}")
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - getToDo - onResponse() called / response : ${response} ")
            }
        })
    }

    fun postToDo(pn : String, h : String, k : String, g : String, w : Int) {
        val call = iRetrofit?.postPetInfo(pn, h, k, g, w).let {
            it
        } ?: return

        call.enqueue(object : retrofit2.Callback<petInfosetting> {
            override fun onFailure(call: Call<petInfosetting>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - postToDo - onFailure() called / :t: ${t} ")
            }

            override fun onResponse(
                call: Call<petInfosetting>,
                response: Response<petInfosetting>
            ) {
                Log.d(TAG, "RetrofitManager - postToDo - onResponse() called / response : ${response.body()} ")
            }
        })
    }
}