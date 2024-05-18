package org.example

import org.example.multiplication.CPUMultiplier
import org.example.multiplication.GPUMultiplier
import org.example.multiplication.Multiplier
import kotlin.math.abs
import com.xenomachina.argparser.ArgParser


class MyArgs(parser: ArgParser) {
    val firstRowCount by parser.storing("-m", help = "Height of the first matrix") { toInt() }
    val firstColumnCount by parser.storing("-k", help = "Weight of the first matrix") { toInt() }
    val secondColumnCount by parser.storing("-n", help = "Weight of the second matrix") { toInt() }
    val firstMatrixInput by parser.storing("-f", help = "Float matrix after -f") { toString() }
    val secondMatrixInput by parser.storing("-s", help = "Float matrix after -s") { toString() }
}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        ArgParser(args).parseInto(::MyArgs).run {
            println("Value after -m: $firstRowCount")
            println("Value after -k: $firstColumnCount")
            println("Value after -n: $secondColumnCount")
            println("Values after -f: $firstMatrixInput")
            println("Values after -s: $secondMatrixInput")
            val firstMat = firstMatrixInput.split(",").map { it.toFloat() }.toFloatArray()
            val secondMat = secondMatrixInput.split(",").map { it.toFloat() }.toFloatArray()
            val gpuMultiplier = GPUMultiplier(firstMat, secondMat, firstRowCount, firstColumnCount, secondColumnCount)
            gpuMultiplier.calculate()
            val cpuMultiplier: Multiplier = CPUMultiplier(firstMat, secondMat, firstRowCount, firstColumnCount, secondColumnCount)
            cpuMultiplier.calculate()
            val multiplicationGPUResult = gpuMultiplier.resultMat
            val multiplicationCPUResult = cpuMultiplier.resultMat
            for (i in 0 until firstRowCount * secondColumnCount) {
                if (abs((multiplicationGPUResult[i] - multiplicationCPUResult[i]).toDouble()) > 0.01f) {
                    println("Error at index $i")
                }
            }
            println(multiplicationGPUResult.contentToString())
            println(multiplicationCPUResult.contentToString())
        }

    }
}
