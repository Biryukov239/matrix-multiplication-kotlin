package org.example

import org.example.multiplication.CPUMultiplier
import org.example.multiplication.GPUMultiplier
import org.example.multiplication.Multiplier
import kotlin.math.abs
import com.xenomachina.argparser.ArgParser

class MyArgs(parser: ArgParser) {
    val m by parser.storing("-m", help = "Height of the first matrix") { toInt() }
    val k by parser.storing("-k", help = "Weight of the first matrix") { toInt() }
    val n by parser.storing("-n", help = "Weight of the second matrix") { toInt() }
    val fst by parser.storing("-f", help = "Float matrix after -f") { toString() }
    val snd by parser.storing("-s", help = "Float matrix after -s") { toString() }
}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        ArgParser(args).parseInto(::MyArgs).run {
            println("Value after -m: $m")
            println("Value after -k: $k")
            println("Value after -n: $n")
            println("Values after -f: $fst")
            println("Values after -s: $snd")
            val firstMat = fst.split(",").map { it.toFloat() }.toFloatArray()
            val secondMat = snd.split(",").map { it.toFloat() }.toFloatArray()
            val gpuMultiplier = GPUMultiplier(firstMat, secondMat, m, k, n)
            gpuMultiplier.calculate()
            val cpuMultiplier: Multiplier = CPUMultiplier(firstMat, secondMat, m, k, n)
            cpuMultiplier.calculate()
            val resGPU = gpuMultiplier.resultMat
            val resCPU = cpuMultiplier.resultMat
            for (i in 0 until m * n) {
                if (abs((resGPU[i] - resCPU[i]).toDouble()) > 0.01f) {
                    println("Error at index $i")
                }
            }
            println(resGPU.contentToString())
            println(resCPU.contentToString())
        }

    }
}