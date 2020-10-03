package com.willspants

import com.willspants.synth.Netlist
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.system.exitProcess

class ParserRunner {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 3) {
                println("Invalid Arguments")
                println("Usage: ./ParserRunner <netlist> <input vector> <output vector>")
                exitProcess(1)
            }

            val netlistFilename = args[0]
            val inputVectorFilename = args[1]
            val outputVectorFilename = args[2]

            val netlist = Netlist.parseNetlist(netlistFilename)

            val outputVector = runCircuit(netlist, parseInputVector(inputVectorFilename))

            println(outputVectorToString(outputVector))
        }

        fun runCircuit(netlist: Netlist, inputVector: Array<Boolean>): Array<Boolean> {
            if (!netlist.validate()) {
                println("netlist was invalid")
                exitProcess(1)
            }

            netlist.doTopologicalSort()

            netlist.loadInputVector(inputVector)
            netlist.propagateInputVector()

            return netlist.getOutputVector()
        }

        fun parseInputVector(filename: String): Array<Boolean> {
            val reader = BufferedReader(FileReader(File(filename)))
            val line = reader.readLine()!!
            return inputVectorToArray(line)
        }

        fun inputVectorToArray(inputVectorString: String): Array<Boolean> {
            val inputVector: Array<Boolean> = Array(inputVectorString.length) { false }
            for (i in inputVectorString.indices) {
                inputVector[i] = inputVectorString[i] == '1'
            }

            return inputVector
        }

        fun outputVectorToString(outputVector: Array<Boolean>): String {
            var outputString = ""
            outputVector.forEach {
                outputString += if (it) "1" else "0"
            }

            return outputString
        }
    }
}