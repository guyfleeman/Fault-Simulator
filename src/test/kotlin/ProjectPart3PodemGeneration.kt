import com.willspants.ParserRunner
import com.willspants.podem.Podem
import com.willspants.synth.Fault
import com.willspants.synth.NetFaultType
import com.willspants.synth.Netlist
import org.junit.Test
import java.io.File

class ProjectPart3PodemGeneration {
    fun checkFault(circuitFile: String, inputVector: String, net: Int, sav: Int): Boolean {
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

    @Test
    fun generateVectorsS27() {
        val faults = listOf(Pair(16, 0), Pair(10, 1), Pair(12, 0), Pair(18, 1),
                            Pair(17, 1), Pair(13, 0), Pair(6, 1), Pair(11, 0))

        val circuitFile = CIRCUIT_S27

        println("running tests for ${circuitFile.substring(circuitFile.lastIndexOf("\\"))}")

        for (f in faults) {
            val podemInst = Podem(File(circuitFile), f.first, f.second)

            val success = podemInst.run()
            if (success) {
                val ans = podemInst.getTestVectorString()
                println("fault $f has a test: $ans")
                //println(ans)
                assert(checkFault(circuitFile, ans, f.first, f.second))
                println("the test vector was verified by the deductive sim")
            } else {
                println("fault $f does not have a test")
            }

            println("")
        }
    }

    @Test
    fun generateVectorsS298() {
        val faults = listOf(Pair(70, 1), Pair(73, 0), Pair(26, 1), Pair(92, 0),
            Pair(38, 0), Pair(46, 1), Pair(3, 1), Pair(68, 0))

        val circuitFile = CIRCUIT_S298F_2

        println("running tests for ${circuitFile.substring(circuitFile.lastIndexOf("\\"))}")

        for (f in faults) {
            val podemInst = Podem(File(circuitFile), f.first, f.second)

            val success = podemInst.run()
            if (success) {
                val ans = podemInst.getTestVectorString()
                println("fault $f has a test: $ans")
                //println(ans)
                assert(checkFault(circuitFile, ans, f.first, f.second))
                println("the test vector was verified by the deductive sim")
            } else {
                println("fault $f does not have a test")
            }

            println("")
        }
    }

    @Test
    fun generateVectorsS344() {
        val faults = listOf(Pair(166, 0), Pair(71, 1), Pair(16, 0), Pair(91, 1),
            Pair(38, 0), Pair(5, 1), Pair(138, 0), Pair(91, 0))

        val circuitFile = CIRCUIT_S344F_2

        println("running tests for ${circuitFile.substring(circuitFile.lastIndexOf("\\"))}")

        for (f in faults) {
            val podemInst = Podem(File(circuitFile), f.first, f.second)

            val success = podemInst.run()
            if (success) {
                val ans = podemInst.getTestVectorString()
                println("fault $f has a test: $ans")
                //println(ans)
                assert(checkFault(circuitFile, ans, f.first, f.second))
                println("the test vector was verified by the deductive sim")
            } else {
                println("fault $f does not have a test")
            }

            println("")
        }
    }

    @Test
    fun generateVectorsS349() {
        val faults = listOf(Pair(25, 1), Pair(51, 0), Pair(105, 1), Pair(105, 0),
            Pair(83, 1), Pair(92, 0), Pair(7, 0), Pair(179, 0))

        val circuitFile = CIRCUIT_S349F_2

        println("running tests for ${circuitFile.substring(circuitFile.lastIndexOf("\\"))}")

        for (f in faults) {
            val podemInst = Podem(File(circuitFile), f.first, f.second)

            val success = podemInst.run()
            if (success) {
                val ans = podemInst.getTestVectorString()
                println("fault $f has a test: $ans")
                //println(ans)
                assert(checkFault(circuitFile, ans, f.first, f.second))
                println("the test vector was verified by the deductive sim")
            } else {
                println("fault $f does not have a test")
            }

            println("")
        }
    }

    companion object {
        const val CIRCUIT_S27 = ".\\src\\main\\resources\\circuits\\s27.txt"
        const val CIRCUIT_S298F_2 = ".\\src\\main\\resources\\circuits\\s298f_2.txt"
        const val CIRCUIT_S344F_2 = ".\\src\\main\\resources\\circuits\\s344f_2.txt"
        const val CIRCUIT_S349F_2 = ".\\src\\main\\resources\\circuits\\s349f_2.txt"
    }
}