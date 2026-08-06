package brickbot.quickstart.controlalgorithms

import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import kotlin.math.abs
import kotlin.math.sign

class PDFSController(
    var kP: Double,
    var kD: Double,
    var minKF: Double,
    var maxKF: Double,
    var kStatic: Double
) {
    var feedforwardMultiplier = 1.0
        private set
    var minPosition = Double.MIN_VALUE
        private set
    var maxPosition = Double.MAX_VALUE
        private set
    var errorThreshold = 50.0
        private set

    private var lastPosition = 0.0
    private var target = 0.0
    private val timer: ElapsedTime = ElapsedTime()

    constructor(kP: Double, kD: Double, kF: Double, kStatic: Double): this(kP, kD, kF, kF, kStatic)
    constructor(pdfsConstants: PDFSConstants) : this(pdfsConstants.kP, pdfsConstants.kD, pdfsConstants.minKF, pdfsConstants.maxKF, pdfsConstants.kStatic)

    fun setConstants(kP: Double, kD: Double, minKF: Double, maxKF: Double, kStatic: Double): PDFSController {
        this.kP = kP
        this.kD = kD
        this.minKF = minKF
        this.maxKF = maxKF
        this.kStatic = kStatic

        return this
    }

    fun setConstants(kP: Double, kD: Double, kF: Double, kStatic: Double): PDFSController {
        return setConstants(kP, kD, kF, kF, kStatic)
    }

    fun setConstants(pdfsConstants: PDFSConstants): PDFSController {
        return setConstants(pdfsConstants.kP, pdfsConstants.kD, pdfsConstants.minKF, pdfsConstants.maxKF, pdfsConstants.kStatic)
    }

    fun setKF(kF: Double): PDFSController {
        if (minKF == maxKF) {
            throw IllegalArgumentException("You tried to set a singular kF to a PDFSController with a min and max kF.")
        }
        minKF = kF
        maxKF = kF

        return this
    }

    fun setTarget(target: Double): PDFSController {
        this.target = target

        return this
    }

    fun setErrorThreshold(errorThreshold: Double): PDFSController {
        this.errorThreshold = errorThreshold

        return this
    }

    fun setLimits(minPosition: Double, maxPosition: Double): PDFSController {
        this.minPosition = minPosition
        this.maxPosition = maxPosition

        return this
    }

    fun compute(currentPosition: Double, targetPosition: Double): Double {
        target = targetPosition
        return compute(currentPosition)
    }

    fun compute(currentPosition: Double): Double {
        val error = target - currentPosition
        val currentVelocity = (lastPosition - currentPosition) / timer.seconds()
        lastPosition = currentPosition

        val feedforwardPower = feedforwardMultiplier * (minKF + (maxKF - minKF) * minPosition / (maxPosition - minPosition))
        val staticPower = if (abs(error) < errorThreshold) sign(error) * kStatic else 0.0

        return kP * error - kD * currentVelocity + staticPower + feedforwardPower
    }

    /**
     * @param multiplier value between -1 and 1
     * Example: consider a pivoting arm, you would want the feedforward
     * to scale sinusoidally with the arm's angle from the ground:
     * for the pivot motor:
        ```
            update(){
                //...
                controller.setFeedforwardMultiplier(sin(angle));
                power = controller.calculate(motor.getPosition());
                //...
            }
        ```
     */
    fun setFeedforwardMultiplier(multiplier: Double): PDFSController {
        feedforwardMultiplier = Range.clip(multiplier, -1.0, 1.0)

        return this
    }
}