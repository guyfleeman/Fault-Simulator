import com.willspants.ParserRunner
import com.willspants.synth.Netlist
import org.junit.Test
import kotlin.random.Random

class ProjectPart2DeductionCoverage {
    private fun generateRandomInputVector(len: Int): String {
         var output = ""
        for (i in 0 until len){
            output += if (Random.nextInt(0, 2) == 0) "0" else "1"
        }
        return output
    }

    private fun runFor(circuitFile: String, ivLen: Int, percent: Float) {
        val circuit = Netlist.parseNetlist(circuitFile)
        circuit.doTopologicalSort()

        val len = ivLen
        val iterThresh = 10000

        var iter: Long = 0
        var lastPct = 0.0f
        var iterSinceProgress: Int = 0
        while (true) {
            // generate vector and propagate values
            val inputVectorStr = generateRandomInputVector(len)
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
            println("$iter $pctDet [${circuit.getTotalNumberOfFaults()}/${circuit.getMaxFaults()}] ($inputVectorStr)")
            if (pctDet > lastPct) {
                iterSinceProgress = 0
                lastPct = pctDet
            } else {
                iterSinceProgress++
            }

            if (pctDet >= percent || iterSinceProgress > iterThresh) {
                break
            }

            iter++
        }
    }

    @Test
    fun test_s27_cov75() {
        runFor(CIRCUIT_S27, 7, 75.0f)
        println("s27 75%")
        println("")
    }

    @Test
    fun test_s298f_cov75() {
        runFor(CIRCUIT_S298F_2, 17, 75.0f)
        println("s298 75%")
        println("")
    }

    @Test
    fun test_s344f_cov75() {
        runFor(CIRCUIT_S344F_2, 24, 75.0f)
        println("s344 75%")
        println("")
    }

    @Test
    fun test_s349f_cov75() {
        runFor(CIRCUIT_S349F_2, 24, 75.0f)
        println("s349 75%")
        println("")
    }

    @Test
    fun test_s27_cov90() {
        runFor(CIRCUIT_S27, 7, 90.0f)
        println("s27 90%")
        println("")
    }

    @Test
    fun test_s298f_cov90() {
        runFor(CIRCUIT_S298F_2, 17, 90.0f)
        println("s298 90%")
        println("")
    }

    @Test
    fun test_s344f_cov90() {
        runFor(CIRCUIT_S344F_2, 24, 90.0f)
        println("s344 90%")
        println("")
    }

    @Test
    fun test_s349f_cov90() {
        runFor(CIRCUIT_S349F_2, 24, 90.0f)
        println("s349 90%")
        println("")
    }

    companion object {
        const val CIRCUIT_S27 = ".\\src\\main\\resources\\circuits\\s27.txt"
        const val CIRCUIT_S298F_2 = ".\\src\\main\\resources\\circuits\\s298f_2.txt"
        const val CIRCUIT_S344F_2 = ".\\src\\main\\resources\\circuits\\s344f_2.txt"
        const val CIRCUIT_S349F_2 = ".\\src\\main\\resources\\circuits\\s349f_2.txt"
    }
}