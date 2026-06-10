package brickbot.quickstart.recordautonomous

import com.qualcomm.robotcore.hardware.Gamepad

abstract class Bindings {
    abstract fun update(gamepad1: Gamepad, gamepad2: Gamepad);

    fun update(gamepad1State: ByteArray, gamepad2State: ByteArray) {
        val gamepad1 = Gamepad()
        val gamepad2 = Gamepad()

        gamepad1.fromByteArray(gamepad1State)
        gamepad2.fromByteArray(gamepad2State)

        update(gamepad1, gamepad2)
    }
}