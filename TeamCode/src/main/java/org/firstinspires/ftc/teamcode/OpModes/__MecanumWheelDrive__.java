package org.firstinspires.ftc.teamcode.OpModes;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Libs.DriveMecanumFTCLib;
import org.firstinspires.ftc.teamcode.Hardware.HWProfile;
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
@TeleOp(name="RR_TeleOP", group="Competition")
@Disabled
 /**

 This is the DriveOpMode. This is the OpMode that is used for the driver-controlled portion, and
 is also sometimes used for testing.

 **/

public class __MecanumWheelDrive__ extends LinearOpMode
{

    private final static HWProfile robot = new HWProfile();
    private final LinearOpMode opMode = this;
    public DriveMecanumFTCLib drive = new DriveMecanumFTCLib(robot, opMode);
    private DistanceSensor sensorColorRange;
    private Servo servoTest;
    private final boolean pad2input = true;

    private double DriveSpeed = 0.5;
    private double TurnSpeed = 0.5;
    private double StrafeSpeed = 0.5;
    private boolean armAngleDrop = false;
    private ElapsedTime dropTime = new ElapsedTime();
    private ElapsedTime buttonPressTime = new ElapsedTime();

    public double intakeAngle = robot.INTAKE_ANGLE_GRAB_SPECIMEN;
    public int armAngle = robot.ARM_ANGLE_GRAB_SPECIMEN;
    public int armControl = 0;
    public RRMechOps mechOps = null;

    public void runOpMode()
    {
        robot.init(hardwareMap, true);
        RRMechOps mechOps = new RRMechOps(robot, opMode);
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
        double aPressCount = 1;
        double bPressCount = 1;
        boolean clawOpen = true;
        ElapsedTime aPressTime = new ElapsedTime();
        ElapsedTime bPressTime = new ElapsedTime();

        //        double armUpDown;
        int armPosition = 0;
        int hangPosition = 0;
        dropTime.reset();

        waitForStart();
        mechOps.tensionRetractionString();
        buttonPressTime.reset();
        aPressTime.reset();
        bPressTime.reset();

        // driving, turning, and strafing
        while (opModeIsActive()) {
            stickDrive = this.gamepad1.left_stick_y * DriveSpeed;
            turn = -this.gamepad1.right_stick_x * TurnSpeed;
            strafe = -this.gamepad1.left_stick_x  * StrafeSpeed;

           drive.StrafeDrive(stickDrive,  turn, strafe);

           // boost
            if (gamepad1.left_stick_button) {
              climb();
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
                armControl = armControl + 10;
                if(armControl > robot.ARM_LENGTH_SCORE_HIGH_BASKET) armControl = robot.ARM_LENGTH_SCORE_HIGH_BASKET;
            } else if(gamepad1.left_trigger > 0){
                armControl = armControl - 10;
                if(armControl < 0 ) armControl = 0;
            }

            //pick up sample
            if(gamepad1.dpad_right){
                intakeAngle = robot.INTAKE_ANGLE_PREP_SCORE_SPECIMEN;
            }
            //score specimen
            if(gamepad1.dpad_left){
                armAngle = robot.ARM_ANGLE_PREP_SCORE_SPECIMEN;
                intakeAngle = robot.INTAKE_ANGLE_PREP_SCORE_SPECIMEN;
                armControl = robot.ARM_LENGTH_SCORE_SPECIMEN;
            }
            // dont mess with unless you know what you are doing
            if(gamepad1.y || gamepad2.y){
                armAngleDrop = false;
                armAnglePower = 0.75;
                armAngle = robot.ARM_ANGLE_SCORE_HIGH_BASKET;
                armControl = robot.ARM_LENGTH_SCORE_HIGH_BASKET;
            } else if (gamepad1.x || gamepad2.x){
                grabSpecimen();
                armAnglePower = 0.45;
//                armAngleDrop = true;
//                dropTime.reset();
            }

            if((gamepad1.b || gamepad2.b) && (bPressTime.time() > 0.2)) {
                bPressCount = bPressCount + 1;
                bPressTime.reset();
            }

            if(bPressCount > 2){
                bPressCount = 1;
            }

            if(bPressCount == 1){
                robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);
            } else if (bPressCount == 2) {
                robot.servoTwist.setPosition(robot.INTAKE_TWIST_90);
            }


            if((gamepad1.a || gamepad2.a) && (aPressTime.time() > 0.2)) {
                aPressCount = aPressCount + 1;
                aPressTime.reset();
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
            if((gamepad1.dpad_up || gamepad2.dpad_up) && buttonPressTime.time() > 0.1) {
                buttonPressTime.reset();
                intakeAngle = intakeAngle + 0.03;
            } else if ((gamepad1.dpad_down || gamepad2.dpad_down) && buttonPressTime.time() > 0.1) {
                buttonPressTime.reset();
                intakeAngle = intakeAngle - 0.03;
            }

            // apply settings to motors and servos
            robot.servoIntakeAngle.setPosition(this.intakeAngle);
            robot.motorArmAngle.setPower(armAnglePower);
            robot.motorArmLength.setPower(1);
            robot.motorArmAngle.setTargetPosition(this.armAngle);
            robot.motorArmLength.setTargetPosition(this.armControl);

            telemetry.addData("armControl = ", this.armControl);
            telemetry.addData("armAngle = ", this.armAngle);
            telemetry.addData("servoIntakeAngle = ", this.intakeAngle);
            telemetry.addData("Status", "Running");
            telemetry.addData("Left Power", leftPower);
            telemetry.addData("Right Power", rightPower);
            telemetry.update();

        }
    }

    private void grabSpecimen(){
        this.armAngle = robot.ARM_ANGLE_GRAB_SPECIMEN;
        this.armControl = robot.ARM_LENGTH_RESET;
        this.intakeAngle = robot.INTAKE_ANGLE_GRAB_SPECIMEN;

    }


    public void climb(){
        robot.servoIntakeAngle.setPosition(robot.INTAKE_ANGLE_GRAB_SAMPLE);
        robot.motorArmAngle.setPower(1);
        robot.motorArmAngle.setTargetPosition(robot.ARM_ANGLE_GRAB_BAR);
        sleep(1500);
        robot.motorRR.setPower(1);
        robot.motorLR.setPower(1);
        robot.motorLF.setPower(1);
        robot.motorRF.setPower(1);
        sleep(500);
        robot.motorArmAngle.setTargetPosition(robot.ARM_ANGLE_CLIMB);
        sleep(1500);
        halt();
    }


    public void halt(){
        robot.motorRR.setPower(0);
        robot.motorLR.setPower(0);
        robot.motorLF.setPower(0);
        robot.motorRF.setPower(0);
    }

}