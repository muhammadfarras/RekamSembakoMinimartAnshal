package com.example.rekamsembakominimartanshal

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rekamsembakominimartanshal.api.ResponseNumberTakenSembako
import com.example.rekamsembakominimartanshal.data.Access
import com.example.rekamsembakominimartanshal.databinding.ActivityMainBinding
import com.example.rekamsembakominimartanshal.network.ApiService
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener,
    OnChartValueSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    lateinit var sharedPref : SharedPreferences
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide()
        sharedPref = PreferenceManager.getDefaultSharedPreferences(
            MainActivity@ this
        )

        showPieAndData ()

//        Swap Refresh
        binding.swapRefresh.setOnRefreshListener (this)

        binding.fabScan.setOnClickListener {


            var hasPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            );

            // check apakah memiliki akses atu tidak
            if (hasPermission == PackageManager.PERMISSION_GRANTED){
                var intent = Intent(this, ScanActivity::class.java)
                startActivity(intent)
                finish()
                // Hide fab setiap intent

            }
            else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    Access.camera
                );
            }
        }

        binding.tollBar.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.item_setting -> {
                    ipConfiguration()
                    true
                }
                R.id.item_about -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun showToast(text: String?){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun showPieAndData () {
        try {
            ApiService.create(sharedPref).getNumberTakenSembako(sharedPref.getString(Access.kodeSembako,"-")).enqueue(
                object : Callback<ResponseNumberTakenSembako> {
                    override fun onResponse(
                        call: Call<ResponseNumberTakenSembako>,
                        response: Response<ResponseNumberTakenSembako>
                    ) {
                        if (response.isSuccessful){
                            var result = response.body()

                            result?.status.let {
                                if (it == "Yes") {
                                    showPieChart (result?.data?.belum?.toFloat() ?: 0f,result?.data?.sudah?.toFloat() ?: 0f)
                                    binding.swapRefresh.isRefreshing = false

                                    showToast("Data benar")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseNumberTakenSembako>, t: Throwable) {
                        binding.swapRefresh.isRefreshing = false
                        binding.tlLegend.visibility = View.GONE
                        showToast("Data salah")
                    }

                })
        }
        catch (e : Exception){
//            Jika error karena ip tidak di set, coba untuk membuka dialog
            e.localizedMessage
            ipConfiguration()
            showToast("Ip Belum di set")
        }
    }

    private fun ipConfiguration () {
        var view = layoutInflater.inflate(R.layout.dialog_view, null)

        var dialog = AlertDialog.Builder(this)
        dialog.setView(view)

        var newIp = view.findViewById<EditText>(R.id.savedIp)
        var kodeSembako = view.findViewById<EditText>(R.id.et_kode_sembako)
        var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        sharedPref.getString(Access.savedIp,"-")?.let { Log.d("LOGD", it) }

        newIp.setText(sharedPref.getString(Access.savedIp,"-").toString())
        kodeSembako.setText(sharedPref.getString(Access.kodeSembako,"-").toString())

        dialog.setPositiveButton("Enter", DialogInterface.OnClickListener { _, _ ->


//            Save shared prefenrence dulu
            with(sharedPref.edit()) {
                putString(Access.kodeSembako,kodeSembako.text.toString())
                putString(Access.savedIp, newIp.text.toString())
                apply()
            }

            showPieAndData ()


        })
        dialog.show()

    }

    private fun showPieChart (belumAmbil : Float, sudahAmbil : Float){
        var entry = ArrayList<PieEntry>()
//        Jika data tidak ada
        binding.pcKondisiSembako.setNoDataText("Data tidak tersedia")
        if (belumAmbil == 0f && sudahAmbil == 0f ){
            binding.tlLegend.visibility = View.GONE
        }
        else {

            entry.add(PieEntry(sudahAmbil,"Sudah"))
            entry.add(PieEntry(belumAmbil,"Belum"))

            binding.tvJmlDone.text = entry[0].value.toInt().toString()
            binding.tvLabelDone.text = entry[0].label.toString()
            binding.tvJmlNoDone.text = entry[1].value.toInt().toString()
            binding.tvLabelNoDone.text = entry[1].label.toString()
        }

        var dataSet = PieDataSet(entry, "Kondisi\n Pengambilan Sembako")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 80f)
        dataSet.selectionShift = 4f
        dataSet.setDrawValues(true)


        binding.pcKondisiSembako.animateY(1400,Easing.EaseInOutQuad)

        // binding.pcKondisiSembako.setDrawEntryLabels(true)
        // Mauskan warna
        var arrayListColor = ArrayList<Int>()
        arrayListColor.add(Color.rgb(32, 214, 214))
        arrayListColor.add(Color.rgb(240, 188, 91))

        dataSet.colors = arrayListColor

        var data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pcKondisiSembako))

        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        if (belumAmbil == 0f && sudahAmbil == 0f ){
//            Jika semua data == 0 maka munculkan error data tidak tersedia
            binding.pcKondisiSembako.data = null
            binding.tlLegend.visibility = View.GONE
        }
        else {
            binding.pcKondisiSembako.data = data
            binding.tlLegend.visibility = View.VISIBLE
        }




        binding.pcKondisiSembako.setUsePercentValues(true)
        binding.pcKondisiSembako.setTransparentCircleColor(Color.WHITE)
        binding.pcKondisiSembako.setTransparentCircleAlpha(110)
        binding.pcKondisiSembako.transparentCircleRadius = 61f
 
        binding.pcKondisiSembako.centerText = generateCenterSpannableText()

        binding.pcKondisiSembako.holeRadius = 50f
        binding.pcKondisiSembako.transparentCircleRadius = 60f

        binding.pcKondisiSembako.setDrawCenterText(true)
        binding.pcKondisiSembako.rotationAngle = 0f
        binding.pcKondisiSembako.isRotationEnabled = true





//        Legend
        var legend = binding.pcKondisiSembako.legend
        legend.isEnabled = false

        var description = binding.pcKondisiSembako.description
        description.isEnabled = false


//        Set table
        binding.ivDone.setBackgroundColor(arrayListColor[0])
        binding.ivNoDone.setBackgroundColor(arrayListColor[1])







        binding.pcKondisiSembako.highlightValue(null)
        binding.pcKondisiSembako.invalidate()
    }

    private fun generateCenterSpannableText() : SpannableString {
        val s = SpannableString("Sistem Sembako\ndeveloped by IT SUPPORT")
        s.setSpan(RelativeSizeSpan(1.2f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 0, 14, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 10, 0)
        s.setSpan(RelativeSizeSpan(.8f), 14, s.length - 10, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 10, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 10, s.length, 0)
        return s
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            Access.camera -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    true
                } else {
                    // Check apakah always deneid atau tidak
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            android.Manifest.permission.CAMERA
                        )
                    ) {
                        Snackbar.make(
                            binding.layout,
                            "R.string.pesan_butuh_akses",
                            Snackbar.LENGTH_LONG
                        )
                            .show()

                    } else {
                        Snackbar.make(
                            binding.layout,
                            "R.string.pesan_butuh_akses",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Pengaturan") {

                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", this.packageName, null)
                                intent.data = uri
                                startActivity(intent)

                            }
                            .show()
                    }
                }
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onNothingSelected() {

    }

    override fun onRefresh() {

        showPieAndData ()

    }
}