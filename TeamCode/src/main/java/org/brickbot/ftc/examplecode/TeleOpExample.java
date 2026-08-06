package org.brickbot.ftc.examplecode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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



