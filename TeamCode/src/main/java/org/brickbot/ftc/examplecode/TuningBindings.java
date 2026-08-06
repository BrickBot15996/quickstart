package org.brickbot.ftc.examplecode;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.brickbot.ftc.examplecode.subsystems.Intake;
import org.jetbrains.annotations.NotNull;

import brickbot.quickstart.recordautonomous.Bindings;
public class TuningBindings extends Bindings {
    RobotHardware robot;
    public TuningBindings() {
        robot = RobotHardware.getInstance();
    }

    @Override
    public void update(@NotNull Gamepad gamepad1, @NotNull Gamepad gamepad2) {
        if (gamepad1.circle) {
            robot.mecanumDrive.rearRight.setPower(1);
        }
        else {
            robot.mecanumDrive.rearRight.setPower(0);
        }
        if (gamepad1.triangle) {
            robot.mecanumDrive.frontRight.setPower(1);
        }
        else {
            robot.mecanumDrive.frontRight.setPower(0);
        }
        if (gamepad1.square) {
            robot.mecanumDrive.frontLeft.setPower(1);
        }
        else {
            robot.mecanumDrive.frontLeft.setPower(0);
        }
        if (gamepad1.x) {
            robot.mecanumDrive.rearLeft.setPower(1);
        }
        else {
            robot.mecanumDrive.rearLeft.setPower(0);
        }
    }
}
