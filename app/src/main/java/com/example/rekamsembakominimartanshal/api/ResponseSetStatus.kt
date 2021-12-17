package com.example.rekamsembakominimartanshal.api

import com.google.gson.annotations.SerializedName

data class ResponseSetStatus(

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
