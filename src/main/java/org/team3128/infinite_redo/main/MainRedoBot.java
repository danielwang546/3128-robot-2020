package org.team3128.infinite_redo.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.team3128.common.NarwhalRobot;
import org.team3128.common.generics.ThreadScheduler;

import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.POVValue;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import org.team3128.common.listener.controltypes.Button;
import org.team3128.common.listener.controltypes.POV;

import org.team3128.infinite_redo.subsystems.Constants;
import org.team3128.infinite_redo.subsystems.Elevator;
import org.team3128.infinite_redo.subsystems.Fourbar;
import org.team3128.infinite_redo.subsystems.Intake;
import org.team3128.infinite_redo.subsystems.Turret;
import org.team3128.infinite_redo.subsystems.Climber;
import org.team3128.infinite_redo.subsystems.Elevator.ElevatorState;
import org.team3128.infinite_redo.subsystems.Fourbar.FourbarState;
import org.team3128.infinite_redo.subsystems.Turret.TurretState;
import org.team3128.compbot.subsystems.FalconDrive;
import org.team3128.common.drive.DriveCommandRunning;

public class MainRedoBot extends NarwhalRobot {

    static FalconDrive drive = FalconDrive.getInstance();
    private DriveCommandRunning driveCmdRunning;

    ExecutorService executor = Executors.newFixedThreadPool(4); 
    ThreadScheduler scheduler = new ThreadScheduler();

    public Elevator elevator = Elevator.getInstance();
    public Fourbar fourbar = Fourbar.getInstance();
    public Intake intake = Intake.getInstance();
    public Turret turret = Turret.getInstance();
    public Climber climber = Climber.getInstance();

    public Joystick joystickRight, joystickLeft;
    public ListenerManager listenerLeft, listenerRight;

    @Override
    protected void constructHardware() {

        joystickLeft = new Joystick(0);
        listenerLeft = new ListenerManager(joystickLeft);
        addListenerManager(listenerLeft);

        joystickRight = new Joystick(1);
        listenerRight = new ListenerManager(joystickRight);
        addListenerManager(listenerRight);
        
        scheduler.schedule(drive, executor);
        scheduler.schedule(elevator, executor);
        scheduler.schedule(fourbar, executor);
        scheduler.schedule(turret, executor);

        drive.resetGyro();
    }

    @Override
    protected void setupListeners() {
        listenerRight.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
        listenerRight.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
        listenerRight.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");

        listenerRight.addMultiListener(() -> {
            if (driveCmdRunning.isRunning) {
                double horiz = -0.5 * listenerRight.getAxis("MoveTurn"); //0.7
                double vert = -1.0 * listenerRight.getAxis("MoveForwards");
                double throttle = -1.0 * listenerRight.getAxis("Throttle");

                drive.arcadeDrive(horiz, vert, throttle, true);
            }
        }, "MoveTurn", "MoveForwards", "Throttle");
        
        listenerRight.nameControl(new Button(3), "Zero");
        listenerRight.nameControl(new Button(4), "Intake");
        listenerRight.nameControl(new Button(6), "Outtake");
        listenerRight.nameControl(new POV(0), "IntakulatorControl");

        listenerLeft.nameControl(ControllerExtreme3D.TRIGGER, "Climb");
        listenerLeft.nameControl(new Button(7), "HighGoal");
        listenerLeft.nameControl(new Button(9), "Intaking");
        listenerLeft.nameControl(new Button(11), "LowGoal");
        listenerLeft.nameControl(new Button(8), "Turret0");
        listenerLeft.nameControl(new Button(10), "Turret90");
        listenerLeft.nameControl(new Button(12), "Turret180");

        listenerRight.addButtonDownListener("Zero", () -> {
            elevator.setState(ElevatorState.ZEROED);
            fourbar.FOURBAR_STATE = FourbarState.VERTICAL;
            turret.setState(TurretState.ZEROED);
        });

        listenerRight.addButtonDownListener("Intake", () -> {
            intake.turnMotorsIn();
        });

        listenerRight.addButtonUpListener("Intake", () -> {
            intake.turnMotorsOff();
        });

        listenerRight.addButtonDownListener("Outtake", () -> {
            intake.turnMotorsOut();
        });

        listenerRight.addButtonUpListener("Outtake", () -> {
            intake.turnMotorsOff();
        });

        listenerRight.addListener("IntakulatorControl", (POVValue pov) -> {
            switch (pov.getDirectionValue()) {
                case 8:
                case 7:
                case 1:
                    intake.moveRollerUp();
                    break;
                case 3:
                case 4:
                case 5:
                    intake.moveRollerDown();
                    break;
                case 0:
                    intake.stopRoller();
                    break;
                default:
                    break;
            }
        });

        listenerLeft.addButtonDownListener("Climb", () -> {
            climber.setisClimbing(true);
            climber.setPower(Constants.ClimberConstants.CLIMB_POWER);
        });
        listenerLeft.addButtonUpListener("Climb", () -> {
            climber.setisClimbing(false);
            climber.setPower(0);
        });

        listenerLeft.addButtonDownListener("HighGoal", () -> {
            elevator.setState(ElevatorState.HIGH_GOAL);
            fourbar.FOURBAR_STATE = FourbarState.HIGH;
        });

        listenerLeft.addButtonDownListener("Intaking", () -> {
            elevator.setState(ElevatorState.INTAKING);
            fourbar.FOURBAR_STATE = FourbarState.INTAKE;
        });

        listenerLeft.addButtonDownListener("LowGoal", () -> {
            elevator.setState(ElevatorState.LOW_GOAL);
            fourbar.FOURBAR_STATE = FourbarState.LOW;
        });

        listenerLeft.addButtonDownListener("Turret0", () -> {
            turret.setState(TurretState.ZEROED);
        });

        listenerLeft.addButtonDownListener("Turret90", () -> {
            turret.setState(TurretState.SIDE);
        });

        listenerLeft.addButtonDownListener("Turret180", () -> {
            turret.setState(TurretState.BACKWARDS);
        });
    }

    @Override
    protected void teleopInit() {
        scheduler.resume();
    }

    @Override
    protected void teleopPeriodic() {
        scheduler.resume();
    }

    @Override
    protected void autonomousInit() {
        scheduler.resume();
    }

    @Override
    protected void disabledInit() {
        
    }

    @Override
    protected void updateDashboard() {

    }

    @Override
    public void endCompetition() {
        // TODO Auto-generated method stub
    }
    public static void main(String... args) {
        RobotBase.startRobot(MainRedoBot::new);
    }

}