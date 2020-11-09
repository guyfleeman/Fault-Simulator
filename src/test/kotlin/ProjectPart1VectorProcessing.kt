import PropagationTests.Companion.CIRCUIT_S27
import PropagationTests.Companion.CIRCUIT_S298F_2
import PropagationTests.Companion.CIRCUIT_S344F_2
import PropagationTests.Companion.CIRCUIT_S349F_2
import com.willspants.ParserRunner
import com.willspants.synth.Netlist
import org.junit.Test

class ProjectPart1VectorProcessing {
    @Test
    fun vector_run_s27() {
        val inputVectors = arrayOf("1110101", "0001010", "1010101", "0110111", "1010001")
        //TODO: get reference output from class colleagues
        //val outputVectors = arrayOf("1110101", "0001010", "1010101", "0110111", "1010001")

        for (inputVectorStr in inputVectors) {
            val circuit = Netlist.parseNetlist(CIRCUIT_S27)
            val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
            val outputVector = ParserRunner.runCircuit(circuit, inputVector)
            val outputVectorString = ParserRunner.outputVectorToString(outputVector)

            println("IN: {$inputVectorStr}, OUT: {$outputVectorString}")

            //assert(outputVectorString == expectedOutputVectorStr)
        }
    }

    @Test
    fun vector_run_s298f_2() {
        val inputVectors = arrayOf("10101010101010101",
            "01011110000000111",
            "11111000001111000",
            "11100001110001100",
            "01111011110000000")
        //TODO: get reference output from class colleagues
        //val outputVectors = arrayOf("1110101", "0001010", "1010101", "0110111", "1010001")

        for (inputVectorStr in inputVectors) {
            val circuit = Netlist.parseNetlist(CIRCUIT_S298F_2)
            val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
            val outputVector = ParserRunner.runCircuit(circuit, inputVector)
            val outputVectorString = ParserRunner.outputVectorToString(outputVector)

            println("IN: {$inputVectorStr}, OUT: {$outputVectorString}")

            //assert(outputVectorString == expectedOutputVectorStr)
        }
    }

    @Test
    fun vector_run_s344f_2() {
        val inputVectors = arrayOf("101010101010101011111111",
            "010111100000001110000000",
            "111110000011110001111111",
            "111000011100011000000000",
            "011110111100000001111111")
        //TODO: get reference output from class colleagues
        //val outputVectors = arrayOf("1110101", "0001010", "1010101", "0110111", "1010001")

        for (inputVectorStr in inputVectors) {
            val circuit = Netlist.parseNetlist(CIRCUIT_S344F_2)
            val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
            val outputVector = ParserRunner.runCircuit(circuit, inputVector)
            val outputVectorString = ParserRunner.outputVectorToString(outputVector)

            println("IN: {$inputVectorStr}, OUT: {$outputVectorString}")

            //assert(outputVectorString == expectedOutputVectorStr)
        }
    }

    @Test
    fun vector_run_s349f_2() {
        val inputVectors = arrayOf("101010101010101011111111",
            "010111100000001110000000",
            "111110000011110001111111",
            "111000011100011000000000",
            "011110111100000001111111")
        //TODO: get reference output from class colleagues
        //val outputVectors = arrayOf("1110101", "0001010", "1010101", "0110111", "1010001")

        for (inputVectorStr in inputVectors) {
            val circuit = Netlist.parseNetlist(CIRCUIT_S349F_2)
            val inputVector = ParserRunner.inputVectorToArray(inputVectorStr)
            val outputVector = ParserRunner.runCircuit(circuit, inputVector)
            val outputVectorString = ParserRunner.outputVectorToString(outputVector)

            println("IN: {$inputVectorStr}, OUT: {$outputVectorString}")

            //assert(outputVectorString == expectedOutputVectorStr)
        }
    }
}