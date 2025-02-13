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

import org.firstinspires.ftc.onbotjava.handlers.objbuild.WaitForBuild;
import org.firstinspires.ftc.teamcode.Hardware.HWProfile;
import org.firstinspires.ftc.teamcode.Libs.RRMechOps;
import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "RR- 4 Specimen", group = "Competition", preselectTeleOp = "__MecanumWheelDrive__")
@Disabled
public class RRSpecimen4Auto extends LinearOpMode {

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
        mechOps.initArmAngle();
        mechOps.setArmAnglePower(1);
        mechOps.setArmAnglePosition(robot.ARM_ANGLE_RESET);
        robot.servoIntakeAngle.setPosition(robot.INTAKE_ANGLE_INIT);
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);

        // Wait for the DS start button to be touched.
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.addData("-------------------", "-------------------");
        telemetry.addData("Retraction Position = ", robot.motorArmLength.getCurrentPosition());
        telemetry.addData("Arm Angle Encoder Value = ", robot.motorArmAngle.getCurrentPosition());
        telemetry.update();

        waitForStart();

        if (opModeIsActive() && !isStopRequested()) {

            scoreSpecimen(drive);
            retrieveColoredSample1(drive);
            scoreSpecimen2(drive);
            retrieveColoredSample2(drive);
            scoreSpecimen3(drive);
            scoreSpecimen3(drive);
            park(drive);
        }

        requestOpModeStop();

    }   // end runOpMode()

    public void scoreSpecimen(MecanumDrive thisDrive) {
        Pose2d specimenScoringPrepPosition = new Pose2d(34,8,0);
        Pose2d midwayPose1 = new Pose2d(20,-8,Math.toRadians(0));

        if(opModeIsActive()) mechOps.setScoreSpecimen();
        safeWaitSeconds(0.3);

        //Approach submersible to latch the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(specimenScoringPrepPosition.position, specimenScoringPrepPosition.heading)
                        .build());

        // Engage the specimen with the submersible
        if(opModeIsActive()) mechOps.scoreSpecimen();
        safeWaitSeconds(0.300);
        mechOps.openClaw();
        safeWaitSeconds(0.1);

        // back away from the submersible before resetting arms
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        if(opModeIsActive()) mechOps.setGrabSpecimen();
        if(opModeIsActive()) mechOps.openClaw();
    }

    public void retrieveColoredSample1(MecanumDrive thisDrive) {
        Pose2d midwayPose1 = new Pose2d(20,-29,Math.toRadians(180));
        Pose2d midwayPose1a = new Pose2d(24,-31,Math.toRadians(180));
        Pose2d midwayPose2 = new Pose2d(55,-31,Math.toRadians(180));
        Pose2d coloredSample1 = new Pose2d(55,-40,Math.toRadians(180));
        Pose2d observationZonePosition = new Pose2d(7,-33,Math.toRadians(180));

        if(opModeIsActive()) mechOps.setGrabSpecimen();
        if(opModeIsActive()) mechOps.openClaw();

        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .strafeToLinearHeading(midwayPose1a.position, midwayPose1a.heading)
                        .strafeToLinearHeading(midwayPose2.position, midwayPose2.heading)
                        .strafeToLinearHeading(coloredSample1.position, coloredSample1.heading)
                        .strafeToLinearHeading(observationZonePosition.position, observationZonePosition.heading)
                        .build());
    }

    public void retrieveColoredSample2(MecanumDrive thisDrive) {
        Pose2d midwayPose1 = new Pose2d(55,-35,Math.toRadians(180));
        Pose2d coloredSample2 = new Pose2d(55,-50,Math.toRadians(180));
        Pose2d observationZonePosition = new Pose2d(5,-30,Math.toRadians(180));

        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .strafeToLinearHeading(coloredSample2.position, coloredSample2.heading)
                        .strafeToLinearHeading(observationZonePosition.position, observationZonePosition.heading)
                        .build());
    }

    public void scoreSpecimen2(MecanumDrive thisDrive) {
        Pose2d specimenPickupPosition = new Pose2d(4,-33,Math.toRadians(180));
        Pose2d midwayPose1 = new Pose2d(20,10,Math.toRadians(0));
        Pose2d specimenScoringPrepPosition = new Pose2d(34,8,Math.toRadians(0));

        if(opModeIsActive()) mechOps.setGrabSpecimen();
        if(opModeIsActive()) mechOps.openClaw();

        // drive to the specimen pickup position
//        Actions.runBlocking(      
//                thisDrive.actionBuilder(thisDrive.pose)
//                        .strafeToLinearHeading(specimenPickupPosition.position, specimenPickupPosition.heading)
//                        .build());
        // grab the specimen and remove it from the wall
        if(opModeIsActive()) mechOps.closeClaw();
        safeWaitSeconds(0.3);
        if(opModeIsActive()) mechOps.setScoreSpecimen();

        // drive to the midway position to prepare to score the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        // Move into specimen scoring position
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(specimenScoringPrepPosition.position, specimenScoringPrepPosition.heading)
                        .build());

        // Score the specimen
        if(opModeIsActive()) mechOps.scoreSpecimen();
        safeWaitSeconds(0.3);
        if(opModeIsActive()) mechOps.openClaw();
        safeWaitSeconds(0.2);
    }

    public void scoreSpecimen3(MecanumDrive thisDrive) {
        Pose2d specimenPickupPosition = new Pose2d(4,-35,Math.toRadians(180));
        Pose2d midwayPose1 = new Pose2d(20,0,Math.toRadians(180));
        Pose2d midwayPose2 = new Pose2d(18,-35,Math.toRadians(180));
        Pose2d midwayPose3 = new Pose2d(26,10,Math.toRadians(0));
        Pose2d specimenScoringPosition = new Pose2d(31,8,Math.toRadians(0));

        // Reset the arm to grab another specimen
        if(opModeIsActive()) mechOps.setGrabSpecimen();
        if(opModeIsActive()) mechOps.openClaw();

        // Drive to the specimen pickup position
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .strafeToLinearHeading(midwayPose2.position, midwayPose2.heading)
                        .strafeToLinearHeading(specimenPickupPosition.position, specimenPickupPosition.heading)
                        .build());

        // Grab the specimen and remove it from the wall
        if(opModeIsActive()) mechOps.removeSpecimen();

        // drive to the midway position to prepare to score the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        // Prepare to score the specimen
        if(opModeIsActive()) mechOps.setScoreSpecimen();

        // Move into specimen scoring position
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose3.position, midwayPose3.heading)
                        .strafeToLinearHeading(specimenScoringPosition.position, specimenScoringPosition.heading)
                        .build());

        // Score the specimen
        if(opModeIsActive()) mechOps.scoreSpecimen();
        safeWaitSeconds(0.5);
        if(opModeIsActive()) mechOps.openClaw();
    }

    public void park(MecanumDrive thisDrive) {
        // Set the positions for this method
        Pose2d midwayPose1 = new Pose2d(20,10,Math.toRadians(0));
        Pose2d parkPose = new Pose2d(0,-55,Math.toRadians(0));

        // drive to the midway position to prepare to score the specimen
        Actions.runBlocking(
                thisDrive.actionBuilder(thisDrive.pose)
                        .strafeToLinearHeading(midwayPose1.position, midwayPose1.heading)
                        .build());

        // Reset the arm to grab another specimen
        if(opModeIsActive()) mechOps.setGrabSpecimen();

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