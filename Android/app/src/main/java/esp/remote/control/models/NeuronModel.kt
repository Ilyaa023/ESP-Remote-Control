package esp.remote.control.models

class NeuronModel(
        var id: Int,
        var keys: Array<Int> = arrayOf(OIN_1, GND, OIN_1),
        var resistances: Array<Int> = arrayOf(32, 64),
) {
    companion object{
        const val OIN_1 = 1
        const val OIN_2 = 2
        const val EXTENSION_PIN = 3
        const val PWR = 4
        const val GND = 5
    }
} 