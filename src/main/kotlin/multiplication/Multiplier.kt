package org.example.multiplication

abstract class Multiplier {
    protected lateinit var firstMat: FloatArray
    protected lateinit var secondMat: FloatArray
    lateinit var resultMat: FloatArray

    abstract fun calculate()
}