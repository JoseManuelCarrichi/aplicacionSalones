package com.example.aplicacionsalonesdisponibles

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionsalonesdisponibles.databinding.ItemEncabezadoBinding






class HeaderViewHolder(view:View): RecyclerView.ViewHolder(view) {
    private val binding = ItemEncabezadoBinding.bind(view)

    fun bind(headerText : String){
        binding.tvEncabezado.text = headerText
    }

}