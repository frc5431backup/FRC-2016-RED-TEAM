package org.usfirst.frc.team5431.libs;

import org.usfirst.frc.team5431.map.OI;
import org.usfirst.frc.team5431.map.MotorMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * Class that handles tank drive.
 * 
 * @author Team 5431
 * */
public class DriveBase {
	
	private CANTalon rearleft, frontleft, rearright, frontright;
	
	private RobotDrive drive;
	/**
	 * Default constructor
	 * 
	 * @see #DriveBase(boolean b)
	 * */
	public DriveBase()
	{
		this(false);
	}
	/**
	 * Construtor with an option to have brakemode enabled
	 * 
	 * @param brakeMode Whether to enable brakemode on the {@linkplain CANTalon motors}
	 * */
	public DriveBase(boolean brakeMode)
	{
		this.rearleft = new CANTalon(MotorMap.RearLeft);
		this.frontleft = new CANTalon(MotorMap.FrontLeft);
		this.rearright = new CANTalon(MotorMap.RearRight);
		this.frontright = new CANTalon(MotorMap.FrontRight);
		
		this.rearleft.enable();
		this.frontleft.enable();
		this.rearright.enable();
		this.frontright.enable();
		
		this.rearleft.clearStickyFaults();
		this.frontleft.clearStickyFaults();
		this.rearright.clearStickyFaults();
		this.frontright.clearStickyFaults();
		
		this.rearleft.enableBrakeMode(brakeMode);
		this.frontleft.enableBrakeMode(brakeMode);
		this.frontright.enableBrakeMode(brakeMode);
		this.rearright.enableBrakeMode(brakeMode);
		
		this.drive = new RobotDrive(this.frontleft, this.rearleft, this.frontright, this.rearright);
	}
	
	/**
	 * Uses {@linkplain RobotDrive#tankDrive(double l, double r) tank drive} to drive.
	 * 
	 * @param left Value of the left joystick, where -1 is the lowest, 0 is the center, and 1 is the highest.
	 * @param right Value of the right joystick, where -1 is the lowest, 0 is the center, and 1 is the highest.
	 * */
	public void drive(double left, double right)
	{
		drive.tankDrive(left, right);
	}
	
	/**
	 * Checks input and drives based on an {@linkplain OI OI}
	 * @param map Current operator interface.
	 * */
	public void checkInput(OI map){
		this.drive(map.getDriveLeftYAxis(),map.getDriveRightYAxis());
	}
	
}
