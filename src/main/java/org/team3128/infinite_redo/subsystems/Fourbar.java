package org.team3128.infinite_redo.subsystems;

import org.team3128.common.utility.Log;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotController;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.team3128.common.generics.Threaded;
import org.team3128.common.hardware.motor.LazyTalonFX;

import org.team3128.common.utility.units.Angle;

public class Fourbar extends Threaded {
    
    public static final Fourbar instance = new Fourbar();
    public LazyTalonFX MAIN_MOTOR;

    public FourbarState FOURBAR_STATE;

    public double setpoint;
    public double current, error, prevError, accumulator, output;
    public int plateauCount;
    boolean isZeroing = false;

    public Fourbar() {
        configMotors();
    }
      
    private void configMotors() {
        MAIN_MOTOR = new LazyTalonFX(Constants.FourbarConstants.MAIN_MOTOR_ID);
    }

    public static Fourbar getInstance() {
        return instance;
    }

    public enum FourbarState {
        VERTICAL(90 * Angle.DEGREES),

		INTAKE(-20 * Angle.DEGREES),
        LOW(80 * Angle.DEGREES), 
		MID(80 * Angle.DEGREES),
		HIGH(82 * Angle.DEGREES);

        private double fourbarAngle;

        private FourbarState(double fourbarAngle) {
            this.fourbarAngle = fourbarAngle;
        }

    }

    @Override
    public void update() {

    }


}

// PID Loop outline
/*
current = getAngle();
error = setpoint - current;
accumulator += error * Constants.MechanismConstants.DT;
if (accumulator > Constants.ArmConstants.ARM_SATURATION_LIMIT) {
    accumulator = Constants.ArmConstants.ARM_SATURATION_LIMIT;
} else if (accumulator < -Constants.ArmConstants.ARM_SATURATION_LIMIT) {
     accumulator = -Constants.ArmConstants.ARM_SATURATION_LIMIT;
}
double kP_term = Constants.ArmConstants.ARM_PID.kP * error;
double kI_term = Constants.ArmConstants.ARM_PID.kI * accumulator;
double kD_term = Constants.ArmConstants.ARM_PID.kD * (error - prevError) / Constants.MechanismConstants.DT;
double voltage_output = armFeedForward(setpoint) + kP_term + kI_term + kD_term;
double voltage = RobotController.getBatteryVoltage();
output = voltage_output / voltage;
*/
