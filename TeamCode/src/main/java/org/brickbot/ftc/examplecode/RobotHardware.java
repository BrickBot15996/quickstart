package org.brickbot.ftc.examplecode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.brickbot.ftc.examplecode.subsystems.Intake;
import org.brickbot.ftc.examplecode.subsystems.MecanumDrive;

import brickbot.quickstart.subsystems.Robot;

public class RobotHardware extends Robot {
    private static final RobotHardware INSTANCE = new RobotHardware();

    public Intake intake;
    public MecanumDrive mecanumDrive;

    public Gamepad gamepad1;
    public Gamepad gamepad2;

    private RobotHardware() {
        intake = new Intake();
        mecanumDrive = new MecanumDrive()
                .setDrivingMode(MecanumDrive.DrivingMode.FIELD_CENTRIC)
                .setSteeringBindings(MecanumDrive.SteeringBindings.RIGHT_STICK)
                .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE)
                .setIsLocalizationEnabled(false);
    }

    public static RobotHardware getInstance() {
        return INSTANCE;
    }
    @Override
    public void init() {
        System.out.println("Init robot");
    }

    @Override
    public void autonomousInit() {

    }

    @Override
    public void teleOpInit() {

    }

    @Override
    public void update() {

    }
}
