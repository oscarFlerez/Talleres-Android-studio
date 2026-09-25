package com.example.calculadoradecreditos

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.pow

class MainActivity : Activity() {

    private lateinit var etCredito: EditText
    private lateinit var etCuotas: EditText
    private lateinit var etInteres: EditText

    private lateinit var btnCalcular: Button

    private lateinit var tvCuota: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvIntereses: TextView

    private lateinit var pantallaCredito: ScrollView
    private lateinit var pantallaMoneda: ScrollView
    private lateinit var pantallaEjemplo: ScrollView
    private lateinit var pantallaInicio: ScrollView

    private lateinit var campoCantidad: EditText
    private lateinit var radioGroupOrigen: RadioGroup
    private lateinit var radioGroupDestino: RadioGroup
    private lateinit var tvResultado: TextView
    private lateinit var campoDato: EditText
    private lateinit var etiMensaje: TextView

    private val tasaUsd = 1.0
    private val tasaCop = 4000.0
    private val tasaJpy = 150.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        etCredito = findViewById(R.id.etCredito)
        etCuotas = findViewById(R.id.etCuotas)
        etInteres = findViewById(R.id.etInteres)

        btnCalcular = findViewById(R.id.btnCalcular)

        tvCuota = findViewById(R.id.tvCuota)
        tvTotal = findViewById(R.id.tvTotal)
        tvIntereses = findViewById(R.id.tvIntereses)

        pantallaCredito = findViewById(R.id.pantallaCredito)
        pantallaMoneda = findViewById(R.id.pantallaMoneda)
        pantallaEjemplo = findViewById(R.id.pantallaEjemplo)
        pantallaInicio = findViewById(R.id.pantallaInicio)

        campoCantidad = findViewById(R.id.campoCantidad)
        radioGroupOrigen = findViewById(R.id.radioGroupOrigen)
        radioGroupDestino = findViewById(R.id.radioGroupDestino)
        tvResultado = findViewById(R.id.tvResultado)
        campoDato = findViewById(R.id.campoDato)
        etiMensaje = findViewById(R.id.etiMensaje)

        btnCalcular.setOnClickListener {
            calcularCredito()
        }
        findViewById<Button>(R.id.btnPantallaCredito).setOnClickListener { mostrarPantalla(pantallaCredito) }
        findViewById<Button>(R.id.btnPantallaMoneda).setOnClickListener { mostrarPantalla(pantallaMoneda) }
        findViewById<Button>(R.id.btnPantallaEjemplo).setOnClickListener { mostrarPantalla(pantallaEjemplo) }
        findViewById<Button>(R.id.btnInicioCredito).setOnClickListener { mostrarPantalla(pantallaCredito) }
        findViewById<Button>(R.id.btnInicioMoneda).setOnClickListener { mostrarPantalla(pantallaMoneda) }
        findViewById<Button>(R.id.btnInicioEjemplo).setOnClickListener { mostrarPantalla(pantallaEjemplo) }
        findViewById<Button>(R.id.btnConvertir).setOnClickListener { convertirMoneda() }
        findViewById<Button>(R.id.botonOk).setOnClickListener { mostrarMensaje() }
        mostrarPantalla(pantallaInicio)
    }

    private fun mostrarPantalla(pantalla: ScrollView) {
        pantallaInicio.visibility = if (pantalla == pantallaInicio) ScrollView.VISIBLE else ScrollView.GONE
        pantallaCredito.visibility = if (pantalla == pantallaCredito) ScrollView.VISIBLE else ScrollView.GONE
        pantallaMoneda.visibility = if (pantalla == pantallaMoneda) ScrollView.VISIBLE else ScrollView.GONE
        pantallaEjemplo.visibility = if (pantalla == pantallaEjemplo) ScrollView.VISIBLE else ScrollView.GONE
    }

    private fun calcularCredito() {

        val creditoTexto = etCredito.text.toString().trim()
        val cuotasTexto = etCuotas.text.toString().trim()
        val interesTexto = etInteres.text.toString().trim()

        if (creditoTexto.isEmpty() ||
            cuotasTexto.isEmpty() ||
            interesTexto.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Complete todos los campos",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val credito = creditoTexto.toDoubleOrNull()
        val cuotas = cuotasTexto.toIntOrNull()
        val interes = interesTexto.toDoubleOrNull()

        if (credito == null ||
            cuotas == null ||
            interes == null
        ) {

            Toast.makeText(
                this,
                "Ingrese valores numéricos válidos",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (credito <= 0 ||
            cuotas <= 0 ||
            interes < 0
        ) {

            Toast.makeText(
                this,
                "Ingrese valores válidos",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val tasa = interes / 100

        val cuota = if (tasa == 0.0) {
            credito / cuotas
        } else {
            credito * tasa /
                    (1 - (1 + tasa).pow(-cuotas))
        }

        val totalCredito = cuota * cuotas

        val totalIntereses = totalCredito - credito

        tvCuota.text =
            String.format(
                "Valor de la cuota: $%.2f",
                cuota
            )

        tvTotal.text =
            String.format(
                "Valor total del crédito: $%.2f",
                totalCredito
            )

        tvIntereses.text =
            String.format(
                "Total de intereses: $%.2f",
                totalIntereses
            )
    }

    private fun convertirMoneda() {
        val textoCantidad = campoCantidad.text.toString().trim()
        if (textoCantidad.isEmpty()) {
            Toast.makeText(this, "Ingresa una cantidad", Toast.LENGTH_SHORT).show()
            return
        }

        val origenId = radioGroupOrigen.checkedRadioButtonId
        val destinoId = radioGroupDestino.checkedRadioButtonId
        if (origenId == -1 || destinoId == -1) {
            Toast.makeText(this, "Selecciona moneda origen y destino", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidad = textoCantidad.toDoubleOrNull()
        if (cantidad == null) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val enUsd = cantidad / obtenerTasa(origenId)
        val resultado = enUsd * obtenerTasa(destinoId)
        tvResultado.text = String.format("Resultado: %.2f", resultado)
    }

    private fun obtenerTasa(radioButtonId: Int): Double {
        return when (radioButtonId) {
            R.id.radioCOP, R.id.radioCOPDestino -> tasaCop
            R.id.radioJPY, R.id.radioJYPDestino -> tasaJpy
            else -> tasaUsd
        }
    }

    private fun mostrarMensaje() {
        etiMensaje.text = "Mensaje: ${campoDato.text}"
    }
}