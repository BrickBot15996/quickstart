package brickbot.quickstart.devices

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap

object HubManager {
    private lateinit var controlHub: LynxModule
    private lateinit var expansionHub: LynxModule

    private var isExpansionHubCachingEnabled = true

    fun init(hardwareMap: HardwareMap) {
        controlHub = hardwareMap.getAll(LynxModule::class.java).first { it.deviceName.lowercase().contains("control") }
        expansionHub = hardwareMap.getAll(LynxModule::class.java).first { it.deviceName.lowercase().contains("expansion") }

        controlHub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        expansionHub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
    }

    fun disableExpansionHubCaching() {
        isExpansionHubCachingEnabled = false
        expansionHub.bulkCachingMode = LynxModule.BulkCachingMode.AUTO
    }

    fun clearCache() {
        controlHub.clearBulkCache()
        if (isExpansionHubCachingEnabled) {
            expansionHub.clearBulkCache()
        }
    }
}