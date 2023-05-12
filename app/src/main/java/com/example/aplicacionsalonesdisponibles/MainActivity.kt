package com.example.aplicacionsalonesdisponibles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionsalonesdisponibles.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import okhttp3.Dispatcher

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SalonesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Iniciar binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instancia de la clase
        val filtro = FiltroDeInformacion(this)
        filtro.iniciarFiltro()

        // Instancia de la clase
        val interfaz = MostrarInformacion(this)
        interfaz.obtenerDiaHora()
        interfaz.buscarSalonesDisponibles()
        val listaSalones: List<salon> = interfaz.mostrarSalones()

        //UI
        initUI(listaSalones)
    }

    private fun initUI(listaSalones:List<salon>) {
        adapter = SalonesAdapter()
        binding.rvTablaSalones.setHasFixedSize(true)
        binding.rvTablaSalones.layoutManager = LinearLayoutManager(this)
        binding.rvTablaSalones.adapter = adapter

        //Actualizar lista del adapter
        adapter.updateList(listaSalones)
    }
}