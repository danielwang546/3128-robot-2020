package org.team3128.infinite_redo.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.team3128.common.generics.Threaded;
import org.team3128.common.hardware.motor.LazyTalonFX;
import org.team3128.common.utility.Log;
import org.team3128.common.utility.RobotMath;
import org.team3128.common.utility.units.Length;

import edu.wpi.first.wpilibj.DigitalInput;
import org.team3128.common.hardware.limelight.Limelight;
import org.team3128.common.hardware.limelight.LimelightKey;

import edu.wpi.first.wpilibj.RobotController;

public class Elevator extends Threaded{

    public static final Elevator instance = new Elevator();

    public LazyTalonFX winchMotorLeader;
    public DigitalInput limitSwitch;
    public ElevatorState elevatorState;
    public Limelight topLimelight;
    
    public double setpoint;
    public double current, error, prevError, accumulator, output;
    public int plateauCount;

    public boolean hadLLTarget = false;

    public static enum ElevatorState {
        ZEROED(0 * Length.in),
        LOW_GOAL(12 * Length.in), //Elevator goes up to allow fourbar to go down
        HIGH_GOAL(108 * Length.in), //The SmartBell high goal is 9 feet off the ground
        INTAKING(48 * Length.in); //Vision will be used to determine fourbar angle

        private double elevatorHeight; //Height in inches

        private ElevatorState (double elevatorHeight){
            this.elevatorHeight = elevatorHeight;
        }
    }

    public static Elevator getInstance() {
        return instance;
    }

    private Elevator() {
        configMotors();
        configSensors();
        setState(ElevatorState.ZEROED);
    }

    public double getEncoderPos() {
        return winchMotorLeader.getSelectedSensorPosition(0);
    }

    public double getCurrentHeight() {
        return (getEncoderPos() / Constants.MechanismConstants.ENCODER_RESOLUTION_PER_ROTATION) 
                * Constants.ElevatorConstants.WINCH_CIRCUMFERENCE; //I don't know if this math is right, but it should return elevator height in inches
    }

    @Override
    public void update() {
        if(topLimelight.hasValidTarget() != hadLLTarget) {
            prevError = 0;
            accumulator = 0;
            plateauCount = 0;
        }

        if(topLimelight.hasValidTarget()) {
            limelightPID();
        } else {
            encoderPositionPID();
        } 

        hadLLTarget = topLimelight.hasValidTarget();
    }
    
    private void encoderPositionPID() {
        setpoint = RobotMath.clamp(setpoint, 0, Constants.ElevatorConstants.MAX_ELEVATOR_HEIGHT);

        if (limitSwitch.get()) {
            winchMotorLeader.setSelectedSensorPosition(0);
        }
        
        current = getCurrentHeight();
        error = setpoint - current;
        accumulator += error * Constants.MechanismConstants.DT;
        accumulator = RobotMath.clamp(accumulator, -Constants.ElevatorConstants.SATURATION_LIMIT, Constants.ElevatorConstants.SATURATION_LIMIT);

        double kP_term = Constants.ElevatorConstants.ElevatorPID.kP * error;
        double kI_term = Constants.ElevatorConstants.ElevatorPID.kI * accumulator;
        double kD_term = Constants.ElevatorConstants.ElevatorPID.kD * (error - prevError) / Constants.MechanismConstants.DT;

        double voltage_output = elevatorFeedForward(setpoint) + kP_term + kI_term + kD_term;
        double voltage = RobotController.getBatteryVoltage();

        output = voltage_output / voltage;
        if (output > 1) {
            Log.info("ELEVATOR",
                    "WARNING: Tried to set power above available voltage! Saturation limit SHOULD take care of this");
            output = 1;
        } else if (output < -1) {
            Log.info("ELEVATOR",
                    "WARNING: Tried to set power above available voltage! Saturation limit SHOULD take care of this ");
            output = -1;
        }

        if (Math.abs(error) < Constants.ElevatorConstants.ELEVATOR_HEIGHT_THRESHOLD) {
            plateauCount++;
        } else {
            plateauCount = 0;
        }

        if((setpoint == 0) && !limitSwitch.get()) {
            output = Constants.ElevatorConstants.ZEROING_POWER;
            // Zeroing
        } else if((setpoint == 0) && limitSwitch.get()) {
            output = 0;
        }


        winchMotorLeader.set(ControlMode.PercentOutput, output);

        prevError = error;
    }

    private void limelightPID() {
        if (limitSwitch.get()) {
            winchMotorLeader.setSelectedSensorPosition(0);
        }

        current = topLimelight.getValue(LimelightKey.VERTICAL_OFFSET, 3);

        //ty is positive when the limelight is too low, so the error is the opposite of what if would be usually (target-current)
        if(elevatorState == ElevatorState.HIGH_GOAL) error = current - Constants.VisionConstants.highGoalVerticalOffset;
        if(elevatorState == ElevatorState.LOW_GOAL) error = current - Constants.VisionConstants.lowGoalVerticalOffset;
        accumulator += error * Constants.MechanismConstants.DT;
        accumulator = RobotMath.clamp(accumulator, -Constants.ElevatorConstants.SATURATION_LIMIT, Constants.ElevatorConstants.SATURATION_LIMIT);

        double kP_term = Constants.VisionConstants.VisionPID.kP * error;
        double kI_term = Constants.VisionConstants.VisionPID.kI * accumulator;
        double kD_term = Constants.VisionConstants.VisionPID.kD * (error - prevError) / Constants.MechanismConstants.DT;

        double voltage_output = elevatorFeedForward(setpoint) + kP_term + kI_term + kD_term;
        double voltage = RobotController.getBatteryVoltage();

        output = voltage_output / voltage;
        if (output > 1) {
            Log.info("ELEVATOR",
                    "WARNING: Tried to set power above available voltage! Saturation limit SHOULD take care of this");
            output = 1;
        } else if (output < -1) {
            Log.info("ELEVATOR",
                    "WARNING: Tried to set power above available voltage! Saturation limit SHOULD take care of this ");
            output = -1;
        }

        if (Math.abs(error) < Constants.VisionConstants.allowableVisionError) {
            plateauCount++;
        } else {
            plateauCount = 0;
        }

        if((setpoint == 0) && !limitSwitch.get()) {
            output = Constants.ElevatorConstants.ZEROING_POWER;
            // Zeroing
        } else if((setpoint == 0) && limitSwitch.get()) {
            output = 0;
        }

        winchMotorLeader.set(ControlMode.PercentOutput, output);

        prevError = error;
    }

    private void configMotors() {
        winchMotorLeader = new LazyTalonFX(Constants.ElevatorConstants.ELEVATOR_MOTOR_LEADER_ID);

        winchMotorLeader.setNeutralMode(Constants.ElevatorConstants.ELEVATOR_NEUTRAL_MODE);
        winchMotorLeader.setSelectedSensorPosition(0); //Starting at zeroed state
    }

    private void configSensors() {
        limitSwitch = new DigitalInput(Constants.ElevatorConstants.ELEVATOR_LIMIT_SWITCH_ID);
        topLimelight = new Limelight("limelight-c", Constants.VisionConstants.topLLAngle, Constants.VisionConstants.topLLHeight, Constants.VisionConstants.topLLFrontDistance, Constants.GameConstants.visionTargetWidth);
    }

    public void setState(ElevatorState state) {
        elevatorState = state;
        setSetpoint(state.elevatorHeight);
    }

    public void setSetpoint(double newSetpoint) {
        setpoint = newSetpoint;
    }

    public void zero() {
        setState(ElevatorState.ZEROED);
    }

    public double elevatorFeedForward(double setpoint) {
        return 0; //Would be tested, but we can't
    }
}