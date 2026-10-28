package org.brickbot.ftc.examplecode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.brickbot.ftc.examplecode.RobotHardware;
import org.jetbrains.annotations.NotNull;
import brickbot.quickstart.devices.BrickMotor;
import brickbot.quickstart.subsystems.Subsystem;

public class Indexer extends Subsystem {
    private RobotHardware robot;
    private BrickMotor indexerMotor;

    public Indexer() {
        indexerMotor = new BrickMotor("intakeMotor");
    }



    @Override
    public void read() {

    }

    @Override
    public void compute() {
    }


    @Override
    public void init(@NotNull HardwareMap hwMap) {

    }

    @Override
    public void write() {

    }
}
