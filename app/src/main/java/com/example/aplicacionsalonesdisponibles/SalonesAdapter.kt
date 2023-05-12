package com.example.aplicacionsalonesdisponibles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView





class SalonesAdapter(var salonesDisponiblesList:List<salon> = emptyList()) :
    RecyclerView.Adapter<SalonesViewHolder>() {

    fun updateList(salonesDisponiblesList: List<salon>){
        this.salonesDisponiblesList = salonesDisponiblesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalonesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SalonesViewHolder(layoutInflater.inflate(R.layout.item_salones, parent, false ))
    }

    // Longitud de la lista de salones a mostrar
    override fun getItemCount() = salonesDisponiblesList.size

    override fun onBindViewHolder(viewholder: SalonesViewHolder, position: Int) {
        viewholder.bind(salonesDisponiblesList[position])
    }
}