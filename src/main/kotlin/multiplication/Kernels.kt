package org.example.multiplication

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.device.Device
import org.example.utils.fit
import org.example.utils.roundUp

object Kernels {
    fun getNaiveKernel(
        a: FloatArray, b: FloatArray, c: FloatArray,
        m: Int, k: Int, n: Int,
        device: Device?
    ): Pair<Kernel, Range> {
        val kernel: Kernel = object : Kernel() {
            override fun run() {
                val col = getGlobalId(0)
                val row = getGlobalId(1)
                if (row >= m || col >= n) {
                    return
                }
                var acc = 0f
                for (i in 0 until k) {
                    acc += a[row * k + i] * b[i * n + col]
                }
                c[row * n + col] = acc
            }
        }
        val workgroupSize = 16
        val range = Range.create2D(
            device,
            fit(n, workgroupSize),
            fit(m, workgroupSize),
            workgroupSize,
            workgroupSize
        )
        return Pair(kernel, range)
    }

    fun getLocalMemKernel(
        a: FloatArray, b: FloatArray, c: FloatArray,
        m: Int, k: Int, n: Int,
        device: Device?
    ): Pair<Kernel, Range> {
        val tileSize = 16
        val kernel: Kernel = object : Kernel() {
            @Local
            var tileA: Array<FloatArray> = Array(tileSize) { FloatArray(tileSize + 1) }

            @Local
            var tileB: Array<FloatArray> = Array(tileSize) { FloatArray(tileSize + 1) }

            override fun run() {
                val col = getGlobalId(0)
                val row = getGlobalId(1)
                val localCol = getLocalId(0)
                val localRow = getLocalId(1)

                var acc = 0f
                val numTiles = k / tileSize + 1
                var tiledRow: Int
                var tiledCol: Int
                var ind: Int
                for (t in 0 until numTiles) {
                    tiledRow = 16 * t + localCol
                    tiledCol = 16 * t + localRow

                    ind = row * k + tiledRow
                    tileA[localRow][localCol] = if ((ind < m * k)) a[ind] else 0f
                    ind = tiledCol * n + col
                    tileB[localRow][localCol] = if ((ind < k * n)) b[ind] else 0f
                    localBarrier()

                    for (i in 0 until tileSize) {
                        acc += tileA[localRow][i] * tileB[i][localCol]
                    }
                    localBarrier()
                }
                if (row < m && col < n) {
                    c[row * n + col] = acc
                }
            }
        }
        val range = Range.create2D(device, fit(n, tileSize), fit(m, tileSize), tileSize, tileSize)
        return Pair(kernel, range)
    }

    fun getWPTOptKernel(
        a: FloatArray, b: FloatArray, c: FloatArray,
        m: Int, k: Int, n: Int,
        device: Device?
    ): Pair<Kernel, Range> {
        val tileSize = 16
        val wpt = 8
        val rts = tileSize / wpt
        val kernel: Kernel = object : Kernel() {
            @Local
            var tileA: Array<FloatArray> = Array(tileSize) { FloatArray(tileSize + 1) }

            @Local
            var tileB: Array<FloatArray> = Array(tileSize) { FloatArray(tileSize + 1) }

            override fun run() {
                val localCol = getLocalId(0)
                val localRow = getLocalId(1)
                val col = tileSize * getGroupId(0) + localCol
                val row = tileSize * getGroupId(1) + localRow

                var acc1 = 0f
                var acc2 = 0f
                var acc3 = 0f
                var acc4 = 0f
                var acc5 = 0f
                var acc6 = 0f
                var acc7 = 0f
                var acc8 = 0f

                val numTiles = k / tileSize + 1
                var tiledRow: Int
                var tiledCol: Int
                var ind : Int


                for (t in 0 until numTiles) {
                    for (w in 0 until wpt) {
                        tiledRow = tileSize * t + localCol
                        tiledCol = tileSize * t + localRow
                        ind = (row + w * rts) * k + tiledRow
                        tileA[localRow + w * rts][localCol] = if ((ind < m * k)) a[ind] else 0f
                        ind = (tiledCol + w * rts) * n + col
                        tileB[localRow + w * rts][localCol] = if ((ind < k * n)) b[ind] else 0f
                    }
                    localBarrier()

                    for (i in 0 until tileSize) {
                        acc1 += tileA[localRow + 0 * rts][i] * tileB[i][localCol]
                        acc2 += tileA[localRow + 1 * rts][i] * tileB[i][localCol]
                        acc3 += tileA[localRow + 2 * rts][i] * tileB[i][localCol]
                        acc4 += tileA[localRow + 3 * rts][i] * tileB[i][localCol]
                        acc5 += tileA[localRow + 4 * rts][i] * tileB[i][localCol]
                        acc6 += tileA[localRow + 5 * rts][i] * tileB[i][localCol]
                        acc7 += tileA[localRow + 6 * rts][i] * tileB[i][localCol]
                        acc8 += tileA[localRow + 7 * rts][i] * tileB[i][localCol]
                    }
                    localBarrier()
                }
                if (col < n) {
                    var ind = (row + 0 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc1
                    }
                    ind = (row + 1 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc2
                    }
                    ind = (row + 2 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc3
                    }
                    ind = (row + 3 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc4
                    }
                    ind = (row + 4 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc5
                    }
                    ind = (row + 5 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc6
                    }
                    ind = (row + 6 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc7
                    }
                    ind = (row + 7 * rts) * n + col
                    if ((ind - col) / n < m) {
                        c[ind] = acc8
                    }
                }
            }
        }
        val range = Range.create2D(
            device,
            fit(n, tileSize),
            roundUp(fit(m, tileSize), wpt),
            tileSize,
            tileSize / wpt
        )
        return Pair(kernel, range)
    }
}
