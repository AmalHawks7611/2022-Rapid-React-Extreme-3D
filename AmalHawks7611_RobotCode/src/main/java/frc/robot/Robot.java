package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Tanjant;

public class Robot extends TimedRobot {
  private static Victor intake = new Victor(1);
  private static Victor palet = new Victor(2);
  private static Victor atis = new Victor(3);
  private static Victor sol_motor = new Victor(4);
  private static Victor sag_motor = new Victor(5);
  private static DifferentialDrive robot_drive = new DifferentialDrive(sol_motor, sag_motor);
  private static Joystick joystick = new Joystick(0);
  NetworkTableInstance photon = NetworkTableInstance.create();
  NetworkTable table = photon.getTable("photonvision").getSubTable("camera");
  private int atis_boolean = 0;
  private int intake_boolean = 0;
  private double palet_double = 0.0;
  private double arcade_x = 0.0;
  private double arcade_y = 0.0;
  private final Timer timer = new Timer();
  
  @Override
  public void robotInit() {
    photon.startClient("10.76.11.102");
  }

  @Override
  public void robotPeriodic() {
    
  }
  @Override
  public void autonomousInit() {
    timer.reset();
  }

  @Override
  public void autonomousPeriodic() {
    double range = olimpiyat_zubeyir_teoremi(getPitch(), 0.6, 0.9);
    double yaw = getYaw();
    atis.set(1);
    robot_drive.arcadeDrive(arcade_x, arcade_y);
      if(hasTarget()){
        System.out.println(range);
        if(yaw > 10){
          arcade_x = -0.45;
          arcade_y = 0.4;
        }
        else if(yaw < -10){
          arcade_x = 0.4;
          arcade_y = 0.4;
        }
        else{
          arcade_x = 0;
          if(range > 2.2){
            arcade_y = 0.4;
          }
          else if(range < 1.85){
            arcade_y = -0.4;
          }
          else{
            arcade_y = 0.0;
            timer.start();
            
            if(timer.get() < 1.0)
              palet.set(1);
            if(timer.get() > 1.0 && timer.get() < 1.4)
              palet.set(0);
            if(timer.get() > 2.0)
              palet.set(-1);
          }
        }
      }
      else
        robot_drive.arcadeDrive(0, 0);
  }

  @Override
  public void teleopInit() {
    timer.reset();
    timer.start();
  }

  @Override
  public void teleopPeriodic() {

    robot_drive.arcadeDrive(joystick.getX() * 0.9, joystick.getY()* -1 * 0.9);
    atis.set(atis_boolean);
    intake.set(intake_boolean);
    palet.set(palet_double);

    if(joystick.getRawButtonReleased(1)){
      if(atis_boolean == 0){
        atis_boolean = 1;
      }
      else if(atis_boolean == 1){
        atis_boolean = 0;
      }
      else{
        atis_boolean = 0;
      }
    }
    if(joystick.getRawButtonReleased(2)){
      if(intake_boolean == 0){
        intake_boolean = 1;
      }
      else if(intake_boolean == 1){
       intake_boolean = 0;
      }
      else{
        intake_boolean = 0;
      }
    }
    if(joystick.getRawButton(6)){
      palet_double = 1;
    }
    else if(joystick.getRawButton(5)){
      palet_double = -1;
    }
    else{
      palet_double = 0;
    }
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {
    robot_drive.arcadeDrive(0, 0.5);
  }

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
  
  public boolean hasTarget(){
    NetworkTableEntry result = table.getEntry("hasTarget");
    return result.getBoolean(false);
  }
  public double getYaw(){
    NetworkTableEntry result = table.getEntry("targetYaw");
    return result.getDouble(0);
  }
  public double getPitch(){
    NetworkTableEntry result = table.getEntry("targetPitch");
    return result.getDouble(0);
  }
  public double olimpiyat_zubeyir_teoremi(double pitch, double cam_height, double target_height){
    double max = Math.max(cam_height, target_height);
    double min = Math.min(cam_height, target_height);
    pitch = mutlak_samet(pitch);
    pitch = Math.round(pitch);
    double result = (max-min) * Tanjant.tan(90 - (int)pitch);
    return result;
  }
  public double mutlak_samet(double deger){
    if(deger > 0){
      return deger;
    }
    else{
      return deger * -1;
    }
  }



}
