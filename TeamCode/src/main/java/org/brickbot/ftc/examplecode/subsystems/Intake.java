package org.brickbot.ftc.examplecode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.brickbot.ftc.examplecode.RobotHardware;
import org.jetbrains.annotations.NotNull;
import brickbot.quickstart.devices.BrickMotor;
import brickbot.quickstart.subsystems.Subsystem;

public class Intake extends Subsystem {
    private RobotHardware robot;
    private BrickMotor intakeMotor;

    public Intake() {
        intakeMotor = new BrickMotor("intakeMotor");
    }

    public enum IntakeState {
        ON(1.0),
        OFF(0.0);
        private final double power;
        public double getPower() {
            return power;
        }

        IntakeState(double power) {
            this.power = power;
        }
    }
    private IntakeState state =  IntakeState.OFF;

    @Override
    public void init(@NotNull HardwareMap hwMap) {
        robot = RobotHardware.getInstance();
        System.out.println("intake init");
    }

    @Override
    public void read() {

    }

    @Override
    public void compute() {
    }

    @Override
    public void write() {
        intakeMotor.setPower(state.getPower());
    }
    public void setIntakeState(IntakeState state) {
        this.state = state;

    }
}
