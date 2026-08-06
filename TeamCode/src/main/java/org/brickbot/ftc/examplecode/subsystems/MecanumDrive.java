package org.brickbot.ftc.examplecode.subsystems;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.brickbot.ftc.examplecode.RobotHardware;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import org.jetbrains.annotations.NotNull;
import org.brickbot.ftc.examplecode.Constants;
import brickbot.quickstart.controlalgorithms.PDFSController;
import brickbot.quickstart.devices.BrickMotor;
import brickbot.quickstart.subsystems.Subsystem;

public class MecanumDrive extends Subsystem {
    private RobotHardware robot;

    public BrickMotor frontLeft;
    public BrickMotor frontRight;
    public BrickMotor rearLeft;
    public BrickMotor rearRight;

    private GoBildaPinpointDriver pinpoint;

    private double xInput;
    private double yInput;
    private double turnInput;
    private double lastTurnInput;

    private double x;
    private double y;
    private double currHeading;
    private double targetHeading;

    private double headingVelocity;
    public static double kHeadingPrediction = 0.2;

    private double brake;

    public static double kP =  Constants.MecanumDriveConstants.kP;
    public static double kD = Constants.MecanumDriveConstants.kD;
    public static double kF = Constants.MecanumDriveConstants.kF;
    public static double kS = Constants.MecanumDriveConstants.kS;
    public static double kStatic = Constants.MecanumDriveConstants.kStatic;

    public PDFSController headingController;

    public MecanumDrive() {
        frontLeft = new BrickMotor("frontLeft");
        frontRight = new BrickMotor("frontRight");
        rearLeft = new BrickMotor("rearLeft");
        rearRight = new BrickMotor("rearRight");

        xInput = 0.0;
        yInput = 0.0;
        turnInput = 0.0;
        lastTurnInput = 0.0;

        brake = 1.0;

        kP = 0.0005;
        kD = 0.0;
        kF = 0.0;
        kS = 0.0;
        kStatic = 0.0;

        headingController = new PDFSController(kP, kD, kF, kS).setErrorThreshold(1.0);
    }

    public enum SteeringBindings {
        RIGHT_STICK,
        TRIGGERS
    }

    public enum DrivingMode {
        FIELD_CENTRIC,
        ROBOT_CENTRIC
    }

    private SteeringBindings steeringBindings = SteeringBindings.RIGHT_STICK;
    private DrivingMode drivingMode = DrivingMode.FIELD_CENTRIC;
    private DcMotor.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;
    private boolean isLocalizationEnabled = false;

    @Override
    public void init(@NotNull HardwareMap hwMap) {
        robot = RobotHardware.getInstance();

        pinpoint = hwMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH,0.0, 0.0, AngleUnit.RADIANS, 0.0));

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rearRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(zeroPowerBehavior);
        frontRight.setZeroPowerBehavior(zeroPowerBehavior);
        rearLeft.setZeroPowerBehavior(zeroPowerBehavior);
        rearRight.setZeroPowerBehavior(zeroPowerBehavior);
    }

    @Override
    public void read() {
        pinpoint.update();

        headingController.setConstants(kP, kD, kF, kS);

        if (!isLocalizationEnabled) {
            double imuHeading = pinpoint.getHeading(AngleUnit.RADIANS);
            currHeading = AngleUnit.normalizeRadians(imuHeading);
        } else {
            Pose2D currPose = pinpoint.getPosition();
            x = currPose.getX(DistanceUnit.INCH);
            y = currPose.getY(DistanceUnit.INCH);
            currHeading = AngleUnit.normalizeRadians(currPose.getHeading(AngleUnit.RADIANS));
        }

        headingVelocity = pinpoint.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS);

        xInput = robot.gamepad1.left_stick_x * (1 - kStatic) + Math.signum(robot.gamepad1.left_stick_x) * kStatic;
        yInput = -robot.gamepad1.left_stick_y * (1 - kStatic) + Math.signum(-robot.gamepad1.left_stick_y) * kStatic;

        if (steeringBindings == SteeringBindings.TRIGGERS) {
            double turnValue = robot.gamepad1.right_trigger - robot.gamepad1.left_trigger;
            turnInput = turnValue * (1 - kStatic) + Math.signum(turnValue) * kStatic;
        } else {
            turnInput = robot.gamepad1.right_stick_x * (1 - kStatic) + Math.signum(robot.gamepad1.right_stick_x) * kStatic;
        }

        if (RobotHardware.getInstance().gamepad1.optionsWasPressed()) {
            pinpoint.setHeading(Math.PI, AngleUnit.RADIANS);
            targetHeading = Math.PI;
        }
    }

    @Override
    public void compute() {
        if (Math.abs(turnInput) < 0.03 && Math.abs(lastTurnInput) > 0.03) {
            targetHeading = AngleUnit.normalizeRadians(currHeading + (headingVelocity * kHeadingPrediction));
        }
        lastTurnInput = turnInput;

        if (drivingMode == DrivingMode.FIELD_CENTRIC) {
            double xCopy = xInput;
            double yCopy = yInput;

            xInput = xCopy * Math.cos(-currHeading) - yCopy * Math.sin(-currHeading);
            yInput = xCopy * Math.sin(-currHeading) + yCopy * Math.cos(-currHeading);
        }

        if (Math.abs(turnInput) < 0.03) {
            double headingErrorDeg = Math.toDegrees(AngleUnit.normalizeRadians(targetHeading - currHeading));
            double correction = headingController.compute(Math.toDegrees(currHeading), Math.toDegrees(currHeading) + headingErrorDeg);

            turnInput = (Math.abs(headingErrorDeg) < 1.0) ? 0.0 : correction;
        }
    }

    @Override
    public void write() {
        double denominator = Math.max((Math.abs(xInput) + Math.abs(yInput) + Math.abs(turnInput)), 1.0);

        double frontLeftPower = (xInput - yInput - turnInput) / denominator;
        double rearLeftPower = (xInput + yInput - turnInput) / denominator;
        double rearRightPower = (xInput - yInput + turnInput) / denominator;
        double frontRightPower = (xInput + yInput + turnInput) / denominator;

        frontLeft.setPower(frontLeftPower);
        rearLeft.setPower(rearLeftPower);
        rearRight.setPower(rearRightPower);
        frontRight.setPower(frontRightPower);
    }

    public MecanumDrive setSteeringBindings(SteeringBindings steeringBindings) {
        this.steeringBindings = steeringBindings;

        return this;
    }

    public MecanumDrive setDrivingMode(DrivingMode drivingMode) {
        this.drivingMode = drivingMode;

        return this;
    }

    public MecanumDrive setIsLocalizationEnabled(boolean isLocalizationEnabled) {
        this.isLocalizationEnabled = isLocalizationEnabled;

        return this;
    }

    public MecanumDrive setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        this.zeroPowerBehavior = zeroPowerBehavior;

        return this;
    }

    public MecanumDrive setHeadingPDFS(double kP, double kD, double kF, double kS) {
        this.kP = kP;
        this.kD = kD;
        this.kF = kF;
        this.kS = kS;

        headingController = new PDFSController(kP, kD, kF, kS).setErrorThreshold(1.0);

        return this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return currHeading;
    }
}