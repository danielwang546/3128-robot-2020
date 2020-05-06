package org.team3128.infinite_redo.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.team3128.common.generics.RobotConstants;
import org.team3128.common.utility.datatypes.PIDConstants;
import org.team3128.common.utility.units.Angle;
import org.team3128.common.utility.units.Length;
import org.team3128.compbot.subsystems.Constants.DriveConstants;

import edu.wpi.first.wpilibj.AnalogAccelerometer;

public class Constants extends RobotConstants{

    //Some of these constants will likely be empty/untuned as we won't be tuning these on an actual bot

    public static class GameConstants {
        public static final double visionTargetWidth = 28.0 * Length.in;
    }

    public static class MechanismConstants {
        public static final double ENCODER_RESOLUTION_PER_ROTATION = 2048;
        public static final double inchesToMeters = 0.0254;
        public static final double DT = 0.005; // time between update() method calls for mechanisms
    }

    public static class DriveConstants {
        //The FalconDrive class(which we're using) uses constants from compbot's Constants class, so I just copied them
        //Assuming that our drive train will be the same as Cheems' drive train
        //Also we can't test/tune constants
        public static final double kDriveInchesPerSecPerNUp100ms = (1000d / 1)
                                * (1 / MechanismConstants.ENCODER_RESOLUTION_PER_ROTATION)
                                * (Constants.DriveConstants.WHEEL_DIAMETER * Math.PI)
                                * Constants.DriveConstants.WHEEL_ROTATIONS_FOR_ONE_ENCODER_ROTATION; // a fairly basic relationship between tangential and rotational speed: NU/100ms * 1000ms/1second * 1/(encoder resolution) * CIRCUM * (relation between encoder rotations and wheel rotations) = in/s
        public static final double kDriveNuToInches = (1
                        / Constants.MechanismConstants.ENCODER_RESOLUTION_PER_ROTATION)
                        * Constants.DriveConstants.WHEEL_DIAMETER * Math.PI
                        * Constants.DriveConstants.WHEEL_ROTATIONS_FOR_ONE_ENCODER_ROTATION;

        public static final NeutralMode DRIVE_IDLE_MODE = NeutralMode.Brake;

        public static final double ENCODER_ROTATIONS_FOR_ONE_WHEEL_ROTATION = 72 / 8; // basically your gearing. Ask Mech for gear teeth number to gear teeth number ratio: 8.3333333

        public static final double WHEEL_ROTATIONS_FOR_ONE_ENCODER_ROTATION = 1
                        / Constants.DriveConstants.ENCODER_ROTATIONS_FOR_ONE_WHEEL_ROTATION;

        public static final int RIGHT_DRIVE_FRONT_ID = 3;
        public static final int RIGHT_DRIVE_MIDDLE_ID = 2;

        public static final int LEFT_DRIVE_FRONT_ID = 1;
        public static final int LEFT_DRIVE_MIDDLE_ID = 0;

        public static final int DRIVE_HIGH_SPEED = 140; // Empirical Max Linear Speed: TBD in/s

        public static final double WHEEL_DIAMETER = 3.55; // effective wheel diameter (measure first then tune this number until distances are accurate)

        public static final double LEFT_SPEEDSCALAR = 1.0; // purely for TELEOP drive (to make sure that when the drive pushes the joystick forward, both sides of the drivetrain are going ROUGHLY the same speed)
        public static final double RIGHT_SPEEDSCALAR = 1.0;// purely for TELEOP drive (to make sure that when the drive pushes the joystick forward, both sides of the drivetrain are going ROUGHLY the same speed)

        public static final double DRIVE_ACCEL_LIMIT = 120; // Ballpark estimates from mech (Be conservative unless you really need the quick auto paths)
        public static final double DRIVE_JERK_LIMIT = 2000; // Ballpark estimates (Be conservative)

        public static double K_AUTO_RIGHT_P = 0.00007; // 0.00065
        public static double K_AUTO_RIGHT_D = 0.000;
        public static double K_AUTO_RIGHT_F = 1 / 145.9150145782 * kDriveInchesPerSecPerNUp100ms; // 1/(consistent max vel of this side of drivetrain in/s) * conversion to NU/s
        public static double K_AUTO_LEFT_P = 0.00007;
        public static double K_AUTO_LEFT_D = 0.000; // 0.0001
        public static double K_AUTO_LEFT_F = 1 / 140.8705712261 * kDriveInchesPerSecPerNUp100ms; // 1/(consistent max vel of this side of drivetrain in/s) * conversion to NU/s
        public static final double K_HOLD_P = 4;

        public static final double kS = 0.178;
        public static final double kV = 0.055;//0.0516;
        public static final double kA = 0.00679;
        public static final double kP = 0.0013;
    }

    public static class VisionConstants {
        //Will be added once limelight positioning is decided
        public static final double topLLAngle = 0.0 * Length.in;
        public static final double topLLHeight = 0.0 * Length.in;
        public static final double topLLFrontDistance = 0.0 * Length.in;

        public static final double highGoalVerticalOffset = 0.0 * Angle.DEGREES;
        public static final double lowGoalVerticalOffset = 0.0 * Angle.DEGREES;
        public static final PIDConstants VisionPID = new PIDConstants(0, 0, 0, 0); 

        public static final double allowableVisionError = 1 * Angle.DEGREES;
    }

    public static class ClimberConstants {

    }
    
    public static class FourbarConstants {

    }

    public static class ElevatorConstants {
        public static final int ELEVATOR_MOTOR_LEADER_ID = 5;
        public static final int ELEVATOR_LIMIT_SWITCH_ID = 0;

        public static final NeutralMode ELEVATOR_NEUTRAL_MODE = NeutralMode.Brake;
        public static final NeutralMode ELEVATOR_DISABLED_NEUTRAL_MODE = NeutralMode.Coast;

        public static final double WINCH_GEARING = 68 / 12; //WINCH_GEARING rotations of the motor = 1 rotation of the winch
        //I have no idea if this number is right, have to ask mech
        public static final double WINCH_CIRCUMFERENCE = 2.375 * Math.PI; //In inches

        public static final PIDConstants ElevatorPID = new PIDConstants(0, 0, 0, 0); //Can't tune these PID constants
        public static final double MAX_ELEVATOR_HEIGHT = 100 * Length.in; //Unknown as of right now
        public static final double SATURATION_LIMIT = 2 / ElevatorPID.kI;
        public static final double ELEVATOR_HEIGHT_THRESHOLD = 1.5; //Allowable height(inches) we can go above the 
		public static final double ZEROING_POWER = -0.3;
        
    }

    //We may need more constants classes, these are just the ones I thought we would definitely need
}