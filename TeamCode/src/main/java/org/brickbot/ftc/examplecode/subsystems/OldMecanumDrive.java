//package org.firstinspires.ftc.teamcode.common.hardware.subsystems;
//
//import com.acmerobotics.dashboard.config.Config;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.hardware.IMU;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.teamcode.common.controltheory.PDFSController;
//import org.firstinspires.ftc.teamcode.common.controltheory.SimpleMath;
//import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;
//import org.opencv.core.Mat;
//
//import brickbot.quickstart.subsystems.Subsystem;
//
//@Config
//public class OldMecanumDrive implements Subsystem {
//
//    RobotHardware robot = RobotHardware.getInstance();
//    private double x;
//    private double y;
//    private double rotX;
//    private double rotY;
//    public double turn;
//    private double lastTurn;
//    public double headingOffset = Math.PI, headingVelocity = 0; //TODO GRAB FROM AUTONOMOUS
//    public double botHeading = 0, lastHeading = 0;
//    public double targetHeading = 0;
//    private double brake = 1.0;
//    public static double P = 0.023, D = 0.03, F = 0, S = 0;
//    public static double P2 = 0.0085, D2 = 0.003, F2 = 0, S2 = 0;
//    public boolean lock = true, headingManuallyControlled = true;
//    PDFSController headingController = new PDFSController(P,D,F,S);
//    PDFSController secondaryheadingController = new PDFSController(P2,D2,F2,S2);
//    private final ElapsedTime headingTimer = new ElapsedTime();
//    public FieldCentricV2() {}
//
//    @Override
//    public void init(HardwareMap hwMap) {
//        robot.frontLeft.init(hwMap);
//        robot.rearLeft.init(hwMap);
//        robot.rearRight.init(hwMap);
//        robot.frontRight.init(hwMap);
//        robot.frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        robot.rearLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        robot.rearRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        robot.frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        robot.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        robot.rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        robot.rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        robot.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        robot.frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
//        robot.rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
//        robot.imu.resetYaw();
//    }
//    @Override
//    public void read() {
//        headingController.setConstants(P, D, F, S);
//        secondaryheadingController.setConstants(P2, D2, F2, S2);
//        botHeading = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) + headingOffset;
//        headingVelocity = (lastHeading - botHeading) / headingTimer.seconds();
//        headingTimer.reset();
//        lastHeading = botHeading;
//        x = robot.gamepad1.left_stick_x;
//        y = -robot.gamepad1.left_stick_y;
//        turn =  (robot.gamepad1.right_stick_x);
//        turn = SimpleMath.clamp(turn, -1, 1);
//        if(robot.slides.getExtensionCm() > 35)
//            brake = 0.37;
//        else if (robot.gamepad1.right_trigger > 0)
//            brake = 0.5;
//        else if(robot.slides.getExtensionCm() > 20){
//            brake = 0.65;
//        }
//        else
//            brake = 1.0;
//        if (robot.gamepad1.options) {
//            robot.imu.resetYaw();
//        }
//    }
//    public void write() {
//        double voltageCorrection = 12.0 / robot.voltage;
//        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(turn), 1);
//        double frontLeftPower = (rotY + rotX + turn) * voltageCorrection/ denominator;
//        double backLeftPower = (rotY - rotX + turn) * voltageCorrection/ denominator;
//        double frontRightPower = (rotY - rotX - turn) * voltageCorrection/ denominator;
//        double backRightPower = (rotY + rotX - turn) * voltageCorrection/ denominator;
//
//        if(!robot.agatat) {
//            robot.frontLeft.setPower(frontLeftPower * brake);
//            robot.rearLeft.setPower(backLeftPower * brake);
//            robot.rearRight.setPower(backRightPower * brake);
//            robot.frontRight.setPower(frontRightPower * brake);
//        }
//        else if(robot.agatat)
//        {
//            if(robot.gamepad2.cross)
//            {
//                robot.ridicat = true;
//                robot.frontLeft.setPower(0.7);
//                robot.frontRight.setPower(0.7);
//                robot.rearLeft.setPower(0);
//                robot.rearRight.setPower(0);
//            }
//            else if(robot.ridicat){
//                robot.frontLeft.setPower(0.3);
//                robot.frontRight.setPower(0.3);
//                robot.rearLeft.setPower(0);
//                robot.rearRight.setPower(0);
//            }
//        }
//    }
//    @Override
//    public void periodic() {
//
//        // Rotate the movement direction counter to the bot's rotation
//        rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
//        rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
//
//        if (turn != 0) {
//            headingManuallyControlled = true;
//            //heading += K_STATIC*Math.signum(heading); //compensate for static friction for more precise control?
//        } else if (lock) {
//            if (Math.abs(headingVelocity) < Math.toRadians(20) && headingManuallyControlled) {
//                headingManuallyControlled = false;
//                targetHeading = botHeading;
//            }
//
//            double delta = Math.atan2(
//                    Math.sin(targetHeading - botHeading),
//                    Math.cos(targetHeading - botHeading)
//            );
//
//
//            if(!headingManuallyControlled && Math.abs(Math.toDegrees(-delta)) > 30)
//                turn = headingController.calculate(0, Math.toDegrees(-delta));
//            else if(!headingManuallyControlled){
//                turn = secondaryheadingController.calculate(0, Math.toDegrees(-delta));
//            }
//        }
//
//    }
//}
//