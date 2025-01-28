/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.OpModes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Hardware.HWProfile;
import org.firstinspires.ftc.teamcode.Libs.DriveMecanumFTCLib;
import org.firstinspires.ftc.teamcode.Libs.RRMechOps;

import java.util.Locale;

@TeleOp(name="New Competition TeleOp", group="Competition")

public class NewTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private final static HWProfile robot = new HWProfile();
    private final LinearOpMode opMode = this;
    private RRMechOps mechOps;

    private final ElapsedTime runtime = new ElapsedTime();
    private DriveMecanumFTCLib drive;
    private double botHeading;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap, true);
        mechOps = new RRMechOps(robot, opMode);
        drive = new DriveMecanumFTCLib(robot, opMode);

        mechOps.armAnglePosition = 0;
        double x, y, rx, denominator, frontLeftPower, frontRightPower, backLeftPower, backRightPower;
        double rotX, rotY;
        boolean clawOpen = true;
        ElapsedTime clawButtonTimer = new ElapsedTime();
        boolean twistTurn90 = false;
        ElapsedTime twistButtonTimer = new ElapsedTime();
        double powerFactor = 0.80;

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)

        waitForStart();
        mechOps.servoIntakePosition = robot.INTAKE_ANGLE_INIT;
        mechOps.armAnglePower = 1;
        clawButtonTimer.reset();
        twistButtonTimer.reset();
        mechOps.initArmAngle();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            /** ##########################################################
             *
             * Drive Control
             *
             #############################################################*/
//            stickDrive = this.gamepad1.left_stick_y * DriveSpeed;
//            turn = -this.gamepad1.right_stick_x * TurnSpeed;
//            strafe = -this.gamepad1.left_stick_x  * StrafeSpeed;
//
//            drive.StrafeDrive(stickDrive,  turn, strafe);

            y = -gamepad1.left_stick_y + gamepad1.right_stick_y;
            x = gamepad1.left_stick_x;
            rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                robot.pinpoint.recalibrateIMU();
                //recalibrates the IMU without resetting position
            }

            robot.pinpoint.update();    //update the IMU value
            Pose2D pos = robot.pinpoint.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.RADIANS));
            telemetry.addData("Position", data);

            //botHeading = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            botHeading = pos.getHeading(AngleUnit.RADIANS);

            rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            frontLeftPower = (rotY + rotX + rx) / denominator;
            backLeftPower = (rotY - rotX + rx) / denominator;
            frontRightPower = (rotY - rotX - rx) / denominator;
            backRightPower = (rotY + rotX - rx) / denominator;

            robot.motorLF.setPower(frontLeftPower * powerFactor);
            robot.motorLR.setPower(backLeftPower * powerFactor);
            robot.motorRF.setPower(frontRightPower * powerFactor);
            robot.motorRR.setPower(backRightPower * powerFactor);


            /** ##########################################################
             *
             * Claw Control
             *
             #############################################################*/

            if((gamepad1.a || gamepad2.a) && clawButtonTimer.time() > 0.3) {
                if(clawOpen) {
                    mechOps.closeClaw();
                    clawOpen = false;
                } else {
                    mechOps.openClaw();
                    clawOpen = true;
                }
                clawButtonTimer.reset();
            }

            if((gamepad1.b || gamepad2.b) && twistButtonTimer.time() > 0.3) {
                if(twistTurn90) {
                    mechOps.twistTurnInit();
                    twistTurn90 = false;
                } else {
                    mechOps.twistTurn90();
                    twistTurn90 = true;
                }
                twistButtonTimer.reset();
            }

            /** ##########################################################
             *
             * Specimen preset controls
             *
             #############################################################*/

            if(gamepad1.dpad_left) mechOps.removeSpecimen();
            if(gamepad1.dpad_up) {
                mechOps.setScoreSpecimen();
            }

            if(gamepad1.dpad_right){
                mechOps.scoreSpecimen();
            }

            /** ##########################################################
             *
             * Manual Arm Angle control
             *
             #############################################################*/
            if(gamepad1.dpad_down){
                mechOps.setGrabSpecimen();
            }

            /** ##########################################################
             *
             * Climb control
             *
             #############################################################*/

            if(gamepad1.y && gamepad2.y) {
                mechOps.climb();
            } else if(gamepad1.y){
                mechOps.prepScoreSampleHigh();
            } else if(gamepad1.x || gamepad2.x){
                mechOps.prepGrabSample();
            }

            /** ##########################################################
             *
             * Manual Arm Angle control
             *
             #############################################################*/
            if(gamepad1.right_trigger != 0 || gamepad2.right_trigger != 0){
                mechOps.armAnglePosition = mechOps.armAnglePosition - 10;
                if(mechOps.armAnglePosition > robot.ARM_ANGLE_RESET) mechOps.armAnglePosition = robot.ARM_ANGLE_RESET;
                if(mechOps.armAnglePosition < robot.ARM_ANGLE_SCORE_HIGH_BASKET) mechOps.armAnglePosition = robot.ARM_ANGLE_SCORE_HIGH_BASKET;

                mechOps.setArmAnglePosition(mechOps.armAnglePosition);
            } else if(gamepad1.left_trigger != 0 || gamepad2.left_trigger != 0){
                mechOps.armAnglePosition = mechOps.armAnglePosition + 10;
                if(mechOps.armAnglePosition > robot.ARM_ANGLE_RESET) mechOps.armAnglePosition = robot.ARM_ANGLE_RESET;
                if(mechOps.armAnglePosition < robot.ARM_ANGLE_SCORE_HIGH_BASKET) mechOps.armAnglePosition = robot.ARM_ANGLE_SCORE_HIGH_BASKET;

                mechOps.setArmAnglePosition(mechOps.armAnglePosition);
            }

            /** ##########################################################
             *
             * Manual Arm Length control
             *
             #############################################################*/
            if(gamepad2.right_stick_y < 0){
                telemetry.addLine("Gamepad2.Right_Stick_Y < 0");
                mechOps.armLengthPosition = mechOps.armLengthPosition + 40;
                if(mechOps.armLengthPosition > robot.ARM_LENGTH_SCORE_HIGH_BASKET) mechOps.armLengthPosition = robot.ARM_LENGTH_SCORE_HIGH_BASKET;
                if(mechOps.armLengthPosition < robot.ARM_LENGTH_RESET) mechOps.armLengthPosition = robot.ARM_LENGTH_RESET;

            } else if(gamepad2.right_stick_y > 0){
                telemetry.addLine("Gamepad2.Right_Stick_Y > 0");
                mechOps.armLengthPosition = mechOps.armLengthPosition - 40;
                if(mechOps.armAnglePosition > robot.ARM_LENGTH_SCORE_HIGH_BASKET) mechOps.armLengthPower = robot.ARM_LENGTH_SCORE_HIGH_BASKET;
                if(mechOps.armLengthPosition < robot.ARM_LENGTH_RESET) mechOps.armLengthPosition = robot.ARM_LENGTH_RESET;

            }

            if(gamepad2.right_bumper){
                mechOps.servoIntakePosition = mechOps.servoIntakePosition + 0.005;
                if(mechOps.servoIntakePosition > robot.INTAKE_ANGLE_GRAB_SAMPLE) mechOps.servoIntakePosition = robot.INTAKE_ANGLE_GRAB_SAMPLE;
                if(mechOps.servoIntakePosition < robot.INTAKE_ANGLE_INIT) mechOps.servoIntakePosition = robot.INTAKE_ANGLE_INIT;
            }
            if (gamepad2.left_bumper) {
                mechOps.servoIntakePosition = mechOps.servoIntakePosition - 0.005;
                if(mechOps.servoIntakePosition > robot.INTAKE_ANGLE_GRAB_SAMPLE) mechOps.servoIntakePosition = robot.INTAKE_ANGLE_GRAB_SAMPLE;
                if(mechOps.servoIntakePosition < robot.INTAKE_ANGLE_INIT) mechOps.servoIntakePosition = robot.INTAKE_ANGLE_INIT;
            }
            /** ##########################################################
             *
             * Check to make sure that the arm does not extend too far out if the angle is brought down
             *
             #############################################################*/

            if((robot.motorArmAngle.getTargetPosition() > robot.ARM_ANGLE_MAX_LENGTH) && robot.motorArmLength.getTargetPosition() > robot.ARM_LENGTH_SAFE){
                mechOps.armLengthPosition = robot.ARM_LENGTH_SAFE;
            }
            mechOps.setArmLengthPosition(mechOps.armLengthPosition);
            mechOps.setServoIntakeAnglePosition(mechOps.servoIntakePosition);

            /** ##########################################################
             *
             * Preset positions for Specimen retrieval and scoring
             *
             #############################################################*/

            if(gamepad2.dpad_up){
                mechOps.setScoreSpecimen();
            }

            if(gamepad1.right_stick_button || gamepad1.left_stick_button){
                mechOps.halt();
            }

            if(gamepad2.dpad_down){
                mechOps.setGrabSpecimen();
            }

            if(gamepad2.dpad_left){
                mechOps.scoreSpecimen();
            }

            if(gamepad2.dpad_right){
                mechOps.prepScoreSampleHigh();
            }

            telemetry.addData("Open Claw = ", "B");
            telemetry.addData("Close Claw = ", "A");
            telemetry.addData("Set Score Specimen = ", "Gamepad2.DPAD_UP");
            telemetry.addData("Score Specimen = ", "Gamepad2.DPAD_LEFT");
            telemetry.addData("Grab Sample = ", "Gamepad2.DPAD_RIGHT");
            telemetry.addData("Reset Arm to Grab Specimen = ", "Gamepad2.DPAD_DOWN");
            telemetry.addData("Grab Specimen = ", "Gamepad1.X");
            telemetry.addData("------------Readings----------", "--------------");
            telemetry.addData("Intake Arm Angle  = ", mechOps.servoIntakePosition);
            telemetry.addData("Arm Angle Position = ", mechOps.armAnglePosition);
            telemetry.addData("Arm Length Position = ", mechOps.armLengthPosition);
            telemetry.addData("Right Front Encoder = ", robot.motorRF.getCurrentPosition());
            telemetry.addData("Right Rear Encoder = ", robot.motorRR.getCurrentPosition());
            telemetry.addData("Left Front Encoder = ", robot.motorLF.getCurrentPosition());
            telemetry.addData("Left Rear Encoder = ", robot.motorLR.getCurrentPosition());
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}
