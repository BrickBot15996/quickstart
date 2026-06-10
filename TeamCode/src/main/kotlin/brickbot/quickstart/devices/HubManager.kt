package brickbot.quickstart.devices

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap

object HubManager {
    private lateinit var controlHub: LynxModule
    private lateinit var expansionHub: LynxModule

    private var isExpansionHubCachingEnabled = true

    fun init(hardwareMap: HardwareMap) {
        try {
            // Pull the first appearance of a Control Hub cause realistically there should not be
            // more than one.
            controlHub = hardwareMap.getAll(LynxModule::class.java)
                .first { it.deviceName.lowercase().contains("control") }

            controlHub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        } catch(e: Exception) {
            throw RuntimeException(
                "Make sure your Control Hub uses the default naming pattern: 'Control Hub X', where X is a number."
            )
        }

        try {
            // Pull the first appearance of an Expansion Hub cause realistically there should not be
            // more than one.
            expansionHub = hardwareMap.getAll(LynxModule::class.java)
                .first { it.deviceName.lowercase().contains("expansion") }

            expansionHub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
        } catch(e: Exception) {
            // Disable the caching altogether as this avoids the Expansion Hub ever getting called
            // again, avoiding a `NullPointerException`
            isExpansionHubCachingEnabled = false
        }
    }

    /**
     * This method disables the manual caching of the REV Expansion Hub.
     * Only call this method if you have an expansion hub on your robot.
     */
    fun disableExpansionHubCaching() {
        isExpansionHubCachingEnabled = false
        try {
            expansionHub.bulkCachingMode = LynxModule.BulkCachingMode.AUTO
        } catch(e: Exception) {
            throw RuntimeException(
                "Do not call disableExpansionHubCaching unless you have an Expansion Hub on your robot."
            )
        }
    }

    fun clearCache() {
        controlHub.clearBulkCache()
        if (isExpansionHubCachingEnabled) {
            expansionHub.clearBulkCache()
        }
    }
}