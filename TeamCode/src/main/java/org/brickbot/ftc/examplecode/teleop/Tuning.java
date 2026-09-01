package org.brickbot.ftc.examplecode.teleop;
import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
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
    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();
    public static void drawRobot(double x, double y , double heading, Style style) {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(heading)) {
            return;
        }
        panelsField.setStyle(style);
        panelsField.moveCursor(x, y);
        panelsField.circle(ROBOT_RADIUS);
        Vector v = new Vector(Math.cos(heading), Math.sin(heading));
        v.setMagnitude(v.getMagnitude() * ROBOT_RADIUS);
        double x1 = x + v.getXComponent() / 2, y1 = y + v.getYComponent() / 2;
        double x2 = x + v.getXComponent(), y2 = y + v.getYComponent();

        panelsField.setStyle(style);
        panelsField.moveCursor(x1, y1);
        panelsField.line(x2, y2);
    }
}
