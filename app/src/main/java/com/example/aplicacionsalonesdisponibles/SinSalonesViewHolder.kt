package com.example.aplicacionsalonesdisponibles

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionsalonesdisponibles.databinding.ItemSinSalonesBinding


class SinSalonesViewHolder(view: View):RecyclerView.ViewHolder(view) {
    private val binding = ItemSinSalonesBinding.bind(view)

    fun bind(message : String){
        binding.tvSinSalonesDisponibles.text = message
    }
}