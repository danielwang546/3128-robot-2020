package org.team3128.infinite_redo.subsystems;

import org.team3128.common.hardware.motor.LazyCANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Intake {

    public static final Intake instance = new Intake();

    public LazyCANSparkMax TOP_ROLLER_MOTOR, BOTTOM_ROLLER_MOTOR, MANIPULATOR_MOTOR;

    private Intake() {
        configMotors();
    }

    public static Intake getInstance() {
        return instance;
    }

    private void configMotors() {
        TOP_ROLLER_MOTOR = new LazyCANSparkMax(Constants.IntakeConstants.TOP_MOTOR_ID, MotorType.kBrushless);
        BOTTOM_ROLLER_MOTOR = new LazyCANSparkMax(Constants.IntakeConstants.BOTTOM_MOTOR_ID, MotorType.kBrushless);
        MANIPULATOR_MOTOR = new LazyCANSparkMax(Constants.IntakeConstants.TOP_MOTOR_ID, MotorType.kBrushless);
    }

    public void turnMotorsOn() {
        TOP_ROLLER_MOTOR.set(0.4);
        BOTTOM_ROLLER_MOTOR.set(-0.4);
    }

    public void turnMotorsOff() {
        TOP_ROLLER_MOTOR.set(0);
        BOTTOM_ROLLER_MOTOR.set(0);
    }

    public void moveRollerUp() {
        MANIPULATOR_MOTOR.set(0.4);
    }

    public void moveRollerDown() {
        MANIPULATOR_MOTOR.set(-0.4);
    }
}