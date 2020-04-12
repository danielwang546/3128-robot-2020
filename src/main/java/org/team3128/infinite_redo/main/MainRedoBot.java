package org.team3128.infinite_redo.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.team3128.common.NarwhalRobot;
import org.team3128.common.generics.ThreadScheduler;

import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;

import org.team3128.infinite_redo.subsystems.Constants;
import org.team3128.compbot.subsystems.FalconDrive;
import org.team3128.common.drive.DriveCommandRunning;

public class MainRedoBot extends NarwhalRobot {

    static FalconDrive drive = FalconDrive.getInstance();
    private DriveCommandRunning driveCmdRunning;

    ExecutorService executor = Executors.newFixedThreadPool(0); //TODO: Change later once the number of threads used are known
    ThreadScheduler scheduler = new ThreadScheduler();

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
        //Empty because we won't be actually logging anything to SmartDashboard
    }

    @Override
    public void endCompetition() {
        // TODO Auto-generated method stub
    }
    public static void main(String... args) {
        RobotBase.startRobot(MainRedoBot::new);
    }

}