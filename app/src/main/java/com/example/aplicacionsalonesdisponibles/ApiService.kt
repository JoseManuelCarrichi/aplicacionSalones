package com.example.aplicacionsalonesdisponibles

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET



interface ApiService {
    @GET("/publicacion?carrera=ic&periodo=2023-2%20-%20Enero%202023%20-%20Julio%202023")
    suspend fun getData() : Response<listResponse>
}
// Objeto principal
data class listResponse(@SerializedName("data") val horarios:List<salonesHorariosResponse>)
// Objeto secundario
data class salonesHorariosResponse(
    @SerializedName("orden") val id:Int,
    @SerializedName("v_l") val l:String,
    @SerializedName("v_m") val m:String,
    @SerializedName("v_x") val x:String,
    @SerializedName("v_j") val j:String,
    @SerializedName("v_v") val v:String
)

data class salonOcupado(
    val dia:Int,
    val horario:String,
    val salon:Int
)

data class salonOrdenado(
    val dia:Int,
    val salon:Int,
    val horaInicio:Float,
    val horaFin:Float
)

data class salonDisponible(
    val dia: Int,
    val salon: Int,
    val horaInicio: Float
)
data class salon(
    val numeroSalon: Int
)