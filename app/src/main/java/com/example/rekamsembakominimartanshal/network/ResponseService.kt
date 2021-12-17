package com.example.rekamsembakominimartanshal.network

import com.example.rekamsembakominimartanshal.api.ResponseBarang
import com.example.rekamsembakominimartanshal.api.ResponseNumberTakenSembako
import com.example.rekamsembakominimartanshal.api.ResponseSetStatus
import com.example.rekamsembakominimartanshal.api.ResponseStatus
import retrofit2.Call
import retrofit2.http.*

interface ResponseService {
    @GET("sembako/api/getStatus/")
    fun getStatusSembako(@Query("id_sembako") id_sembako: String?) : Call<ResponseStatus>

    @GET("sembako/api/getBarang")
    fun getBarangSembako(@Query("id_sembako") id_sembako: String?) : Call<ResponseBarang>


    @GET("sembako/api/getNumberSembakoTaken")
    fun getNumberTakenSembako(@Query("kode_sembako") kode_sembako: String?) : Call<ResponseNumberTakenSembako>

    @FormUrlEncoded
    @POST("sembako/api/setStatus/index.php")
    fun setStatusSembako(@Field("id_sembako") id_sembako: String?) : Call<ResponseSetStatus>

}