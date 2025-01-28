package org.firstinspires.ftc.teamcode.Libs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import org.firstinspires.ftc.teamcode.Hardware.HWProfile;

import java.util.Locale;

public class DriveMecanumFTCLib {

    private final HWProfile robot;
    public LinearOpMode opMode;
    private double botHeading;

    FtcDashboard dashboard;


    /*
     * Constructor method
     */
    public DriveMecanumFTCLib(HWProfile myRobot, LinearOpMode myOpMode){
        robot = myRobot;
        opMode = myOpMode;

    }   // close DriveMecanum constructor Method



    /* #########################################################################################
       #########################################################################################
       ################################  DRIVE METHODS #########################################
       #########################################################################################
       #########################################################################################
     */

    /******************************************************************************************
     * Method:      ftclibDrive
     * Function:    Robot drives the direction of the heading, at the power provided,
     *              for the distance provided
     * Note:        This function is intended to work at 0, 90, 180, and -90 headings
     * Parameters:
     * @param heading   - Direction robot should drive
     * @param distance  - Distance in Inches to drive
     */


    /* #########################################################################################
       #########################################################################################
       ################################  MECHANISM METHODS #####################################
       #########################################################################################
       #########################################################################################
     */

    public void StrafeDrive(double drive, double turn, double strafe) {

        double leftPower    = -Range.clip(drive, -robot.MAX_DRIVING_POWER, robot.MAX_DRIVING_POWER);
        double rightPower   = -Range.clip(drive, -robot.MAX_DRIVING_POWER, robot.MAX_DRIVING_POWER);
        double strafePower = Range.clip(-strafe, -robot.MAX_DRIVING_POWER, robot.MAX_DRIVING_POWER);

        robot.motorLF.setPower(leftPower - turn + strafePower);
        robot.motorLR.setPower(leftPower - turn - strafePower);
        robot.motorRF.setPower(rightPower + turn - strafePower);
        robot.motorRR.setPower(rightPower + turn + strafePower);

    }

    public void fieldCentricDrive(double drive, double turn, double strafe) {


    }


    /******************************************************************************************
     * Method:  getZAngle()
     ******************************************************************************************/
    public double getZAngle(){
        return (-robot.imu.getAbsoluteHeading());
    }   // close getZAngle method

    /******************************************************************************************
     * Method:  getZAngleRadians()
     ******************************************************************************************/
    public double getZAngleRadians(){
        return (Math.toRadians(-robot.imu.getAbsoluteHeading()));
    }   // close getZAngle method


}   // close the class
