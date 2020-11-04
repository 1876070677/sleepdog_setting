package com.example.sleepdog.Retrofit


import android.content.ContentValues.TAG
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.GenericDeclaration
data class petInfosetting (
    val Petname : String,
    val Happy : String,
    val Kind : String,
    val Gender : String,
    val Weight : Int
)
//싱글턴-메모리를 하나만 사용
object RetrofitClient {
    //레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit? = null
    //private lateinit var retrofitClient: Retrofit? = null

    //레트로핏 클라이언트 가져오기
    fun getClient(baseUrl:String): Retrofit? {
        Log.d(TAG, "RetrofitClient-getClient() called")

        if(retrofitClient == null){ //레트로핏 클라이언트가 없으면
            //레트로핏 클라이언트 빌더를 통해 인스턴스 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitClient
    }
}