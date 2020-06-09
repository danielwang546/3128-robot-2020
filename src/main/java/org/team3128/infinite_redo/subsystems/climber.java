package org.team3128.infinite_redo.subsystems;

import org.team3128.common.hardware.motor.LazyCANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class climber{
    
    public static final climber instance = new climber();

    public LazyCANSparkMax CLIMBER_MOTOR;
    public boolean isClimbing = false; 

    public climber () {
        configMotors();
    }

    private void configMotors() {
        CLIMBER_MOTOR = new LazyCANSparkMax(Constants.ClimberConstants.CLIMBER_MOTOR_ID,MotorType.kBrushless);
    }

    public void setPower(double power){
        if (isClimbing) {
            CLIMBER_MOTOR.set(-power);
        } else {
            CLIMBER_MOTOR.set(0);
        }

    public void setisClimbing (boolean value) {
        isClimbing = value;
    }

}