package com.example.rekamsembakominimartanshal

import android.app.Application
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rekamsembakominimartanshal.adapter.ListDaftarSembakoAdapter
import com.example.rekamsembakominimartanshal.api.ResponseBarang
import com.example.rekamsembakominimartanshal.api.ResponseSetStatus
import com.example.rekamsembakominimartanshal.data.Access
import com.example.rekamsembakominimartanshal.databinding.ActivityCheckSembakoBinding
import com.example.rekamsembakominimartanshal.network.ApiService
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class CheckSembakoActivity : AppCompatActivity() {

    companion object {
        var isChecked = 0
        var totalData = 0
    }

    lateinit var listRecyclerView: RecyclerView
    lateinit var binding : ActivityCheckSembakoBinding
    lateinit var myContext : Context

    fun restartCheckedItem () {
        isChecked = 0
        totalData = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Ketika onCreate selalu noll kan companionObjectnya
        restartCheckedItem ()

        binding = ActivityCheckSembakoBinding.inflate(layoutInflater)
        myContext = this
        setContentView(binding.root)

        var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)



        val idSembako = intent.extras?.getString(Access.idSembako)
        val namaPegawai = intent.extras?.getString(Access.namaPegawai)
        val statusPengambilan = intent.extras?.getString(Access.statusPengambilan)
        val niyPegawai = intent.extras?.getString(Access.niyPegawai)
        val waktuPengambilan = dateFormater(intent.extras?.getString(Access.waktuPengambilan))

        //        Jika sembako sudah diambil maka tombol button di disable
        if (statusPengambilan == "Sudah"){
            binding.trWaktuPengambilan.visibility = View.VISIBLE
            binding.tvWaktuPengambilan.text = ": $waktuPengambilan"
            binding.btnLengkap.isEnabled = false


            Snackbar.make (binding.root,"Sembako telah diambil",Snackbar.LENGTH_INDEFINITE)
                .setAction("Scan Ulang"){
                    startActivity(Intent(myContext,ScanActivity::class.java))
                    finish()
                }.show()
        }

        binding.tvNamaPegawai.text = namaPegawai
        binding.tvStatus.text = ": $statusPengambilan"
        binding.tvNiy.text = niyPegawai


        listRecyclerView = findViewById(R.id.rv_daftar_sembako)
        listRecyclerView.layoutManager = LinearLayoutManager (this)





        ApiService.create(sharedPref).getBarangSembako(idSembako)
            .enqueue(object : Callback<ResponseBarang> {
                override fun onResponse(
                    call: Call<ResponseBarang>,
                    response: Response<ResponseBarang>
                ) {
                    if (response.isSuccessful){
                        listRecyclerView.adapter = ListDaftarSembakoAdapter(response.body(),intent?.extras?.getString(Access.statusPengambilan))

                    }

                }

                override fun onFailure(call: Call<ResponseBarang>, t: Throwable) {
                    Log.d("MainActivity","Test Retro error is Executed: ${call.isExecuted}")
                    Log.d("MainActivity","Test Retro error: ${t.localizedMessage}")
                }
            })

        binding.btnLengkap.setOnClickListener {
            if (totalData == isChecked){
                updateSembakoTaken (idSembako.toString(),sharedPref)

//                Toast.makeText(CheckSembakoActivity@this,"Jalankan perintah",Toast.LENGTH_LONG).show()
            }
            else {
                Snackbar.make (binding.root,"Item sembako tidak lengkap",Snackbar.LENGTH_SHORT).show()
            }
        }


    }

    private fun updateSembakoTaken (primaryKey : String, sharedPreferences: SharedPreferences){
        ApiService.create(sharedPreferences).setStatusSembako(primaryKey).enqueue(object : Callback<ResponseSetStatus>{
            override fun onResponse(
                call: Call<ResponseSetStatus>,
                response: Response<ResponseSetStatus>
            ) {
                if (response.isSuccessful){

                    var status = response.body()?.status.toString()
//                    Toast.makeText(myContext,"${response.body()?.status.toString()} dan ${response.body()?.text.toString()}",Toast.LENGTH_LONG).show()
                    Log.d("Set Sembako",response.body()?.status.toString())
                    Log.d("Set Sembako",response.body()?.text.toString())

                    if (status == "Yes"){

//                        Show Snack bar
                        Snackbar.make (binding.root,"Data telah diupdate",Snackbar.LENGTH_INDEFINITE)
                            .setAction("Scan Ulang"){
                                startActivity(Intent(myContext,ScanActivity::class.java))
                                finish()
                            }.show()

//                        Disable button
                        binding.btnLengkap.isEnabled = false
                    }


                }
            }

            override fun onFailure(call: Call<ResponseSetStatus>, t: Throwable) {
                Log.d("Set Sembako","Test Retro error is Executed: ${call.isExecuted}")
                Log.d("Set Sembako","Test Retro error: ${t.localizedMessage}")
            }

        })
    }

    override fun onBackPressed() {
//        agar ketika kembali dari check sembako, piechart diupdate
        startActivity(Intent(this@CheckSembakoActivity,MainActivity::class.java))
        finish()
    }

    fun dateFormater (date : String?) : String {
        val formater = SimpleDateFormat ("yyyy-MM-dd HH:mm:ss")

        date?.let {
            val date = formater.parse(date)
            //        val formaterOut = SimpleDateFormat ("dd - M - yyyy HH:mm:ss").format(date)
            val tanggal = SimpleDateFormat ("dd").format(date)

            val bulan = SimpleDateFormat ("M").format(date).let {
                when (it.toInt()) {
                    1 -> "Januri"
                    3 -> "Maret"

                    else -> {
                        "Wrong Number"
                    }
                }
            }
            val tahun = SimpleDateFormat ("yyyy").format(date)
            val waktu = SimpleDateFormat ("HH:mm:ss").format(date)

            return ("$tanggal $bulan $tahun $waktu")
        }
        return ""
    }
}