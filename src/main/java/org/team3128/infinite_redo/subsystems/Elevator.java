package org.team3128.infinite_redo.subsystems;

import org.team3128.common.generics.Threaded;
import org.team3128.common.hardware.motor.LazyTalonFX;
import org.team3128.common.utility.units.Length;

public class Elevator extends Threaded{

    public static final Elevator instance = new Elevator();

    public LazyTalonFX winchMotorLeader, winchMotorFollower;

    public static enum ElevatorHeightState {
        ZEROED(0 * Length.in),
        LOW_GOAL(12 * Length.in), //Elevator goes up to allow fourbar to go down
        HIGH_GOAL(108 * Length.in), //The SmartBell high goal is 9 feet off the ground
        INTAKING(48 * Length.in); //Vision will be used to determine fourbar angle

        private double elevatorHeight;

        private ElevatorHeightState (double elevatorHeight){
            this.elevatorHeight = elevatorHeight;
        }
    }

    public static Elevator getInstance() {
        return instance;
    }

    private Elevator() {
        configMotors();
    }

    @Override
    public void update() {

    }

    private void configMotors() {
        winchMotorLeader = new LazyTalonFX(Constants.ElevatorContants.elevatorMotorLeaderID);
        winchMotorFollower = new LazyTalonFX(Constants.ElevatorContants.elevatorMotorFollowerID);
    }

}