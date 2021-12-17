package com.example.rekamsembakominimartanshal.api

import com.google.gson.annotations.SerializedName

data class ResponseBarang(

	@field:SerializedName("daftar")
	val daftar: List<String?>? = null
)
