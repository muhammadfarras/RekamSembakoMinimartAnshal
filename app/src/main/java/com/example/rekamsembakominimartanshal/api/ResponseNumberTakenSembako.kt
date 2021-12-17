package com.example.rekamsembakominimartanshal.api

import com.google.gson.annotations.SerializedName

data class ResponseNumberTakenSembako(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Data(

	@field:SerializedName("Belum")
	val belum: Int? = null,

	@field:SerializedName("Sudah")
	val sudah: Int? = null
)
