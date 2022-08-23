package esp.remote.control.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import esp.remote.control.models.CounterModel
import esp.remote.control.models.IdsModel
import esp.remote.control.models.NeuronModel
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class Connection (address: String) {
    companion object{
        const val SUCCESS = 0
        const val FAIL = 1
    }
    private val service: APIService
    init {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(" http://${address}/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        service = retrofit.create(APIService::class.java)
    }

    fun start(callback: (Int) -> Unit){
        val call = service.loadCount()
        call.enqueue(object : Callback<CounterModel>{
            override fun onResponse(call: Call<CounterModel>, response: Response<CounterModel>) {
                callback(if (response.isSuccessful && response.body() != null) SUCCESS else FAIL)
            }
            override fun onFailure(call: Call<CounterModel>, t: Throwable) {
                callback(FAIL)
            }
        })
    }

    fun getIds(callback: (Int, IdsModel?) -> Unit){
        val call = service.loadIds()
        call.enqueue(object : Callback<IdsModel>{
            override fun onResponse(call: Call<IdsModel>, response: Response<IdsModel>) {
                callback(
                    if (response.isSuccessful) SUCCESS else FAIL,
                    response.body()
                )
            }
            override fun onFailure(call: Call<IdsModel>, t: Throwable) {
                callback(FAIL, null)
            }
        })
    }

    fun getValues(id: Int, callback: (Int, NeuronModel?) -> Unit){
        val call = service.loadValues(id)
        call.enqueue(object : Callback<NeuronModel>{
            override fun onResponse(call: Call<NeuronModel>, response: Response<NeuronModel>) {
                callback(
                    if (response.isSuccessful) SUCCESS else FAIL,
                    response.body()
                )
            }
            override fun onFailure(call: Call<NeuronModel>, t: Throwable) {
                callback(FAIL, null)
            }
        })
    }

    fun setKey(id: Int, key: Int, state: Int, callback: (Int, NeuronModel?) -> Unit){
        val call = service.loadKeys(id = id, key = key, keyValue = state)
        call.enqueue(object : Callback<NeuronModel>{
            override fun onResponse(call: Call<NeuronModel>, response: Response<NeuronModel>) {
                callback(
                    if (response.isSuccessful) SUCCESS else FAIL,
                    response.body()
                )
            }
            override fun onFailure(call: Call<NeuronModel>, t: Throwable) {
                callback(FAIL, null)
            }
        })
    }

    fun setRes(id: Int, resIndex: Int, res: Int, callback: (Int, NeuronModel?) -> Unit){
        val call = service.loadRes(id = id, res = resIndex, resValue = res)
        call.enqueue(object : Callback<NeuronModel>{
            override fun onResponse(call: Call<NeuronModel>, response: Response<NeuronModel>) {
                callback(
                    if (response.isSuccessful) SUCCESS else FAIL,
                    response.body()
                )
            }
            override fun onFailure(call: Call<NeuronModel>, t: Throwable) {
                callback(FAIL, null)
            }
        })
    }
}