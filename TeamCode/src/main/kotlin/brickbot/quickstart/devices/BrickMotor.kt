package brickbot.quickstart.devices

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import kotlin.math.abs

class BrickMotor(deviceName: String): DcMotorEx, BrickDevice(deviceName) {
    private lateinit var motor: DcMotorEx

    private var writeDelta = 0.05
    private var lastPower = 0.0

    override fun init(hwMap: HardwareMap) {
        System.out.println("Motor ${this.deviceName} init")
        motor = hwMap.get(DcMotorEx::class.java, deviceName)
    }

    fun setWriteDelta(newWriteDelta: Double) {
        writeDelta = newWriteDelta
    }

    override fun setPower(power: Double) {
        val newPower = Range.clip(power, -1.0, 1.0)
        if (abs(newPower - lastPower) > writeDelta || (newPower == 0.0 && lastPower != 0.0)) {
            lastPower = newPower
            motor.power = newPower
        }
    }

    override fun getPower(): Double {
        return lastPower
    }

    override fun setMotorEnable() {
        motor.setMotorEnable()
    }

    override fun setMotorDisable() {
        motor.setMotorDisable()
    }

    override fun isMotorEnabled(): Boolean {
        return motor.isMotorEnabled
    }

    override fun setVelocity(angularRate: Double) {
        motor.setVelocity(angularRate)
    }

    override fun setVelocity(
        angularRate: Double,
        unit: AngleUnit?
    ) {
        motor.setVelocity(angularRate, unit)
    }

    override fun getVelocity(): Double {
        return motor.velocity
    }

    override fun getVelocity(unit: AngleUnit?): Double {
        return motor.getVelocity(unit)
    }

    override fun setPIDCoefficients(
        mode: DcMotor.RunMode?,
        pidCoefficients: PIDCoefficients?
    ) {
        motor.setPIDCoefficients(mode, pidCoefficients)
    }

    override fun setPIDFCoefficients(
        mode: DcMotor.RunMode?,
        pidfCoefficients: PIDFCoefficients?
    ) {
        motor.setPIDFCoefficients(mode, pidfCoefficients)
    }

    override fun setVelocityPIDFCoefficients(
        p: Double,
        i: Double,
        d: Double,
        f: Double
    ) {
        motor.setVelocityPIDFCoefficients(p, i, d, f)
    }

    override fun setPositionPIDFCoefficients(p: Double) {
        motor.setPositionPIDFCoefficients(p)
    }

    override fun getPIDCoefficients(mode: DcMotor.RunMode?): PIDCoefficients? {
        return motor.getPIDCoefficients(mode)
    }

    override fun getPIDFCoefficients(mode: DcMotor.RunMode?): PIDFCoefficients? {
        return motor.getPIDFCoefficients(mode)
    }

    override fun setTargetPositionTolerance(tolerance: Int) {
        motor.targetPositionTolerance = tolerance
    }

    override fun getTargetPositionTolerance(): Int {
        return motor.targetPositionTolerance
    }

    override fun getCurrent(unit: CurrentUnit?): Double {
        return motor.getCurrent(unit)
    }

    override fun getCurrentAlert(unit: CurrentUnit?): Double {
        return motor.getCurrentAlert(unit)
    }

    override fun setCurrentAlert(
        current: Double,
        unit: CurrentUnit?
    ) {
        motor.setCurrentAlert(current, unit)
    }

    override fun isOverCurrent(): Boolean {
        return motor.isOverCurrent
    }

    override fun getMotorType(): MotorConfigurationType? {
        return motor.motorType
    }

    override fun setMotorType(motorType: MotorConfigurationType?) {
        motor.motorType = motorType
    }

    override fun getController(): DcMotorController? {
        return motor.controller
    }

    override fun getPortNumber(): Int {
        return motor.portNumber
    }

    override fun setZeroPowerBehavior(zeroPowerBehavior: DcMotor.ZeroPowerBehavior?) {
        motor.zeroPowerBehavior = zeroPowerBehavior
    }

    override fun getZeroPowerBehavior(): DcMotor.ZeroPowerBehavior? {
        return motor.zeroPowerBehavior
    }

    override fun setPowerFloat() {
        motor.setPowerFloat()
    }

    override fun getPowerFloat(): Boolean {
        return motor.powerFloat
    }

    override fun setTargetPosition(position: Int) {
        motor.targetPosition = position
    }

    override fun getTargetPosition(): Int {
        return motor.targetPosition
    }

    override fun isBusy(): Boolean {
        return motor.isBusy
    }

    override fun getCurrentPosition(): Int {
        return motor.currentPosition
    }

    override fun setMode(mode: DcMotor.RunMode?) {
        motor.mode = mode
    }

    override fun getMode(): DcMotor.RunMode? {
        return motor.mode
    }

    override fun setDirection(direction: DcMotorSimple.Direction?) {
        motor.direction = direction
    }

    override fun getDirection(): DcMotorSimple.Direction? {
        return motor.direction
    }

    override fun getManufacturer(): HardwareDevice.Manufacturer? {
        return motor.manufacturer
    }

    override fun getConnectionInfo(): String? {
        return motor.connectionInfo
    }

    override fun getVersion(): Int {
        return motor.version
    }

    override fun resetDeviceConfigurationForOpMode() {
        motor.resetDeviceConfigurationForOpMode()
    }

    override fun close() {
        motor.close()
    }
}