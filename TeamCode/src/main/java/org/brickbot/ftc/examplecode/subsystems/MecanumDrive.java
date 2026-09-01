package org.brickbot.ftc.examplecode.subsystems;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.brickbot.ftc.examplecode.RobotHardware;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import org.jetbrains.annotations.NotNull;
import org.brickbot.ftc.examplecode.Constants;
import brickbot.quickstart.controlalgorithms.PDFSController;
import brickbot.quickstart.devices.BrickMotor;
import brickbot.quickstart.devices.HubManager;
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

    private boolean manualSteering;

    private double xVector;
    private double yVector;
    private double turnVector;

    private double xPos;
    private double yPos;
    private double currHeading;
    private double lastHeading;
    private double targetHeading;

    private ElapsedTime headingTimer;

    private double headingVelocity;
    public static double kHeadingPrediction = 0.2;

    private double brake;

    private double voltage;
    private double voltageCompensationTarget;

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

        manualSteering = false;

        xVector = 0.0;
        yVector = 0.0;
        turnVector = 0.0;

        lastHeading = Double.MIN_VALUE;
        headingTimer = new ElapsedTime();

        brake = 1.0;

        kP = 0.0005;
        kD = 0.0;
        kF = 0.0;
        kS = 0.0;
        kStatic = 0.0;

        headingController = new PDFSController(kP, kD, kF, kS).setErrorThreshold(1.0);

        voltage = 12.0;
        voltageCompensationTarget = 12.0;
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

         kP =  Constants.MecanumDriveConstants.kP;
         kD = Constants.MecanumDriveConstants.kD;
         kF = Constants.MecanumDriveConstants.kF;
         kS = Constants.MecanumDriveConstants.kS;
         kStatic = Constants.MecanumDriveConstants.kStatic;

        headingController.setConstants(kP, kD, kF, kS);

        if (!isLocalizationEnabled) {
            currHeading = AngleUnit.normalizeRadians(
                    pinpoint.getHeading(AngleUnit.RADIANS)
            );
        } else {
            Pose2D currPose = pinpoint.getPosition();
            xPos = currPose.getX(DistanceUnit.INCH);
            yPos = currPose.getY(DistanceUnit.INCH);
            currHeading = AngleUnit.normalizeRadians(
                    currPose.getHeading(AngleUnit.RADIANS)
            );
        }

        headingVelocity = (lastHeading - currHeading) / headingTimer.seconds();
        headingTimer.reset();

        if (lastHeading == Double.MIN_VALUE) {
            headingVelocity = 0.0;
        }

        lastHeading = currHeading;

        xInput = robot.gamepad1.left_stick_x;
        yInput = -robot.gamepad1.left_stick_y;

        if (steeringBindings == SteeringBindings.TRIGGERS) {
            turnInput = robot.gamepad1.right_trigger - robot.gamepad1.left_trigger;
        } else {
            turnInput = robot.gamepad1.right_stick_x;
        }

        if (turnInput != 0.0) {
            manualSteering = true;
        } else if (Math.abs(headingVelocity) < Math.toRadians(20) && manualSteering) {
            manualSteering = false;
            targetHeading = currHeading;
        }

        lastTurnInput = turnInput;
    }

    @Override
    public void compute() {
        xVector = xInput * (1 - kStatic) + Math.signum(xInput) * kStatic;
        yVector = yInput * (1 - kStatic) + Math.signum(yInput) * kStatic;
        turnVector = turnInput * (1 - kStatic) + Math.signum(turnInput) * kStatic;

        if (drivingMode == DrivingMode.FIELD_CENTRIC) {
            double xCopy = xVector;
            double yCopy = yVector;

            xVector = xCopy * Math.cos(-currHeading) - yCopy * Math.sin(-currHeading);
            yVector = xCopy * Math.sin(-currHeading) + yCopy * Math.cos(-currHeading);
        }

        if (Math.abs(turnInput) < 0.03) {
            if (!manualSteering) {
                double delta = Math.atan2(
                        Math.sin(targetHeading - currHeading),
                        Math.cos(targetHeading - currHeading)
                );

                turnVector = headingController.compute(0, Math.toDegrees(-delta));
            }
        }
    }

    @Override
    public void write() {
        double denominator = Math.max((Math.abs(xVector) + Math.abs(yVector) + Math.abs(turnVector)), 1.0);
        voltage = HubManager.INSTANCE.getVoltage().getAsDouble();

        double frontLeftPower = (xVector - yVector - turnVector) / denominator * (voltageCompensationTarget / voltage);
        double rearLeftPower = (xVector + yVector - turnVector) / denominator * (voltageCompensationTarget / voltage);
        double rearRightPower = (xVector - yVector + turnVector) / denominator * (voltageCompensationTarget / voltage);
        double frontRightPower = (xVector + yVector + turnVector) / denominator * (voltageCompensationTarget / voltage);

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

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public double getHeading() {
        return currHeading;
    }

    public void setHeading(double heading, AngleUnit angleUnit) {
        pinpoint.setHeading(heading, angleUnit);
    }

    public void setPosition(DistanceUnit distanceUnit, double x, double y, AngleUnit angleUnit, double heading) {
        setPosition(new Pose2D(distanceUnit, x, y, angleUnit, heading));
        targetHeading = heading;
    }

    public void setPosition(Pose2D pose2D) {
        pinpoint.setPosition(pose2D);
        targetHeading = pose2D.getHeading(AngleUnit.RADIANS);
    }
}