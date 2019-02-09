package frc.robot;

// Import the classes of objects you will use
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Ultrasonic2537; 

import edu.wpi.first.wpilibj.livewindow.LiveWindow;

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
  private boolean    f_safetyStop;
  private Ultrasonic2537 f_ultrasonic;
  private Ultrasonic2537 b_ultrasonic;
  private Encoder l_encoder;  
  private Encoder r_encoder; 
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    // initialize the objects and connect them to their underlying hardware
    m_chooser.addDefault("Default Auto", kDefaultAuto);
    m_chooser.addObject("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    xbox = new XboxController(0);

    timer = new Timer();

    m_rearLeft = new WPI_TalonSRX(0);
    m_rearRight = new WPI_TalonSRX(1);
    m_drive = new DifferentialDrive(m_rearLeft, m_rearRight);

    f_ultrasonic = new Ultrasonic2537(1,0); // ping, echo
    b_ultrasonic = new Ultrasonic2537 (9,8); 
    f_ultrasonic.setAutomaticMode(true);
    f_ultrasonic.setEnabled(true);
 
    CameraServer.getInstance().startAutomaticCapture();

    r_encoder = new Encoder(3,4);
    l_encoder = new Encoder(5,6, true); 
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
    l_encoder.reset(); 
    r_encoder.reset(); 
    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    timer.reset();
    timer.start();
  }

  /*public void teleopInit() {

  }*/

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
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    // Use front-mounted ultrasonic sensor to stop robot
    // if it gets too close to an obstacle
    
    

    // Use controller joysticks to set drive speed, but
    // safety stop if too close to an obstacle
    double leftSpeed  = -0.5*xbox.getY(Hand.kLeft);
    double rightSpeed = -0.5*xbox.getY(Hand.kRight);

    // If there's an obstacle in front of us, don't
    // allow any more forward motion
    if (safetyStop(f_ultrasonic, 15.0) && 
        (leftSpeed > 0.0) && (rightSpeed > 0.0)) {
       m_drive.stopMotor();
    } else if (safetyStop(b_ultrasonic, 15.0) && (leftSpeed < 0.0) && (rightSpeed < 0.0)) {
       m_drive.stopMotor();
    } else {
      // otherwise, set motors according to joysticks
       m_drive.tankDrive(leftSpeed, rightSpeed);
    }
    //Timer.delay(0.01);
  }

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