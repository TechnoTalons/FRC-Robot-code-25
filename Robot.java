// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//We added the imports below this comment!
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.XboxController;


/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCenterCoral = "Center and Coral";
  private static final String kJustDrive = "Just Drive";
  private static final String kLeftAuto = "Left Auton";
  private static final string kRightAuto = "Right Auton";
  private static final string m_autoSelected = kCenterCoral;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private final SparkMax rollerMotor = new SparkMax(5, MotorType.kBrushed);

  private final SparkMax leftLeader = new SparkMax(1, MotorType.kBrushed);
  private final SparkMax leftFollower = new SparkMax(2, MotorType.kBrushed);
  private final SparkMax rightLeader = new SparkMax(3, MotorType.kBrushed);
  private final SparkMax rightFollower = new SparkMax(4, MotorType.kBrushed);
  
  private final DifferentialDrive myDrive = new DifferentialDrive(leftLeader, rightLeader);

  private final SparkMaxConfig driveConfig = new SparkMaxConfig();
  private final SparkMaxConfig rollerConfig = new SparkMaxConfig();

  private final Timer timer1 = new Timer();
  private final double ROLLER_EJECT_VALUE = .44;
  //usually set at .44 --> 44%
  private double driveSpeed = 1;
  private double rollerOut = 0;


  private final XboxController gamepad0 = new XboxController(0);
  private final XboxController gamepad1 = new XboxController(1);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Center and Coral", kCenterCoral);
    m_chooser.addOption("Just Drive", kJustDrive);
    m_chooser.addOption("Left Auto", kLeftAuto);
    m_chooser.addOption("Right Auto", kRightAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    driveConfig.smartCurrentLimit(60);
    driveConfig.voltageCompensation(12);
    
    driveConfig.follow(leftLeader);
    leftFollower.configure(driveConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
    driveConfig.follow(rightLeader);
    rightFollower.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    driveConfig.disableFollowerMode();
    rightLeader.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    //Go back to video to figure out what is going on with motors once we get this running :)
    //Line 66 needs to be added to which ever lead motor is reversed. 

    driveConfig.inverted(true);
    leftLeader.configure(driveConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    rollerConfig.smartCurrentLimit(60); 
    rollerConfig.voltageCompensation(10);
    rollerMotor.configure(rollerConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    timer1.start();

    CameraServer.startAutomaticCapture("Camera", 0); //id is what you set your camera to.
    }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected:" + m_autoSelected);
    timer1.restart();
  }

  /** This function is called periodically during autonomous. */
  
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCenterCoral:
        if (timer1.get() < 1.85) {//This is drive to the reef
          myDrive.tankDrive(.5, .5);
        } 
        else if (timer1.get() < 3){
        myDrive.tankDrive(0, 0);
        }  
        else if (timer1.get() < 5.3) {//This is to stop moving and spit out coral
          myDrive.tankDrive(0, 0);
          rollerMotor.set(ROLLER_EJECT_VALUE);
         }
      
        else {//End of auton- turn off everything. 
          myDrive.tankDrive(0, 0);
          rollerMotor.set(0);
        } 
        case kJustDrive:
        if (timer1.get() < 2.5) {
          myDrive.tankDrive(.5, .5);
          break;
          
        }
        case kLeftAuto:
        if (timer1.get () > 1) {//This is drive to position
          myDrive.tankDrive(.4, .4); 
        }
        else if (timer1.get() < 1){
          myDrive.tankDrive(0, 0);
        }
        else if (timer1.get() < 1){
          myDrive.leftLeader(1).leftFollower(1); //This should hopefully rotate the bot
        }
        else if (timer1.get() < 1.3){
          myDrive.leftLeader(0).leftFollower(0);
        }
        else if (timer1.get() < 1.3){ //bring bot to the reef
          myDrive.tankDrive(1);
        }
        else if (timer1.get() < 1.35){ //stops bot and ejects coral
          myDrive.tankDrive(0);
          rollerMotor.set(ROLLER_EJECT_VALUE);
        }
        else if (timer.get() < 1.37){ //moves bot to coral station
          rollerMoter.set(0);
          myDrive.tankDrive(-1);
        }
        else if (timer.get() < 1.42){ //bot stops and waits for human player
          myDrive.tankdrive(0);
        }
        else if (timer.get() < 1.48){ //drive to reef
          mydrive.tankdrive(1);
        }
        else if (timer.get() < 1.55)[ //eject second coral
          myDrive.tankdrive(0)
          rollerMotor.set(.5)
        ]
        else (timer.get() <2.5){ //bot shuts down until teleop
          rollerMotor.set(0)
        }
        case kDefaultAuto:
      
        break;

      }
      }
    }
  case kRightAuto:
  if (timer1.get() )

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic(){
    myDrive.arcadeDrive(-gamepad1.getLeftTriggerAxis()/driveSpeed, -gamepad1.getRightX()/driveSpeed);
    //myDrive.tankDrive(gamepad0.getLeftY(),gamepad0.getRightY());
   // Original code: myDrive.arcadeDrive(-gamepad1.getLeftTriggerAxis()/driveSpeed, -gamepad1.getRightX()/driveSpeed);

    if (gamepad1.getLeftBumperButton()==true){
      driveSpeed = 1.65;
      //For motor speed. Go down in value for increased speed. Original was set at 2. h
    }
    if (gamepad1.getRightBumperButton()){
      driveSpeed = 1;
    }
      
    if (gamepad0.getAButton()) {
      rollerOut = ROLLER_EJECT_VALUE;
      
    }
    else
      rollerOut = gamepad0.getRightTriggerAxis() - gamepad0.getLeftTriggerAxis();

    rollerMotor.set(rollerOut);

    }
   
    
    // The divide by driveSpeed is to slow down the motors without messing with gear ratio. 
    //Line 50 also needs to be deleted if you are gonna run full power.
    //If you need to flip? Add negative in front og the gamepad.
  



  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
