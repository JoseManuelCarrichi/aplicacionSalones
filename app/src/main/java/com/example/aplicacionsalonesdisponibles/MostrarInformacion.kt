package com.example.aplicacionsalonesdisponibles

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.InputStreamReader
import java.lang.Exception
import java.util.Calendar

class MostrarInformacion(private val context: Context) {
    private var dayNumber = 0
    private var horarioActual = 0.0f
    private var listaJson = ""
    //Lista de sslones disponibles
    private val listaSalones = ArrayList<salon>()
    fun obtenerDiaHora(){
        try {
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            val hora: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val minutos: Int = calendar.get(Calendar.MINUTE)
            // Convertir la hora actual a un formato decimal
            horarioActual = hora.toFloat() + (minutos.toFloat()/60)


            dayNumber = when (dayOfWeek){
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar. WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> throw IllegalArgumentException("Día de la semana inválido")
            }
            Log.i("AppConfirm", "Test 7 Horario: Pass")
            Log.d("AppConfirm", dayNumber.toString())
            Log.d("AppConfirm", horarioActual.toString())


        }catch (e:Exception){
            Log.e("Error", "Se produjo un error al obtener el día y hora actuales")
        }
    }

    fun buscarSalonesDisponibles(){

        try {
            // Leer archivo
            val inputStream = context.openFileInput("salonesDisponibles.json")
            val inputStreamReader = InputStreamReader(inputStream)
            val datosJson = inputStreamReader.readText()
            inputStreamReader.close()
            inputStream.close()
            val datosGson: List<salonDisponible> = Gson().fromJson(datosJson, Array<salonDisponible>::class.java).toList()

            // Verificar qué salones están dispoibles
            for (salon in datosGson){
                if (salon.dia == dayNumber && salon.horaInicio <= horarioActual && horarioActual < salon.horaInicio + 1.5 ){
                    listaSalones.add(salon(salon.salon))
                }
            }
            listaJson = Gson().toJson(listaSalones)
            Log.i("AppConfirm", "Test 8 Salones Disponibles: Pass")
            Log.d("AppConfirm", listaJson.toString())
        }catch (e:Exception){
            Log.e("Error", "Se produjo un error al buscar los salones disponibles")
        }
    }


    fun mostrarSalones():List<salon> = listaSalones
}