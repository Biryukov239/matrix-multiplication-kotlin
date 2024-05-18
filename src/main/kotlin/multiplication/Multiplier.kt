package org.example.multiplication

abstract class Multiplier {
    protected lateinit var firstMatrix: FloatArray
    protected lateinit var secondMatrix: FloatArray
    lateinit var resultMatrix: FloatArray

    abstract fun calculate()
}