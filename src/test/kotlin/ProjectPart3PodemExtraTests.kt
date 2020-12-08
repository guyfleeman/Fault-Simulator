import com.willspants.ParserRunner
import com.willspants.podem.Podem
import com.willspants.synth.Fault
import com.willspants.synth.NetFaultType
import com.willspants.synth.Netlist
import org.junit.Test
import java.io.File

class ProjectPart3PodemExtraTests {
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
    fun s27_n16_sa0() {
        println("s27 NET 16 s-a-0")
        val circuitFile = CIRCUIT_S27
        val net = 16
        val sav = 0

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    @Test
    fun s27_n18_sa1() {
        println("s27 NET 18 s-a-1")

        val circuitFile = CIRCUIT_S27
        val net = 18
        val sav = 1

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    @Test
    fun s298f_2_n70_sa1() {
        println("s298 NET 70 s-a-1")

        val circuitFile = CIRCUIT_S298F_2
        val net = 70
        val sav = 1

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    @Test
    fun s298f_2_n92_sa0() {
        println("s298 NET 92 s-a-0")

        val circuitFile = CIRCUIT_S298F_2
        val net = 92
        val sav = 0

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    @Test
    fun s344f_2_n166_sa0() {
        println("s344 NET 166 s-a-0")


        val circuitFile = CIRCUIT_S344F_2
        val net = 166
        val sav = 0

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    @Test
    fun s344f_2_n91_sa1() {
        println("s344 NET 91 s-a-1")

        val circuitFile = CIRCUIT_S344F_2
        val net = 91
        val sav = 1

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    @Test
    fun s349f_2_n25_sa1() {
        println("s349 NET 25 s-a-1")

        val circuitFile = CIRCUIT_S349F_2
        val net = 25
        val sav = 1

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    @Test
    fun s349f_2_n7_sa0() {
        println("s349 NET 7 s-a-0")

        val circuitFile = CIRCUIT_S344F_2
        val net = 7
        val sav = 0

        val podemInst = Podem(File(circuitFile), net, sav)

        val success = podemInst.run()
        assert(success)

        val ans = podemInst.getTestVectorString()
        println(ans)
        assert(checkFault(circuitFile, ans, net, sav))

        println("test found and verified with deductive sim")
        println("")
    }

    companion object {
        const val CIRCUIT_S27 = ".\\src\\main\\resources\\circuits\\s27.txt"
        const val CIRCUIT_S298F_2 = ".\\src\\main\\resources\\circuits\\s298f_2.txt"
        const val CIRCUIT_S344F_2 = ".\\src\\main\\resources\\circuits\\s344f_2.txt"
        const val CIRCUIT_S349F_2 = ".\\src\\main\\resources\\circuits\\s349f_2.txt"
    }
}