package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
Description: Autonomous OpMode for the Decode season.
Strafes based on selected alliance, spins up the launcher, feeds game elements, and stops.
 */

@Autonomous(name="Shoot Auto Decode", group="Use This")
public class BioBuzzAutoSimple extends OpMode {
    final double FEED_TIME_SECONDS = 1.0;
    final double STOP_SPEED = 0.0;
    final double FULL_SPEED = 1.0;

    final double LAUNCHER_TARGET_VELOCITY = 1500;
    final double LAUNCHER_MIN_VELOCITY = 1450;

    private DcMotorEx launcher = null;
    public DcMotor frontLeftMotor;
    public DcMotor frontRightMotor;
    public DcMotor backLeftMotor;
    public DcMotor backRightMotor;
/*
    private CRServo leftFeeder = null;
    private CRServo rightFeeder = null;
*/
    double frontLeftPower = 0;
    double frontRightPower = 0;
    double backLeftPower = 0;
    double backRightPower = 0;

    double moveTime = 2.0; // seconds

    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime shootTimer = new ElapsedTime();

    private enum AutoState { MOVE, SPIN_UP, SHOOT, DONE }
    private AutoState autoState = AutoState.MOVE;

    private enum Direction { LEFT, RIGHT, FORWARD, BACKWARD }
    private enum Alliance { RED, BLUE }

    private Alliance alliance = Alliance.RED;

    @Override
    public void init() {
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);
        /*
        launcher.setZeroPowerBehavior(BRAKE);

        leftFeeder = hardwareMap.get(CRServo.class, "left_feeder");
        rightFeeder = hardwareMap.get(CRServo.class, "right_feeder");

        leftFeeder.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);
*/
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
        if (gamepad2.square) {
            alliance = Alliance.RED;
        } else if (gamepad2.circle) {
            alliance = Alliance.BLUE;
        }

        telemetry.addData("Press circle", "for BLUE Alliance");
        telemetry.addData("Press square", "for RED Alliance");
        telemetry.addData("Selected Alliance", alliance);
    }

    @Override
    public void start() {
        timer.reset();
        autoState = AutoState.MOVE;
    }

    @Override
    public void loop() {
        switch (autoState) {
            case MOVE:
                // Move based on selected alliance (RED = Strafe Left, BLUE = Strafe Right)
                if (alliance == Alliance.RED) {
                    move(Direction.LEFT);
                } else {
                    move(Direction.RIGHT);
                }

                if (timer.seconds() >= moveTime) {
                    mecanum_drivetrain(0, 0, 0); // Stop robot drivetrain
                    autoState = AutoState.SPIN_UP;
                }
                break;

            case SPIN_UP:
                /*launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (launcher.getVelocity() >= LAUNCHER_MIN_VELOCITY) {
                    shootTimer.reset();
                    autoState = AutoState.SHOOT;
                }*/
                break;

            case SHOOT:
                /*leftFeeder.setPower(FULL_SPEED);
                rightFeeder.setPower(FULL_SPEED);
                if (shootTimer.seconds() >= FEED_TIME_SECONDS) {
                    leftFeeder.setPower(STOP_SPEED);
                    rightFeeder.setPower(STOP_SPEED);
                    launcher.setVelocity(STOP_SPEED);
                    autoState = AutoState.DONE;
                }*/
                break;

            case DONE:
                mecanum_drivetrain(0, 0, 0);
                /*launcher.setVelocity(STOP_SPEED);
                leftFeeder.setPower(STOP_SPEED);
                rightFeeder.setPower(STOP_SPEED);*/
                break;
        }

        general_telemetry();
    }

    public void mecanum_drivetrain(double y, double x, double r) {
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(r), 1);

        frontLeftPower = (y + x + r) / denominator;
        frontRightPower = (y - x - r) / denominator;
        backLeftPower = (y - x + r) / denominator;
        backRightPower = (y + x - r) / denominator;

        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);
    }

    private void move(Direction direction) {
        switch (direction) {
            case LEFT:
                mecanum_drivetrain(0, 0.5, 0); // strafe left
                break;
            case RIGHT:
                mecanum_drivetrain(0, -0.5, 0); // strafe right
                break;
            case FORWARD:
                mecanum_drivetrain(0.5, 0, 0); // move forward
                break;
            case BACKWARD:
                mecanum_drivetrain(-0.5, 0, 0); // move backward
                break;
        }
    }

    public void general_telemetry() {
        telemetry.addData("Alliance", alliance);
        telemetry.addData("Auto State", autoState);
        telemetry.addData("Timer (s)", "%.2f", timer.seconds());
        telemetry.addData("Launcher Speed", launcher.getVelocity());
        telemetry.addData("Motors", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                frontLeftPower, frontRightPower, backLeftPower, backRightPower);
    }
}
