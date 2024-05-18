package org.example.multiplication

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.device.Device
import com.aparapi.device.OpenCLDevice
import org.example.multiplication.Kernels.getLocalMemProgram
import org.example.multiplication.Kernels.getNaiveProgram
import org.example.multiplication.Kernels.getWPTOptProgram

data class Program(val kernel: Kernel, val range: Range)

class MultiplierBasedOnGPU(
    device: Device?,
    firstMatrix: FloatArray,
    secondMatrix: FloatArray,
    firstRowCount: Int,
    firstColumnCount: Int,
    secondColumnCount: Int,
    kernelType: KernelType? = KernelType.NAIVE
) :
    Multiplier() {
    override var firstMatrix: FloatArray
    override var secondMatrix: FloatArray
    override var resultMatrix: FloatArray
    private var program: Program

    private val device: Device

    companion object {
        private fun getDefaultDevice(): Device? {
            if (OpenCLDevice.listDevices(Device.TYPE.GPU).isNotEmpty()) {
                return OpenCLDevice.listDevices(Device.TYPE.GPU).first()
            }
            if (OpenCLDevice.listDevices(Device.TYPE.CPU).isNotEmpty()) {
                return OpenCLDevice.listDevices(Device.TYPE.CPU).first()
            }
            return null
        }

        operator fun invoke(
            firstMatrix: FloatArray,
            secondMatrix: FloatArray,
            firstRowCount: Int,
            firstColumnCount: Int,
            secondColumnCount: Int,
            kernelType: KernelType
        ) = MultiplierBasedOnGPU(
            getDefaultDevice(),
            firstMatrix,
            secondMatrix,
            firstRowCount,
            firstColumnCount,
            secondColumnCount,
            kernelType
        )

        operator fun invoke(firstMatrix: FloatArray, secondMatrix: FloatArray, firstRowCount: Int, firstColumnCount: Int, secondColumnCount: Int) =
            MultiplierBasedOnGPU(getDefaultDevice(), firstMatrix, secondMatrix, firstRowCount, firstColumnCount, secondColumnCount, KernelType.NAIVE)
    }

    init {
        requireNotNull(device) { "No devices available" }
        this.device = device
        this.firstMatrix = firstMatrix
        this.secondMatrix = secondMatrix
        this.resultMatrix = FloatArray(firstRowCount * secondColumnCount)
        when (kernelType) {
            KernelType.WITH_WPT_OPTIMIZATION -> this.program = getWPTOptProgram(
                firstMatrix,
                secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
            )

            KernelType.WITH_LOCAL_MEM_OPTIMIZATION -> this.program = getLocalMemProgram(
                firstMatrix,
                secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
            )

            else -> this.program = getNaiveProgram(
                firstMatrix,
                secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
            )
        }
    }

    override fun calculate() {
        program.kernel.execute(program.range)
    }
}