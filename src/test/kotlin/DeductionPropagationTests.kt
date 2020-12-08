import com.willspants.ParserRunner
import com.willspants.synth.Netlist
import org.junit.Test

class DeductionPropagationTests {
    @Test
    fun test_and_00() {
        val circuit = Netlist.parseNetlist(CIRCUIT_AND)
        val inputVectorStr = "00"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_and_10() {
        val circuit = Netlist.parseNetlist(CIRCUIT_AND)
        val inputVectorStr = "10"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_and_01() {
        val circuit = Netlist.parseNetlist(CIRCUIT_AND)
        val inputVectorStr = "01"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_and_11() {
        val circuit = Netlist.parseNetlist(CIRCUIT_AND)
        val inputVectorStr = "11"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nand_00() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NAND)
        val inputVectorStr = "00"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nand_10() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NAND)
        val inputVectorStr = "10"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nand_01() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NAND)
        val inputVectorStr = "01"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nand_11() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NAND)
        val inputVectorStr = "11"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_or_00() {
        val circuit = Netlist.parseNetlist(CIRCUIT_OR)
        val inputVectorStr = "00"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_or_10() {
        val circuit = Netlist.parseNetlist(CIRCUIT_OR)
        val inputVectorStr = "10"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_or_01() {
        val circuit = Netlist.parseNetlist(CIRCUIT_OR)
        val inputVectorStr = "01"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_or_11() {
        val circuit = Netlist.parseNetlist(CIRCUIT_OR)
        val inputVectorStr = "11"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nor_00() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NOR)
        val inputVectorStr = "00"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nor_10() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NOR)
        val inputVectorStr = "10"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nor_01() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NOR)
        val inputVectorStr = "01"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_nor_11() {
        val circuit = Netlist.parseNetlist(CIRCUIT_NOR)
        val inputVectorStr = "11"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_s27_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S27)
        val inputVectorStr = "1110101"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_s298f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S298F_2)
        val inputVectorStr = "10101010101010101"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_s344f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S344F_2)
        val inputVectorStr = "101010101010101011111111"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    @Test
    fun test_s349f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S349F_2)
        val inputVectorStr = "101010101010101011111111"

        circuit.doTopologicalSort()
        circuit.loadInputVector(ParserRunner.inputVectorToArray(inputVectorStr))
        circuit.propagateInputVector()

        circuit.applyCheckpointFaults()
        circuit.propagateDeduction()
        circuit.printFaults()
    }

    companion object {
        const val CIRCUIT_OR = ".\\src\\main\\resources\\circuits\\or_00.txt"
        const val CIRCUIT_NOR = ".\\src\\main\\resources\\circuits\\nor_00.txt"
        const val CIRCUIT_AND = ".\\src\\main\\resources\\circuits\\and_00.txt"
        const val CIRCUIT_NAND = ".\\src\\main\\resources\\circuits\\nand_00.txt"
        const val CIRCUIT_S27 = ".\\src\\main\\resources\\circuits\\s27.txt"
        const val CIRCUIT_S298F_2 = ".\\src\\main\\resources\\circuits\\s298f_2.txt"
        const val CIRCUIT_S344F_2 = ".\\src\\main\\resources\\circuits\\s344f_2.txt"
        const val CIRCUIT_S349F_2 = ".\\src\\main\\resources\\circuits\\s349f_2.txt"
    }
}