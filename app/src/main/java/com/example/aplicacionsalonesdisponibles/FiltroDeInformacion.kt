package com.example.aplicacionsalonesdisponibles

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStreamReader
import java.lang.Exception
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.LocalDateTime
import java.util.Calendar

class FiltroDeInformacion (private val context:Context) {
    // Instancia del objeto retrofit
    private val retrofit = getRetrofit()
    fun iniciarFiltro(){
        consultaAPI()
    }
    // Función para realizar una consulta a la API
    private fun consultaAPI(){
        //Crear una corrutina
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<listResponse> = retrofit.create(ApiService::class.java).getData()
            if (myResponse.isSuccessful){
                try {
                    Log.i("AppConfirm","Test 1 API R: Pass")
                    val response: listResponse? = myResponse.body()

                    if (response != null){
                        // Convertir los datos a una cadena en formato JSON
                        val datosJson = Gson().toJson(response)

                        // Guardar datos en un archivo
                        val outputStream = context.openFileOutput("datosAPI.json", Context.MODE_PRIVATE)
                        val outputStreamWriter = OutputStreamWriter(outputStream)
                        outputStreamWriter.write(datosJson)
                        outputStreamWriter.close()
                        Log.i("AppConfirm", "Test 2 Saved data: Pass")
                    }else{
                        Log.e("Error", "Test 1 API R: Fail")
                    }
                    // Llamada al siguiente método
                    obtenerSalonesOcupados()

                }catch (e:Exception){
                    Log.e("Error", "Error al extraer datos de la API: ${e.message}", e)
                }
            }
            else{
                Log.e("Error", "No funciona :(")
            }
        }
    }
    // Función que obtiene los salones y horarios en los que un salón está ocupado
    private fun obtenerSalonesOcupados(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = context.openFileInput("datosAPI.json")
                val inputStreamReader = InputStreamReader(inputStream)
                val datosJson = inputStreamReader.readText()
                inputStreamReader.close()
                inputStream.close()
                val datosGson: listResponse? = Gson().fromJson(datosJson,listResponse::class.java)
                if(datosGson != null){
                    Log.i("AppConfirm","Test 3 Read Data: pass")
                    val listaSalonesOcupados = ArrayList<salonOcupado>()
                    for (elemento in datosGson.horarios){
                        // Buscar en los pares
                        if (elemento.id % 2 == 0){
                            var i  = elemento.id + 1
                            var count = 1
                            val horarios = listOf(elemento.l, elemento.m, elemento.x, elemento.j, elemento.v)
                            val salones = listOf(datosGson.horarios[i].l,datosGson.horarios[i].m,
                                datosGson.horarios[i].x, datosGson.horarios[i].j, datosGson.horarios[i].v)

                            for (i in 0..4){
                                if (horarios[i] != "-"){
                                    try {
                                        val dia = count
                                        val horario = horarios[i]
                                        val salon = salones[i].toInt()
                                        listaSalonesOcupados.add(salonOcupado(dia, horario, salon))
                                    }catch (e:Exception){}finally {count += 1}
                                }else{count += 1}
                            }
                        }
                    }
                    // Convertir la lista a JSON
                    val datosJson = Gson().toJson(listaSalonesOcupados)
                    //Log.d("AppConfirm", datosJson.toString())
                    // Guardar datos en un archivo
                    val outputStream = context.openFileOutput("salonesOcupados.json", Context.MODE_PRIVATE)
                    val outputStreamWriter = OutputStreamWriter(outputStream)
                    outputStreamWriter.write(datosJson)
                    outputStreamWriter.close()
                    Log.i("AppConfirm", "Test 4 Saved Data Update: Pass")
                }
                // Llamada al siguiente método
                ordenarSalones()
            }catch (e:Exception){
                Log.e("Error", "Error al manipular los datos del archivo: ${e.message}",e)
            }
        }
    }

    // Función que ordena los salones de manera ascendente
    private fun ordenarSalones(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Leer archivo
                val inputStream = context.openFileInput("salonesOcupados.json")
                val inputStreamReader = InputStreamReader(inputStream)
                val datosJson = inputStreamReader.readText()
                inputStreamReader.close()
                inputStream.close()
                val datosGson: List<salonOcupado> = Gson().fromJson(datosJson,Array<salonOcupado>::class.java).toList()
                if(datosGson != null){
                    val listaSalones = ArrayList<salonOrdenado>()
                    for (elemento in datosGson){
                        // Separar la cadena del horario
                        val (horaInicio, horaFin) = elemento.horario.split(" a ")
                        //Eliminar espacios
                        horaInicio.trim()
                        horaFin.trim()
                        //Separar la hora en horas y minutos
                        val (horaInicio_HH,horaInicio_MM) = horaInicio.split(":")
                        val (horaFin_HH, horaFin_MM) = horaFin.split(":")
                        val horaInicioDecimal= (horaInicio_HH.toInt() + (horaInicio_MM.toFloat()/60))
                        val horaFinDecimal = (horaFin_HH.toInt() + (horaFin_MM.toFloat()/60))
                        //Agregar el objeto a la lista
                        listaSalones.add(salonOrdenado(
                            elemento.dia,
                            elemento.salon,
                            horaInicioDecimal,
                            horaFinDecimal))
                    }
                    // Ordena los elementos según el número de salón, el número de día y la hora de inicio de clase
                    val listaSalonesOrdenados = listaSalones.sortedWith(compareBy(
                        salonOrdenado::salon,
                        salonOrdenado::dia,
                        salonOrdenado::horaInicio))

                    // Convertir la lista a JSON
                    val datosJson = Gson().toJson(listaSalonesOrdenados)
                    //Log.d("AppConfirm", datosJson.toString())
                    // Guardar datos en un archivo
                    val outputStream = context.openFileOutput("archivo_ordenado.json", Context.MODE_PRIVATE)
                    val outputStreamWriter = OutputStreamWriter(outputStream)
                    outputStreamWriter.write(datosJson)
                    outputStreamWriter.close()
                    Log.i("AppConfirm", "Test 5 Saved Data Update: Pass")
                }else{
                    Log.e("Error", "Test 5 Read Data: Fail")
                }
                obtenerSalonesDisponibles()
            }catch (e:Exception){
                Log.e("Error", "Error al ordenar los salones: ${e.message}",e)
            }
        }
    }

    // Función que obtiene los salones disponibles
    private fun obtenerSalonesDisponibles(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Leer archivo
                val inputStream = context.openFileInput("archivo_ordenado.json")
                val inputStreamReader = InputStreamReader(inputStream)
                val datosJson = inputStreamReader.readText()
                inputStreamReader.close()
                inputStream.close()
                val datosGson: List<salonOrdenado> = Gson().fromJson(datosJson, Array<salonOrdenado>::class.java).toList()
                if(datosGson != null){
                    // Horas de inicio y fin de cada clase
                    val horasClase = arrayListOf<Float>(7.0f, 8.5f, 10.0f, 11.5f, 13.0f, 14.5f, 16.0f, 17.5f, 19.0f, 20.5f)
                    // Lita de salones
                    val salones = arrayListOf(
                        1101, 1102, 1103, 1104, 1105, 1106, 1107, 1108, 1109, 1110,
                        1111, 1112, 1113, 1114, 1115, 1119,
                        1201, 1202, 1203, 1204, 1205, 1206,1207, 1208, 1209, 1210,
                        1211, 1212, 1213, 1214, 1215,
                        2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110,
                        2111, 2112,
                        2201, 2202, 2203, 2204, 2205, 2206, 2207, 2208, 2209, 2210,
                        2211, 2212, 2213, 2214, 2215,
                        3101, 3102, 3103, 3104, 3105, 3106, 3107, 3108, 3109, 3110,
                        3111, 3112, 3113, 3114, 3115,
                        3201, 3202, 3203, 3204, 3205, 3206, 3207, 3208, 3209, 3210,
                        3211, 3212, 3213, 3214, 3215)
                    val salonesDisponibles = ArrayList<salonDisponible>()
                    for(dia in 1..5){
                        for (hora in horasClase){
                            for(salon in salones){
                                var disponible = false
                                for(horario in datosGson){
                                    if (horario.dia == dia && horario.salon == salon && horario.horaInicio <= hora && horario.horaFin >= hora+1.5){
                                        disponible = true
                                        break
                                    }
                                }
                                if(!disponible){
                                    //Si está disponible , se agrega a una lista de salones disponibles siempre que
                                    //el salón no haya ido agregado anteriormente
                                    salonesDisponibles.add(salonDisponible(dia, salon, hora))
                                }
                            }
                        }
                    }
                    // Ordenar los salones disponibles
                    val listaSalonesDisponibles: List<salonDisponible> = salonesDisponibles.sortedWith(compareBy(
                        salonDisponible::salon,
                        salonDisponible::dia,
                        salonDisponible::horaInicio))
                    // Convertir a JSON
                    val datosJson = Gson().toJson(listaSalonesDisponibles)
                    // Guardar datos en un archivo
                    val outputStream = context.openFileOutput("salonesDisponibles.json", Context.MODE_PRIVATE)
                    val outputStreamWriter = OutputStreamWriter(outputStream)
                    outputStreamWriter.write(datosJson)
                    outputStreamWriter.close()

                    //Obtener fecha actual
                    val calendar = Calendar.getInstance()
                    val currentDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
                    //Guardar la fecha en preferecias compartidas
                    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("lastDate",currentDate)
                    editor.apply()
                    Log.i("AppConfirm", "Test 6 Saved Data Update: Pass")

                }else{
                    Log.e("Error", "Test 6 Saved Data Update: Fail")
                }
            }catch (e:Exception){
                Log.e("Error", "Error al obtener los salones disponibles: ${e.message}", e)
            }
        }
    }

    //Objeto Retrofit
    private fun getRetrofit():Retrofit{
        return Retrofit
            .Builder()
            .baseUrl("https://www.eventos.esimecu.ipn.mx/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}