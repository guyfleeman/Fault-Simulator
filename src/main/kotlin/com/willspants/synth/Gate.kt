package com.willspants.synth

import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashSet


class Gate(var gateFunction: GateFunction, private val inputPins: List<Pin>, private val outputPin: Pin) {
    companion object {
        private val staticGateCounts: EnumMap<GateFunction, Int> = EnumMap(GateFunction::class.java)

        init {
            GateFunction.values().forEach {
                staticGateCounts[it] = 0
            }
        }

        fun getGateInstanceName(gateFunction: GateFunction): String {
            val curGateCt = staticGateCounts[gateFunction]
            val gateInstanceName = gateFunction.toString().toUpperCase() + "_" + curGateCt;
            if (curGateCt != null) {
                staticGateCounts[gateFunction] = curGateCt + 1
            }
            return gateInstanceName
        }
    }

    private val pins: HashSet<Pin> = HashSet()
    private val name: String
    private var consumedByTopolSort = false

    var driveValid = false

    init {
        name = Gate.getGateInstanceName(gateFunction)

        pins.addAll(inputPins)
        pins.add(outputPin)
    }

    fun topolSortConsume() {
        consumedByTopolSort = true
    }

    fun consumedByTopolSort(): Boolean {
        return consumedByTopolSort
    }

    fun getSinkPins(): List<Pin> {
        return inputPins
    }

    fun getDrivePin(): Pin {
        return outputPin
    }

    fun propagate() {
        getSinkPins().forEach { if (!it.isValid) throw RuntimeException("Attempted to propagate on invalid nets.") }

        val gateValue: Boolean = when (gateFunction) {
            GateFunction.INV -> !getSinkPins()[0].booleanValue
            GateFunction.BUF -> getSinkPins()[0].booleanValue
            GateFunction.NAND -> !(getSinkPins()[0].booleanValue && getSinkPins()[1].booleanValue)
            GateFunction.AND -> (getSinkPins()[0].booleanValue && getSinkPins()[1].booleanValue)
            GateFunction.NOR -> !(getSinkPins()[0].booleanValue || getSinkPins()[1].booleanValue)
            GateFunction.OR -> (getSinkPins()[0].booleanValue || getSinkPins()[1].booleanValue)
            GateFunction.XNOR -> !(getSinkPins()[0].booleanValue.xor(getSinkPins()[1].booleanValue))
            GateFunction.XOR -> (getSinkPins()[0].booleanValue.xor(getSinkPins()[1].booleanValue))
        }

        getDrivePin().booleanValue = gateValue
        getDrivePin().isValid = true
        driveValid = true

        getDrivePin().propagate()
    }

    fun getBooleanValue(): Boolean {
        return getDrivePin().booleanValue
    }
}
