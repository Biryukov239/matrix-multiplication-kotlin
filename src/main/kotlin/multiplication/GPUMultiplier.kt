package org.example.multiplication

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.device.Device
import com.aparapi.device.OpenCLDevice
import org.example.multiplication.Kernels.getLocalMemKernel
import org.example.multiplication.Kernels.getNaiveKernel
import org.example.multiplication.Kernels.getWPTOptKernel

class GPUMultiplier (
    device: Device?,
    firstMat: FloatArray?,
    secondMat: FloatArray?,
    m: Int,
    k: Int,
    n: Int,
    kernelType: KernelType? = KernelType.NAIVE
) :
    Multiplier() {
    private var program: Pair<Kernel, Range>? = null

    private val device: Device

    constructor(firstMat: FloatArray?, secondMat: FloatArray?, m: Int, k: Int, n: Int) : this(
        defaultDevice, firstMat, secondMat, m, k, n, KernelType.NAIVE
    )

    constructor(firstMat: FloatArray?, secondMat: FloatArray?, m: Int, k: Int, n: Int, kernelType: KernelType?) : this(
        defaultDevice, firstMat, secondMat, m, k, n, kernelType
    )

    init {
        requireNotNull(device) { "No devices available" }
        this.device = device
        this.firstMat = firstMat!!
        this.secondMat = secondMat!!
        this.resultMat = FloatArray(m * n)
        when (kernelType) {
            KernelType.WITH_WPT_OPTIMIZATION -> this.program = getWPTOptKernel(
                firstMat,
                secondMat, resultMat, m, k, n, device
            )

            KernelType.WITH_LOCAL_MEM_OPTIMIZATION -> this.program = getLocalMemKernel(
                firstMat,
                secondMat, resultMat, m, k, n, device
            )

            else -> this.program = getNaiveKernel(
                firstMat,
                secondMat, resultMat, m, k, n, device
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