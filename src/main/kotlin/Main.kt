package org.example

import org.example.multiplication.MultiplierBasedOnCPU
import org.example.multiplication.MultiplierBasedOnGPU
import org.example.multiplication.Multiplier
import kotlin.math.abs
import com.xenomachina.argparser.ArgParser
import org.example.multiplication.KernelType
import kotlin.system.exitProcess


class MyArgs(parser: ArgParser) {
    val firstRowCount by parser.storing("-m", help = "Height of the first matrix") { toInt() }
    val firstColumnCount by parser.storing("-k", help = "Weight of the first matrix") { toInt() }
    val secondColumnCount by parser.storing("-n", help = "Weight of the second matrix") { toInt() }
    val firstMatrixInput by parser.storing("-f", help = "Float matrix after -f") { toString() }
    val secondMatrixInput by parser.storing("-s", help = "Float matrix after -s") { toString() }
}


fun main(args: Array<String>) {
    ArgParser(args).parseInto(::MyArgs).run {
        println("Value after -m: $firstRowCount")
        println("Value after -k: $firstColumnCount")
        println("Value after -n: $secondColumnCount")
        println("Values after -f: $firstMatrixInput")
        println("Values after -s: $secondMatrixInput")
        val firstMatrix = firstMatrixInput.split(",").map { it.toFloat() }.toFloatArray()
        if (firstMatrix.size != firstRowCount * firstColumnCount) {
            System.err.println(
                "Invalid size of first matrix, expected: ${firstRowCount * firstColumnCount} float(s), not ${firstMatrix.size}"
            )
            exitProcess(2)
        }
        val secondMatrix = secondMatrixInput.split(",").map { it.toFloat() }.toFloatArray()
        if (secondMatrix.size != firstColumnCount * secondColumnCount) {
            System.err.println(
                "Invalid size of second matrix, expected: ${firstColumnCount * secondColumnCount} float(s), not ${secondMatrix.size}"
            )
            exitProcess(2)
        }
        val multiplierBasedOnGPU = MultiplierBasedOnGPU(
            firstMatrix,
            secondMatrix,
            firstRowCount,
            firstColumnCount,
            secondColumnCount,
            KernelType.WITH_WPT_OPTIMIZATION
        )
        multiplierBasedOnGPU.calculate()
        val multiplierBasedOnCPU: Multiplier =
            MultiplierBasedOnCPU(firstMatrix, secondMatrix, firstRowCount, firstColumnCount, secondColumnCount)
        multiplierBasedOnCPU.calculate()
        val multiplicationGPUResult = multiplierBasedOnGPU.resultMatrix
        val multiplicationCPUResult = multiplierBasedOnCPU.resultMatrix
        for (i in 0 until firstRowCount * secondColumnCount) {
            if (abs((multiplicationGPUResult[i] - multiplicationCPUResult[i]).toDouble()) > 0.01f) {
                println("Error at index $i")
            }
        }
        println(multiplicationGPUResult.contentToString())
        println(multiplicationCPUResult.contentToString())
    }

}
