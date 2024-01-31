package com.andres.consumoapis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.andres.consumoapis.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit

    private lateinit var adapter: METAdapter

    //Esta variable almacernará todas las ids de resultados
    private lateinit var resultados: List<IDsCollection>
    private var index: Int = 0
    private var numeroTotal: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {

        //Esto siempre la primera
        super.onCreate(savedInstanceState)

        //La api que vamos a utilizar es la del
        // The Metropolitan Museum of Art Collection API
        // https://metmuseum.github.io/

        //Esto es para poder usar el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Esto es únicamente para que el recyclerview tenga un snap a la pantalla
        val snap = LinearSnapHelper()
        snap.attachToRecyclerView(binding.rvItems)

        retrofit = getRetrofit()
        initUI()
    }



    private fun moveIndex(valor: Int){
        index += valor
        if(index<0){
            //Vamos a la última página
            index = resultados.size-1
        } else if(index>= resultados.size){
            //Reset a la primera
            index = 0
        }
        cargarItems(numeroTotal)
    }

    private fun initUI() {
        binding.searchbar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Añadimos el orEmpty para que si está nulo no haga na
                searchByName(query.orEmpty())
                return false
            }
            override fun onQueryTextChange(newText: String?) = false
        })
        adapter = METAdapter ()
        binding.rvItems.setHasFixedSize(true)
        binding.rvItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvItems.adapter = adapter

        binding.btnLeftMain.setOnClickListener { moveIndex(-1) }
        binding.btnRightMain.setOnClickListener { moveIndex(1) }
    }

    private fun searchByName(query: String) {

        //Reseteamos las variables
        resultados = mutableListOf<IDsCollection>()
        index = 0
        numeroTotal = 0

        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<METDataResponse> =
                retrofit.create(ApiService::class.java).getQuery(query)
            Log.i("consulta", query)
            if (myResponse.isSuccessful) {

                val response: METDataResponse? = myResponse.body()

                if (response != null && response.ids != null) {

                    //Aquí tenemos que insertar un método que devuleva lista de listas
                    var ids : List<String> = response.ids
                    var tot = ids.size
                    numeroTotal = ids.size
                    var ind = 0

                    while (ind <= tot-10) {
                        var partlist : IDsCollection = IDsCollection(ids.subList(ind, ind+10))
                        resultados += partlist
                        ind += 10
                    }
                    cargarItems(tot)

                } else {
                    runOnUiThread {

                        binding.progressBar.isVisible = false
                        binding.responseTotal.text = "No results found"

                    }
                }
            } else {
                Log.i("Consulta", "No funciona :(")
            }
        }
    }

    private fun cargarItems(total: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            var objetos : List<METItemResponse> = ArrayList<METItemResponse>()

            for (id in resultados.get(index).Ids) {

                //Esta comprobación la hacemos porque hay casos donde devuelve una id que luego no existe
                val obj: Response<METItemResponse> =
                    retrofit.create(ApiService::class.java).getObjectData(id)
                val objR : METItemResponse? = obj.body()
                if(objR != null) {objetos += objR}
            }

            runOnUiThread {
                adapter.updateList(objetos)
                binding.responseTotal.text = "Results: ${total}"
                binding.progressBar.isVisible = false
                //Hacemos visibles los botones despues de la carga de imágenes para que quede mas natural
                if (total>10){

                    binding.btnRightMain.isVisible = true
                    binding.btnLeftMain.isVisible = true
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://collectionapi.metmuseum.org/public/collection/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}