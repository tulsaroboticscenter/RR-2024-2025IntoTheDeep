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

package org.firstinspires.ftc.teamcode.OpModes.Debug;


import android.icu.lang.UCharacter;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.HWProfile;


@TeleOp(name="System Test", group="Test Mode")
@Disabled
public class SystemTest extends LinearOpMode {

    private final static HWProfile robot = new HWProfile();
    private final LinearOpMode opMode = this;

    @Override
    public void runOpMode() {
        double intakePosition = robot.INTAKE_CLAW_OPEN;
        double intakeAnglePosition = robot.INTAKE_ANGLE_INIT;
        double intakeTwistPosition = robot.INTAKE_TWIST_INIT;
        int armAnglePosition = 0;
        ElapsedTime bumperTimer = new ElapsedTime();
        ElapsedTime touchTimer = new ElapsedTime();
        ElapsedTime armAngleTimer = new ElapsedTime();

        robot.init(hardwareMap, true);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        bumperTimer.reset();
        touchTimer.reset();
        armAngleTimer.reset();

        while (opModeIsActive()) {
            if(gamepad1.right_bumper && bumperTimer.time() > 0.2){
                intakeAnglePosition = intakeAnglePosition + 0.02;
                bumperTimer.reset();
            } else if(gamepad1.left_bumper & bumperTimer.time() > 0.2){
                intakeAnglePosition = intakeAnglePosition - 0.02;
                bumperTimer.reset();
            }

            if(gamepad1.a && touchTimer.time() > 0.2){
                intakePosition = robot.INTAKE_CLAW_OPEN;
                touchTimer.reset();
            } else if(gamepad1.b && touchTimer.time() > 0.2){
                intakePosition = robot.INTAKE_CLAW_CLOSE;
                touchTimer.reset();
            }

            if(gamepad1.x && touchTimer.time() > 0.2){
                intakeTwistPosition = intakeTwistPosition + 0.02;
                touchTimer.reset();
            } else if(gamepad1.y && touchTimer.time() > 0.2){
                intakeTwistPosition = intakeTwistPosition - 0.02;
                touchTimer.reset();
            }

            if(gamepad1.right_trigger > 0 && armAngleTimer.time() > 0.05){
                armAnglePosition = armAnglePosition + 10;
                armAngleTimer.reset();
            } else if(gamepad1.left_trigger > 0 && armAngleTimer.time() > 0.05){
                armAnglePosition = armAnglePosition - 10;
                armAngleTimer.reset();
            }

            if(intakePosition > 1) intakePosition = 1;
            if(intakePosition < 0) intakePosition = 0;
            if(intakeAnglePosition > 1) intakeAnglePosition = 1;
            if(intakeAnglePosition < 0) intakeAnglePosition = 0;
            if(intakeTwistPosition > 1) intakeTwistPosition = 1;
            if(intakeTwistPosition < 0) intakeTwistPosition = 0;


            robot.motorArmAngle.setPower(1);
            robot.motorArmAngle.setTargetPosition(armAnglePosition);
            robot.servoIntake.setPosition(intakePosition);
            robot.servoIntakeAngle.setPosition(intakeAnglePosition);
            robot.servoTwist.setPosition(intakeTwistPosition);


            telemetry.addData("Intake Position = ", intakePosition);
            telemetry.addData("Intake Angle Position = ", intakeAnglePosition);
            telemetry.addData("intake Twist Position = ", intakeTwistPosition);
            telemetry.addData("Arm Angle Position = ", armAnglePosition);
            telemetry.update();

        }
    }
}
