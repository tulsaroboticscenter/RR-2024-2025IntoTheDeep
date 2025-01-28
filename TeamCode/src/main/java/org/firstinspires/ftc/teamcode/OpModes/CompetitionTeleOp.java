package org.firstinspires.ftc.teamcode.OpModes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.HWProfile;
import org.firstinspires.ftc.teamcode.Libs.DriveMecanumFTCLib;
import org.firstinspires.ftc.teamcode.Libs.RRMechOps;


/**
 #########################################

                                CONTROLS MAPPING -- TBD

 #########################################

  ==GAMEPAD 1==

    --DRIVE--
    Left Stick -- Forward/Backward (y-axis) & Strafe Left/Right (x-axis)
    Right Stick -- Turn Left/Right (x-axis)

    --SPECIAL--
    Hold Left Bumper -- Drive Dampener (Slows down drive speed for needed precision movements.)

  ==GAMEPAD 2==

    --TO BE DETERMINED--

 **/
@TeleOp(name="TeleOp - Experimental", group="Competition")
@Disabled
 /**

 This is the DriveOpMode. This is the OpMode that is used for the driver-controlled portion, and
 is also sometimes used for testing.

 **/

public class CompetitionTeleOp extends LinearOpMode
{

    private final static HWProfile robot = new HWProfile();
    private final LinearOpMode opMode = this;
    public DriveMecanumFTCLib drive = new DriveMecanumFTCLib(robot, opMode);
    public RRMechOps mechOps;
    private DistanceSensor sensorColorRange;
    private Servo servoTest;
    private final boolean pad2input = true;

    private double DriveSpeed = 1;
    private double TurnSpeed = 1;
    private double StrafeSpeed = 1;
    private boolean armAngleDrop = false;
    private ElapsedTime dropTime = new ElapsedTime();
    private ElapsedTime buttonPressTime = new ElapsedTime();

    public double intakeAngle = 0;
    public int armAngle = robot.ARM_ANGLE_GRAB_SPECIMEN;
    public int armLength = 0;

    public void runOpMode()
    {
        robot.init(hardwareMap, true);
        mechOps = new RRMechOps(robot, opMode);
        telemetry.addData("Status:", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
//        drive.haltandresetencoders();
        //runtime.reset();


        // run until the end of the match (driver presses STOP)
        double stickDrive = 0;
        double turn = 0;
        double strafe = 0;
        double leftPower = 0;
        double rightPower = 0;
        double armAnglePower = 1;
        double intakeAngle = 0;
        double aPressCount = 1;
        boolean clawOpen = true;
        ElapsedTime aPressTime = new ElapsedTime();
        boolean wristRotate = false;

        //        double armUpDown;
        int armPosition = 0;
        int hangPosition = 0;
        dropTime.reset();

        waitForStart();
        buttonPressTime.reset();
        aPressTime.reset();

        // driving, turning, and strafing
        while (opModeIsActive()) {
            stickDrive = this.gamepad1.left_stick_y * DriveSpeed;
            turn = -this.gamepad1.right_stick_x * TurnSpeed;
            strafe = -this.gamepad1.left_stick_x  * StrafeSpeed;

           drive.StrafeDrive(stickDrive,  turn, strafe);

           // boost
            if (gamepad1.left_stick_button) {
                DriveSpeed = 1;
                StrafeSpeed = 1;
                TurnSpeed = 1;
            } else {
                DriveSpeed = 0.5;
                StrafeSpeed = 0.5;
                TurnSpeed = 0.5;
            }

            if(gamepad1.left_bumper || gamepad2.left_bumper) {
                armAngle = armAngle + 2;
                if(armAngle > 0) armAngle = 0;
                armAnglePower = 0.75;
            } else if(gamepad1.right_bumper || gamepad2.right_bumper) {
                armAngle = armAngle - 2;
                armAnglePower = 0.25;
                if (armAngle < robot.ARM_ANGLE_SCORE_HIGH_BASKET) armAngle = robot.ARM_ANGLE_SCORE_HIGH_BASKET;
            }

            // arm extension
            if((gamepad1.right_trigger > 0) || (gamepad2.right_trigger > 0)) {
                armLength = armLength + 10;
                if(armLength > robot.ARM_LENGTH_SCORE_HIGH_BASKET) armLength = robot.ARM_LENGTH_SCORE_HIGH_BASKET;
            } else if(gamepad1.left_trigger > 0){
                armLength = armLength - 10;
                if(armLength < 0 ) armLength = 0;
            }

            //pick up sample
            if(gamepad1.dpad_right){
                armAnglePower = 1;
                armAngle = robot.ARM_ANGLE_PREP_SCORE_SPECIMEN + 500;
            }

            if(gamepad1.dpad_down) {
                aPressCount = 1;        // open the claw
                armAnglePower = 0.2;
                intakeAngle = robot.INTAKE_ANGLE_GRAB_SPECIMEN;
                armAngle = robot.ARM_ANGLE_GRAB_SPECIMEN;
                armLength = robot.ARM_LENGTH_RESET;
            }

            if(gamepad1.left_stick_button){
                armAngle = robot.ARM_ANGLE_REMOVE_SPECIMEN;
            }
            if(gamepad1.dpad_up) {
                armAnglePower = 1;
                intakeAngle = robot.INTAKE_ANGLE_PREP_SCORE_SPECIMEN;
                armAngle = robot.ARM_ANGLE_PREP_SCORE_SPECIMEN;
                armLength = robot.ARM_LENGTH_SCORE_SPECIMEN;
                intakeAngle = robot.INTAKE_ANGLE_GRAB_SPECIMEN;
            }

            //score specimen
            if(gamepad1.dpad_left){
                armAngle = robot.ARM_ANGLE_PREP_SCORE_SPECIMEN;
                intakeAngle = robot.INTAKE_ANGLE_PREP_SCORE_SPECIMEN;
                armLength = robot.ARM_LENGTH_SCORE_SPECIMEN;
            }
            // dont mess with unless you know what you are doing
            if(gamepad1.y || gamepad2.y){
                armAngleDrop = false;
                armAnglePower = 0.5;
                armAngle = robot.ARM_ANGLE_SCORE_HIGH_BASKET;
                armLength = robot.ARM_LENGTH_SCORE_HIGH_BASKET;
            } else if (gamepad1.x || gamepad2.x){
                grabSpecimen();
                armAnglePower = 0.25;
                armAngleDrop = true;
                dropTime.reset();
            }

            if((gamepad1.a || gamepad2.a) && (aPressTime.time() > 0.2)) {
                aPressCount = aPressCount + 1;
                aPressTime.reset();
            }

            if((gamepad1.b || gamepad2.b) && (buttonPressTime.time() > 0.2)) {
                if(wristRotate){
                    robot.servoTwist.setPosition(robot.INTAKE_TWIST_90);
                    wristRotate = false;
                } else {
                    robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);
                    wristRotate = true;
                }
                buttonPressTime.reset();
            }

            if(aPressCount > 2){
                aPressCount = 1;
            }

            //opens and closes claw
            if(aPressCount == 1){
                robot.servoIntake.setPosition(robot.INTAKE_CLAW_OPEN);
            } else if (aPressCount == 2) {
                robot.servoIntake.setPosition(robot.INTAKE_CLAW_CLOSE);
            }

            //resets intake angle
            if(intakeAngle > 1) intakeAngle = 1;
            if(intakeAngle < 0) intakeAngle = 0;

            // angle of intake
            if((gamepad2.dpad_up) && buttonPressTime.time() > 0.1) {
                buttonPressTime.reset();
                intakeAngle = intakeAngle - 0.03;
            } else if ((gamepad2.dpad_down) && buttonPressTime.time() > 0.1) {
                buttonPressTime.reset();
                intakeAngle = intakeAngle + 0.03;
            }

            // apply settings to motors and servos
            robot.servoIntakeAngle.setPosition(intakeAngle);
            robot.motorArmAngle.setPower(armAnglePower);
            robot.motorArmLength.setPower(1);
            robot.motorArmAngle.setTargetPosition(armAngle);
            robot.motorArmLength.setTargetPosition(armLength);

            telemetry.addData("armControl = ", armLength);
            telemetry.addData("armAngle = ", armAngle);
            telemetry.addData("servoIntakeAngle = ", intakeAngle);
            telemetry.addData("Status", "Running");
            telemetry.addData("Left Power", leftPower);
            telemetry.addData("Right Power", rightPower);
            telemetry.update();

        }
    }

    private void grabSpecimen(){
        this.intakeAngle = robot.INTAKE_ANGLE_GRAB_SPECIMEN;
        this.armAngle = robot.ARM_ANGLE_GRAB_SPECIMEN;
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_OPEN);
        this.armLength = robot.ARM_LENGTH_RESET;

    }

}