package com.willspants.podem

import java.io.File
import java.lang.RuntimeException

class Podem(private val file: File, private val stuckNet: Int, private val stuckVal: Int) {
    companion object {
        val ioTypes = listOf("INPUT", "OUTPUT")
        val singleInputTypes = listOf("INV", "BUF")
        val dualInputTypes = listOf("AND", "NAND", "OR", "NOR")
    }

    private val stuckValBoolean: Boolean = stuckVal == 1
    private var faultSensitized = false

    private val gates: MutableSet<PodemGate> = HashSet()
    private val inputs: MutableSet<Int> = HashSet()
    private val output: MutableSet<Int> = HashSet()

    private val simStateRecord: MutableList<HashMap<Int, PodemValue>> = ArrayList()
    private val simState: HashMap<Int, PodemValue> = HashMap()

    private val dFrontier: MutableList<PodemGate> = ArrayList()

    init {
        loadFromFile()
    }

    private fun loadFromFile() {
        file.forEachLine {
            val lineElements = it.split(" ")

            val gateType = lineElements[0]
            if (gateType in singleInputTypes) {
                val input = lineElements[1].toInt()
                val output = lineElements[2].toInt()
                gates.add(PodemGate(gateType, listOf(input), output))
                if (input !in simState) {
                    simState[input] = PodemValue.X
                }

                if (output !in simState) {
                    simState[output] = PodemValue.X
                }
            }
            else if (gateType in dualInputTypes) {
                val input1 = lineElements[1].toInt()
                val input2 = lineElements[2].toInt()
                val output = lineElements[3].toInt()

                gates.add(PodemGate(gateType, listOf(input1, input2), output))
                if (input1 !in simState) {
                    simState[input1] = PodemValue.X
                }

                if (input2 !in simState) {
                    simState[input2] = PodemValue.X
                }

                if (output !in simState) {
                    simState[output] = PodemValue.X
                }
            }
            else if (gateType == "INPUT") {
                for (i in (1..lineElements.size - 2)) {
                    val iLine = lineElements[i].toInt()
                    inputs.add(iLine)
                    simState[iLine] = PodemValue.X
                }
            }
            else if (gateType == "OUTPUT") {
                for (i in (1..lineElements.size - 2)) {
                    val oLine = lineElements[i].toInt()
                    output.add(oLine)
                    simState[oLine] = PodemValue.X
                }
            }
            else if (it.trim() == "") {
                // probably have a blank line
            } else {
                throw RuntimeException("Unknown type in circuit file: $gateType")
            }
        }
    }

    private fun propagateValues() {
        val gatesCopy = HashSet(gates)
        val evalSet: HashSet<PodemGate> = HashSet()

        var prevEvalSize: Int = Int.MAX_VALUE

        while (gatesCopy.isNotEmpty()) {
            for (gate in gatesCopy) {
                if (simState[gate.output]?.needsEvaluation()!!) {
                    evalSet.add(gate)
                }
            }

            val numEvals = evalSet.size
            while (evalSet.isNotEmpty()) {
                val evalGate = evalSet.first()
                evalSet.remove(evalGate)

                when (evalGate.type) {
                    "INV" -> {
                        simState[evalGate.output] = !simState[evalGate.inputs[0]]!!
                    }
                    "BUF" -> {
                        simState[evalGate.output] = simState[evalGate.inputs[0]]!!
                    }
                    "AND" -> {
                        simState[evalGate.output] = simState[evalGate.inputs[0]]!!.and(simState[evalGate.inputs[1]]!!)
                    }
                    "NAND" -> {
                        simState[evalGate.output] = simState[evalGate.inputs[0]]!!.nand(simState[evalGate.inputs[1]]!!)
                    }
                    "OR" -> {
                        simState[evalGate.output] = simState[evalGate.inputs[0]]!!.or(simState[evalGate.inputs[1]]!!)
                    }
                    "NOR" -> {
                        simState[evalGate.output] = simState[evalGate.inputs[0]]!!.nor(simState[evalGate.inputs[1]]!!)
                    }
                }

                if (evalGate.output == stuckNet) {
                    if (simState[evalGate.output]!! == PodemValue.fromBoolean(!stuckValBoolean)) {
                        faultSensitized = true
                        simState[evalGate.output] = PodemValue.fromSensitizedSav(stuckValBoolean)
                    }
                }
            }

            if (numEvals == prevEvalSize) {
                break
            } else {
                prevEvalSize = numEvals
            }
        }
    }

    private fun objective(): Pair<Int, PodemValue>? {
        // fault is at an input, trivial case
        if (stuckNet in inputs && simState[stuckNet] == PodemValue.X) {
            //println("stuck net is in inputs, directly initialize")
            return Pair(stuckNet, if (stuckValBoolean) PodemValue.D else PodemValue.D_BAR)
        }

        // state is unknown, we need to trace and control it
        if (simState[stuckNet] == PodemValue.X) {
            return Pair(stuckNet, !PodemValue.fromBoolean(stuckValBoolean))
        }

        // other, pick something from the d frontier
        //println("df $dFrontier")
        val cur = dFrontier.removeAt(0)
        //println("selected: $cur")
        val controllingValue = cur.getControllingPodemValue()
        //println("cv $controllingValue")
        cur.inputs.forEach {
            if (simState[it]!!.unknown()) {
                return Pair(it, !controllingValue)
            }
        }

        // issue
        return null
    }

    private fun backtrace(obj: Pair<Int, PodemValue>): Pair<Int, PodemValue>? {
        var curTgtNet: Int = obj.first
        var curTgtNetVal = obj.second

        //println("backtrace initial params $curTgtNet, $curTgtNetVal")

        while (curTgtNet !in inputs) {
            //println("loop")
            val curGate = gates.first { it.output == curTgtNet }
            //println("$curGate")
            when (curGate.type) {
                "BUF", "INV" -> {
                    curTgtNet = curGate.inputs[0]
                    curTgtNetVal = if (curGate.type == "INV") !curTgtNetVal else curTgtNetVal
                }
                "AND", "NAND", "OR", "NOR" -> {
                    curTgtNet = if (simState[curGate.inputs[0]]!!.unknown()) curGate.inputs[0] else curGate.inputs[1]
                    curTgtNetVal = curTgtNetVal.xor(curGate.getInversionPodemValue())
                }
                else -> {
                    throw RuntimeException("unsupported gate type")
                }
            }
            //println("new tgt: $curTgtNet")
            //println("new tgt val: $curTgtNetVal")
        }

        //println("return")

        return Pair(curTgtNet, curTgtNetVal)
    }

    private fun updateDFrontier() {
        dFrontier.clear()
        gates.forEach { gate ->
            when (gate.type) {
                "AND", "NAND", "OR", "NOR" -> {
                    if (gate.inputs.any { simState[it]!!.isFault() } && gate.inputs.any { simState[it]!!.unknown() }) {
                        dFrontier.add(gate)
                    }
                }
                else -> {}
            }
        }
    }

    private fun imply(implication: Pair<Int, PodemValue>) {
        simStateRecord.add(HashMap(simState))
        simState[implication.first] = implication.second
        propagateValues()
        updateDFrontier()
    }

    private fun podem(): Boolean {
        //println("invoke podem $simState")
        //println("")
        if (output.any { simState[it]!!.isFault() }) {
            //println("fault at output")
            return true
        }

        if (simState[stuckNet] == PodemValue.fromBoolean(stuckValBoolean)) {
            //println("infeasible sensitization")
            return false
        }

        if (faultSensitized && dFrontier.isEmpty()) {
            //println("empty d frontier")
            return false
        }

        val obj = objective() ?: return false
        //println("got objective $obj")

        var btTgt = backtrace(obj) ?: return false
        //println("got backtrace $btTgt")

        imply(btTgt)
        //println("state $simState")

        //println("check $btTgt")
        var success = podem()
        if (success) {
            //println("$btTgt good!")
            return true
        }

        //println("revert $btTgt")
        simStateRecord.removeAt(simStateRecord.size - 1)
        if (!btTgt.second.isFault()) {
            btTgt = Pair(btTgt.first, !btTgt.second)
        }

        //println("check inverse $btTgt")
        imply(btTgt)
        success = podem()
        if (success) {
            //println("inverse $btTgt good!")
            return true
        }

        //println("all options bad, unwind")
        simStateRecord.removeAt(simStateRecord.size - 1)
        imply(Pair(btTgt.first, PodemValue.X))
        return false
    }

    fun run(): Boolean {
        return podem()
    }

    fun getTestVectorString(allowUnknown: Boolean = true, replChar: Char = '0'): String {
        var ret = ""
        inputs.forEach {
            when {
                simState[it] == PodemValue.TRUE -> {
                    ret += "1"
                }
                simState[it] == PodemValue.FALSE -> {
                    ret += "0"
                }
                simState[it] == PodemValue.D -> {
                    ret += "0"
                }
                simState[it] == PodemValue.D_BAR -> {
                    ret += "1"
                }
                allowUnknown -> {
                    ret += "X"
                }
                else -> {
                    ret += replChar
                }
            }
      }

        return ret
    }
}