package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous(name = "BioBuzz Auto Navigation", group = "BioBuzz")
public class BioBuzzAutoNavigation extends LinearOpMode {

    // =========================
    // DRIVE MOTORS
    // =========================

    private DcMotor leftFrontDrive;
    private DcMotor rightFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightBackDrive;

    // =========================
    // IMU
    // =========================

    private IMU imu;

    // =========================
    // CALIBRATION
    // =========================

    // TEMPORARY VALUE.
    // We will calibrate this with the real robot.
    private static final double TICKS_PER_INCH = 100.0;

    @Override
    public void runOpMode() {

        // =========================
        // CONNECT TO MOTORS
        // =========================

        leftFrontDrive = hardwareMap.get(
                DcMotor.class, "frontLeftMotor");

        rightFrontDrive = hardwareMap.get(
                DcMotor.class, "frontRightMotor");

        leftBackDrive = hardwareMap.get(
                DcMotor.class, "backLeftMotor");

        rightBackDrive = hardwareMap.get(
                DcMotor.class, "backRightMotor");


        // =========================
        // MOTOR DIRECTIONS
        // Same as your TeleOp
        // =========================

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);

        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);


        // =========================
        // BRAKE WHEN POWER = 0
        // =========================

        leftFrontDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        rightFrontDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        leftBackDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        rightBackDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);


        // =========================
        // ENCODERS
        // =========================

        resetEncoders();


        // =========================
        // IMU
        // =========================

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                );

        imu.initialize(
                new IMU.Parameters(orientationOnRobot)
        );


        // =========================
        // READY
        // =========================

        telemetry.addLine("BioBuzz Auto Ready");
        telemetry.addLine("Waiting for START...");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }


        // =========================
        // TEST ROUTE
        // =========================

        // Turn to 90 degrees
        turnToHeading(90);

        // Drive forward 24 inches
        driveForward(24);

        // Stop
        stopDrive();


        // =========================
        // DONE
        // =========================

        telemetry.addLine("Route Complete");
        telemetry.update();

        sleep(1000);
    }


    // ============================================================
    // RESET ENCODERS
    // ============================================================

    private void resetEncoders() {

        leftFrontDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rightFrontDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftBackDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rightBackDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        leftFrontDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER);

        rightFrontDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER);

        leftBackDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER);

        rightBackDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER);
    }


    // ============================================================
    // GET CURRENT HEADING
    // ============================================================

    private double getCurrentHeading() {

        return imu.getRobotYawPitchRollAngles()
                .getYaw(AngleUnit.DEGREES);
    }


    // ============================================================
    // GET HEADING ERROR
    // ============================================================

    private double getHeadingError(double targetHeading) {

        double currentHeading = getCurrentHeading();

        double error = targetHeading - currentHeading;


        // Convert error to -180 through +180

        while (error > 180) {
            error -= 360;
        }

        while (error < -180) {
            error += 360;
        }

        return error;
    }


    // ============================================================
    // TURN TO HEADING
    // ============================================================

    private void turnToHeading(double targetHeading) {

        telemetry.addLine("Turning...");
        telemetry.update();


        while (opModeIsActive()) {

            double error =
                    getHeadingError(targetHeading);


            // Stop when we are close enough
            if (Math.abs(error) <= 2) {
                break;
            }


            // Proportional turning power

            double turnPower = error * 0.01;


            // Limit maximum power

            turnPower = Math.max(
                    -0.5,
                    Math.min(0.5, turnPower)
            );


            // Turn

            leftFrontDrive.setPower(-turnPower);
            rightFrontDrive.setPower(turnPower);

            leftBackDrive.setPower(-turnPower);
            rightBackDrive.setPower(turnPower);


            // Telemetry

            telemetry.addData(
                    "Target Heading",
                    targetHeading
            );

            telemetry.addData(
                    "Current Heading",
                    getCurrentHeading()
            );

            telemetry.addData(
                    "Heading Error",
                    error
            );

            telemetry.addData(
                    "Turn Power",
                    turnPower
            );

            telemetry.update();
        }


        stopDrive();
    }


    // ============================================================
    // DRIVE FORWARD
    // ============================================================

    private void driveForward(double distanceInches) {

        resetEncoders();


        telemetry.addLine("Driving...");
        telemetry.update();


        while (opModeIsActive()) {

            double distance =
                    getDistanceTraveled();


            // Stop when target distance is reached

            if (distance >= distanceInches) {
                break;
            }


            // Drive forward

            double drivePower = 0.6;


            leftFrontDrive.setPower(drivePower);
            rightFrontDrive.setPower(drivePower);

            leftBackDrive.setPower(drivePower);
            rightBackDrive.setPower(drivePower);


            // Telemetry

            telemetry.addData(
                    "Target Distance",
                    distanceInches
            );

            telemetry.addData(
                    "Distance Traveled",
                    distance
            );

            telemetry.update();
        }


        stopDrive();
    }


    // ============================================================
    // GET AVERAGE ENCODER TICKS
    // ============================================================

    private double getAverageEncoderTicks() {

        double leftFront =
                Math.abs(leftFrontDrive.getCurrentPosition());

        double rightFront =
                Math.abs(rightFrontDrive.getCurrentPosition());

        double leftBack =
                Math.abs(leftBackDrive.getCurrentPosition());

        double rightBack =
                Math.abs(rightBackDrive.getCurrentPosition());


        return (
                leftFront +
                rightFront +
                leftBack +
                rightBack
        ) / 4.0;
    }


    // ============================================================
    // GET DISTANCE TRAVELED
    // ============================================================

    private double getDistanceTraveled() {

        return getAverageEncoderTicks()
                / TICKS_PER_INCH;
    }


    // ============================================================
    // STOP ALL DRIVE MOTORS
    // ============================================================

    private void stopDrive() {

        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);

        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }
}
