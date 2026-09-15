package org.brickbot.ftc.examplecode.teleop;
//import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.brickbot.ftc.examplecode.RobotHardware;
import org.brickbot.ftc.examplecode.TeleOpBindings;

import brickbot.quickstart.opmode.BrickOpMode;

@TeleOp(name = "BrickTuning", group = "Development")
@brickbot.quickstart.opmode.annotations.Robot(robot = RobotHardware.class)
@brickbot.quickstart.opmode.annotations.Bindings(bindings = TeleOpBindings.class)
public class Tuning extends BrickOpMode {
    RobotHardware robot;
    @Override
    public void onInit() {
        RobotHardware.getInstance().gamepad1 = gamepad1;
        RobotHardware.getInstance().gamepad2 = gamepad2;
        RobotHardware.getInstance().init();
        robot = RobotHardware.getInstance();
    }

    @Override
    public void initLoop() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void run() {
        sleep(10);
       // drawRobot(robot.mecanumDrive.getX());
    }
    public static final double ROBOT_RADIUS = 9;

}
