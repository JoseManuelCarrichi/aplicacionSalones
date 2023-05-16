package com.example.aplicacionsalonesdisponibles

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionsalonesdisponibles.databinding.ItemSalonesBinding





class SalonesViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSalonesBinding.bind(view)

    fun bind(salon: salon){
        binding.tvSalonDisponible.text = salon.numeroSalon.toString()
    }
}