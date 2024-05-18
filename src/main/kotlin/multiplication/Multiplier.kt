package org.example.multiplication

abstract class Multiplier {
    protected abstract var firstMatrix: FloatArray
    protected abstract var secondMatrix: FloatArray
    abstract var resultMatrix: FloatArray

    abstract fun calculate()
}