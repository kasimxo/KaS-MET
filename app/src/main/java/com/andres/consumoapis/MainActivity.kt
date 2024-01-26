package com.andres.consumoapis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    }

    private fun searchByName(query: String) {

        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<METDataResponse> =
                retrofit.create(ApiService::class.java).getQuery(query)
            Log.i("consulta", query)
            if (myResponse.isSuccessful) {



                val response: METDataResponse? = myResponse.body()



                if (response != null && response.ids != null) {

                    var ids = response.ids
                    if (ids.size>10) {

                        ids = ids.subList(0,10)

                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        var objetos : List<METItemResponse> = ArrayList<METItemResponse>()
                        for (id in ids) {
                            objetos += retrofit.create(ApiService::class.java).getObjectData(id)
                        }



                            runOnUiThread {

                                adapter.updateList(objetos)
                                binding.responseTotal.text = "Results: ${response.total}"
                                binding.progressBar.isVisible = false

                            }
                        }
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


    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://collectionapi.metmuseum.org/public/collection/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    }
}