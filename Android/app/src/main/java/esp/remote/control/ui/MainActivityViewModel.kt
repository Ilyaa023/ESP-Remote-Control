package esp.remote.control.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import esp.remote.control.data.Connection
import esp.remote.control.models.IdsModel
import esp.remote.control.models.NeuronModel
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class MainActivityViewModel: ViewModel() {
    val address = MutableLiveData("")
    val resistance1 = MutableLiveData("32")
    val resistance2 = MutableLiveData("64")
    val addressCorrect = MutableLiveData(true)
    val idsList = MutableLiveData<ArrayList<Int>>()

    val selectedId = MutableLiveData<Int>()
    val keyStates = MutableLiveData<Array<Int>>()

    private lateinit var connection: Connection

    fun setAddress(ipStr: String){
        address.value = ipStr
    }

    fun checkAndConnect(callback: (Int) -> Unit){
        addressCorrect.value = address.value!!.matches(Regex(
            //"([0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]{1,5})"))
            "([0-9]{1,3}\\.){3}[0-9]{1,3}"))
        if (addressCorrect.value!!) {
            connection = Connection(address = address.value!!)
            connection.start{
                callback(it)
                if (it == Connection.SUCCESS)
                    connection.getIds{ result, ids ->
                        if (result == Connection.SUCCESS) {
                            idsList.value = ids!!.ids.toCollection(ArrayList())
                            setId(ids.ids.first())
                        }
                        callback(result)
                    }
            }
        }
    }

    fun setId(id: Int){
        selectedId.value = id
        connection.getValues(id){ result, neuron ->
            if (result == Connection.SUCCESS){
                keyStates.value = neuron!!.keys
                resistance1.value = neuron.resistances[0].toString()
                resistance2.value = neuron.resistances[1].toString()
            }
        }
    }
    fun setKey(keyIndex: Int, state: Int){
        when(keyIndex){
            0, 2 -> if (state == NeuronModel.GND || state == NeuronModel.PWR){
                return
            }
            1 -> if(state != NeuronModel.GND && state != NeuronModel.PWR){
                return
            }
        }
        selectedId.value?.let {
            connection.setKey(id = it, key = keyIndex, state = state){ result, neuron ->
                if (result == Connection.SUCCESS){
                    keyStates.value = neuron!!.keys
                    resistance1.value = neuron.resistances[0].toString()
                    resistance2.value = neuron.resistances[1].toString()
                }
            }
        }
    }
    fun setRes(resIndex: Int, res: String){
        if (res.matches(Regex("[0-9]{0,2}"))){
            when (resIndex){ 0 -> resistance1.value = res 1 -> resistance2.value = res}
            try {
                val resistance = res.toInt()
                if (resistance in 1..64)
                    connection.setRes(id = selectedId.value!!,
                                      resIndex = resIndex, res = resistance){ result, neuron ->
                        if (result == Connection.SUCCESS){
                            keyStates.value = neuron!!.keys
                            resistance1.value = neuron.resistances[0].toString()
                            resistance2.value = neuron.resistances[1].toString()
                        }
                    }
            }catch (e: NumberFormatException){}
        }
    }
}