package com.example.aplicacionsalonesdisponibles

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionsalonesdisponibles.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SalonesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Iniciar binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si el archivo de los salones disponibles está disponible
        comprobarEjecucionPrevia(this)
    }

    fun comprobarEjecucionPrevia(context: Context) {
        val prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val ultimaEjecucion = prefs.getLong("ultimaEjecucion", 0)
        val ahora = System.currentTimeMillis()
        val seisMesesEnMilis = 15778800000 // Número de milisegundos en 6 meses (considerando años no bisiestos)
        val unMesEnMilis = 2629800000 // Número de milisegundos en 1 mes

        if (ultimaEjecucion == 0L || ahora - ultimaEjecucion >= seisMesesEnMilis) {
            searchData(context)
            val editor = prefs.edit()
            editor.putLong("ultimaEjecucion", ahora)
            editor.apply()
        }else{
            actualizarSalones(context)
        }
    }

    private fun searchData(context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            //Instancia de la clase
            val filtro = FiltroDeInformacion(context)
            filtro.iniciarFiltro()

            delay(6000)

            // Instancia de la clase
            val interfaz = MostrarInformacion(context)
            interfaz.obtenerDiaHora()

            interfaz.buscarSalonesDisponibles()
            val listaSalonesHorario: listaSalonesHorario = interfaz.mostrarSalones()
            //Ejecutar en el Hilo Principal
            runOnUiThread {
                tablaUI(listaSalonesHorario.listaSalones)
                binding.txtHorarioActual.text = listaSalonesHorario.horario
            }
        }
    }

    private fun actualizarSalones(context: Context){
        // Instancia de la clase
        val interfaz = MostrarInformacion(context)
        interfaz.obtenerDiaHora()
        //binding.txtHorarioActual.text = "Horario actual: "
        interfaz.buscarSalonesDisponibles()
        val listaSalonesHorario:listaSalonesHorario = interfaz.mostrarSalones()
        tablaUI(listaSalonesHorario.listaSalones)
        binding.txtHorarioActual.text = listaSalonesHorario.horario
    }


    private fun tablaUI(listaSalones:List<salon>) {
        adapter = SalonesAdapter()
        binding.rvTablaSalones.setHasFixedSize(true)
        binding.rvTablaSalones.layoutManager = LinearLayoutManager(this)
        binding.rvTablaSalones.adapter = adapter

        //Actualizar lista del adapter
        adapter.updateList(listaSalones)
    }

}