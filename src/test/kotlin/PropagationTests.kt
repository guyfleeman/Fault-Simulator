import com.willspants.ParserRunner
import com.willspants.synth.Netlist
import org.junit.Test

class PropagationTests {
    @Test
    fun test_s27_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S27)
        val inputVectorStr = "1110101"
        val expectedOutputVectorStr = "1001"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    @Test
    fun test_s27_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S27)
        val inputVectorStr = "0001010"
        val expectedOutputVectorStr = "0100"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    @Test
    fun test_s298f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S298F_2)
        val inputVectorStr = "10101010101010101"
        val expectedOutputVectorStr = "00000010101000111000"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    @Test
    fun test_s298f_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S298F_2)
        val inputVectorStr = "01011110000000111"
        val expectedOutputVectorStr = "00000000011000001000"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    @Test
    fun test_s344f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S344F_2)
        val inputVectorStr = "101010101010101011111111"
        val expectedOutputVectorStr = "10101010101010101010101101"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    @Test
    fun test_s344f_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S344F_2)
        val inputVectorStr = "010111100000001110000000"
        val expectedOutputVectorStr = "00011110000000100001111100"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    @Test
    fun test_s349f_iv0() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S349F_2)
        val inputVectorStr = "101010101010101011111111"
        val expectedOutputVectorStr = "10101010101010101101010101"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    @Test
    fun test_s349f_iv1() {
        val circuit = Netlist.parseNetlist(CIRCUIT_S349F_2)
        val inputVectorStr = "010111100000001110000000"
        val expectedOutputVectorStr = "00011110000000101011110000"

        val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
        val outputVector = ParserRunner.runCircuit(circuit, inputVector)
        val outputVectorString = ParserRunner.outputVectorToString(outputVector)

        assert(outputVectorString == expectedOutputVectorStr)
    }

    companion object {
        const val CIRCUIT_S27 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s27.txt"
        const val CIRCUIT_S298F_2 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s298f_2.txt"
        const val CIRCUIT_S344F_2 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s344f_2.txt"
        const val CIRCUIT_S349F_2 = "C:\\Users\\guyfleeman\\IdeaProjects\\ECE6140-Proj\\src\\main\\resources\\circuits\\s349f_2.txt"
    }
}