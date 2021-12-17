package com.example.rekamsembakominimartanshal.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rekamsembakominimartanshal.CheckSembakoActivity
import com.example.rekamsembakominimartanshal.R
import com.example.rekamsembakominimartanshal.api.ResponseBarang
import com.example.rekamsembakominimartanshal.databinding.ActivityCheckSembakoBinding
import com.example.rekamsembakominimartanshal.holder.ListDaftarSembakoHolder


class ListDaftarSembakoAdapter(private var body: ResponseBarang?, var status: String?) : RecyclerView.Adapter<ListDaftarSembakoHolder>()
{

    private lateinit var binding: ActivityCheckSembakoBinding



    override fun getItemCount(): Int {
        return body?.daftar?.size!!
    }


//    Function ini akan dipanggil sebanyak item yg ada getItemCount FUnction
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDaftarSembakoHolder {


        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_sembako, parent, false)

        binding = ActivityCheckSembakoBinding.inflate(LayoutInflater.from(parent.context))




        return ListDaftarSembakoHolder(view)
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ListDaftarSembakoHolder, position: Int) {
        Log.d("Switch", binding.tvStatus.text.toString())
        holder.swBarang.text =body?.daftar?.get(position)

        if ( status == "Sudah"){
            holder.swBarang.isChecked = true
//            Dari pada disable mending ontouch yg tidak bisa digunakan saja

            holder.swBarang.setOnTouchListener { _, _ ->
                holder.swBarang.isClickable = false
                false
            }
        }

        CheckSembakoActivity.totalData = body?.daftar?.size!!

        holder.swBarang.setOnCheckedChangeListener { _, isChecked ->

            Log.d("Switch", "Is Checked $isChecked")

            if (isChecked){
                CheckSembakoActivity.isChecked++
                Log.d("Switch", "${holder.swBarang.text} aktif")
            }
            else {
                CheckSembakoActivity.isChecked--
                Log.d("Switch", "${holder.swBarang.text} non aktif")
            }
        }


    }




}