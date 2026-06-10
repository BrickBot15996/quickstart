package brickbot.quickstart.follower

import brickbot.quickstart.controlalgorithms.PDFSController
import brickbot.quickstart.devices.BrickMotor
import brickbot.quickstart.updatable.Updatable
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class Follower(
    private var hardwareMap: HardwareMap
): Updatable {
    companion object {
        @JvmStatic var xP = 0.0
        @JvmStatic var xD = 0.0
        @JvmStatic var xF = 0.0
        @JvmStatic var xS = 0.0

        @JvmStatic var yP = 0.0
        @JvmStatic var yD = 0.0
        @JvmStatic var yF = 0.0
        @JvmStatic var yS = 0.0

        @JvmStatic var headingP = 0.0
        @JvmStatic var headingD = 0.0
        @JvmStatic var headingF = 0.0
        @JvmStatic var headingS = 0.0
    }

    private lateinit var targetPose: Pose
    private lateinit var pinpointLocalizer: PinpointLocalizer

    private lateinit var flDirection: DcMotorSimple.Direction
    private lateinit var frDirection: DcMotorSimple.Direction
    private lateinit var rlDirection: DcMotorSimple.Direction
    private lateinit var rrDirection: DcMotorSimple.Direction

    var xPDFS = PDFSController(0.0, 0.0, 0.0, 0.0)
    var yPDFS = PDFSController(0.0, 0.0, 0.0, 0.0)
    var headingPDFS = PDFSController(0.0, 0.0, 0.0, 0.0)

    private lateinit var frontLeft: BrickMotor
    private lateinit var frontRight: BrickMotor
    private lateinit var rearLeft: BrickMotor
    private lateinit var rearRight: BrickMotor

    fun setEncoderDirections(directionStrafe: GoBildaPinpointDriver.EncoderDirection, directionForward: GoBildaPinpointDriver.EncoderDirection) {
        pinpointLocalizer.setEncoderDirection(directionStrafe, directionForward)
    }

    fun setEncoderOffsets(offsetStrafe: Double, offsetForward: Double, distanceUnit: DistanceUnit) {
        pinpointLocalizer.setEncoderOffsets(offsetStrafe, offsetForward, distanceUnit)
    }

    fun setMotorNames(frontLeftName: String, frontRightName: String, rearLeftName: String, rearRightName: String) {
        frontLeft = BrickMotor(frontLeftName)
        frontRight = BrickMotor(frontRightName)
        rearLeft = BrickMotor(rearLeftName)
        rearRight = BrickMotor(rearRightName)
    }

    fun setMotorDirections(frontLeftDirection: DcMotorSimple.Direction, frontRightDirection: DcMotorSimple.Direction, rearLeftDirection: DcMotorSimple.Direction, rearRightDirection: DcMotorSimple.Direction) {
        flDirection = frontLeftDirection
        frDirection = frontRightDirection
        rlDirection = rearLeftDirection
        rrDirection = rearRightDirection
    }

    fun setTargetPose(pose: Pose) {
        targetPose = pose
    }

    fun setXConstants(xP: Double, xD: Double, xF: Double, xS: Double) {
        Companion.xP = xP
        Companion.xD = xP
        Companion.xF = xP
        Companion.xS = xP
    }

    fun setYConstants(yP: Double, yD: Double, yF: Double, yS: Double) {
        Companion.yP = yP
        Companion.yD = yP
        Companion.yF = yP
        Companion.yS = yP
    }

    fun setHeadingConstants(headingP: Double, headingD: Double, headingF: Double, headingS: Double) {
        Companion.headingP = headingP
        Companion.headingD = headingD
        Companion.headingF = headingF
        Companion.headingS = headingS
    }

    fun init() {
        pinpointLocalizer = PinpointLocalizer()
        pinpointLocalizer.setHardwareMap(hardwareMap).init()

        frontLeft.init(hardwareMap)
        frontRight.init(hardwareMap)
        rearLeft.init(hardwareMap)
        rearRight.init(hardwareMap)

        frontLeft.direction = flDirection
        frontRight.direction = frDirection
        rearLeft.direction = rlDirection
        rearRight.direction = rrDirection

        frontLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        frontRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rearLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rearRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        frontLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        frontRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rearLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rearRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        frontLeft.power = 0.0
        frontRight.power = 0.0
        rearLeft.power = 0.0
        rearRight.power = 0.0
    }

    override fun update() {
        xPDFS.setConstants(xP, xD, xF, xS)
        yPDFS.setConstants(yP, yD, yF, yS)
        headingPDFS.setConstants(headingP, headingD, headingF, headingS)

        val posX = pinpointLocalizer.getX()
        val posY = pinpointLocalizer.getY()
        val botHeading = pinpointLocalizer.getHeading()

        val x = xPDFS.calculate(posX, targetPose.x)
        val y = yPDFS.calculate(posY, targetPose.y)
        val rx = headingPDFS.calculate(pinpointLocalizer.getHeading(), targetPose.heading)

        val rotX = x * cos(-botHeading) - y * sin(-botHeading)
        val rotY = x * sin(-botHeading) + y * cos(-botHeading)

        val denominator = max(abs(rotY) + abs(rotX) + abs(rx), 1.0)
        val frontLeftPower = (rotX + rotY + rx) / denominator
        val frontRightPower = (-rotX + rotY - rx) / denominator
        val rearLeftPower = (-rotX + rotY + rx) / denominator
        val rearRightPower = (rotX + rotY - rx) / denominator

        frontLeft.power = frontLeftPower
        frontRight.power = frontRightPower
        rearLeft.power = rearLeftPower
        rearRight.power = rearRightPower
    }
}