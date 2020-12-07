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

        fun getInversionValue(gate: Gate): Boolean {
            return getInversionValue(gate.gateFunction)
        }

        fun getInversionValue(type: GateFunction): Boolean {
            return when (type) {
                GateFunction.INV, GateFunction.NAND, GateFunction.NOR, GateFunction.XNOR -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        fun getControllingValue(gate: Gate): GateControllingType {
            return getControllingValue(gate.gateFunction)
        }

        fun getControllingValue(type: GateFunction): GateControllingType {
            return when (type) {
                GateFunction.INV, GateFunction.BUF, GateFunction.XNOR, GateFunction.XOR -> {
                    GateControllingType.ALL
                }
                GateFunction.NOR, GateFunction.OR -> {
                    GateControllingType.ZERO
                }
                GateFunction.NAND, GateFunction.AND -> {
                    GateControllingType.ONE
                }
            }
        }

        fun getPodemControllingValue(gate: Gate): PodemValue {
            return getPodemControllingValue(gate.gateFunction)
        }

        fun getPodemControllingValue(type: GateFunction): PodemValue {
            return when (type) {
                GateFunction.AND, GateFunction.NAND -> {
                    PodemValue.L_FALSE
                }
                GateFunction.OR, GateFunction.NOR -> {
                    PodemValue.L_TRUE
                }
                else -> {
                    PodemValue.UNKNOWN
                }
            }
        }
    }

    private val pins: HashSet<Pin> = HashSet()
    private val name: String
    private var consumedByTopolSort = false

    private val applicableLocalFaults: Set<NetFaultType> = HashSet()
    private val applicableUpstreamFaults: Set<NetFaultType> = HashSet()

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

    fun getInversion(): Boolean {
        return getInversionValue(gateFunction)
    }

    fun getControlling(): GateControllingType {
        return getControllingValue(this)
    }

    fun propagateDeduction() {
//        println("Propagating Faults for $gateFunction and net ${getDrivePin().net.getIndex()}")
//        getSinkPins().forEach {
//            println(it.net)
//        }

        val gateInversion = getInversion()
        val gateControllingType = getControlling()

        val allInputNets = HashSet(getSinkPins().map { it.net })
        val controllingInputNets = HashSet(getSinkPins().filter { it.net.isControlling(gateControllingType) }.map { it.net })

        val downstreamFaults: MutableSet<Fault> = HashSet()
        if (controllingInputNets.size == 2) {
            inputPins.forEach { downstreamFaults.addAll(it.net.getAllFaults()) }
            downstreamFaults.add(Fault.createFromGate(this))
        } else {
            val nonControllingNets = allInputNets - controllingInputNets
            val allNonControllingFaults: MutableSet<Fault> = HashSet()
            nonControllingNets.forEach { allNonControllingFaults.addAll(it.getAllFaults()) }

            val allControllingFaults: MutableSet<Fault> = HashSet()
            controllingInputNets.forEach { allControllingFaults.addAll(it.getAllFaults()) }

            if (allControllingFaults.isEmpty()) {
                downstreamFaults.add(Fault.createFromGate(this, true))
            } else {
                var faultDifference = allControllingFaults - allNonControllingFaults
                if (gateFunction == GateFunction.NAND || gateFunction == GateFunction.AND
                    || gateFunction == GateFunction.NOR || gateFunction == GateFunction.OR) {
                    faultDifference =  allNonControllingFaults - allControllingFaults
                }
                downstreamFaults.addAll(faultDifference + Fault.createFromGate(this, true))
            }
        }

        getDrivePin().net.getAllFaults().addAll(downstreamFaults)
    }

    fun podemPropagate() {
        when (gateFunction) {
            GateFunction.BUF -> {
                outputPin.podemValue = inputPins[0].podemValue
            }
            GateFunction.INV -> {
                outputPin.podemValue = invertPodemValueWeak(inputPins[0].podemValue)
            }
            GateFunction.AND -> {
                if (inputPins.any { it.podemValue == getPodemControllingValue(gateFunction) }) {
                    outputPin.podemValue = PodemValue.L_FALSE
                    return
                }

                if (inputPins.any { podemValueIsFault(it.podemValue) } && inputPins.any { it.podemValue == invertPodemValueWeak(
                        getPodemControllingValue(gateFunction))}) {
                    outputPin.podemValue = inputPins.first { podemValueIsFault(it.podemValue) }.podemValue
                    return
                }

                if (inputPins.any { it.podemValue == PodemValue.UNKNOWN || it.podemValue == PodemValue.UNSET}) {
                    outputPin.podemValue = PodemValue.UNKNOWN
                    return
                }

                outputPin.podemValue = PodemValue.L_TRUE
            }
            GateFunction.NAND -> {
                if (inputPins.any { it.podemValue == getPodemControllingValue(gateFunction) }) {
                    outputPin.podemValue = PodemValue.L_TRUE
                    return
                }

                if (inputPins.any { podemValueIsFault(it.podemValue) } && inputPins.any { it.podemValue == invertPodemValueWeak(
                        getPodemControllingValue(gateFunction))}) {
                    outputPin.podemValue = invertPodemValueWeak(inputPins.first { podemValueIsFault(it.podemValue) }.podemValue)
                    return
                }

                if (inputPins.any { it.podemValue == PodemValue.UNKNOWN || it.podemValue == PodemValue.UNSET}) {
                    outputPin.podemValue = PodemValue.UNKNOWN
                    return
                }

                outputPin.podemValue = PodemValue.L_FALSE
            }
            GateFunction.OR -> {
                if (inputPins.any { it.podemValue == getPodemControllingValue(gateFunction) }) {
                    outputPin.podemValue = PodemValue.L_TRUE
                    return
                }

                if (inputPins.any { podemValueIsFault(it.podemValue) } && inputPins.any { it.podemValue == invertPodemValueWeak(
                        getPodemControllingValue(gateFunction))}) {
                    outputPin.podemValue = inputPins.first { podemValueIsFault(it.podemValue) }.podemValue
                    return
                }

                if (inputPins.any { it.podemValue == PodemValue.UNKNOWN || it.podemValue == PodemValue.UNSET}) {
                    outputPin.podemValue = PodemValue.UNKNOWN
                    return
                }

                outputPin.podemValue = PodemValue.L_FALSE
            }
            GateFunction.NOR -> {
                if (inputPins.any { it.podemValue == getPodemControllingValue(gateFunction) }) {
                    outputPin.podemValue = PodemValue.L_FALSE
                    return
                }

                if (inputPins.any { podemValueIsFault(it.podemValue) } && inputPins.any { it.podemValue == invertPodemValueWeak(
                        getPodemControllingValue(gateFunction))}) {
                    outputPin.podemValue = invertPodemValueWeak(inputPins.first { podemValueIsFault(it.podemValue) }.podemValue)
                    return
                }

                if (inputPins.any { it.podemValue == PodemValue.UNKNOWN || it.podemValue == PodemValue.UNSET}) {
                    outputPin.podemValue = PodemValue.UNKNOWN
                    return
                }

                outputPin.podemValue = PodemValue.L_TRUE
            }
            else -> throw RuntimeException("unsupported gate type")
        }

        outputPin.net.podemImpliedValue = outputPin.podemValue
    }

    fun podemIsOnDFrontier(): Boolean {
        if (inputPins.any { podemValueIsFault(it.podemValue) } && outputPin.podemValue == PodemValue.UNKNOWN) {
            return true
        }

        return false
    }
}
