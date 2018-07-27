/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3240.robot; //This Is The Main Robot Package. ~~This Is The Robot~~

import edu.wpi.first.wpilibj.AnalogInput; //Imports The AnalogInput Class
import edu.wpi.first.wpilibj.CameraServer; //Imports The CameraServer Class
import edu.wpi.first.wpilibj.Compressor; //Imports The Compressor Class
import edu.wpi.first.wpilibj.DriverStation; //Imports The DriverStation Class
import edu.wpi.first.wpilibj.IterativeRobot; //Imports The IterativeRobot Class
import edu.wpi.first.wpilibj.Joystick; //Imports The Joystick Class
import edu.wpi.first.wpilibj.Solenoid; //Imports The Solenoid Class
import edu.wpi.first.wpilibj.Spark; //Imports The Spark Class
import edu.wpi.first.wpilibj.Timer; //Imports The Timer Class
import edu.wpi.first.wpilibj.drive.DifferentialDrive; //Imports The DifferentialDrive Class
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; //Imports The SmartDashboard Class
import edu.wpi.first.wpilibj.SerialPort; //Imports The SerialPort Class
import edu.wpi.first.wpilibj.CameraServer;
import com.kauailabs.navx.frc.AHRS; //Imports The AHRS Gyro Class

public class Robot extends IterativeRobot {
	public static DifferentialDrive myDrive21, myDrive22, myDrive23; //Creates The 3 Differential Drives
	public Spark OneLeft, OneRight, TwoLeft, TwoRight, ThreeLeft, ThreeRight, Wrist, LiftLift; //Creates All Of The Motor Controllers
	public Joystick Controller, Controller2; //Creates The Controllers
	public Solenoid ShiftersIn, ShiftersOut, ArmUp, ArmDown, ClawOpen, ClawClose, LiftUp, LiftDown; //Creates All Of The Solenoids 
	public Compressor airCompressor; //Creates The Compressor Variable
	public static Timer forwardTimer; //Creates The Timer Variable
	private double AutoSpeed = 1.0; //Creates And Sets The AutoSpeed
	private double FullSpeed = .55; //Creates And Sets The Forward Speed
	private double TurnSpeed = 0.65; //Creates And Sets The Turn Speed
	private double DownSpeed = 0.15; //Creates And Sets The Wrist Hold Speed When Not Holding A Block
	private double UpSpeed = 0.2; //Creates And Sets The Wrist Hold Speed When Holding A Block
	public String gameData; //Creates The GameData Variable
	public String pressure; //Creates Pressure Variable 
	AHRS ahrs; //Creates The AHRS Variable For The Gyro
	public AnalogInput mAnalogInput; //Creates The AnalogInput Variable
	
	@Override
	public void robotInit() {
		/*vvvvvvvvvvvvvvv Coding For The Camera vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		CameraServer.getInstance().startAutomaticCapture(0);
		
		/*vvvvvvvvvvvvvvv Coding For The Nav-X Gyro vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        try 
        {
            ahrs = new AHRS(SerialPort.Port.kMXP); 
        } 
        catch (RuntimeException ex )
        {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        
        /*vvvvvvvvvvvvvvv Mapping Of The Controllers vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
      	Controller = new Joystick(0); 
      	Controller2 = new Joystick(1);
      	
      	/*vvvvvvvvvvvvvvv Mapping Of The Motor Controllers To The RoboRio vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/	
        OneLeft = new Spark(6);
        TwoLeft = new Spark(7); 
      	ThreeLeft = new Spark(8); 	
        OneRight = new Spark(3); 
        TwoRight = new Spark(4); 
        ThreeRight = new Spark(5);    
        Wrist = new Spark(0); 
        LiftLift = new Spark(1);
        
        /*vvvvvvvvvvvvvvv Mapping Of The Solenoids To The Ports On The PCM vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        ShiftersIn = new Solenoid(4);                 
		ShiftersOut = new Solenoid(5); 
		ArmUp = new Solenoid(0);
		ArmDown = new Solenoid(1);
		ClawOpen = new Solenoid(2);
		ClawClose = new Solenoid(3);
		LiftUp = new Solenoid(6);
		LiftDown = new Solenoid(7);	
		
		/*vvvvvvvvvvvvvvv Drive Grouping vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		myDrive21 = new DifferentialDrive(OneLeft, OneRight);
		myDrive22 = new DifferentialDrive(TwoLeft, TwoRight);
		myDrive23 = new DifferentialDrive(ThreeLeft, ThreeRight);
		
		/*vvvvvvvvvvvvvvv Air Compressor vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/		
		airCompressor = new Compressor(1);
		airCompressor.start();	
		/*vvvvvvvvvvvvvvv Receives Analog Input From Pressure Sensor vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		mAnalogInput = new AnalogInput(0);
	}
	
	@Override
	public void teleopPeriodic() {
		/*vvvvvvvvvvvvvvv Sends Pressure Output To DriverStationvvvvvvvvvvvvvvv ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		SmartDashboard.putString("DB/String 5", String.valueOf(250.0 * mAnalogInput.getVoltage() / 5.0 - 15.0));
		
		/*vvvvvvvvvvvvvvv Sets What Joystick Controls Which DriveTrain Sidevvvvvvvvvvvvvvv ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		myDrive21.arcadeDrive(Controller.getRawAxis(1), -Controller.getRawAxis(4), true);
		myDrive22.arcadeDrive(Controller.getRawAxis(1), -Controller.getRawAxis(4), true);
		myDrive23.arcadeDrive(Controller.getRawAxis(1), -Controller.getRawAxis(4), true);
		
		/*vvvvvvvvvvvvvvv Sets What Button Controls The Shiftersvvvvvvvvvvvvvvv ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		if(Controller.getRawButton(6)) {
			ShiftersIn.set(true);
			ShiftersOut.set(false);
		}
		else if(Controller.getRawButton(5)) {
			ShiftersIn.set(false);
			ShiftersOut.set(true);
		}
		else {
			ShiftersIn.set(false);
			ShiftersOut.set(false);
		}
		
		/*vvvvvvvvvvvvvvv Sets What Button Controls The Lift System vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		if(Controller.getRawButton(9)) {
			LiftUp.set(true);
			LiftDown.set(false);
		}
		else if(Controller.getRawButton(10)) {
			LiftUp.set(false);
			LiftDown.set(true);
		}
		else {
			LiftUp.set(false);
			LiftDown.set(false);
		}
		
		/*vvvvvvvvvvvvvvv Sets What Button Controls The Arm System vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		if(Controller2.getRawButton(5)) {
			ArmUp.set(true);
			ArmDown.set(false);
		}
		else if(Controller2.getRawButton(6)) {
			ArmUp.set(false);
			ArmDown.set(true);
		}
		else {
			ArmUp.set(false);
			ArmDown.set(false);
		}
		
		/*vvvvvvvvvvvvvvv Sets What Button Controls The Motor For The Lift System vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~*/
		if(Controller2.getRawButton(9)) {
			LiftLift.set(0);
		}
		else if(Controller2.getRawButton(10)) {
			LiftLift.set(-AutoSpeed);
		}
		else {
			LiftLift.set(0);
		}
		
		/*vvvvvvvvvvvvvvv Sets What Button Controls The Claw System vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		if(Controller.getRawButton(3) || Controller2.getRawButton(3)) {
			ClawOpen.set(true);
			ClawClose.set(false);
		}
		else if(Controller.getRawButton(1)|| Controller2.getRawButton(1)) {
			ClawOpen.set(false);
			ClawClose.set(true);
		}
		else {
			ClawOpen.set(false);
			ClawClose.set(false);
		}
		
		/*vvvvvvvvvvvvvvv Sets What Button Controls The Wrist System vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/		
		if(Controller.getRawButton(4) || Controller2.getRawButton(4)) {
			Wrist.set(AutoSpeed/2);
		}
		else if(Controller.getRawButton(2) || Controller2.getRawButton(2)) {
			Wrist.set(-AutoSpeed/2);
		}
		else if(Controller2.getRawButton(6))
		{
			Wrist.set(UpSpeed);
		}
		else {
			Wrist.set(DownSpeed);
		}
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	}
	
	@Override
	public void autonomousInit() 
	{
		/*vvvvvvvvvvvvvvv Initalizes And Starts A Timer vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	    forwardTimer = new Timer();
		forwardTimer.start();
		/*vvvvvvvvvvvvvvv Receives Data Sent By The Field And Interperates It vvvvvvvvvvvvvvv~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
	}
	
	@Override
	public void autonomousPeriodic() 
	{
		
		/*if(gameData.charAt(0) == 'R') {
			if(forwardTimer.get() < 1) {
				ShiftersIn.set(false);
				ShiftersOut.set(true);
			}
			else if(forwardTimer.get() < 2.5) {
			myDrive21.arcadeDrive(-FullSpeed, 0);
			myDrive22.arcadeDrive(-FullSpeed, 0);                 
			myDrive23.arcadeDrive(-FullSpeed, 0);
			}
			else if(forwardTimer.get() > 2.5) {
				myDrive21.arcadeDrive(0, 0);
				myDrive22.arcadeDrive(0, 0);                 
				myDrive23.arcadeDrive(0, 0);
			}
			
		}
		else if(gameData.charAt(0) == 'L') {
			if(forwardTimer.get() < 1) {
				ShiftersIn.set(false);
				ShiftersOut.set(true);
			}
			else if(forwardTimer.get() < 2.5) {
			myDrive21.arcadeDrive(-FullSpeed, 0);
			myDrive22.arcadeDrive(-FullSpeed, 0);                 
			myDrive23.arcadeDrive(-FullSpeed, 0);
			}
			else if(forwardTimer.get() > 2.5) {
				myDrive21.arcadeDrive(0, 0);
				myDrive22.arcadeDrive(0, 0);                 
				myDrive23.arcadeDrive(0, 0);
			}
		}
		else
		{
			if(forwardTimer.get() < 1) {
				ShiftersIn.set(false);
				ShiftersOut.set(true);
			}
			else if(forwardTimer.get() < 2.4) {
			myDrive21.arcadeDrive(-FullSpeed, 0);
			myDrive22.arcadeDrive(-FullSpeed, 0);                 
			myDrive23.arcadeDrive(-FullSpeed, 0);
			}
			else if(forwardTimer.get() > 2.4) {
				myDrive21.arcadeDrive(0, 0);
				myDrive22.arcadeDrive(0, 0);                 
				myDrive23.arcadeDrive(0, 0);
			}
		}*/
		if(gameData.charAt(0) == 'R')
		{
			if(forwardTimer.get() < 0.5) {
				ShiftersIn.set(false);
				ShiftersOut.set(true);
				Wrist.set(-AutoSpeed/1.7);
	
		
			}
			else if(forwardTimer.get() > 0.75 && forwardTimer.get() < 1.75) {
				Wrist.set(0);
				LiftLift.set(-AutoSpeed/1.5);
				
			}
			else if(forwardTimer.get() > 1.75 && forwardTimer.get() < 1.85) {
				LiftLift.set(0);
				
			}
			else if(forwardTimer.get() > 1.85 && forwardTimer.get() < 3.5) {
				LiftLift.set(0);
				ArmUp.set(false);
				ArmDown.set(true);
				Wrist.set(AutoSpeed/1.5);
				
			}
			else if(forwardTimer.get() > 3.5 && forwardTimer.get() < 3.6) {
				Wrist.set(UpSpeed);
				
			}
			else if(forwardTimer.get() > 3.6 && forwardTimer.get() < 4.5) {
				myDrive21.arcadeDrive(-FullSpeed, 0);
				myDrive22.arcadeDrive(-FullSpeed, 0);                 
				myDrive23.arcadeDrive(-FullSpeed, 0);
				
			}
			else if(forwardTimer.get() > 4.5 && forwardTimer.get() < 4.6) {
				myDrive21.arcadeDrive(0, 0);
				myDrive22.arcadeDrive(0, 0);
				myDrive23.arcadeDrive(0, 0);
				
			}
			else if(forwardTimer.get() > 4.6 && forwardTimer.get() < 5.5 ) {
				myDrive21.arcadeDrive(0, -TurnSpeed);
				myDrive22.arcadeDrive(0, -TurnSpeed);
				myDrive23.arcadeDrive(0, -TurnSpeed);
				
			}
			else if(forwardTimer.get() > 5.1 && forwardTimer.get() < 7.3 ) {
				myDrive21.arcadeDrive(-FullSpeed, 0);
				myDrive22.arcadeDrive(-FullSpeed, 0);
				myDrive23.arcadeDrive(-FullSpeed, 0);
				
			}
			else if(forwardTimer.get() > 7.3 && forwardTimer.get() < 8.4 ) {
				myDrive21.arcadeDrive(0, TurnSpeed);
				myDrive22.arcadeDrive(0, TurnSpeed);
				myDrive23.arcadeDrive(0, TurnSpeed);
				
			}
			else if(forwardTimer.get() > 8.4 && forwardTimer.get() < 9.5 ) {
				myDrive21.arcadeDrive(-FullSpeed, 0);
				myDrive22.arcadeDrive(-FullSpeed, 0);
				myDrive23.arcadeDrive(-FullSpeed, 0);
				
			}
			else if(forwardTimer.get() > 9.5 && forwardTimer.get() < 9.6) {
				Wrist.set(-AutoSpeed/1.7);
				ClawOpen.set(false);
				ClawClose.set(true);
				myDrive21.arcadeDrive(0, 0);
				myDrive22.arcadeDrive(0, 0);
				myDrive23.arcadeDrive(0, 0); 
				
			}
			else if(forwardTimer.get() > 9.9) {
				ClawOpen.set(true);
				ClawClose.set(false);
				Wrist.set(DownSpeed);
				
			}
		}
		else if (gameData.charAt(0) == 'L')
		{
			if(forwardTimer.get() < 0.5) {
				ShiftersIn.set(false);
				ShiftersOut.set(true);
				Wrist.set(-AutoSpeed/1.7);		
				
			}
			else if(forwardTimer.get() > 0.75 && forwardTimer.get() < 1.75) {
				Wrist.set(0);
				LiftLift.set(-AutoSpeed/1.5);
				
			}
			else if(forwardTimer.get() > 1.75 && forwardTimer.get() < 1.85) {
				LiftLift.set(0);
				
			}
			
			else if(forwardTimer.get() > 3.0 && forwardTimer.get() < 3.5) {
				LiftLift.set(0);
				ArmUp.set(false);
				ArmDown.set(true);
				Wrist.set(AutoSpeed/1.5);
				
			}
			else if(forwardTimer.get() > 3.5 && forwardTimer.get() < 3.6) {
				Wrist.set(UpSpeed);
				
			}
			else if(forwardTimer.get() > 3.6 && forwardTimer.get() < 4.5) {
				myDrive21.arcadeDrive(-FullSpeed, 0);
				myDrive22.arcadeDrive(-FullSpeed, 0);                 
				myDrive23.arcadeDrive(-FullSpeed, 0);
				
			}
			else if(forwardTimer.get() > 4.5 && forwardTimer.get() < 4.6) {
				myDrive21.arcadeDrive(0, 0);
				myDrive22.arcadeDrive(0, 0);
				myDrive23.arcadeDrive(0, 0);
				
			}
			else if(forwardTimer.get() > 4.6 && forwardTimer.get() < 5.4 ) {
				myDrive21.arcadeDrive(0, TurnSpeed);
				myDrive22.arcadeDrive(0, TurnSpeed);
				myDrive23.arcadeDrive(0, TurnSpeed);
				
			}
			else if(forwardTimer.get() > 5.4 && forwardTimer.get() < 7.5 ) {
				myDrive21.arcadeDrive(-FullSpeed, 0);
				myDrive22.arcadeDrive(-FullSpeed, 0);
				myDrive23.arcadeDrive(-FullSpeed, 0);
				
			}
			else if(forwardTimer.get() > 7.5 && forwardTimer.get() < 8.1 ) {
				myDrive21.arcadeDrive(0, -TurnSpeed);
				myDrive22.arcadeDrive(0, -TurnSpeed);
				myDrive23.arcadeDrive(0, -TurnSpeed);
				
			}
			else if(forwardTimer.get() > 8.1 && forwardTimer.get() < 8.3 ) {
				myDrive21.arcadeDrive(-FullSpeed, 0);
				myDrive22.arcadeDrive(-FullSpeed, 0);
				myDrive23.arcadeDrive(-FullSpeed, 0);
				
			}
			else if(forwardTimer.get() > 8.3 && forwardTimer.get() < 9.3) {
				Wrist.set(-AutoSpeed/1.7);
				ClawOpen.set(false);
				ClawClose.set(true);
				myDrive21.arcadeDrive(0, 0);
				myDrive22.arcadeDrive(0, 0);
				myDrive23.arcadeDrive(0, 0); 
				
			}
			else if(forwardTimer.get() > 9.3) {
				ClawOpen.set(true);
				ClawClose.set(false);
				Wrist.set(DownSpeed);
				
			}
		}
		else
		{
			
		}
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	}
		
}


