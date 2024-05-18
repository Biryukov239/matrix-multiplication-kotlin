package org.example.multiplication

class MultiplierBasedOnCPU(
    firstMatrix: FloatArray?,
    secondMatrix: FloatArray?,
    private val firstRowCount: Int,
    firstColumnCount: Int,
    private val secondColumnCount: Int
) :
    Multiplier() {

    override var firstMatrix: FloatArray = firstMatrix!!
    override var secondMatrix: FloatArray = secondMatrix!!
    override var resultMatrix: FloatArray = FloatArray(firstRowCount * secondColumnCount)

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