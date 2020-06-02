package org.team3128.infinite_redo.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team3128.common.generics.Threaded;
import org.team3128.common.hardware.motor.LazyCANSparkMax;
import org.team3128.common.utility.Log;
import org.team3128.common.utility.RobotMath;
import org.team3128.common.utility.units.Angle;

import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.RobotController;

public class Turret extends Threaded {

    public static final Turret instance = new Turret();

    public LazyCANSparkMax turretMotorLeader;
    public LazyCANSparkMax turretMotorFollower;
    public TurretState turretState;

    public DigitalInput limitSwitch0, limitSwitch90, limitSwitch180;
    public CANEncoder encoder;
    
    public double setpoint, current, error, prevError, accumulator, output;
    public int plateauCount;

    private void configMotors() {
        turretMotorLeader = new LazyCANSparkMax(Constants.TurretConstants.TURRET_MOTOR_LEADER_ID, MotorType.kBrushless);
        turretMotorFollower = new LazyCANSparkMax(Constants.TurretConstants.TURRET_MOTOR_FOLLOWER_ID, MotorType.kBrushless);
    }

    private void configSensors() {
        limitSwitch0 = new DigitalInput(Constants.TurretConstants.LIMIT_SWITCH_0_ID);
        limitSwitch90 = new DigitalInput(Constants.TurretConstants.LIMIT_SWITCH_90_ID);
        limitSwitch180 = new DigitalInput(Constants.TurretConstants.LIMIT_SWITCH_180_ID);
    }

    public static enum TurretState {
        ZEROED(0 * Angle.DEGREES), 
        BACKWARDS(180 * Angle.DEGREES), 
        SIDE(90 * Angle.DEGREES); 

        private double turretAngle; //Height in inches

        private TurretState (double turretAngle){
            this.turretAngle = turretAngle;
        }
    }

    public static Turret getInstance() {
        return instance;
    }

    private Turret() {
        configMotors();
        configSensors();
        setState(TurretState.ZEROED);
    }

    public double getEncoderPos() {
        return encoder.getPosition(); //returns encoder position in rotations
    }

    public double getCurrentAngle() {
        if(limitSwitch0.get()) {
            return 0 * Angle.DEGREES;
        } else if(limitSwitch90.get()) {
            return 90 * Angle.DEGREES;
        } else if(limitSwitch180.get()) {
            return 180 * Angle.DEGREES;
        }

        return getEncoderPos() / Constants.TurretConstants.TURRET_GEARING * 360;
    }

    @Override
    public void update() {
        if (limitSwitch0.get()) {
            encoder.setPosition(0);
        } else if(limitSwitch90.get()) {
            encoder.setPosition(Constants.TurretConstants.TURRET_GEARING / 4);
        } else if(limitSwitch180.get()) {
            encoder.setPosition(Constants.TurretConstants.TURRET_GEARING / 2);
        }
        
        current = getCurrentAngle();
        error = setpoint - current;
        accumulator += error * Constants.MechanismConstants.DT;
        accumulator = RobotMath.clamp(accumulator, -Constants.TurretConstants.SATURATION_LIMIT, Constants.TurretConstants.SATURATION_LIMIT);

        double kP_term = Constants.TurretConstants.TurretPID.kP * error;
        double kI_term = Constants.TurretConstants.TurretPID.kI * accumulator;
        double kD_term = Constants.TurretConstants.TurretPID.kD * (error - prevError) / Constants.MechanismConstants.DT;

        double voltage_output = kP_term + kI_term + kD_term;
        double voltage = RobotController.getBatteryVoltage();

        output = voltage_output / voltage;
        if (output > 1) {
            Log.info("TURRET",
                    "WARNING: Tried to set power above available voltage! Saturation limit SHOULD take care of this");
            output = 1;
        } else if (output < -1) {
            Log.info("TURRET",
                    "WARNING: Tried to set power above available voltage! Saturation limit SHOULD take care of this ");
            output = -1;
        }

        if (Math.abs(error) < Constants.TurretConstants.TURRET_ANGLE_THRESHOLD) {
            plateauCount++;
        } else {
            plateauCount = 0;
        }

        if((setpoint == 0) && !limitSwitch0.get()) {
            output = Constants.TurretConstants.ZEROING_POWER;
            // Zeroing
        } else if((setpoint == 0 * Angle.DEGREES) && limitSwitch0.get()) {
            output = 0;
        } else if((setpoint == 90 * Angle.DEGREES) && limitSwitch90.get()) {
            output = 0;
        } else if((setpoint == 180 * Angle.DEGREES) && limitSwitch180.get()) {
            output = 0;
        }


        setMotorPowers(output);
        prevError = error;
    }

    public void setState(TurretState state) {
        turretState = state;
        setSetpoint(state.turretAngle);
    }

    public void setSetpoint(double newSetpoint) {
        setpoint = newSetpoint;
    }

    public void zero() {
        setState(TurretState.ZEROED);
    }

    public void setMotorPowers(double power) {
        turretMotorLeader.set(power);
        turretMotorFollower.set(power);
    }
}