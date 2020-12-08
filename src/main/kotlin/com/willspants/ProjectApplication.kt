package com.willspants

import com.willspants.podem.Podem
import com.willspants.synth.Fault
import com.willspants.synth.NetFaultType
import com.willspants.synth.Netlist
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random
import kotlin.system.exitProcess

class ProjectApplication {
    companion object {
        private fun printHelp() {
            println("")
            println("")
            println("USAGE:")
            println("sim --circuit-file=<file> --input-vector=<vector> (? --fault-file=<file> | --fault=<N,V>)")
            println("deductive --circuit-file=<file> --input-vector=<vector> (? --fault-file=<file> | --fault=<N,V>)")
            println("random --circuit-file=<file> --percent-coverage=<0-100>")
            println("podem --circuit-file=<file> --fault=<N, V> (? --verify)")
            println("")
            println("chevrons contain values, e.g. type \"--fault=1,1\" not \"--fault=<1,1>\"")
            println("")
            println("")
        }

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) {
                printHelp()
                exitProcess(1)
            }

            when (args[0].toLowerCase()) {
                "sim" -> {
                    if (args.size != 3 && args.size != 4) {
                        printHelp()
                        exitProcess(1)
                    }

                    if (!args[1].startsWith("--circuit-file=")
                        && !args[2].startsWith("--input-vector=")) {
                        printHelp()
                        exitProcess(1)
                    }

                    val circuitFile = args[1].split("=")[1]
                    val inputVectorStr = args[2].split("=")[1]

                    val faults: HashMap<Int, NetFaultType> = HashMap()
                    if (args.size == 4) {
                        if (!(args[3].startsWith("--fault-file=") || args[3].startsWith("--fault="))) {
                            printHelp()
                            exitProcess(1)
                        }

                        if (args[3].startsWith("--fault-file=")) {
                            val faultFile = File(args[3].split("=")[1])
                            faultFile.forEachLine {
                                val faultEls = it.split(" ")
                                if (faultEls.size != 2) {
                                    println("malformed faults file")
                                }
                                val netNo = faultEls[0].trim().toInt()
                                val netValue = if (faultEls[1].trim().toInt() == 0) NetFaultType.S_A_0 else NetFaultType.S_A_1
                                println("Adding fault NET_$netNo ${netValue.toString().toLowerCase().replace("_", "-")}.")
                                faults[netNo] = netValue
                            }
                        }

                        if (args[3].startsWith("--fault=")) {
                            val fault = args[3].split("=")[1]
                            val faultEls = fault.split(",")
                            val netNo = faultEls[0].trim().toInt()
                            val netValue = if (faultEls[1].trim().toInt() == 0) NetFaultType.S_A_0 else NetFaultType.S_A_1
                            println("Adding fault NET_$netNo ${netValue.toString().toLowerCase().replace("_", "-")}.")
                            faults[netNo] = netValue
                        }
                    }

                    val circuit = Netlist.parseNetlist(circuitFile)
                    val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
                    circuit.loadNetFaults(faults)
                    circuit.applyFaults()
                    val outputVector = ParserRunner.runCircuit(circuit, inputVector)

                    println("")
                    println("Simulating IV: $inputVectorStr for $circuitFile")
                    val outputVectorString = ParserRunner.outputVectorToString(outputVector)
                    println("OV: $outputVectorString")
                }
                "deductive" -> {
                    if (args.size != 3 && args.size != 4) {
                        printHelp()
                        exitProcess(1)
                    }

                    if (!args[1].startsWith("--circuit-file=")
                        && !args[2].startsWith("--input-vector=")) {
                        printHelp()
                        exitProcess(1)
                    }

                    val circuitFile = args[1].split("=")[1]
                    val inputVectorStr = args[2].split("=")[1]

                    val faults: HashMap<Int, NetFaultType> = HashMap()
                    if (args.size == 4) {
                        if (!(args[3].startsWith("--fault-file=") || args[3].startsWith("--fault="))) {
                            printHelp()
                            exitProcess(1)
                        }

                        if (args[3].startsWith("--fault-file=")) {
                            val faultFile = File(args[3].split("=")[1])
                            faultFile.forEachLine {
                                val faultEls = it.split(" ")
                                if (faultEls.size != 2) {
                                    println("malformed faults file")
                                }
                                val netNo = faultEls[0].trim().toInt()
                                val netValue =
                                    if (faultEls[1].trim().toInt() == 0) NetFaultType.S_A_0 else NetFaultType.S_A_1
                                println(
                                    "Adding fault NET_$netNo ${
                                        netValue.toString().toLowerCase().replace("_", "-")
                                    }."
                                )
                                faults[netNo] = netValue
                            }
                        }

                        if (args[3].startsWith("--fault=")) {
                            val fault = args[3].split("=")[1]
                            val faultEls = fault.split(",")
                            val netNo = faultEls[0].trim().toInt()
                            val netValue =
                                if (faultEls[1].trim().toInt() == 0) NetFaultType.S_A_0 else NetFaultType.S_A_1
                            println("Adding fault NET_$netNo ${netValue.toString().toLowerCase().replace("_", "-")}.")
                            faults[netNo] = netValue
                        }
                    }

                    val circuit = Netlist.parseNetlist(circuitFile)

                    println("")
                    println("Simulating - IV: $inputVectorStr for $circuitFile")
                    circuit.doTopologicalSort()
                    circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
                    circuit.propagateInputVector()

                    println("Running Deductive Fault Simulation - IV: $inputVectorStr for $circuitFile")
                    circuit.applyCheckpointFaults()
                    circuit.propagateDeduction()

                    if (faults.isEmpty()) {
                        println(circuit.getFormattedFaults())
                    } else {
                        println("Checking if specified faults were detected...")
                        val deductiveFaults = circuit.getFaultSet()
                        val notDetectedFaults: HashSet<Fault> = HashSet()
                        faults.forEach {
                            val fault = Fault(it.key, it.value)
                            if (fault in deductiveFaults) {
                                println("+ $fault detected!")
                            } else {
                                println("- $fault not detected!")
                                notDetectedFaults.add(fault)
                            }
                        }

                        println("")
                        if (notDetectedFaults.isNotEmpty()) {
                            println("MISSED FAULTS:")
                            notDetectedFaults.forEach {
                                println(it)
                            }
                            println("")
                            exitProcess(1)
                        } else {
                            println("ALL FAULTS DETECTED")
                            println("")
                            exitProcess(0)
                        }
                    }
                }
                "random" -> {
                    if (args.size != 3) {
                        printHelp()
                        exitProcess(1)
                    }

                    if (!args[1].startsWith("--circuit-file=")
                        && !args[2].startsWith("--percent-coverage=")) {
                        printHelp()
                        exitProcess(1)
                    }

                    val circuitFile = args[1].split("=")[1]
                    val percentCoverage = args[2].split("=")[1].toInt()

                    val circuit = Netlist.parseNetlist(circuitFile)
                    circuit.doTopologicalSort()

                    val len = circuit.getInputVectorSize()
                    val iterThresh = 1000

                    var iter: Long = 0
                    var lastPct = 0.0f
                    var iterSinceProgress: Int = 0
                    while (true) {
                        // generate vector and propagate values
                        var inputVectorStr = ""
                        for (i in 0 until len){
                            inputVectorStr += if (Random.nextInt(0, 2) == 0) "0" else "1"
                        }

                        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
                        circuit.propagateInputVector()

                        // propagate induction
                        circuit.applyCheckpointFaults()
                        circuit.propagateDeduction()

                        // svae results and reset
                        circuit.cacheFaultsFromRun()
                        circuit.clearRunResults()

                        // logging and termination
                        val pctDet = circuit.getPercentDetectedFromAllRuns()
                        //println(circuit.getAllCachedFaults())
                        println("$iter" +
                                "\t${BigDecimal(pctDet.toDouble()).setScale(2, RoundingMode.HALF_EVEN)}" +
                                "\t[${circuit.getTotalNumberOfFaults()}/${circuit.getMaxFaults()}]" +
                                "\t($inputVectorStr)")
                        if (pctDet > lastPct) {
                            iterSinceProgress = 0
                            lastPct = pctDet
                        } else {
                            iterSinceProgress++
                        }

                        if (pctDet >= percentCoverage || iterSinceProgress > iterThresh) {
                            break
                        }

                        iter++
                    }
                }
                "podem" -> {
                    if (args.size != 3 && args.size != 4) {
                        printHelp()
                        exitProcess(1)
                    }

                    if (!args[1].startsWith("--circuit-file=")
                        && !args[2].startsWith("--fault=")) {
                        printHelp()
                        exitProcess(1)
                    }

                    val circuitFile = args[1].split("=")[1]

                    val fault = args[2].split("=")[1]
                    val faultEls = fault.split(",")
                    val netNo = faultEls[0].trim().toInt()
                    val netValue = faultEls[1].trim().toInt()
                    println("using fault NET_$netNo ${netValue.toString().toLowerCase().replace("_", "-")}.")
                    val f = Pair(netNo, netValue)

                    var verify = args.size == 4 && args[3] == "--verify"

                    println("")
                    println("running podem for $fault (verify=$verify)")
                    val podemInst = Podem(File(circuitFile), f.first, f.second)
                    val success = podemInst.run()
                    if (success) {
                        val ans = podemInst.getTestVectorString()
                        println("fault $f has a test: $ans")
                        println("")
                        if (verify) {
                            println("verifying in the deductive simulator...")
                            val res = checkFault(circuitFile, ans, f.first, f.second)
                            if (res) {
                                println("the test vector was verified by the deductive sim")
                            } else {
                                println("!!! this test vector was not verified by the deductive sim !!!")
                                exitProcess(1)
                            }
                        } else {
                            println("verification not requested")
                        }
                    } else {
                        println("fault $f does not have a test")
                    }

                    println("")
                }
                "help" -> {
                    printHelp()
                    exitProcess(0)
                }
                else -> {
                    printHelp()
                    exitProcess(1)
                }
            }
        }

        private fun checkFault(circuitFile: String, inputVector: String, net: Int, sav: Int): Boolean {
            val circuit = Netlist.parseNetlist(circuitFile)
            val inputVectorClean = inputVector.replace("X", "1")

            circuit.doTopologicalSort()
            circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorClean))
            circuit.propagateInputVector()

            circuit.applyCheckpointFaults()
            circuit.propagateDeduction()
            //println(circuit.getFormattedFaults())
            val faults = circuit.getFaultSet();
            val testFault = Fault(net, if (sav == 1) NetFaultType.S_A_1 else NetFaultType.S_A_0)
            return testFault in faults
        }
    }
}