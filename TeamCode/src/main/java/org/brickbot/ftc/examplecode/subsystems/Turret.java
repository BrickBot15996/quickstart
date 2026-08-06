package org.brickbot.ftc.examplecode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.brickbot.ftc.examplecode.Constants;
import org.brickbot.ftc.examplecode.RobotHardware;
import org.jetbrains.annotations.NotNull;
import brickbot.quickstart.devices.BrickMotor;
import brickbot.quickstart.devices.BrickServo;
import brickbot.quickstart.subsystems.Subsystem;
import com.qualcomm.robotcore.util.Range;


public class Turret extends Subsystem {
    private RobotHardware robot;
    private BrickServo turretLeft;
    private BrickServo turretRight;
    public Turret() {
        turretLeft = new BrickServo("turretLeft");
        turretRight = new BrickServo("turretRight");
    }
    public enum TurretState {
        TRAKING,
        CONSTANT;
    }
    private TurretState state =  TurretState.CONSTANT;
    private double servoPosition;

    @Override
    public void init(@NotNull HardwareMap hwMap) {
        robot = RobotHardware.getInstance();
    }

    @Override
    public void read() {

    }

    @Override
    public void compute() {
        if(state  == TurretState.CONSTANT)
        {
            servoPosition  = Constants.TurretConstants.constantServoPosition;
        }
    }
    @Override
    public void write() {
        turretRight.setPosition(Range.clip(servoPosition, Constants.TurretConstants.turretMinLimit, Constants.TurretConstants.turretMaxLimit));
        turretLeft.setPosition(Range.clip(servoPosition, Constants.TurretConstants.turretMinLimit, Constants.TurretConstants.turretMaxLimit));
    }
    public void setTurretState(TurretState state) {
        this.state = state;

    }
}
