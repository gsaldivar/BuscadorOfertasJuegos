package com.miapp.cheapshark

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miapp.cheapshark.api.CheapSharkApiService
import com.miapp.cheapshark.api.GameDeal
import com.miapp.cheapshark.api.MindicadorApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var etGameTitle: EditText
    private lateinit var btSearch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private var cheapSharkApiService: CheapSharkApiService? = null
    private var mindicadorApiService: MindicadorApiService? = null

    private var dolarValue: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etGameTitle = findViewById(R.id.etGameTitle)
        btSearch = findViewById(R.id.btSearch)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)

        // Configura el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupRetrofitServices()
        fetchDolarPrice()

        btSearch.setOnClickListener {
            val title = etGameTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                fetchGameDeals(title)
            } else {
                Toast.makeText(this, "Por favor, ingresa un título de juego", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRetrofitServices() {
        try {
            val cheapSharkRetrofit = Retrofit.Builder()
                .baseUrl("https://www.cheapshark.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            cheapSharkApiService = cheapSharkRetrofit.create(CheapSharkApiService::class.java)

            val mindicadorRetrofit = Retrofit.Builder()
                .baseUrl("https://mindicador.cl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            mindicadorApiService = mindicadorRetrofit.create(MindicadorApiService::class.java)

        } catch (e: Exception) {
            Log.e("CheapSharkApp", "Error al inicializar Retrofit", e)
            Toast.makeText(this, "Error al inicializar el servicio de red.", Toast.LENGTH_LONG).show()
            btSearch.isEnabled = false
        }
    }

    private fun fetchDolarPrice() {
        if (mindicadorApiService == null) return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = mindicadorApiService?.getDolarPrice()
                if (response != null && response.isSuccessful) {
                    dolarValue = response.body()?.dolar?.valor
                }
            } catch (e: Exception) {
                Log.e("CheapSharkApp", "Error al obtener el valor del dólar", e)
            }
        }
    }

    private fun fetchGameDeals(title: String) {
        if (cheapSharkApiService == null || dolarValue == null) {
            Toast.makeText(this, "Servicio API o valor del dólar no disponible.", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    cheapSharkApiService?.getGameDeals(title, limit = 10)
                }

                if (response != null && response.isSuccessful) {
                    val deals = response.body()
                    if (!deals.isNullOrEmpty()) {
                        updateUI(deals, dolarValue!!)
                    } else {
                        Toast.makeText(this@MainActivity, "No se encontraron ofertas para ese juego.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener los datos de los juegos.", Toast.LENGTH_SHORT).show()
                    Log.e("CheapSharkApp", "Error API: ${response?.code()} - ${response?.message()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ocurrió un error inesperado.", Toast.LENGTH_SHORT).show()
                Log.e("CheapSharkApp", "Excepción durante la búsqueda", e)
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateUI(deals: List<GameDeal>, dolarValue: Double) {
        val adapter = GameAdapter(deals, dolarValue)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
    }
}