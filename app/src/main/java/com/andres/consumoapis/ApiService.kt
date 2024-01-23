package com.andres.consumoapis

import android.util.Log
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //Hay que modificar esto

    @GET("search")
    suspend fun getQuery(@Query("q") consulta:String) : Response<METDataResponse>

    @GET("objects/{id}")
    suspend fun getObjectData(@Path("id") id:String) : METItemResponse

/*
    @GET("v1/")
    suspend fun getQuery(@Query("search") consulta: String) : Response<METDataResponse>


    @Query("search")
    suspend fun getQuery(consulta: String): Response<METDataResponse>
*/
}