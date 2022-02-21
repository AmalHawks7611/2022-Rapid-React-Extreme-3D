package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.Victor;

public class Robot extends TimedRobot {
  private static PWMSparkMax actuator = new PWMSparkMax(0);
  private static Victor intake = new Victor(1);
  private static Victor palet = new Victor(2);
  private static Victor atis = new Victor(3);
  private static Victor sol_motor = new Victor(4);
  private static Victor sag_motor = new Victor(5);
  private static DifferentialDrive robot_drive = new DifferentialDrive(sol_motor, sag_motor);
  private static Joystick joystick = new Joystick(0);
  private static NetworkTableInstance photon = NetworkTableInstance.create();
  private static NetworkTable table = photon.getTable("photonvision").getSubTable("camera");
  private double atis_double = 0;
  private int intake_double = 0;
  private static double palet_double = 0.0;
  private static double arcade_x = 0.0;
  private static double arcade_y = 0.0;
  private static Timer timer = new Timer();
  private static double CAM_HEIGHT = 0.67;
  private static double TARGET_HEIGHT = 2.5;
  private static double CAM_PITCH = 50.0;
  private static double TARGET_DISTANCE = 1.20;
  
  @Override
  public void robotInit() {
    photon.startClient("10.76.11.102");
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void disabledInit() {
    timer.reset();
  }
  @Override
  public void autonomousInit() {
    timer.reset();
  }

  @Override
  public void autonomousPeriodic() {
    double range = olimpiyat_zubeyir_teoremi(CAM_HEIGHT, TARGET_HEIGHT, CAM_PITCH);
    
    atis.set(1);
    align_robot();
    distance_set_auto(range, TARGET_DISTANCE);
  }

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    robot_drive.arcadeDrive(arcade_x, arcade_y, true);
    atis.set(atis_double);
    intake.set(intake_double);
    palet.set(palet_double);

    if(joystick.getRawButton(12)){
      actuator.set(1);
    }
    else{
      actuator.set(0);
    }

    if(joystick.getRawButton(10)){
      align_robot();
    }
    else{
      arcade_y = joystick.getY() * -1;
      arcade_x = joystick.getZ() * 0.7;
    }
    if(joystick.getRawButton(7)){
      atis.set(-1);
    }

    if(joystick.getRawButtonReleased(1)){
      if(atis_double == 0){
        atis_double = 1;
      }
      else if(atis_double == 1){
        atis_double = 0;
      }
      else{
        atis_double = 0;
      }
    }
    if(joystick.getRawButtonReleased(2)){
      if(intake_double == 0){
        intake_double = 1;
      }
      else if(intake_double == 1){
       intake_double = 0;
      }
      else{
        intake_double = 0;
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
  
  public static boolean hasTarget(){
    NetworkTableEntry result = table.getEntry("hasTarget");
    return result.getBoolean(false);
  }
  public static double getYaw(){
    NetworkTableEntry result = table.getEntry("targetYaw");
    return result.getDouble(0);
  }
  public static double getPitch(){
    NetworkTableEntry result = table.getEntry("targetPitch");
    return result.getDouble(0);
  }
  public static double olimpiyat_zubeyir_teoremi(double cam_height, double target_height, double cam_pitch){
    double pitch = getPitch();
    pitch = Math.round(pitch);
    double result = (target_height - cam_height) / Tangent.tan(((int)pitch) + (int)cam_pitch);
    return result;
  }
  public double mutlak_samet(double deger){
    if(deger > 0)
      return deger;
    else
      return deger * -1;
  }
  public static void distance_set_auto(double range, double target_distance){
    if(hasTarget()){
      if(range > (target_distance + 0.15)){
        arcade_y = 0.4;
      }
      else if(range < (target_distance - 0.15)){
        arcade_y = -0.4;
      }
      else{
        arcade_y = 0.0;
        timer.start();
        palet.set(1);
        if(timer.get() > 0.8)
          palet.set(0);
        if(timer.get() > 2.0)
          palet.set(-1);
      }
      
    }
  }


  public static void align_robot(){
    if(hasTarget()){
    double yaw_teleop = getYaw();
    if(yaw_teleop > 3){
      arcade_x = 0.4;
      arcade_y = 0.35;
    }
    else if(yaw_teleop < -3){
      arcade_x = -0.4;
      arcade_y = 0.35;
    }
    else{
      arcade_x = 0;
      arcade_y = 0;
    }
  }
  }

}
