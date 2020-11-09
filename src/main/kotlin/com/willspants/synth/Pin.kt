package com.willspants.synth

data class Pin(val name: String,
               val type: PinType,
               val direction: PinDirection,
               var net: Net,
               var booleanValue: Boolean = false,
               var isValid: Boolean = false,
               val _metaFileLineReference: Int = -1) {
    var parentGate: Gate? = null

    var takenByTopolSort = false

    fun consumedByTopolSort(): Boolean {
        return takenByTopolSort
    }

    fun topolSortConsume() {
        takenByTopolSort = true
    }

    fun propagate() {
        net.propogate(booleanValue)
    }
}