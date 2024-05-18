package org.example.multiplication

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.device.Device
import com.aparapi.device.OpenCLDevice
import org.example.multiplication.Kernels.getLocalMemKernel
import org.example.multiplication.Kernels.getNaiveKernel
import org.example.multiplication.Kernels.getWPTOptKernel

class MultiplierBasedOnGPU(
    device: Device?,
    firstMatrix: FloatArray?,
    secondMatrix: FloatArray?,
    firstRowCount: Int,
    firstColumnCount: Int,
    secondColumnCount: Int,
    kernelType: KernelType? = KernelType.NAIVE
) :
    Multiplier() {
    private var program: Pair<Kernel, Range>? = null

    private val device: Device

    constructor(
        firstMatrix: FloatArray?,
        secondMatrix: FloatArray?,
        firstRowCount: Int,
        firstColumnCount: Int,
        secondColumnCount: Int
    ) : this(
        defaultDevice, firstMatrix, secondMatrix, firstRowCount, firstColumnCount, secondColumnCount, KernelType.NAIVE
    )

    constructor(
        firstMatrix: FloatArray?,
        secondMatrix: FloatArray?,
        firstRowCount: Int,
        firstColumnCount: Int,
        secondColumnCount: Int,
        kernelType: KernelType?
    ) : this(
        defaultDevice, firstMatrix, secondMatrix, firstRowCount, firstColumnCount, secondColumnCount, kernelType
    )

    init {
        requireNotNull(device) { "No devices available" }
        this.device = device
        this.firstMatrix = firstMatrix!!
        this.secondMatrix = secondMatrix!!
        this.resultMatrix = FloatArray(firstRowCount * secondColumnCount)
        when (kernelType) {
            KernelType.WITH_WPT_OPTIMIZATION -> this.program = getWPTOptKernel(
                firstMatrix,
                secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
            )

            KernelType.WITH_LOCAL_MEM_OPTIMIZATION -> this.program = getLocalMemKernel(
                firstMatrix,
                secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
            )

            else -> this.program = getNaiveKernel(
                firstMatrix,
                secondMatrix, resultMatrix, firstRowCount, firstColumnCount, secondColumnCount, device
            )
        }
    }

    override fun calculate() {
        program?.first?.execute(program?.second)
    }

    companion object {
        private val defaultDevice: Device?
            get() {
                if (OpenCLDevice.listDevices(Device.TYPE.GPU).isNotEmpty()) {
                    return OpenCLDevice.listDevices(Device.TYPE.GPU).first()
                }
                if (OpenCLDevice.listDevices(Device.TYPE.CPU).isNotEmpty()) {
                    return OpenCLDevice.listDevices(Device.TYPE.CPU).first()
                }
                return null
            }
    }
}