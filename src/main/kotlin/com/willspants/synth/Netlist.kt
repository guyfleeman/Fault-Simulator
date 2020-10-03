package com.willspants.synth

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class Netlist {
    companion object {
        private const val GLOBAL_INPUT_PIN_PREFIX = "I_GLB"
        private const val GLOBAL_OUTPUT_PIN_PREFIX = "O_GLB"

        private fun createOneInputGate(elements: List<String>, netlist: Netlist, lineCt: Int) {
            // recover data types from parsed strings
            val gateFunc = GateFunction.valueOf(elements[0])
            val inputPinNetIndex = elements[1].toInt()
            val outputPinNetIndex = elements[2].toInt()

            // access existing net, or create a new one
            val inputNet = netlist.getOrCreateNet(inputPinNetIndex)
            val outputNet = netlist.getOrCreateNet(outputPinNetIndex)

            // add line numbers to meta data for debugging
            inputNet._metaAddLineReference(lineCt)
            outputNet._metaAddLineReference(lineCt)

            // create input pin, cross link nets
            val inputPinList: ArrayList<Pin> = ArrayList()
            val inputPin = Pin("I", PinType.GATE, PinDirection.SINK, inputNet, _metaFileLineReference = lineCt)
            inputNet.addSink(inputPin)
            inputPinList.add(inputPin)

            // create output pin, cross link nets
            val outputPin = Pin("O", PinType.GATE, PinDirection.DRIVE, outputNet, _metaFileLineReference = lineCt)
            if (outputNet.hasDriver()) {
                println("ERROR: attempted creation of multi driver net on line $lineCt " +
                        "(previous driver was on ${outputNet.getDriver()._metaFileLineReference}")
                throw RuntimeException("net $outputNet already has a driver, multi driven nets are not allowed")
            }
            outputNet.setDriver(outputPin)

            // create gate
            val g = Gate(gateFunc, inputPinList, outputPin)

            inputPin.parentGate = g
            outputPin.parentGate = g
        }

        private fun createTwoInputGate(elements: List<String>, netlist: Netlist, lineCt: Int) {
            // recover data type from parsed strings
            val gateFunc = GateFunction.valueOf(elements[0])
            val inputPin1NetIndex = elements[1].toInt()
            val inputPin2NetIndex = elements[2].toInt()
            val outputPinNetIndex = elements[3].toInt()

            val input1Net = netlist.getOrCreateNet(inputPin1NetIndex)
            val input2Net = netlist.getOrCreateNet(inputPin2NetIndex)
            val outputNet = netlist.getOrCreateNet(outputPinNetIndex)

            input1Net._metaAddLineReference(lineCt)
            input2Net._metaAddLineReference(lineCt)
            outputNet._metaAddLineReference(lineCt)

            val inputPinList: ArrayList<Pin> = ArrayList()
            val input1Pin = Pin("I0", PinType.GATE, PinDirection.SINK, input1Net, _metaFileLineReference = lineCt)
            val input2Pin = Pin("I1", PinType.GATE, PinDirection.SINK, input2Net, _metaFileLineReference = lineCt)
            input1Net.addSink(input1Pin)
            input2Net.addSink(input2Pin)
            inputPinList.add(input1Pin)
            inputPinList.add(input2Pin)

            val outputPin = Pin("O", PinType.GATE, PinDirection.DRIVE, outputNet, _metaFileLineReference = lineCt)
            if (outputNet.hasDriver()) {
                println("ERROR: attempted creation of multi driver net on line $lineCt " +
                        "(previous driver was on ${outputNet.getDriver()._metaFileLineReference}")
                throw RuntimeException("net $outputNet already has a driver, multi driven nets are not allowed")
            }
            outputNet.setDriver(outputPin)

            val g = Gate(gateFunc, inputPinList, outputPin)

            input1Pin.parentGate = g
            input2Pin.parentGate = g
            outputPin.parentGate = g
        }

        fun parseNetlist(sourceFilename: String): Netlist {
            return parseNetlist(File(sourceFilename))
        }

        fun parseNetlist(sourceFile: File): Netlist {
            val reader = BufferedReader(FileReader(sourceFile))

            val netlist = Netlist()

            var lineCt = 1
            reader.forEachLine {
                val elements = it.split(" ")
                when (elements[0]) {
                    "INPUT" -> {
                        for (i in 1 until elements.size - 1) {
                            val netIndex = elements[i].toInt()
                            val inputPinNet: Net = netlist.getOrCreateNet(netIndex)
                            inputPinNet._metaAddLineReference(lineCt)
                            val inputPin = Pin("${GLOBAL_INPUT_PIN_PREFIX}_${i - 1}",
                                PinType.GLOBAL, PinDirection.DRIVE, inputPinNet)
                            inputPinNet.setDriver(inputPin)
                            netlist.globalInputPins.add(inputPin)
                        }
                    }
                    "OUTPUT" -> {
                        for (i in 1 until elements.size - 1) {
                            val netIndex = elements[i].toInt()
                            val outputPinNet: Net = netlist.getOrCreateNet(netIndex)
                            outputPinNet._metaAddLineReference(lineCt)
                            val outputPin = Pin("${GLOBAL_OUTPUT_PIN_PREFIX}_${i - 1}",
                                PinType.GLOBAL, PinDirection.DRIVE, outputPinNet)
                            outputPinNet.addSink(outputPin)
                            netlist.globalOutputPins.add(outputPin)
                        }
                    }
                    "INV", "BUF" -> {
                        createOneInputGate(elements, netlist, lineCt)
                    }
                    "NAND", "AND", "NOR", "OR", "XNOR", "XOR" -> {
                        createTwoInputGate(elements, netlist, lineCt)
                    }
                }

                lineCt++
            }

            return netlist
        }
    }

    private val gates: HashSet<Gate> = HashSet()
    private var hierarchicalGates: ArrayList<Gate> = ArrayList()
    private val globalInputPins: HashSet<Pin> = HashSet()
    private val globalOutputPins: HashSet<Pin> = HashSet()

    private val nets: HashMap<Int, Net> = HashMap()

    private var inputVector: Array<Boolean>? = null

    fun validate(): Boolean {
        if (!isFullyConnected()) {
            return false
        }

        if (hasCycles()) {
            return false
        }

        return true
    }

    private fun isFullyConnected(): Boolean {
        // check net sanity
        // each net should have a source and sink
        nets.forEach {
            if (!it.value.hasSink()) {
                throw java.lang.RuntimeException("Net ${it.value.getName()} has no sinks! " +
                        "(defined on line ${it.value.getLineReferencesDebugString()})")
            }
        }

        // gates can't be created without net references, so they can't exist in an invalid configuration

        return true
    }

    private fun hasCycles(): Boolean {
        globalInputPins.forEach { gip ->
            gip.net.getSinks().forEach { sinkToGip ->
                dfsCheckCycleFrame(sinkToGip, HashSet())
            }
        }

        return false
    }

    private fun dfsCheckCycleFrame(arrivalPin: Pin, visitedSet: HashSet<Gate>): Boolean {
        if (arrivalPin.parentGate == null) {
            // we found a global sink
            // no cycles here, do nothing
            return false
        }

        val parentGate: Gate = arrivalPin.parentGate!!
        if (parentGate in visitedSet) {
            // we have a loopback to a gate already on this driving network
            // this is not currently supported
            println("found backedge at AP: $arrivalPin, net ${arrivalPin.net}")
            return true
        }

        val updatedVisitedSet: HashSet<Gate> = HashSet(visitedSet)
        updatedVisitedSet.add(parentGate)

        parentGate.getDrivePin().net.getSinks().forEach {
            val subTreeHasBackEdge = dfsCheckCycleFrame(it, updatedVisitedSet)
            if (subTreeHasBackEdge) {
                println("backedge trace at AP: $it, net ${it.net}")
                return true
            }
        }

        return false
    }

    fun doTopologicalSort() {
        val globalInputs = HashSet(globalInputPins)
        val gates = HashSet<Gate>(gates)
        val nets = HashSet<Net>(nets.values)

        val sortedGates = ArrayList<Gate>()

        while (globalInputs.isNotEmpty()) {
            val curPin = globalInputs.first()
            globalInputs.remove(curPin)

            if (curPin.parentGate != null) {
                sortedGates.add(curPin.parentGate!!)
            }

            curPin.net.getSinks().forEach { curNetSink ->
                curNetSink.topolSortConsume()

                if (curNetSink !in globalOutputPins) {
                    if (curNetSink.parentGate?.getSinkPins()?.all { it.consumedByTopolSort() }!!) {
                        globalInputs.add(curNetSink.parentGate?.getDrivePin())
                    }
                }
            }
        }

        hierarchicalGates = sortedGates
    }

    fun loadInputVector(inputVector: Array<Boolean>) {
        this.inputVector = inputVector
    }

    fun propagateInputVector() {
        val liv = inputVector ?: throw java.lang.RuntimeException("input vector in uninitialized")

        if (liv.size != globalInputPins.size) {
            throw java.lang.RuntimeException("input vector is not mappable to input pins")
        }

        globalInputPins.forEach {
            val name = it.name
            val pinIndex = name.split("_").last().toInt()
            it.booleanValue = liv[pinIndex]
            it.isValid = true
            it.propagate()
        }

        hierarchicalGates.forEach() {
            it.propagate()
        }
    }

    fun getOutputVector(): Array<Boolean> {
        val outputVector = Array(globalOutputPins.size) { false }

        globalOutputPins.forEach {
            val name = it.name
            val pinIndex = name.split("_").last().toInt()
            outputVector[pinIndex] = it.booleanValue
        }

        return outputVector
    }

    fun getOrCreateNet(netIndex: Int): Net {
        if (nets.containsKey(netIndex)) {
            return nets[netIndex]!!
        }

        val net = Net(netIndex)
        nets[netIndex] = net

        return net
    }

    fun addGate(gate: Gate) {
        gates.add(gate)
    }
}