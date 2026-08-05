package org.brickbot.ftc.examplecode;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.brickbot.ftc.examplecode.subsystems.Intake;
import org.jetbrains.annotations.NotNull;

import brickbot.quickstart.recordautonomous.Bindings;

public class TeleOpBindings extends Bindings {
    RobotHardware robot = RobotHardware.getInstance();
    @Override
    public void update(@NotNull Gamepad gamepad1, @NotNull Gamepad gamepad2) {
        if (gamepad1.right_trigger > 0.5) {
            robot.intake.setIntakeState(Intake.IntakeState.ON);
        }
        else {
            robot.intake.setIntakeState(Intake.IntakeState.OFF);
        }
    }

}
