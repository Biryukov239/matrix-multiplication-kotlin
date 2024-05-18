package org.example.multiplication

interface Multiplier {
    val firstMatrix: FloatArray
    val secondMatrix: FloatArray
    var resultMatrix: FloatArray

    fun calculate()
}