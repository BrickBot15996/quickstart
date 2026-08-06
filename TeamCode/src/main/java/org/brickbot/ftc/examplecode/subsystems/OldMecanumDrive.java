package org.brickbot.ftc.examplecode.subsystems;
import androidx.annotation.NonNull;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.brickbot.ftc.examplecode.RobotHardware;

import brickbot.quickstart.controlalgorithms.PDFSController;
import brickbot.quickstart.devices.BrickMotor;
import brickbot.quickstart.subsystems.Subsystem;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class OldMecanumDrive extends Subsystem {
    private RobotHardware robot = RobotHardware.getInstance();

    private BrickMotor frontLeft = new BrickMotor("frontLeft");
    private BrickMotor frontRight = new BrickMotor("frontRight");
    private BrickMotor rearLeft = new BrickMotor("rearLeft");
    private BrickMotor rearRight = new BrickMotor("rearRight");

    private GoBildaPinpointDriver pinpoint;

    private double x;
    private double y;
    private double turn;
    private double lastTurn;
    private double headingOffset = Math.PI / 2.0; //TODO GRAB FROM AUTONOMOUS
    private double currHeading = 0;
    private double targetHeading;
    private double brake = 1.0;
    private double kStatic = 0.0;
    public static double kP = 0, kD = 0, kF = 0, kS = 0;
    public PDFSController headingController = new PDFSController(0,0,0,0);

    public OldMecanumDrive() { }

    public OldMecanumDrive setHeadingPDFS(double kP, double kD, double kF, double kStatic) {
        headingController = new PDFSController(kP, kD, kF, kStatic).setErrorThreshold(1.0);
        return this;
    }

    @Override
    public void init(@NonNull HardwareMap hwMap) {
        System.out.println("Init robot");

        pinpoint = hwMap.get(GoBildaPinpointDriver.class, "pinpoint");

//        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        rearLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        rearRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//
//        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        frontLeft.setDirection(DcMotor.Direction.REVERSE);
//        frontRight.setDirection(DcMotor.Direction.REVERSE);
//        rearLeft.setDirection(DcMotor.Direction.REVERSE);
//        rearRight.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void read() {
        pinpoint.update();
        headingController.setConstants(kP, kD, kF, kS);
        double imuHeading = pinpoint.getHeading(AngleUnit.RADIANS);

        currHeading = AngleUnit.normalizeRadians(imuHeading + headingOffset);

        x = RobotHardware.getInstance().gamepad1.left_stick_x * (1 - kStatic);
        x = Math.abs(x) > 0.03 ? x + Math.signum(x) * kStatic : 0.0;

        y = -RobotHardware.getInstance().gamepad1.left_stick_y * (1 - kStatic);
        y = Math.abs(y) > 0.03 ? y + Math.signum(y) * kStatic : 0.0;

        turn = - (RobotHardware.getInstance().gamepad1.right_stick_x) * (1 - kStatic);
        turn = Math.abs(turn) > 0.03 ? turn + Math.signum(turn) * kStatic : 0.0;

        if (RobotHardware.getInstance().gamepad1.options)
            headingOffset = -imuHeading;
    }

    @Override
    public void compute() {
        if (Double.compare(turn, 0.0) == 0 && Double.compare(lastTurn, 0.0) != 0) {
            targetHeading = currHeading;
        }
        lastTurn = turn;

        double xCopy = x;
        double yCopy = y;

        x = xCopy * Math.cos(-currHeading) - yCopy * Math.sin(-currHeading);
        y = xCopy * Math.sin(-currHeading) + yCopy * Math.cos(-currHeading);

        if (Double.compare(turn, 0.0) == 0)
            turn = headingController.compute(Math.toDegrees(currHeading), Math.toDegrees(targetHeading));
    }

    @Override
    public void write() {
        double voltageCorrection = 1;
        double denominator = Math.max((Math.abs(y) + Math.abs(x) + Math.abs(turn)) * voltageCorrection, 1.0);
        double frontLeftPower = (-y + x - turn) * voltageCorrection / denominator;
        double rearLeftPower = (y + x - turn) * voltageCorrection / denominator;
        double rearRightPower = (-y + x + turn) * voltageCorrection / denominator;
        double frontRightPower = (y + x + turn) * voltageCorrection / denominator;

        frontLeft.setPower(frontLeftPower * brake);
        rearLeft.setPower(rearLeftPower * brake);
        rearRight.setPower(rearRightPower * brake);
        frontRight.setPower(frontRightPower * brake);
    }

    public double getHeading() {
        return currHeading;
    }
}

