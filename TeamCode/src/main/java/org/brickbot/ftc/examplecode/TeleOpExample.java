package org.brickbot.ftc.examplecode.teleop;

import com.bylazar.field.Style;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.brickbot.ftc.examplecode.RobotHardware;
import org.brickbot.ftc.examplecode.TeleOpBindings;

import brickbot.quickstart.opmode.BrickOpMode;

@TeleOp(name = "TeleOpExample", group = "Examples")
@brickbot.quickstart.opmode.annotations.Robot(robot = RobotHardware.class)
@brickbot.quickstart.opmode.annotations.Bindings(bindings = TeleOpBindings.class)
public class TeleOpExample extends BrickOpMode {
    @Override
    public void onInit() {
        System.out.println("Passed gamepads to robot.");

        RobotHardware.getInstance().gamepad1 = gamepad1;
        RobotHardware.getInstance().gamepad2 = gamepad2;
        RobotHardware.getInstance().init();

        getHubManager().disableExpansionHubCaching();
    }

    @Override
    public void initLoop() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void run() {
    }

}



