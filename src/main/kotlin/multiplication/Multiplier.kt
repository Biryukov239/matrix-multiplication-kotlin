package org.example.multiplication

interface Multiplier {
    val firstMatrix: FloatArray
    val secondMatrix: FloatArray
    val resultMatrix: FloatArray

    fun calculate()
}