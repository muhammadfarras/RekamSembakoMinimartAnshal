package com.example.rekamsembakominimartanshal.api

import com.google.gson.annotations.SerializedName

data class ResponseStatus(

	@field:SerializedName("nilai")
	val nilai: Nilai? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Nilai(

	@field:SerializedName("idSembako")
	val idSembako: String? = null,

	@field:SerializedName("niyPegawai")
	val niyPegawai: String? = null,

	@field:SerializedName("statusPengambilan")
	val statusPengambilan: String? = null,

	@field:SerializedName("namaPegawai")
	val namaPegawai: String? = null,

	@field:SerializedName("waktuPengambilan")
	val waktuPengambilan: String? = null
)
