package org.firstinspires.ftc.teamcode.Libs;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Hardware.HWProfile;

public class RRMechOps{

    /**

        This is a Library used for

    **/

    public HWProfile robot;
    public LinearOpMode opMode;

    public int armAnglePosition = 0;
    public double armAnglePower = 0;
    public int armLengthPosition = 0;
    public double armLengthPower = 0;
    public double servoIntakePosition = 0;

    /*
     * Constructor method
     */
    public RRMechOps(HWProfile myRobot, LinearOpMode myOpMode){
        robot = myRobot;
        opMode = myOpMode;

    }   // close DriveMecanum constructor Method

    /*
     * ###########################################################################################
     * ###########################################################################################
     * #######################      SYSTEM CONTROLS      #########################################
     * ###########################################################################################
     * ###########################################################################################
     */

    public void closeClaw(){
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_CLOSE);
    }

    public void autoCloseClaw(){
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_CLOSE_TIGHT);
    }

    public void openClaw(){
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_OPEN);
    }

    public void twistTurn90(){
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_90);
    }

    public void twistTurnInit(){
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);
    }

    public void setServoIntakeAnglePosition(double targetPosition){
        servoIntakePosition = targetPosition;
        robot.servoIntakeAngle.setPosition(this.servoIntakePosition);
    }

    public void setArmAnglePower(double armAnglePower){
        this.armAnglePower = armAnglePower;
        robot.motorArmAngle.setPower(this.armAnglePower);
    }

    public void setArmAnglePosition(int armAnglePosition){
        this.armAnglePosition = armAnglePosition;
        robot.motorArmAngle.setTargetPosition(this.armAnglePosition);
    }

    public void setArmLengthPower(double armLengthPower){
        this.armLengthPower = armLengthPower;
        robot.motorArmLength.setPower(this.armLengthPower);
    }

    public void setArmLengthPosition(int armLengthPosition){
        this.armLengthPosition = armLengthPosition;
        robot.motorArmLength.setTargetPosition(this.armLengthPosition);
    }

    public void setScoreSpecimen(){
        setArmAnglePower(1);
        setArmLengthPower(1);
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);
        setServoIntakeAnglePosition(robot.INTAKE_ANGLE_SCORE_SPECIMEN);
        setArmAnglePosition(robot.ARM_ANGLE_PREP_SCORE_SPECIMEN);
        setArmLengthPosition(robot.ARM_LENGTH_SCORE_SPECIMEN);
    }

    public void prepScoreSampleHigh(){
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_SCORE_HIGH_BASKET);
        setArmLengthPower(1);
        setArmLengthPosition(robot.ARM_LENGTH_SCORE_HIGH_BASKET);
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_0);
        setServoIntakeAnglePosition(robot.INTAKE_ANGLE_SCORE_SAMPLE);
    }

    public void prepGrabSample(){
        setArmLengthPower(1);
        setArmLengthPosition(robot.ARM_LENGTH_GRAB_SAMPLE);
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_GRAB_SAMPLE);
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);
        setServoIntakeAnglePosition(robot.INTAKE_ANGLE_GRAB_SAMPLE);
    }

    public void autoPrepGrabSample(){
        setArmLengthPower(1);
        setArmLengthPosition(robot.ARM_LENGTH_AUTO_GRAB_SAMPLE);
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_AUTO_PREP_GRAB_SAMPLE2);
        robot.servoTwist.setPosition(robot.INTAKE_TWIST_INIT);
        setServoIntakeAnglePosition(robot.INTAKE_ANGLE_GRAB_SAMPLE);
    }

    public void scoreSpecimen(){
        setServoIntakeAnglePosition(robot.INTAKE_ANGLE_SCORE_SPECIMEN);
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_SCORE_SPECIMEN);
    }

    public void setGrabSpecimen(){
        setArmLengthPower(1);
        setArmLengthPosition(robot.ARM_LENGTH_RESET);
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_GRAB_SPECIMEN);
        setServoIntakeAnglePosition(robot.INTAKE_ANGLE_GRAB_SPECIMEN);
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_OPEN);
    }

    public void removeSpecimen(){
        setArmAnglePower(1);
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_CLOSE);
        opMode.sleep(300);
        setArmAnglePosition(robot.ARM_ANGLE_REMOVE_SPECIMEN);
    }

    public void resetArm(){
//        robot.servoIntake.setPosition(robot.INTAKE_CLAW_OPEN);
        setServoIntakeAnglePosition(robot.INTAKE_ANGLE_GRAB_SPECIMEN);
        setArmAnglePower(0.4);
        setArmAnglePosition(robot.ARM_ANGLE_RESET);
        setArmLengthPower(1);
        setArmLengthPosition(robot.ARM_LENGTH_RESET);
    }

    public void grabSample(){
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_OPEN);
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_GRAB_SAMPLE);
        setArmLengthPower(1);
        setArmLengthPosition(robot.ARM_LENGTH_SAFE);
        setServoIntakeAnglePosition(robot.ARM_ANGLE_GRAB_SAMPLE);
    }


    public void scoreSample(){
        robot.servoIntake.setPosition(robot.INTAKE_CLAW_OPEN);
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_SCORE_HIGH_BASKET);
        setArmLengthPower(1);
        setArmLengthPosition(robot.ARM_LENGTH_SCORE_HIGH_BASKET);
    }

    public void autoGrabSample(){
        robot.servoIntake.setPosition(robot.INTAKE_ANGLE_GRAB_SAMPLE);
        setArmAnglePower(0.3);
        setArmAnglePosition(robot.ARM_ANGLE_AUTO_GRAB_SAMPLE);
        opMode.sleep(500);
        closeClaw();
        opMode.sleep(250);
        setArmAnglePosition(robot.ARM_ANGLE_SCORE_HIGH_BASKET);
    }

    public void autoGrabSample2(){
        robot.servoIntake.setPosition(robot.INTAKE_ANGLE_GRAB_SAMPLE);
        setArmAnglePower(0.3);
        setArmAnglePosition(robot.ARM_ANGLE_AUTO_GRAB_SAMPLE2);
        opMode.sleep(400);
        autoCloseClaw();
        opMode.sleep(250);
        setArmAnglePosition(robot.ARM_ANGLE_SCORE_HIGH_BASKET);
    }

    public void climb(){
        setArmAnglePower(1);
        setArmAnglePosition(robot.ARM_ANGLE_GRAB_BAR);
        robot.motorRR.setPower(-0.1);
        robot.motorLR.setPower(-0.1);
        robot.motorLF.setPower(-0.1);
        robot.motorRF.setPower(-0.1);
        opMode.sleep(1500);
        robot.motorRR.setPower(1);
        robot.motorLR.setPower(1);
        robot.motorLF.setPower(1);
        robot.motorRF.setPower(1);
        setArmAnglePosition(robot.ARM_ANGLE_CLIMB);
        opMode.sleep(500);
        halt();
    }

    public void halt(){
        robot.motorRR.setPower(0);
        robot.motorLR.setPower(0);
        robot.motorLF.setPower(0);
        robot.motorRF.setPower(0);
    }

    public void initArmAngle(){
        boolean armNotSet = true;

        setArmAnglePower(1);
        this.armAnglePosition = 0;
        while(armNotSet){
            this.armAnglePosition = this.armAnglePosition  + 1;
            setArmAnglePosition(this.armAnglePosition);
            if(robot.motorArmAngle.getCurrent(CurrentUnit.AMPS) > 2){
                armNotSet = false;
                robot.motorArmAngle.setPower(0);
                robot.motorArmAngle.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                robot.motorArmAngle.setTargetPosition(0);
                robot.motorArmAngle.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
                robot.motorArmAngle.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        }
    }

    public void tensionRetractionString(){
        boolean notRetracted = true;

        setArmLengthPower(1);
        setArmAnglePosition(0);
        while(notRetracted){
            this.armLengthPosition = this.armLengthPosition-25;
            robot.motorArmLength.setTargetPosition(this.armLengthPosition);
            if(robot.motorArmLength.getCurrent(CurrentUnit.AMPS) > 2){
                notRetracted = false;
                robot.motorArmLength.setPower(0);
                robot.motorArmLength.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                robot.motorArmLength.setTargetPosition(0);
                robot.motorArmLength.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
                robot.motorArmLength.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            opMode.telemetry.addData(">", "##################################<");
            opMode.telemetry.addData(">", "                                  <");
            opMode.telemetry.addData(">", "PREPARING HARDWARE - DO NOT START <");
            opMode.telemetry.addData(">", "                                  <");
            opMode.telemetry.addData(">", "##################################<");
            opMode.telemetry.addData("Target Position = ", this.armLengthPosition);
            opMode.telemetry.addData("motorArmLength Position = ", robot.motorArmLength.getCurrentPosition());
            opMode.telemetry.addData("motorArmLength Current Draw = ", robot.motorArmLength.getCurrent(CurrentUnit.AMPS));

            opMode.telemetry.update();
        }
    }

    public void resetAngleArm(){
        boolean notReset = true;

        setArmAnglePower(1);
        while(notReset){
            this.armAnglePosition = this.armAnglePosition+10;
            robot.motorArmAngle.setTargetPosition(this.armAnglePosition);
            if(robot.motorArmAngle.getCurrent(CurrentUnit.AMPS) > 3){
                notReset = false;
                robot.motorArmAngle.setPower(0);
                robot.motorArmAngle.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                robot.motorArmAngle.setTargetPosition(0);
                robot.motorArmAngle.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
                robot.motorArmAngle.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            opMode.telemetry.addData(">", "#####################################<");
            opMode.telemetry.addData(">", "                                     <");
            opMode.telemetry.addData(">", "PREPARING ANGLE MOTOR - DO NOT START <");
            opMode.telemetry.addData(">", "                                     <");
            opMode.telemetry.addData(">", "#####################################<");
            opMode.telemetry.addData("Target Position = ", this.armAnglePosition);
            opMode.telemetry.addData("motorArmLength Position = ", robot.motorArmAngle.getCurrentPosition());
            opMode.telemetry.addData("motorArmLength Current Draw = ", robot.motorArmAngle.getCurrent(CurrentUnit.AMPS));

            opMode.telemetry.update();
        }
    }

}   // close the RRMechOps class