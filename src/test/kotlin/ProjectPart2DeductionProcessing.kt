import com.willspants.ParserRunner
import com.willspants.synth.Netlist
import org.junit.Test

class ProjectPart2DeductionProcessing {
    @Test
    fun test_s27_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S27)
        val inputVectorStr = "1101101"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    @Test
    fun test_s344_sup_test() {

    }

    @Test
    fun test_s27_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S27)
        val inputVectorStr = "0101001"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    @Test
    fun test_s298f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S298F_2)
        val inputVectorStr = "10101011110010101"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    @Test
    fun test_s298f_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S298F_2)
        val inputVectorStr = "11101110101110111"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    @Test
    fun test_s344f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S344F_2)
        val inputVectorStr = "101010101010111101111111"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    @Test
    fun test_s344f_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S344F_2)
        val inputVectorStr = "111010111010101010001100"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    @Test
    fun test_s349f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S349F_2)
        val inputVectorStr = "101000000010101011111111"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    @Test
    fun test_s349f_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S349F_2)
        val inputVectorStr = "111111101010101010001111"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        println(circuit.getFormattedFaults())
    }

    companion object {
        const val CIRCUIT_S27 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s27.txt"
        const val CIRCUIT_S298F_2 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s298f_2.txt"
        const val CIRCUIT_S344F_2 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s344f_2.txt"
        const val CIRCUIT_S349F_2 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s349f_2.txt"
    }
}