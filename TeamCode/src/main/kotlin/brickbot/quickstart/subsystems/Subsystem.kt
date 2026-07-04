package brickbot.quickstart.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap

abstract class Subsystem {
    init {
        SubsystemManager.add(this)
    }

    private var readIntervalMs = 0
    private var computeIntervalMs = 0
    private var writeIntervalMs = 0
    private var lastReadTimestamp = 0
    private var lastComputeTimestamp = 0
    private var lastWriteTimestamp = 0

    abstract fun init(hwMap: HardwareMap)
    abstract fun read()
    abstract fun compute()
    abstract fun write()

    fun setReadIntervalMs(readIntervalMs: Int) {
        this.readIntervalMs = readIntervalMs
    }

    fun setComputeIntervalMs(computeIntervalMs: Int) {
        this.computeIntervalMs = computeIntervalMs
    }

    fun setWriteIntervalMs(writeIntervalMs: Int) {
        this.writeIntervalMs = writeIntervalMs
    }

    /**
     * This method is used by the SubsystemManager, do not use it.
     */
    internal fun update(timestamp: Int) {
        if (timestamp - lastReadTimestamp >= readIntervalMs) {
            read()
            lastReadTimestamp = timestamp
        }
        if (timestamp - lastComputeTimestamp >= computeIntervalMs) {
            compute()
            lastComputeTimestamp = timestamp
        }
        if (timestamp - lastWriteTimestamp >= writeIntervalMs) {
            write()
            lastWriteTimestamp = timestamp
        }
    }
}