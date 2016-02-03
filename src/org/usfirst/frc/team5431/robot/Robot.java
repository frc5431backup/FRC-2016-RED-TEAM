
package org.usfirst.frc.team5431.robot;

import org.usfirst.frc.team5431.libs.DriveBase;
import org.usfirst.frc.team5431.libs.EncoderBase;
import org.usfirst.frc.team5431.libs.Intake;
import org.usfirst.frc.team5431.libs.PneumaticBase;
import org.usfirst.frc.team5431.libs.TurretBase;
import org.usfirst.frc.team5431.libs.Vision;
import org.usfirst.frc.team5431.map.OI;
import org.usfirst.frc.team5431.map.SensorMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is the code for red teams (There are two robots building and red is awesome)
 * Look under the libs package to see where all the main classes are split up
 * This Robot.java is just the main thread(s) that will be controlling those classes
 * Have your looksez
 */
public class Robot extends IterativeRobot {
	
	//Better than strings
	enum AutoTask{ AutoShoot, StandStill};
    AutoTask currentAuto;
    SendableChooser auton_select;
    private TurretBase turret;
    private DriveBase drive;
    private Intake intake;
    private OI oi;
    private Vision vision;
    private PneumaticBase pneumatic;
    public static DigitalInput boulderLimit;
	private EncoderBase encoder;
    private boolean runOnce = false; //Don't mess with please
    
    public static volatile double[] autoAimVals = {0, 0, 0}; //Make sure the other thread can see the vals
    public static volatile double 
    		onTarget = 0.7, //Value to shoot at target
    		offTarget = 0.2; //Value to idle flyWheels

    public void robotInit() {
    	runOnce = true;
    	
    	turret = new TurretBase();
    	intake=new Intake();
    	drive = new DriveBase();
    	oi = new OI(); //Joystick mapping
		boulderLimit = new DigitalInput(SensorMap.intakeLimit);
    	encoder = new EncoderBase();
    	pneumatic = new PneumaticBase();
		
       	intake.setSpeed(1);
        turret.setSpeed(0.7);
    	
        auton_select = new SendableChooser();
        auton_select.addDefault("AutoShoot Lowbar", AutoTask.AutoShoot);
        auton_select.addObject("StandStill", AutoTask.StandStill);
        
        pneumatic.startCompressor();
        
        SmartDashboard.putData("Auto choices", auton_select);
    }
    
    public void autonomousInit() {
    	currentAuto = (AutoTask) auton_select.getSelected();
 		SmartDashboard.putString("Auto Selected: ", currentAuto.toString());
    }
    
    public void lowbarMode() {
    	//Drive 15 feet
    	this.auto_driveStraight(156, 0.5, 0.05); //Distance (in), speed (0-1), curve(0-0.1)
    	
    }

    /**
     * This function is called periodically during autonomous. Effect changes based on the value of {@link #autoSelected}.
     */
    public void autonomousPeriodic() {
		vision.updateVals();

    	switch(currentAuto) {
    		case AutoShoot:
    			if(runOnce) {
    				this.lowbarMode();
    				runOnce = false;
    			}
    			break;
    		case StandStill:
    			default:
    				Timer.delay(0.01);
    				break;
    	}
    }

    /*
     * Run once blah... blah... blah... you get it
     */
    
    public void teleopInit() {
    	runOnce = true;
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//intake.checkInput(oi);
    	//turret.checkInput(oi);
    	drive.checkInput(oi);
    }
    
    public void disabledPeriodic() {
    	runOnce = true;
    }
    
    private void auto_driveStraight(double distance, double speed, double curve) {
    	encoder.resetDrive();
    	
    	double left = 0;
    	double right = 0;
    	
    	while(((left = encoder.LeftDistance()) < distance) && ((right = encoder.RightDistance()) < distance)) {
    		if(left < right) {
    			drive.drive(speed+curve, speed-curve);
    		} else if(right > left) {
    			drive.drive(speed-curve, speed+curve);
    		} else {
    			drive.drive(speed, speed);
    		}
    	}
    	drive.drive(0, 0);
    }
    
    public void testPeriodic() {}
}

class VisionThread extends Thread {

	private Vision vision;
	private static double onTarget, offTarget;
	
	public VisionThread() {
		vision = new Vision();
		onTarget = Robot.onTarget;
		offTarget = Robot.offTarget;
	}
	
	@Override
	public void run() {
		while(true)
		{
			vision.updateVals();
			Robot.autoAimVals = vision.updateSmartDash(onTarget, offTarget);
			Timer.delay(0.01);
		}
		
	}
	
}
