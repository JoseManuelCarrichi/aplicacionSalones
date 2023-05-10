package com.example.aplicacionsalonesdisponibles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Instancia de la clase
        val filtro = FiltroDeInformacion(this)
        filtro.obtenerInformacion()

    }
}