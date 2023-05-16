package com.example.aplicacionsalonesdisponibles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView





class SalonesAdapter(var salonesDisponiblesList:List<salon> = emptyList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
     {

    // Variables para detectar el tipo de Item
    private val HEADER = 0
    private val ITEM = 1

    fun updateList(salonesDisponiblesList: List<salon>){
        this.salonesDisponiblesList = salonesDisponiblesList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) HEADER else ITEM
        //Si se trata de la primera posición, seré el encabezado, sino, un item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Retornar el tipo de layout a pintar en pantalla con el ViewHolder
        return if(viewType == HEADER){
            HeaderViewHolder(layoutInflater.inflate(R.layout.item_encabezado, parent, false))
        }else{
            return SalonesViewHolder(layoutInflater.inflate(R.layout.item_salones, parent, false ))
        }
    }

    // Longitud de la lista de salones a mostrar
    override fun getItemCount() = salonesDisponiblesList.size + 1
    //Agrega un elemento adicional al número de salones a mostrar

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        if(viewholder is HeaderViewHolder){
            viewholder.bind("Salones")
        }else if(viewholder is SalonesViewHolder){
            viewholder.bind(salonesDisponiblesList[position - 1]) // Se resta 1 porque la posición 0 es el encabezado
        }


        //viewholder.bind(salonesDisponiblesList[position])
    }
}