package org.example.multiplication

class MultiplierBasedOnCPU(firstMatrix: FloatArray?, secondMatrix: FloatArray?, firstRowCount: Int, firstColumnCount: Int, secondColumnCount: Int) :
    Multiplier() {
    private val firstRowCount: Int
    private val secondColumnCount: Int

    init {
        this.firstMatrix = firstMatrix!!
        this.secondMatrix = secondMatrix!!
        this.resultMatrix = FloatArray(firstRowCount * secondColumnCount)
        this.firstRowCount = firstRowCount
        this.secondColumnCount = secondColumnCount
    }

    private val transposedMat: FloatArray
        get() {
            val k = secondMatrix.size / secondColumnCount
            val res = FloatArray(secondMatrix.size)
            for (i in 0 until k) {
                for (j in 0 until secondColumnCount) {
                    res[j * k + i] = secondMatrix[i * secondColumnCount + j]
                }
            }
            return res
        }

    override fun calculate() {
        val k = firstMatrix.size / firstRowCount
        val transposed = transposedMat
        for (row in 0 until firstRowCount) {
            for (col in 0 until secondColumnCount) {
                resultMatrix[row * secondColumnCount + col] = 0f
                for (i in 0 until k) {
                    resultMatrix[row * secondColumnCount + col] += firstMatrix[row * k + i] * transposed[col * k + i]
                }
            }
        }
    }
}