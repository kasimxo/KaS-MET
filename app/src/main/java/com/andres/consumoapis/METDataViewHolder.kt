package com.andres.consumoapis

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.andres.consumoapis.databinding.MetResultListBinding
import com.squareup.picasso.Picasso

class METDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = MetResultListBinding.bind(view)

    fun bind(objeto: METItemResponse) {


        binding.tvTitulo.text = objeto.title
        var medio = objeto.medio
        var fecha = objeto.fecha
        var autor = objeto.autor
        if(medio.isBlank() || medio.isEmpty()) {medio = "Unknown"}
        if(fecha.isBlank() || fecha.isEmpty()) {fecha = "Unknown"}
        if(autor.isBlank() || autor.isEmpty()) {autor = "Unknown"}
        var descripcion : String = "Medium: " + medio + "\nDate: " + fecha + "\nAuthor: " + autor
        binding.tvDescripcion.text = descripcion

        if (objeto.imageSmall.isNotEmpty()) {
            //Primero intentamos usar la imagen pequeña para ir lo mas rápido posible
            //Reescala las imagenes y las centra sin hacer strech
            Picasso.get().load(objeto.imageSmall).fit().centerCrop().into(binding.ivPieza)
        } else if (objeto.imageBig.isNotEmpty()) {
            //Si el objeto no tiene imagen pequeña, usamos la grande
            Picasso.get().load(objeto.imageSmall).fit().centerCrop().into(binding.ivPieza)
        }

        if (objeto.imagenesAlternativas.size<1){
            binding.btnLeft.isVisible = false
            binding.btnRight.isVisible = false
        }



    }

}