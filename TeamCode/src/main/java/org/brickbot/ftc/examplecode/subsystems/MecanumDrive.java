package org.brickbot.ftc.examplecode.subsystems;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.brickbot.ftc.examplecode.RobotHardware;
import org.jetbrains.annotations.NotNull;

import brickbot.quickstart.controlalgorithms.PDFSController;
import brickbot.quickstart.subsystems.Subsystem;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MecanumDrive extends Subsystem {
private RobotHardware robot = RobotHardware.getInstance();


        private double x;
        private double y;
        private double turn;
        private double lastTurn;
        private double headingOffset = Math.PI / 2.0; //TODO GRAB FROM AUTONOMOUS
        private double currHeading = 0;
        private double targetHeading;
        private double brake = 1.0;
        private double kStatic = 0.0;
        public  static  double kP = 0, kD = 0, kF = 0, kS = 0;

        public FieldCentricDrive() {}
        public FieldCentricDrive setHeadingPDFS(double kP, double kD, double kF, double kStatic) {
            headingController = new PDFSController(kP, kD, kF, kStatic).setFeedForwardType(PDFSController.FeedForwardType.CONSTANT).setDeadzone(1).sethomedConstant(0);
            return this;
        }
        @Override
        public void read() {
            robot.pinpoint.update();
            headingController.updateConstants(kP, kD, kF, kS);
            double imuHeading = robot.pinpoint.getHeading();

            currHeading = AngleUnit.normalizeRadians(imuHeading + headingOffset);

            x = robot.gamepad1.left_stick_x * (1 - kStatic);
            x = Math.abs(x) > 0.03 ? x + Math.signum(x) * kStatic : 0.0;

            y = -robot.gamepad1.left_stick_y * (1 - kStatic);
            y = Math.abs(y) > 0.03 ? y + Math.signum(y) * kStatic : 0.0;

            turn = - (robot.gamepad1.right_stick_x) * (1 - kStatic);
            turn = Math.abs(turn) > 0.03 ? turn + Math.signum(turn) * kStatic : 0.0;

            if(robot.slides.getExtensionCm() > 50)
                brake = 0.7;
            else if (robot.gamepad1.right_trigger > 0 || robot.slides.getExtensionCm() > 20)
                brake = 0.5;
            else
                brake = 1.0;

            if (robot.gamepad1.options)
                headingOffset = -imuHeading;
        }

        @Override
        public void periodic() {
            if (Double.compare(turn, 0.0) == 0 && Double.compare(lastTurn, 0.0) != 0) {
                targetHeading = currHeading;
            }
            lastTurn = turn;

            double xCopy = x;
            double yCopy = y;

            x = xCopy * Math.cos(-currHeading) - yCopy * Math.sin(-currHeading);
            y = xCopy * Math.sin(-currHeading) + yCopy * Math.cos(-currHeading);

        /*if (Double.compare(turn, 0.0) == 0)
            turn = headingController.run(Math.toDegrees(currHeading), Math.toDegrees(targetHeading), 90);*/
        }

        @Override
        public void write() {
            double voltageCorrection = 12.0 / robot.voltage;
            double denominator = Math.max((Math.abs(y) + Math.abs(x) + Math.abs(turn)) * voltageCorrection, 1.0);
            double frontLeftPower = (-y + x - turn) * voltageCorrection / denominator;
            double rearLeftPower = (y + x - turn) * voltageCorrection / denominator;
            double rearRightPower = (-y + x + turn) * voltageCorrection / denominator;
            double frontRightPower = (y + x + turn) * voltageCorrection / denominator;

            robot.frontLeft.setPower(frontLeftPower * brake);
            robot.rearLeft.setPower(rearLeftPower * brake);
            robot.rearRight.setPower(rearRightPower * brake);
            robot.frontRight.setPower(frontRightPower * brake);
        }

        @Override
        public void init(HardwareMap hwMap) {
            robot.frontLeft.init(hwMap);
            robot.rearLeft.init(hwMap);
            robot.rearRight.init(hwMap);
            robot.frontRight.init(hwMap);
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.rearLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.rearRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            robot.rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            robot.rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            robot.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            robot.frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
            robot.rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        public double getHeading()
        {
            return currHeading;
        }
    }
}
