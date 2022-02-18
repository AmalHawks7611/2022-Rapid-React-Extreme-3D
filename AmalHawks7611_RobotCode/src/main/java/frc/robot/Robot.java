package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import frc.robot.Tanjant;

public class Robot extends TimedRobot {
  private static Victor intake = new Victor(1);
  private static Victor palet = new Victor(2);
  private static Victor atis = new Victor(3);
  private static Victor sol_motor = new Victor(4);
  private static Victor sag_motor = new Victor(5);
  private static DifferentialDrive robot_drive = new DifferentialDrive(sol_motor, sag_motor);
  private static Joystick joystick = new Joystick(0);
  private static NetworkTableInstance photon = NetworkTableInstance.create();
  private static NetworkTable table = photon.getTable("photonvision").getSubTable("camera");
  private double atis_boolean = 0;
  private int intake_boolean = 0;
  private static double palet_double = 0.0;
  private static double arcade_x_auto = 0.0;
  private static double arcade_y_auto = 0.0;
  private static double arcade_x_teleop = 0.0;
  private static double arcade_y_teleop = 0.0;
  private static Timer timer = new Timer();
  private static double palet_status = 0.0;
  private static double atis_status = 0.0;

  private static double target_distance = 1.50;
  
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
    //e = olimpiyat_zubeyir_teoremi(getPitch(), 0.67, 2.5, 40);
    //double yaw = getYaw();
    double pitch_teleop = getPitch();
    double range = olimpiyat_zubeyir_teoremi(pitch_teleop, 0.67, 2.5, 50);
    atis.set(1);
    align_robot(arcade_x_auto, arcade_y_auto);
    distance_set_auto(arcade_x_auto, arcade_y_auto, range, target_distance);
    robot_drive.arcadeDrive(arcade_x_auto, arcade_y_auto);
  }

  @Override
  public void teleopInit() {
    timer.reset();
  }

  @Override
  public void teleopPeriodic() {
    
    //robot_drive.arcadeDrive(joystick.getX() * 0.9, joystick.getY()* -1 * 0.9);
    //robot_drive.arcadeDrive(arcade_x_teleop, arcade_y_teleop);
    robot_drive.arcadeDrive(arcade_x_teleop, arcade_y_teleop, true);
    atis.set(atis_boolean);
    intake.set(intake_boolean);
    palet.set(palet_double);

    if(joystick.getRawButton(10)){
      align_robot(arcade_x_teleop, arcade_y_teleop);
    }
    else{
      arcade_y_teleop = joystick.getY() * -1;
      arcade_x_teleop = joystick.getZ();
    }
    if(joystick.getRawButton(7)){
      atis.set(-1);
    }

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
    robot_drive.curvatureDrive(arcade_x_teleop, arcade_y_teleop, true);
  }

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
  
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
  public static double olimpiyat_zubeyir_teoremi(double pitch, double cam_height, double target_height, double cam_pitch){
    pitch = Math.round(pitch);
    //double result = (max-min) * Tanjant.tan(90 - ((int)pitch) + 45);
    double result = (target_height - cam_height) / Tanjant.tan(((int)pitch) + (int)cam_pitch);
    return result;
  }
  public double mutlak_samet(double deger){
    if(deger > 0)
      return deger;
    else
      return deger * -1;
  }
  public static void distance_set_auto(double arcade_x, double arcade_y, double range, double target_distance){
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


  public static void align_robot(double arcade_x, double arcade_y){
    if(hasTarget()){
    double yaw_teleop = getYaw();
    if(yaw_teleop > 3){
      arcade_x_teleop = 0.4;
      arcade_y_teleop = 0.4;
    }
    else if(yaw_teleop < -3){
      arcade_x_teleop = -0.4;
      arcade_y_teleop = 0.4;
    }
    else{
      arcade_x_teleop = 0;
      arcade_y_teleop = 0;
    }
  }
  }

}
