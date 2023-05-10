package com.example.aplicacionsalonesdisponibles

import android.content.Context
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

class FiltroDeInformacion (private val context:Context) {
    // Instancia del objeto retrofit
    private val retrofit = getRetrofit()
    // Función general para llamar a todas las funciones manteniendo la
    // Seguridad de las funciones
    fun obtenerInformacion(){
        consultaAPI()
        obtenerSalonesOcupados()

    }
    // Función para realizar una consulta a la API
    private fun consultaAPI(){
        //Crear una corrutina
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<listResponse> = retrofit.create(ApiService::class.java).getData()
            if (myResponse.isSuccessful){
                try {
                    Log.i("Aplicacion","Test 1 API R: Pass")
                    val response: listResponse? = myResponse.body()

                    if (response != null){
                        // Convertir los datos a una cadena en formato JSON
                        val datosJson = Gson().toJson(response)

                        // Guardar datos en un archivo
                        val outputStream = context.openFileOutput("datosAPI.json", Context.MODE_PRIVATE)
                        val outputStreamWriter = OutputStreamWriter(outputStream)
                        outputStreamWriter.write(datosJson)
                        outputStreamWriter.close()
                        Log.i("JoseManuel", "Test 2 Saved data: Pass")
                    }else{
                        Log.e("JoseManuel", "Test 1 API R: Fail")
                    }
                }catch (e:Exception){
                    Log.e("JoseManuel", "Error al extraer datos de la API: ${e.message}", e)
                }
            }
            else{
                Log.i("JoseManuel", "No funciona :(")
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
                    Log.i("JoseManuel","Test 3 Read Data: pass")
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
                    Log.d("JoseManuel", datosJson.toString())
                    // Guardar datos en un archivo
                    val outputStream = context.openFileOutput("salonesOcupados.json", Context.MODE_PRIVATE)
                    val outputStreamWriter = OutputStreamWriter(outputStream)
                    outputStreamWriter.write(datosJson)
                    outputStreamWriter.close()
                    Log.i("JoseManuel", "Test 4 Saved Data Update: Pass")
                }
            }catch (e:Exception){
                Log.e("JoseManuel", "Error al manipular los datos del archivo: ${e.message}",e)
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
                    Log.d("JoseManuel", datosJson.toString())
                    // Guardar datos en un archivo
                    val outputStream = context.openFileOutput("archivo_ordenado.json", Context.MODE_PRIVATE)
                    val outputStreamWriter = OutputStreamWriter(outputStream)
                    outputStreamWriter.write(datosJson)
                    outputStreamWriter.close()
                    Log.i("JoseManuel", "Test 5 Saved Data Update: Pass")
                }else{
                    Log.e("JoseManuel", "Test 5 Read Data: Fail")
                }
            }catch (e:Exception){
                Log.e("JoseManuel", "Error al ordenar los salones: ${e.message}",e)
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