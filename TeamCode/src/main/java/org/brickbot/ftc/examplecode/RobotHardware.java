package org.brickbot.ftc.examplecode;

import org.brickbot.ftc.examplecode.subsystems.Intake;

import brickbot.quickstart.subsystems.Robot;

public class RobotHardware extends Robot {
    private static RobotHardware INSTANCE;
    private SubsystemExample subsystemExample = new SubsystemExample();
    public Intake intake = new Intake();
    private RobotHardware() { }

    public static RobotHardware getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RobotHardware();
        }
        return INSTANCE;
    }
    @Override
    public void init() {
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
