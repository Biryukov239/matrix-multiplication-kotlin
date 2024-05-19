package org.example.multiplication

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.device.Device
import com.aparapi.device.OpenCLDevice
import org.example.multiplication.Kernels.getLocalMemProgram
import org.example.multiplication.Kernels.getNaiveProgram
import org.example.multiplication.Kernels.getWPTOptProgram
import org.example.multiplication.MultiplierBasedOnGPU.Companion.getDefaultDevice

data class Program(val kernel: Kernel, val range: Range)

class MultiplierBasedOnGPU(
    device: Device?,
    override val firstMatrix: FloatArray,
    override val secondMatrix: FloatArray,
    firstRowCount: Int,
    firstColumnCount: Int,
    secondColumnCount: Int,
    kernelType: KernelType = KernelType.NAIVE,
    override val resultMatrix: FloatArray = FloatArray(firstRowCount * secondColumnCount),
    private val program: Program = when (kernelType) {
        KernelType.WITH_WPT_OPTIMIZATION -> getWPTOptProgram(
            firstMatrix,
            secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
        )

        KernelType.WITH_LOCAL_MEM_OPTIMIZATION -> getLocalMemProgram(
            firstMatrix,
            secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
        )

        else -> getNaiveProgram(
            firstMatrix,
            secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
        )
    }
) :
    Multiplier {

    companion object {
        fun getDefaultDevice(): Device? {
            return OpenCLDevice.listDevices(Device.TYPE.GPU).firstOrNull() ?: OpenCLDevice.listDevices(Device.TYPE.CPU)
                .firstOrNull()
        }
    }

    override fun calculate() {
        program.kernel.execute(program.range)
    }
}

fun MultiplierBasedOnGPU(
    firstMatrix: FloatArray,
    secondMatrix: FloatArray,
    firstRowCount: Int,
    firstColumnCount: Int,
    secondColumnCount: Int
): MultiplierBasedOnGPU {
    return MultiplierBasedOnGPU(
        getDefaultDevice(),
        firstMatrix,
        secondMatrix,
        firstRowCount,
        firstColumnCount,
        secondColumnCount,
        KernelType.NAIVE
    )
}

fun MultiplierBasedOnGPU(
    firstMatrix: FloatArray,
    secondMatrix: FloatArray,
    firstRowCount: Int,
    firstColumnCount: Int,
    secondColumnCount: Int,
    kernelType: KernelType
): MultiplierBasedOnGPU {
    return MultiplierBasedOnGPU(
        getDefaultDevice(),
        firstMatrix,
        secondMatrix,
        firstRowCount,
        firstColumnCount,
        secondColumnCount,
        kernelType
    )
}