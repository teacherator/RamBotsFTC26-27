package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous(name = "BioBuzz Auto Navigation", group = "BioBuzz")
public class BioBuzzAutoNavigation extends LinearOpMode {

    // ============================================================
    // DRIVE MOTORS
    // ============================================================

    private DcMotor leftFrontDrive;
    private DcMotor rightFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightBackDrive;

    // ============================================================
    // IMU
    // ============================================================

    private IMU imu;

    // ============================================================
    // ENCODER CALIBRATION
    // ============================================================

    // TEMPORARY VALUE.
    // We will calibrate this when you have the robot.
    private static final double TICKS_PER_INCH = 100.0;

    // ============================================================
    // PRESET ROBOT STARTING POSITION
    // ============================================================

    // Coordinate system:
    //
    // 0 degrees   = East
    // 90 degrees  = North
    // 180 degrees = West
    // 270 degrees = South
    //
    // Change these values for your starting position.

    private static final double START_ROBOT_X = 0;
    private static final double START_ROBOT_Y = 0;
    private static final double START_ROBOT_HEADING = 0;

    // ============================================================
    // PRESET BALL POSITIONS
    // ============================================================

    // Change these to the actual field coordinates later.

    private static final double BALL_1_X = 60;
    private static final double BALL_1_Y = 40;

    private static final double BALL_2_X = 100;
    private static final double BALL_2_Y = 80;

    private static final double BALL_3_X = 140;
    private static final double BALL_3_Y = 40;

    // ============================================================
    // HOOP POSITION
    // ============================================================

    private static final double HOOP_X = 180;
    private static final double HOOP_Y = 100;

    // ============================================================
    // PARKING POSITION
    // ============================================================

    private static final double PARKING_X = 40;
    private static final double PARKING_Y = 140;


    // ============================================================
    // INTERNAL ROBOT POSITION
    // ============================================================

    private double robotX;
    private double robotY;
    private double robotHeading;


    @Override
    public void runOpMode() {

        // ========================================================
        // CONNECT TO DRIVE MOTORS
        // ========================================================

        leftFrontDrive = hardwareMap.get(
                DcMotor.class,
                "frontLeftMotor"
        );

        rightFrontDrive = hardwareMap.get(
                DcMotor.class,
                "frontRightMotor"
        );

        leftBackDrive = hardwareMap.get(
                DcMotor.class,
                "backLeftMotor"
        );

        rightBackDrive = hardwareMap.get(
                DcMotor.class,
                "backRightMotor"
        );


        // ========================================================
        // MOTOR DIRECTIONS
        // Same as your TeleOp
        // ========================================================

        leftFrontDrive.setDirection(
                DcMotor.Direction.REVERSE
        );

        rightFrontDrive.setDirection(
                DcMotor.Direction.FORWARD
        );

        leftBackDrive.setDirection(
                DcMotor.Direction.REVERSE
        );

        rightBackDrive.setDirection(
                DcMotor.Direction.FORWARD
        );


        // ========================================================
        // BRAKE
        // ========================================================

        leftFrontDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        rightFrontDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        leftBackDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );

        rightBackDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE
        );


        // ========================================================
        // ENCODERS
        // ========================================================

        resetEncoders();


        // ========================================================
        // IMU
        // ========================================================

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                );

        imu.initialize(
                new IMU.Parameters(orientationOnRobot)
        );


        // ========================================================
        // SET INITIAL POSITION
        // ========================================================

        robotX = START_ROBOT_X;
        robotY = START_ROBOT_Y;
        robotHeading = START_ROBOT_HEADING;


        // ========================================================
        // SHOW STARTING INFORMATION
        // ========================================================

        telemetry.addLine("BioBuzz Auto Navigation");
        telemetry.addLine("-----------------------");

        telemetry.addData("Starting X", robotX);
        telemetry.addData("Starting Y", robotY);
        telemetry.addData(
                "Starting Heading",
                robotHeading
        );

        telemetry.addLine("");
        telemetry.addLine("Ball 1:");
        telemetry.addData("X", BALL_1_X);
        telemetry.addData("Y", BALL_1_Y);

        telemetry.addLine("");
        telemetry.addLine("Ball 2:");
        telemetry.addData("X", BALL_2_X);
        telemetry.addData("Y", BALL_2_Y);

        telemetry.addLine("");
        telemetry.addLine("Ball 3:");
        telemetry.addData("X", BALL_3_X);
        telemetry.addData("Y", BALL_3_Y);

        telemetry.addLine("");
        telemetry.addLine("Waiting for START...");

        telemetry.update();


        // ========================================================
        // WAIT FOR START
        // ========================================================

        waitForStart();

        if (isStopRequested()) {
            return;
        }


        // ========================================================
        // AUTONOMOUS ROUTE
        // ========================================================

        // Go to Ball 1
        navigateToPoint(
                BALL_1_X,
                BALL_1_Y,
                "Ball 1"
        );


        // Go to Ball 2
        navigateToPoint(
                BALL_2_X,
                BALL_2_Y,
                "Ball 2"
        );


        // Go to Ball 3
        navigateToPoint(
                BALL_3_X,
                BALL_3_Y,
                "Ball 3"
        );


        // Go to Hoop
        navigateToPoint(
                HOOP_X,
                HOOP_Y,
                "Hoop"
        );


        // Go to Parking
        navigateToPoint(
                PARKING_X,
                PARKING_Y,
                "Parking"
        );


        // ========================================================
        // FINISHED
        // ========================================================

        stopDrive();

        telemetry.addLine("");
        telemetry.addLine("======================");
        telemetry.addLine("AUTONOMOUS COMPLETE");
        telemetry.addLine("======================");

        telemetry.addData("Final X", robotX);
        telemetry.addData("Final Y", robotY);
        telemetry.addData(
                "Final Heading",
                robotHeading
        );

        telemetry.update();

        sleep(3000);
    }


    // ============================================================
    // NAVIGATE TO A POINT
    // ============================================================

    private void navigateToPoint(
            double targetX,
            double targetY,
            String name
    ) {

        // --------------------------------------------------------
        // CALCULATE DIFFERENCE IN X AND Y
        // --------------------------------------------------------

        double dx = targetX - robotX;
        double dy = targetY - robotY;


        // --------------------------------------------------------
        // CALCULATE DISTANCE
        // --------------------------------------------------------

        double distance = Math.sqrt(
                (dx * dx) +
                (dy * dy)
        );


        // --------------------------------------------------------
        // CALCULATE TARGET ANGLE
        //
        // atan2 gives us the angle from the current position
        // to the target.
        // --------------------------------------------------------

        double targetAngle =
                Math.toDegrees(
                        Math.atan2(dy, dx)
                );


        // Convert negative angles to 0-360

        if (targetAngle < 0) {
            targetAngle += 360;
        }


        // --------------------------------------------------------
        // CALCULATE TURN
        // --------------------------------------------------------

        double turnAmount =
                getAngleDifference(
                        targetAngle,
                        robotHeading
                );


        // --------------------------------------------------------
        // DISPLAY CALCULATIONS
        // --------------------------------------------------------

        telemetry.addLine("");
        telemetry.addLine("======================");
        telemetry.addData("Destination", name);
        telemetry.addLine("======================");

        telemetry.addData(
                "Current X",
                robotX
        );

        telemetry.addData(
                "Current Y",
                robotY
        );

        telemetry.addData(
                "Target X",
                targetX
        );

        telemetry.addData(
                "Target Y",
                targetY
        );

        telemetry.addData(
                "Delta X",
                dx
        );

        telemetry.addData(
                "Delta Y",
                dy
        );

        telemetry.addData(
                "Distance",
                distance
        );

        telemetry.addData(
                "Target Angle",
                targetAngle
        );

        telemetry.addData(
                "Turn Amount",
                turnAmount
        );

        telemetry.update();


        sleep(500);


        // --------------------------------------------------------
        // TURN
        // --------------------------------------------------------

        turnToHeading(targetAngle);


        // --------------------------------------------------------
        // DRIVE
        // --------------------------------------------------------

        driveForward(distance);


        // --------------------------------------------------------
        // UPDATE OUR CALCULATED POSITION
        // --------------------------------------------------------

        robotX = targetX;
        robotY = targetY;
        robotHeading = targetAngle;


        stopDrive();


        telemetry.addLine("");
        telemetry.addData(
                "Reached",
                name
        );

        telemetry.addData(
                "New X",
                robotX
        );

        telemetry.addData(
                "New Y",
                robotY
        );

        telemetry.addData(
                "New Heading",
                robotHeading
        );

        telemetry.update();


        sleep(500);
    }


    // ============================================================
    // CALCULATE SHORTEST ANGLE DIFFERENCE
    // ============================================================

    private double getAngleDifference(
            double targetAngle,
            double currentAngle
    ) {

        double difference =
                targetAngle - currentAngle;


        while (difference > 180) {
            difference -= 360;
        }


        while (difference < -180) {
            difference += 360;
        }


        return difference;
    }


    // ============================================================
    // GET CURRENT IMU HEADING
    // ============================================================

    private double getCurrentHeading() {

        return imu
                .getRobotYawPitchRollAngles()
                .getYaw(AngleUnit.DEGREES);
    }


    // ============================================================
    // GET HEADING ERROR
    // ============================================================

    private double getHeadingError(
            double targetHeading
    ) {

        double currentHeading =
                getCurrentHeading();


        double error =
                targetHeading - currentHeading;


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

    private void turnToHeading(
            double targetHeading
    ) {

        telemetry.addLine("");
        telemetry.addLine("TURNING");
        telemetry.addData(
                "Target",
                targetHeading
        );

        telemetry.update();


        while (opModeIsActive()) {

            double error =
                    getHeadingError(
                            targetHeading
                    );


            // ----------------------------------------------------
            // CLOSE ENOUGH
            // ----------------------------------------------------

            if (Math.abs(error) <= 2) {
                break;
            }


            // ----------------------------------------------------
            // PROPORTIONAL TURN POWER
            // ----------------------------------------------------

            double turnPower =
                    error * 0.01;


            // Maximum turn power

            turnPower =
                    Math.max(
                            -0.5,
                            Math.min(
                                    0.5,
                                    turnPower
                            )
                    );


            // ----------------------------------------------------
            // TURN
            // ----------------------------------------------------

            leftFrontDrive.setPower(
                    -turnPower
            );

            rightFrontDrive.setPower(
                    turnPower
            );

            leftBackDrive.setPower(
                    -turnPower
            );

            rightBackDrive.setPower(
                    turnPower
            );


            // ----------------------------------------------------
            // TELEMETRY
            // ----------------------------------------------------

            telemetry.addData(
                    "Target Heading",
                    targetHeading
            );

            telemetry.addData(
                    "Current Heading",
                    getCurrentHeading()
            );

            telemetry.addData(
                    "Error",
                    error
            );

            telemetry.addData(
                    "Power",
                    turnPower
            );

            telemetry.update();
        }


        stopDrive();
    }


    // ============================================================
    // DRIVE FORWARD
    // ============================================================

    private void driveForward(
            double distanceInches
    ) {

        resetEncoders();


        telemetry.addLine("");
        telemetry.addLine("DRIVING");
        telemetry.addData(
                "Target Distance",
                distanceInches
        );

        telemetry.update();


        while (opModeIsActive()) {

            double distance =
                    getDistanceTraveled();


            // ----------------------------------------------------
            // TARGET REACHED
            // ----------------------------------------------------

            if (distance >= distanceInches) {
                break;
            }


            // ----------------------------------------------------
            // DRIVE POWER
            // ----------------------------------------------------

            double drivePower = 0.6;


            leftFrontDrive.setPower(
                    drivePower
            );

            rightFrontDrive.setPower(
                    drivePower
            );

            leftBackDrive.setPower(
                    drivePower
            );

            rightBackDrive.setPower(
                    drivePower
            );


            // ----------------------------------------------------
            // TELEMETRY
            // ----------------------------------------------------

            telemetry.addData(
                    "Target",
                    distanceInches
            );

            telemetry.addData(
                    "Distance",
                    distance
            );

            telemetry.update();
        }


        stopDrive();
    }


    // ============================================================
    // RESET ENCODERS
    // ============================================================

    private void resetEncoders() {

        leftFrontDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );

        rightFrontDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );

        leftBackDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );

        rightBackDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );


        leftFrontDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER
        );

        rightFrontDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER
        );

        leftBackDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER
        );

        rightBackDrive.setMode(
                DcMotor.RunMode.RUN_USING_ENCODER
        );
    }


    // ============================================================
    // GET AVERAGE ENCODER TICKS
    // ============================================================

    private double getAverageEncoderTicks() {

        double leftFront =
                Math.abs(
                        leftFrontDrive.getCurrentPosition()
                );

        double rightFront =
                Math.abs(
                        rightFrontDrive.getCurrentPosition()
                );

        double leftBack =
                Math.abs(
                        leftBackDrive.getCurrentPosition()
                );

        double rightBack =
                Math.abs(
                        rightBackDrive.getCurrentPosition()
                );


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
    // STOP DRIVE
    // ============================================================

    private void stopDrive() {

        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);

        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }
}
