/* Copyright (c) 2019 FIRST. All rights reserved.
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

import static com.qualcomm.robotcore.util.ElapsedTime.Resolution.SECONDS;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.HWProfile;
import org.firstinspires.ftc.teamcode.Libs.RRMechOps;
import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "RR Sample", group = "Competition", preselectTeleOp = "__MecanumWheelDrive__")
@Disabled
public class DeprecatedRRSampleAuto extends LinearOpMode {

    public static String TEAM_NAME = "Robo Renegades";
    public static int TEAM_NUMBER = 18802;

    public final static HWProfile robot = new HWProfile();
    public LinearOpMode opMode = this;
    public RRMechOps mechOps = new RRMechOps(robot, opMode);

    //Initialize Pose2d as desired
    public Pose2d initPose = new Pose2d(0, 0, 0); // Starting Pose
    public Pose2d specimenScoringPrepPosition = new Pose2d(0,0,0);
    public Pose2d specimenScoringPosition = new Pose2d(0, 0, 0);
    public Pose2d midwayPose1 = new Pose2d(0,0,0);
    public Pose2d coloredSample1 = new Pose2d(0,0,0);
    public Pose2d coloredSample2 = new Pose2d(0,0,0);
    public Pose2d coloredSample3 = new Pose2d(0,0,0);
    public Pose2d midwayPose2 = new Pose2d(0,0,0);
    public Pose2d specimenPickupPosition = new Pose2d(0,0,0);
    public Pose2d dropYellowPixelPose = new Pose2d(0, 0, 0);
    public Pose2d parkPose = new Pose2d(0,0, 0);


    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap, false);
        MecanumDrive drive = new MecanumDrive(hardwareMap, initPose);

        telemetry.addData(">", ">##################################<");
        telemetry.addData(">", ">##################################<");
        telemetry.addData(">", ">PREPARING HARDWARE - DO NOT START <");
        telemetry.addData(">", ">##################################<");
        telemetry.addData(">", ">##################################<");
        telemetry.update();

        mechOps.tensionRetractionString();
        mechOps.resetAngleArm();

        mechOps.closeClaw();
//        mechOps.initArmAngle();
        mechOps.setArmAnglePower(1);
//        mechOps.setArmAnglePosition(robot.ARM_ANGLE_RESET);
        robot.servoIntakeAngle.setPosition(robot.INTAKE_ANGLE_INIT);
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);

        // Wait for the DS start button to be touched.
        while(!opModeIsActive()){
            telemetry.addData(">", "Touch Play to start OpMode");
            telemetry.addData("-------------------", "-------------------");
            telemetry.addData("Retraction Position = ", robot.motorArmLength.getCurrentPosition());
            telemetry.addData("Arm Angle Encoder Value = ", robot.motorArmAngle.getCurrentPosition());
            telemetry.update();
        }

        if (opModeIsActive() && !isStopRequested()) {

            scoreSample(drive);
            scoreSample2(drive);
            scoreSample3(drive);
            scoreSample4(drive);
//            retrieveColoredSample1(drive);
//            retrieveColoredSample2(drive);
            park(drive);
        }

        requestOpModeStop();

    }   // end runOpMode()

    public void scoreSample(MecanumDrive thisDrive) {
        // make sure the bot has a good grip on the pixels
        Pose2d sampleScoringPrepPosition = new Pose2d(9,27,Math.toRadians(130));
        Pose2d midwayPose1 = new Pose2d(20,8,Math.toRadians(0));
        Pose2d midwayPose1a = new Pose2d(8,5,Math.toRadians(75));

        if(opModeIsActive()) mechOps.prepScoreSampleHigh();
        safeWaitSeconds(0.4);

        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1a.position, midwayPose1a.heading)
                        .build());


        //Approach submersible to latch the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(sampleScoringPrepPosition.position, sampleScoringPrepPosition.heading)
                        .build());

        // Engage the specimen with the submersible
        if(opModeIsActive()) mechOps.openClaw();
        safeWaitSeconds(0.2);

        // back away from the submersible before resetting arms
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        mechOps.setArmLengthPower(1);
        mechOps.setArmLengthPosition(robot.ARM_LENGTH_RESET);
        safeWaitSeconds(0.4);
        if(opModeIsActive()) mechOps.resetArm();
        safeWaitSeconds(0.7);
    }

    public void scoreSample2(MecanumDrive thisDrive) {
        // make sure the bot has a good grip on the pixels
        Pose2d sampleScoringPrepPosition = new Pose2d(7,21,Math.toRadians(135));
        Pose2d midwayPose1 = new Pose2d(23,8,Math.toRadians(0));
        Pose2d midwayPose1a = new Pose2d(20,9,Math.toRadians(0));

        if(opModeIsActive()) mechOps.prepGrabSample();
        safeWaitSeconds(0.3);

        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        if(opModeIsActive()) mechOps.autoGrabSample();
        safeWaitSeconds(0.3);
        if(opModeIsActive()) mechOps.prepScoreSampleHigh();
        safeWaitSeconds(0.75);

        //Approach submersible to latch the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(sampleScoringPrepPosition.position, sampleScoringPrepPosition.heading)
                        .build());

        // Engage the specimen with the submersible
        if(opModeIsActive()) mechOps.openClaw();

        // back away from the submersible before resetting arms
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1a.position, midwayPose1a.heading)
                        .build());

        mechOps.setArmLengthPower(1);
        mechOps.setArmLengthPosition(robot.ARM_LENGTH_RESET);
        safeWaitSeconds(0.5);
        if(opModeIsActive()) mechOps.resetArm();
        safeWaitSeconds(0.75);
    }

    public void scoreSample3(MecanumDrive thisDrive) {
        // make sure the bot has a good grip on the pixels
        Pose2d sampleScoringPrepPosition = new Pose2d(7,24,Math.toRadians(135));
        Pose2d midwayPose1 = new Pose2d(27,15.5,Math.toRadians(0));
        Pose2d midwayPose1a = new Pose2d(20,9,Math.toRadians(0));

        if(opModeIsActive()) mechOps.prepGrabSample();
        safeWaitSeconds(0.3);

        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        if(opModeIsActive()) mechOps.autoGrabSample();
        safeWaitSeconds(0.4);
        if(opModeIsActive()) mechOps.prepScoreSampleHigh();
        safeWaitSeconds(0.650);

        //Approach submersible to latch the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(sampleScoringPrepPosition.position, sampleScoringPrepPosition.heading)
                        .build());

        // Engage the specimen with the submersible
        if(opModeIsActive()) mechOps.openClaw();
        safeWaitSeconds(0.2);

        // back away from the submersible before resetting arms
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1a.position, midwayPose1a.heading)
                        .build());

        mechOps.setArmLengthPower(1);
        mechOps.setArmLengthPosition(robot.ARM_LENGTH_RESET);
        safeWaitSeconds(0.2);
        if(opModeIsActive()) mechOps.resetArm();
        safeWaitSeconds(0.7);
    }

    public void scoreSample4(MecanumDrive thisDrive) {
        // make sure the bot has a good grip on the pixels
        Pose2d sampleScoringPrepPosition = new Pose2d(7,23,Math.toRadians(135));
        Pose2d midwayPose1 = new Pose2d(31,17.5,Math.toRadians(30));
        Pose2d midwayPose1a = new Pose2d(10,9,Math.toRadians(0));

        if(opModeIsActive()) mechOps.prepGrabSample();
        safeWaitSeconds(0.2);

        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        if(opModeIsActive()) mechOps.autoGrabSample();
        safeWaitSeconds(0.5);
        if(opModeIsActive()) mechOps.prepScoreSampleHigh();
        safeWaitSeconds(0.650);

        //Approach submersible to latch the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(sampleScoringPrepPosition.position, sampleScoringPrepPosition.heading)
                        .build());

        // Engage the specimen with the submersible
        if(opModeIsActive()) mechOps.openClaw();
        safeWaitSeconds(0.2);

        // back away from the submersible before resetting arms
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1a.position, midwayPose1a.heading)
                        .build());

        mechOps.setArmLengthPower(1);
        mechOps.setArmLengthPosition(robot.ARM_LENGTH_RESET);
        safeWaitSeconds(0.4);
        if(opModeIsActive()) mechOps.resetArm();
        safeWaitSeconds(0.9);
    }

    public void park(MecanumDrive thisDrive) {
        // Set the positions for this method
        Pose2d parkPose = new Pose2d(0,-85,Math.toRadians(0));

        // Reset the arm to grab another specimen
        if(opModeIsActive()) mechOps.resetArm();
        if(opModeIsActive()) mechOps.openClaw();

        // Go to the park position
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(parkPose.position, parkPose.heading)
                        .build());
    }

    //method to wait safely with stop button working if needed. Use this instead of sleep
    public void safeWaitSeconds(double time) {
        ElapsedTime timer = new ElapsedTime(SECONDS);
        timer.reset();
        while (!isStopRequested() && timer.time() < time) {
        }
    }

}   // end class