package com.example.aplicacionsalonesdisponibles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView





class SalonesAdapter(var salonesDisponiblesList:List<salon> = emptyList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
     {

    // Variables para detectar el tipo de Item
    private val HEADER = 0
    private val ITEM = 1
    private val EMPTY = 2

    fun updateList(salonesDisponiblesList: List<salon>){
        this.salonesDisponiblesList = salonesDisponiblesList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return HEADER
        }else if(salonesDisponiblesList.size == 0){
            return EMPTY
        }
        else{
            return ITEM
        }
        //return if(position == 0) HEADER else ITEM
        //Si se trata de la primera posición, será el encabezado, si no, y si la posición actual es la ´lutim de
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Retornar el tipo de layout a pintar en pantalla con el ViewHolder
        if(viewType == HEADER){
            return HeaderViewHolder(layoutInflater.inflate(R.layout.item_encabezado, parent, false))
        }else if(viewType == ITEM){
            return SalonesViewHolder(layoutInflater.inflate(R.layout.item_salones, parent, false ))
        }else{
            return SinSalonesViewHolder(layoutInflater.inflate(R.layout.item_sin_salones, parent, false))
        }
    }

    // Longitud de la lista de salones a mostrar
    override fun getItemCount(): Int{
        if(salonesDisponiblesList.size == 0){
            return 2
        }
        else{
            return salonesDisponiblesList.size + 1
        }
    }
    //Agrega un elemento adicional al número de salones a mostrar

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        if(viewholder is HeaderViewHolder){
            viewholder.bind("Salones")
        }else if(viewholder is SalonesViewHolder){
            viewholder.bind(salonesDisponiblesList[position - 1]) // Se resta 1 porque la posición 0 es el encabezado
        }else if(viewholder is SinSalonesViewHolder){
            viewholder.bind("No hay salones disponibles en este momento.")
        }


        //viewholder.bind(salonesDisponiblesList[position])
    }
}