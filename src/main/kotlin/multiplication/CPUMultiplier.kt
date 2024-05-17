package org.example.multiplication

class CPUMultiplier(firstMat: FloatArray?, secondMat: FloatArray?, m: Int, k: Int, n: Int) :
    Multiplier() {
    private val m: Int
    private val n: Int

    init {
        this.firstMat = firstMat!!
        this.secondMat = secondMat!!
        this.resultMat = FloatArray(m * n)
        this.m = m
        this.n = n
    }

    private val transposedMat: FloatArray
        get() {
            val k = secondMat.size / n
            val res = FloatArray(secondMat.size)
            for (i in 0 until k) {
                for (j in 0 until n) {
                    res[j * k + i] = secondMat[i * n + j]
                }
            }
            return res
        }

    override fun calculate() {
        val k = firstMat.size / m
        val transposed = transposedMat
        for (row in 0 until m) {
            for (col in 0 until n) {
                resultMat[row * n + col] = 0f
                for (i in 0 until k) {
                    resultMat[row * n + col] += firstMat[row * k + i] * transposed[col * k + i]
                }
            }
        }
    }
}