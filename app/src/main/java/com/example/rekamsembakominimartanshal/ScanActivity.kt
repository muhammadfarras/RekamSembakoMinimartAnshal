package com.example.rekamsembakominimartanshal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.rekamsembakominimartanshal.api.ResponseStatus
import com.example.rekamsembakominimartanshal.costume.CostumeScannerView
import com.example.rekamsembakominimartanshal.data.Access
import com.example.rekamsembakominimartanshal.network.ApiService
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

private const val TAG = "ScanActivity"

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    lateinit var starterIntent: Intent
    lateinit var scannerView: ZXingScannerView
    lateinit var myActivity:ScanActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isFlashOn:Boolean = false;
        myActivity = this


        Log.d(TAG, "onCreate: ${TAG}")
        supportActionBar?.hide()

        starterIntent = intent
        scannerView = CostumeScannerView (this@ScanActivity)


//        untuk menyalakan flas ketika sedikit gelap
        scannerView.setOnClickListener {
            if (isFlashOn){
                Snackbar.make(scannerView,"Flash is Off", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Turn On"){
                        isFlashOn = true
                        scannerView.flash = true
                    }
                    .show()
                isFlashOn = false
                scannerView.flash = false
            }
            else {
                Snackbar.make(scannerView,"Flash is On", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Turn Off"){
                        isFlashOn = false
                        scannerView.flash = false
                    }
                    .show()
                isFlashOn = true
                scannerView.flash = true
            }

        }
        setContentView(scannerView)
    }
    override fun handleResult(rawResult: Result?) {
//        onRestart(rawResult)
//        scannerView.setResultHandler (this)
//        scannerView.startCamera()


        Log.d(TAG, "handleResult: ${TAG} ${rawResult}")
        var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        Log.d("MainActivity","ip nya :${sharedPref.getString(Access.savedIp,"tidak ada")}")
        //        Text Retrofit


        try {
            ApiService.create(sharedPref).getStatusSembako(rawResult.toString()).enqueue(
                object : Callback<ResponseStatus> {
                    override fun onResponse(
                        call: Call<ResponseStatus>,
                        response: Response<ResponseStatus>
                    ) {

                        if (response.isSuccessful){
                            Log.d("MainActivity","Hasil Retrofit :${response.body()?.status}")

                            if (response.body()?.status.toString() == "Yes"){
                                //                        Intent ke Check Sembako Activity
                                val intent = Intent(this@ScanActivity,CheckSembakoActivity::class.java)
                                intent.putExtra(Access.idSembako,response.body()?.nilai?.idSembako)
                                intent.putExtra(Access.namaPegawai,response.body()?.nilai?.namaPegawai)
                                intent.putExtra(Access.statusPengambilan,response.body()?.nilai?.statusPengambilan)
                                intent.putExtra(Access.niyPegawai,response.body()?.nilai?.niyPegawai)
                                intent.putExtra (Access.waktuPengambilan,response.body()?.nilai?.waktuPengambilan)
                                startActivity(intent)
                                finish()
                            }
                            else {
                                scannerView.resumeCameraPreview (this@ScanActivity)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                        Log.d("MainActivity","error Retrofit : ${t.localizedMessage}")
                        scannerView.resumeCameraPreview (this@ScanActivity)
                    }

                }
            )
        }
        catch (e : Exception){
            scannerView.resumeCameraPreview (this@ScanActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler (this)
        scannerView.startCamera()
        Log.d(TAG, "onResume: ${TAG}")
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onBackPressed() {
        scannerView.stopCamera()

//        Back to start activity intent
        startActivity(Intent(this@ScanActivity,MainActivity::class.java))

    }
}