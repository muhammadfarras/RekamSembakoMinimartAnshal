package com.example.rekamsembakominimartanshal.network

import android.content.SharedPreferences
import android.util.Log
import com.example.rekamsembakominimartanshal.data.Access
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import kotlin.jvm.Throws

object ApiService {

    fun create(link : SharedPreferences) : ResponseService {

        val baseUrl = "http://"+link.getString(Access.savedIp,"http://localhost")+"/"
        Log.d("API Service :",baseUrl)
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(ResponseService::class.java)
    }
}