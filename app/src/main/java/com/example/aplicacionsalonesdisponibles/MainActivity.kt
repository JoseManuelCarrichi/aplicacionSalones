package com.example.aplicacionsalonesdisponibles

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
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
        createNotificationChannel()


        // Verificar si el archivo de los salones disponibles está disponible
        comprobarEjecucionPrevia(this)

    }

    fun comprobarEjecucionPrevia(context: Context) {
        val prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val ultimaEjecucion = prefs.getLong("ultimaEjecucion", 0)
        val ahora = System.currentTimeMillis()
        val seisMesesEnMilis = 15778800000 // Número de milisegundos en 6 meses (considerando años no bisiestos)
        val unMesEnMilis = 2629800000 // Número de milisegundos en 1 mes
        Log.i("AppConfirm", "Validado ejecuciones previas")
        if (ultimaEjecucion == 0L || ahora - ultimaEjecucion >= seisMesesEnMilis) {
            searchData(context)
            val editor = prefs.edit()
            editor.putLong("ultimaEjecucion", ahora)
            editor.apply()
        }else{
            Log.i("AppConfirm", "Actualizando salones")
            actualizarSalones(context)
        }
    }

    private fun searchData(context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("AppConfirm", "Consultando Salones en el Generador de Horarios")
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
                binding.txtHorarioActual.isVisible = true
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
        Log.i("Lista", "Lista: ${listaSalonesHorario.horario.length}")
        binding.txtHorarioActual.isVisible = true
    }


    private fun tablaUI(listaSalones:List<salon>) {
        adapter = SalonesAdapter()
        binding.rvTablaSalones.setHasFixedSize(true)
        binding.rvTablaSalones.layoutManager = LinearLayoutManager(this)
        binding.rvTablaSalones.adapter = adapter

        //Actualizar lista del adapter
        adapter.updateList(listaSalones)

        // Preferencias del botón de notificaciones
        val sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        updateButton(sharedPreferences)
        binding.switchButton.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                editor.putBoolean(SWITCH_BUTTON_KEY, true).apply()
                Log.i("AppConfirm", "Notificaciones activadas")
                programarNotificacion()
                updateButton(sharedPreferences)
            }else{
                editor.putBoolean(SWITCH_BUTTON_KEY, false).apply()
                Log.i("AppConfirm", "Notificaciones desactivadas")
                updateButton(sharedPreferences)
            }
        }
    }

    private fun programarNotificacion() {
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        // Establece la primera alarma a las 6:50
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 50)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HOUR + AlarmManager.INTERVAL_HALF_HOUR,
            pendingIntent
        )
    }

    // Notificaciones
    companion object{
        const val SWITCH_BUTTON_KEY = "switch"
        const val PREF_KEY = "pref"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "NotificationChannel"
    }

    private fun updateButton(sharedPreferences: SharedPreferences ){
        binding.switchButton.apply {
            isChecked = sharedPreferences.getBoolean(SWITCH_BUTTON_KEY, false)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Descripción del canal"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}