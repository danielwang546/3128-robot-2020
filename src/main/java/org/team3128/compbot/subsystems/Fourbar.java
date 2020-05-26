package org.team3128.compbot.subsystems;

import org.team3128.common.utility.Log;
import org.team3128.compbot.calibration.CmdFourbarFF.FourBarState;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotController;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team3128.common.generics.Threaded;
import org.team3128.common.hardware.motor.LazyCANSparkMax;
import org.team3128.common.hardware.motor.LazyTalonFX;

public class Fourbar extends Constants {

	FourBarControlMode controlMode;
    FourBarState state;
    private double joystickThreshold = 0.1;
    public TalonSRX MAIN_MOTOR;

    public Fourbar (){
        configMotors();
    }
      
    private void configMotors() {
        MAIN_MOTOR = new TalonSRX(Constants.FourbarConstants.MAIN_MOTOR_ID);
    }

    public void setPower(double power){
        boolean isMoving;
        if (isMoving) {
            MAIN_MOTOR.set(ControlMode.PercentOutput, -power);
        } else {
            MAIN_MOTOR.set(ControlMode.PercentOutput, 0);

        }
    }

    public static Fourbar getInstance() {
        return instance;
    }

    public static void initialize(TalonSRX fourBarMotor, FourBarState state, DigitalInput limitSwitch, double ratio, double limitSwitchAngle, int maxVelocity) {
		instance = new Fourbar(fourBarMotor, state, limitSwitch, ratio, limitSwitchAngle, maxVelocity);


    public Fourbar(TalonSRX fourBarMotor, FourBarState state, DigitalInput limitSwitch, double ratio,
            double limitSwitchAngle, int maxVelocity){
                @Override
                public void update() {
                    // TODO Auto-generated method stub
                    
            }
               
    public static final Fourbar instance = new Fourbar();
    public LazyTalonFX FOURBAR_MOTOR_LEADER, FOURBAR_MOTOR_FOLLOWER;
    public DigitalInput LIMIT_SWITCH;
    public double setpoint;
    double current = 0;
    double error = 0;
    public double output = 0;
    double accumulator = 0;
    double prevError = 0;
    public FourbarState FOURBAR_STATE;
    boolean isZeroing = false;
    public int plateauCount = 0;
    }

    public enum FourbarState {
        VERTICAL(90 * Angle.DEGREES),

		INTAKE(-20 * Angle.DEGREES),
        LOW(80 * Angle.DEGREES), 
		MID(80 * Angle.DEGREES),
		HIGH(82 * Angle.DEGREES),
		
		HATCH_LOW(-60 * Angle.DEGREES),
        HATCH_HIGH(54 * Angle.DEGREES), 
        }



	public static void initialize(TalonSRX fourBarMotor, FourBarState state, DigitalInput limitSwitch, double ratio, double limitSwitchAngle, int maxVelocity) {
		Fourbar instance = new Fourbar(fourBarMotor, state, limitSwitch, ratio, limitSwitchAngle, maxVelocity);

}

// PID Loop outline
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
    }
}