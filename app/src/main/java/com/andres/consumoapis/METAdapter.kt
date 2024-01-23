package com.andres.consumoapis

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class METAdapter( var objetos: List<METItemResponse> = emptyList()) : RecyclerView.Adapter<METDataViewHolder>() {

    fun updateList(list: List<METItemResponse>) {
        objetos = list
        notifyDataSetChanged()

        for (o in list){
            Log.d("Lista", o.title)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): METDataViewHolder {
        return METDataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.met_result_list, parent, false)
        )
    }
    override fun onBindViewHolder(holder: METDataViewHolder, position: Int) {
        holder.bind(objetos[position])
    }
    override fun getItemCount() = objetos.size
}