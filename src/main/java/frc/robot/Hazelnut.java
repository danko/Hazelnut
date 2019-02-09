package frc.robot;

// Import the classes of objects you will use
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Ultrasonic2537; 

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Hazelnut extends  TimedRobot {
  // Create instances of each object
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private XboxController xbox;

  private Timer timer;

  private WPI_TalonSRX m_rearLeft;
  private WPI_TalonSRX m_rearRight;
  private DifferentialDrive m_drive;
  private final int LEFT_REAR_MOTOR = 0;
  private final int RIGHT_REAR_MOTOR = 1;

  private Ultrasonic2537 f_ultrasonic;
  private Ultrasonic2537 b_ultrasonic;
  private final int F_ULTRA_PING = 1;
  private final int F_ULTRA_ECHO = 0;
  private final int B_ULTRA_PING = 9;
  private final int B_ULTRA_ECHO = 8;
  private final double SAFE_DISTANCE = 15.0;

  private Encoder l_encoder;  
  private Encoder r_encoder; 
  private final int L_ENCODER_A = 3;
  private final int L_ENCODER_B = 4;
  private final int R_ENCODER_A = 5;
  private final int R_ENCODER_B = 6;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    // initialize the objects and connect them to their underlying hardware
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    xbox = new XboxController(0);

    timer = new Timer();

    m_rearLeft = new WPI_TalonSRX(LEFT_REAR_MOTOR);
    m_rearRight = new WPI_TalonSRX(RIGHT_REAR_MOTOR);
    m_drive = new DifferentialDrive(m_rearLeft, m_rearRight);

    f_ultrasonic = new Ultrasonic2537(F_ULTRA_PING, F_ULTRA_ECHO); // ping, echo
    b_ultrasonic = new Ultrasonic2537 (B_ULTRA_PING, B_ULTRA_ECHO); 
    f_ultrasonic.setEnabled(true);
    b_ultrasonic.setEnabled(true);
    f_ultrasonic.setAutomaticMode(true);
 
    CameraServer.getInstance().startAutomaticCapture();

    r_encoder = new Encoder(R_ENCODER_A, R_ENCODER_B);
    l_encoder = new Encoder(L_ENCODER_A, L_ENCODER_B, true); 
}

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    
    timer.reset();
    timer.start();
    l_encoder.reset(); 
    r_encoder.reset(); 
  }

  public void teleopInit() {
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override 
  public void autonomousPeriodic() {

    if (timer.get() < 1.0) {
      m_drive.tankDrive(0.3, 0.3);
    }
    else
    {
      m_drive. curvatureDrive(0.0, 0.0,false);
    }
    System.out.println("Left" + l_encoder.getRaw()); 
    System.out.println("Right" + r_encoder.getRaw()); 
 
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control. Drive robot using xbox controller joysticks
   * but stop if you get too close to an obstacle.
   */
  @Override
  public void teleopPeriodic() {

    // set speed from joystick y values
    double leftSpeed  = -0.5*xbox.getY(Hand.kLeft);
    double rightSpeed = -0.5*xbox.getY(Hand.kRight);

    // Stop if there's an obstacle in front of us and we are driving forward
    if (safetyStop(f_ultrasonic, SAFE_DISTANCE) && (leftSpeed > 0.0) && (rightSpeed > 0.0)) {
       m_drive.stopMotor();
    }  // Stop if there's an obstacle in back of us and we are driving backward
    else if (safetyStop(b_ultrasonic, SAFE_DISTANCE) && (leftSpeed < 0.0) && (rightSpeed < 0.0)) {
       m_drive.stopMotor();
    } 
    else {
      // otherwise, set motors according to joysticks
       m_drive.tankDrive(leftSpeed, rightSpeed);
    }
  }

    /**
   * This function is called to see if it's safe to continue driving forward or backward
   */
public boolean safetyStop(Ultrasonic2537 sensor, double distance){
  double f_range = sensor.getRangeInches();
  if (f_range < distance){
    return true; 
  }
  else {
    return false; 
  }
}

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    // LiveWindow.run();
  }
}