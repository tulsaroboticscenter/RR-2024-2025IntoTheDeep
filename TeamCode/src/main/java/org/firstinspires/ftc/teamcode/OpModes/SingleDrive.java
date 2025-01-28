package org.firstinspires.ftc.teamcode.OpModes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.HWProfile;
import org.firstinspires.ftc.teamcode.Libs.DriveMecanumFTCLib;


/**
#########################################

                               CONTROLS MAPPING -- TBD

#########################################



**/
@TeleOp(name="SingleDrive", group="LinearOpMode")

/**

This is the DriveOpMode. This is the OpMode that is used for the driver-controlled portion, and
is also sometimes used for testing.

notes:

The armPosition for the high level of the team shipping hub is -0.588.

**/

@Disabled

public class SingleDrive extends LinearOpMode {

    private final static HWProfile robot = new HWProfile();
    private final LinearOpMode opMode = this;
    public DriveMecanumFTCLib drive = new DriveMecanumFTCLib(robot, opMode);
    private DistanceSensor sensorColorRange;
    private Servo servoTest;
    private final boolean pad2input = true;

    private double DriveSpeed = 1;
    private double TurnSpeed = 1;
    private double StrafeSpeed = 1;

    private ElapsedTime buttonPress = new ElapsedTime();

    private boolean IsOverrideActivated = false;

    public void runOpMode() {
        robot.init(hardwareMap, true);
        telemetry.addData("Status:", "Initialized");
        telemetry.update();


        // Wait for the game to start (driver presses PLAY)
        waitForStart();


        // run until the end of the match (driver presses STOP)
        buttonPress.reset();
        double stickDrive = 0;
        double turn = 0;
        double strafe = 0;
        double leftPower = 0;
        double rightPower = 0;
        int dpadCount = 0;


        while (opModeIsActive()) {
            stickDrive = this.gamepad1.left_stick_y * DriveSpeed;
            turn = this.gamepad1.right_stick_x * TurnSpeed;
            strafe = this.gamepad1.left_stick_x * StrafeSpeed;

            drive.StrafeDrive(stickDrive, turn, strafe);



            // DRIVE POWER

            if (gamepad1.left_bumper) {
                DriveSpeed = 1;
                StrafeSpeed = 1;
                TurnSpeed = 1;
            } else {
                DriveSpeed = 0.5;
                StrafeSpeed = 0.5;
                TurnSpeed = 0.5;
            }

            telemetry.addData("Status", "Running");
            telemetry.addData("Left Power", leftPower);
            telemetry.addData("Right Power", rightPower);
            telemetry.update();

        }
    }
}

