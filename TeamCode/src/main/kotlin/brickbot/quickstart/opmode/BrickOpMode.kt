package brickbot.quickstart.opmode

import android.os.Environment
import android.util.Base64
import android.util.Log
import brickbot.quickstart.follower.Localizer
import brickbot.quickstart.commandbase.CommandScheduler
import brickbot.quickstart.devices.DeviceManager
import brickbot.quickstart.devices.HubManager
import brickbot.quickstart.follower.Pose
import brickbot.quickstart.subsystems.Robot
import brickbot.quickstart.recordautonomous.Bindings
import brickbot.quickstart.recordautonomous.RecordingData
import brickbot.quickstart.recordautonomous.RobotState
import brickbot.quickstart.subsystems.SubsystemManager
import brickbot.quickstart.updatable.UpdatableManager
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStreamReader

/**
 * This is the OpMode class that you need to extend whenever you want to create a new OpMode.
 *
 * There are 7 available annotation types for this class:
 * 1. `@TeleOp` - Marks this OpMode as TeleOp and makes it show up in the TeleOp list on the driver station.
 * 2. `@Autonomous` - Marks this OpMode as Autonomous and makes it show up in the Autonomous list on the driver station.
 * 3. `@Disabled` - Hides this OpMode on the driver station.
 * 4. `@Bindings` - Accepts your bindings class so the OpMode can call the `update()` method with
 * the gamepads as parameters.
 * 5. `@Localizer` - Accepts your localizer instance so the OpMode can track the position of your robot,
 * generally used for `@Recording` or `@Playback` OpModes.
 * 6. `@Recording` - Marks this OpMode as Recording, giving you the functionality to save a 30-second
 * sequence of robot and gamepad states to a file so they can be replayed.
 * 7. `@Playback` - Marks this OpMode as Playback, allowing you to play a previous recording of a
 * 30-second TeleOp sequence.
 *
 * There are 4 available functions at your disposal to override:
 * - `onInit()` - This method runs once when init is pressed on the driver station.
 * - `initLoop()` - This method runs continuously after `onInit()`, until start is pressed on the driver station,
 * - `onStart()` - This method runs once after start is pressed on the driver station.
 * - `run()` - This method run continuously after `onStart()`, until stop is pressed on the driver station.
 *
 * There are 4 user-accessible features inside BrickOpMode:
 * 1. `commandScheduler` - The globally unique instance of `CommandScheduler`.
 * 2. `subsystemManager` - The globally unique instance of `SubsystemManager`.
 * 3. `updatableManager` - The globally unique instance of `UpdatableManager`.
 * 4. `hubManager` - The globally unique instance of `HubManager`.
 *
 * How the `@Recording` works:
 * - `onRecordingStart()` - This method can be overridden. It gets called at the start of a recording
 * and can be used to simulate the behavior at the start of autonomous (e.g. Having the flywheel spin
 * up at the start of the simulation, so it can be accurately reproduced in playback).
 * - `onRecordingEnd()` - This method can be overridden. It gets called 30 seconds after the start
 * of a recording. It generally should be used to go back to the state previous to the recording starting
 * (referring to the previous example, this method could turn off the flywheel, in preparation for
 * another recording in the same OpMode run).
 * - Pressing `share` starts the recording, and it automatically counts to 30 seconds.
 * - After the 30 seconds are up, both gamepads will start rumbling for about 2 seconds, trying to
 * alert the drivers that any input after that point will not be saved.
 * - After the end of a recording, you can press the `touchpad` to save the robot and gamepad states
 * to the file passed in the `@Recording` annotation. The file will show up on the REV Control Hub
 * inside "FIRST/Recordings". WARNING: If you try to save a file as "Autonomous.json", the filename
 * might change to "Autonomous1.json" or "Autonomous2.json" or so on, to avoid deleting previously
 * recorded files.
 *
 * How the `@Playback` works:
 * - Yet to be implemented :/
 *
 * @see TeleOp
 * @see Autonomous
 * @see brickbot.quickstart.opmode.annotations.Bindings
 * @see brickbot.quickstart.opmode.annotations.Localizer
 * @see brickbot.quickstart.opmode.annotations.Recording
 * @see brickbot.quickstart.opmode.annotations.Playback
 * @see CommandScheduler
 * @see SubsystemManager
 * @see UpdatableManager
 * @see HubManager
 * @see DeviceManager
 */
abstract class BrickOpMode: LinearOpMode() {
    // This is the infrastructure
    protected val commandScheduler = CommandScheduler
    protected val subsystemManager = SubsystemManager
    protected val updatableManager = UpdatableManager
    protected val hubManager = HubManager

    // FIXME: Pretty sure this generates a duplicate robot instance,
    //        different from the one the user will use. Needs to be looked into and fixed
    // This is the robot passed by the user to have its init and update methods called
    //private lateinit var internalRobot: Robot

    // This inits all the devices in the hardwareMap
    private val deviceManager = DeviceManager

    // This is used to determine the average loop times
    private var opModeStartTimestamp = 0L
    private var loopCount = 0
    protected var loopFrequency: Double = 0.0

    // This is used to record a TeleOp sequence and play it back as autonomous
    private lateinit var bindings: Bindings
    private lateinit var stateFilename: String
    private lateinit var lastSavedFile: String
    private lateinit var latestRecordingData: RecordingData
    private lateinit var readStates: MutableList<RobotState>
    private lateinit var localizer: Localizer

    private var isRecording = false

    private var teleOpAnnotation: TeleOp? = null
    private var autonomousAnnotation: Autonomous? = null

    private var recordingAnnotation: brickbot.quickstart.opmode.annotations.Recording? = null
    private var playbackAnnotation: brickbot.quickstart.opmode.annotations.Playback? = null
    private var bindingsAnnotation: brickbot.quickstart.opmode.annotations.Bindings? = null
    private var localizerAnnotation: brickbot.quickstart.opmode.annotations.Localizer? = null

    /**
     * This method is called once when the driver hits INIT.
     */
    abstract fun onInit()

    /**
     * This method is called repeatedly after driver hits INIT, but before they hit START.
     */
    abstract fun initLoop()

    /**
     * This method is called once when the driver hits START.
     */
    abstract fun onStart()

    /**
     * This method is called repeatedly after driver hits START.
     */
    abstract fun run()

    /**
     * This method is called at the start of a TeleOp recording.
     */
    open fun onRecordingStart() { }

    /**
     * This method is called at the end of a TeleOp recording.
     */
    open fun onRecordingEnd() { }

    override fun runOpMode() {
        // This checks for all annotations of interest, ensures they make sense
        // and then reads the data from them
        handleAnnotations()

        initInfrastructure()

        telemetry.addLine("Infrastructure has finished setting up.")
        telemetry.update()

        // This is the user's onInit method
        onInit()

        telemetry.addLine("OnInit has finished running.")
        telemetry.update()

        while (!isStarted() && !isStopRequested) {
            // This is the user's initLoop method
            initLoop()

            runInfrastructure()

            telemetry.addLine("InitLoop is running. OpMode should be ready for start.")
            telemetry.update()
        }
        waitForStart()

        // This timestamp is used to calculate the loop times
        opModeStartTimestamp = System.nanoTime()
        // This is the user's onStart method
        onStart()
        while (opModeIsActive() && !isStopRequested) {
            // This is the user's run method that runs until stop is requested or the OpMode ends
            run()
            // TODO: Separate the recording and playback behaviours into separate methods
            //  to clean up the runOpMode method
            if (isRecordingOpMode()) {
                if (gamepad1.shareWasReleased()) {
                    // Clear the latest saved recording and invoke the gc to release memory
                    // occupied by the previously saved recording
                    latestRecordingData = RecordingData()
                    System.gc()

                    isRecording = true
                    latestRecordingData.startTimestamp = System.nanoTime()
                    onRecordingStart()
                }

                if (isRecording) {
                    latestRecordingData.addState(
                        RobotState(
                            System.nanoTime(),
                            gamepad1.toByteArray(),
                            gamepad2.toByteArray(),
                            localizer.getPosition()
                        )
                    )
                }

                if (System.nanoTime() - latestRecordingData.startTimestamp > 30e9) {
                    isRecording = false
                    onRecordingEnd()
                    gamepad1.rumble(2000)
                    gamepad2.rumble(2000)
                }

                if (::latestRecordingData.isInitialized && gamepad1.touchpadWasReleased()) {
                    writeFile()
                }
            }

            // TODO: ADD PLAYBACK LOGIC AFTER INTEGRATING WITH PATH FOLLOWER
            if (isPlaybackOpMode()) {

            }

            runInfrastructure()
            bindings.update(gamepad1, gamepad2)

            loopFrequency = ++loopCount / ((System.nanoTime() - opModeStartTimestamp) * 1e-9)
            telemetry.addData("Loop frequency:",(""+ loopFrequency + "Hz"))

            if (isRecordingOpMode() && ::lastSavedFile.isInitialized) {
                telemetry.addData("Last recording saved in: ", lastSavedFile)
            }

            telemetry.update()
        }

        stopInfrastructure()
    }

    private fun initInfrastructure() {
        commandScheduler.reset()

        // This calls the init methods automatically for all devices
        deviceManager.initDevices(hardwareMap)

        // This calls the init methods automatically for all subsystems
        subsystemManager.init(hardwareMap)

        // This grabs the hubs from the hardware map
        hubManager.init(hardwareMap)

        // This block calls the default init method and the opMode type specific init methods
//        internalRobot.init()
//        if (isAutonomous()) {
//            internalRobot.autonomousInit()
//        } else {
//            internalRobot.teleOpInit()
//        }
    }
    /**
     * This method runs the commandScheduler and subsystemManager during init and run.
     * If any other infrastructure gets written, it should be called inside here.
     */
    private fun runInfrastructure() {
       // internalRobot.update()
        commandScheduler.run()
        subsystemManager.run()
        updatableManager.run()
        hubManager.clearCache()
    }

    private fun stopInfrastructure() {
        updatableManager.clear()
        commandScheduler.reset()
    }

    private fun handleAnnotations() {
        // OpMode type annotations
        teleOpAnnotation = this.javaClass.getAnnotation(TeleOp::class.java)
        autonomousAnnotation = this.javaClass.getAnnotation(Autonomous::class.java)

        // Additional BrickBot annotations
        recordingAnnotation = this.javaClass.getAnnotation(
            brickbot.quickstart.opmode.annotations.Recording::class.java
        )
        playbackAnnotation = this.javaClass.getAnnotation(
            brickbot.quickstart.opmode.annotations.Playback::class.java
        )
        bindingsAnnotation = this.javaClass.getAnnotation(
            brickbot.quickstart.opmode.annotations.Bindings::class.java
        )
        localizerAnnotation = this.javaClass.getAnnotation(
            brickbot.quickstart.opmode.annotations.Localizer::class.java
        )

        checkAnnotationsMakeSense()

        bindings = bindingsAnnotation!!.bindings.java.getDeclaredConstructor().newInstance()
        //FIXME: Null pointer Exception
//        localizer = localizerAnnotation!!.localizer.java.getDeclaredConstructor().newInstance()


        handleFilename()
    }

    private fun handleFilename() {
        stateFilename = if (isRecordingOpMode()) recordingAnnotation!!.filename
        else if (isPlaybackOpMode()) playbackAnnotation!!.filename
        else "autonomous"

        val temp = stateFilename.split(".")

        if (temp.last().lowercase() == "json") {
            temp.dropLast(1)
        }

        stateFilename = temp.joinToString()

        if (isPlaybackOpMode()) {
            readFile()
        }
    }

    private fun checkAnnotationsMakeSense() {
        if (isAutonomous()) {
            if (isRecordingOpMode()) {
                throw RuntimeException("An Autonomous cannot be an @Recording annotated OpMode.")
            }
            if (isPlaybackOpMode() && !submittedBindings()) {
                throw RuntimeException("You need to add an @Bindings annotation containing the Bindings to be able to playback a recording.")
            }
        } else if (isTeleOp()) {
            if (!submittedBindings()) {
                throw RuntimeException("You need to add an @Bindings annotation containing the Bindings to be able to control the robot in TeleOp.")
            }
            if (isPlaybackOpMode()) {
                throw RuntimeException("A TeleOp cannot be an @Playback annotated OpMode.")
            }
            if (isRecordingOpMode() && !submittedLocalizer()) {
                throw RuntimeException("You need to add an @Localizer annotation containing the Localizer to be able to record a TeleOp sequence.")
            }
        } else {
            throw RuntimeException("Your OpMode needs to be annotated as @Autonomous or @TeleOp.")
        }
    }

    private fun readFile() {
        val path = File(Environment.getExternalStorageDirectory(), "FIRST/recordings")
        val file = File(path, "$stateFilename.json")

        val output = mutableListOf<RobotState>()

        try {
            // Read the file content into a String
            val fis = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(fis))
            val sb = StringBuilder()
            var line = reader.readLine()

            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }

            reader.close()

            // Parse the string as a JSON Array
            val jsonArray = JSONArray(sb.toString())

            // Iterate through the array and reconstruct RobotState objects
            for (i in 0..<jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val timestamp = obj.getLong("timestamp")

                // Decode the Base64 strings back into byte arrays
                val g1 = Base64.decode(obj.getString("gamepad1State"), Base64.NO_WRAP)
                val g2 = Base64.decode(obj.getString("gamepad2State"), Base64.NO_WRAP)

                val x = obj.getDouble("x")
                val y = obj.getDouble("y")
                val heading = obj.getDouble("heading")

                output.add(RobotState(timestamp, g1, g2, Pose(x, y, heading)))
            }
        } catch (e: Exception) {
            Log.e("JsonReader", "Error reading file: " + e.message)
            e.printStackTrace()
        }

        readStates = output
    }

    // TODO: Make this async
    private fun writeFile() {
        val robotStateArray = JSONArray()

        try {
            for (state in latestRecordingData.stateList) {
                val stateNode = JSONObject()

                stateNode.put("timestamp", state.timestamp)

                // Encode the byte arrays into Base64 strings for JSON compatibility
                stateNode.put("gamepad1State", Base64.encodeToString(state.gamepad1State, Base64.NO_WRAP))
                stateNode.put("gamepad2State", Base64.encodeToString(state.gamepad2State, Base64.NO_WRAP))

                stateNode.put("x", state.pose.x)
                stateNode.put("y", state.pose.y)
                stateNode.put("heading", state.pose.heading)

                robotStateArray.put(stateNode)
            }

            val path = File(Environment.getExternalStorageDirectory(), "FIRST/recordings")

            if (!path.exists()) {
                path.mkdirs()
            }

            var file = File(path, stateFilename)
            var counter = 0

            // Try to find a file that doesn't already exist, to avoid overwriting
            while (file.exists()) {
                file = File(path, stateFilename + (++counter) + ".json")
            }

            lastSavedFile = "$stateFilename$counter.json"

            val writer = FileWriter(file)
            writer.write(robotStateArray.toString(2))
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            Log.e("JsonReader", "Error writing to file: " + e.message)
            e.printStackTrace()
        }
    }

    private fun isAutonomous(): Boolean {
        return autonomousAnnotation != null
    }

    private fun isTeleOp(): Boolean {
        return teleOpAnnotation != null
    }

    private fun isRecordingOpMode(): Boolean {
        return recordingAnnotation != null
    }

    private fun isPlaybackOpMode(): Boolean {
        return playbackAnnotation != null
    }

    private fun submittedBindings(): Boolean {
        return bindingsAnnotation != null
    }

    private fun submittedLocalizer(): Boolean {
        return localizerAnnotation != null
    }
}