package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.Victor;

public class Robot extends TimedRobot {
  private static PWMSparkMax actuator = new PWMSparkMax(0);
  private static PWMSparkMax samet_actuator = new PWMSparkMax(6);
  private static Victor intake = new Victor(1);
  private static Victor palet = new Victor(2);
  private static Victor atis = new Victor(3);
  private static Victor sol_motor = new Victor(4);
  private static Victor sag_motor = new Victor(5);
  private static Victor target_led = new Victor(7);
  private static DifferentialDrive robot_drive = new DifferentialDrive(sol_motor, sag_motor);
  private static Joystick joystick = new Joystick(0);
  private static NetworkTableInstance photon = NetworkTableInstance.create();
  private static NetworkTable table = photon.getTable("photonvision").getSubTable("camera");
  private static double atis_double = 0;
  private static double intake_double = 0;
  private static double palet_double = 0.0;
  private static double actuator_double = 0;
  private static double samet_actuator_double = 1.0;
  private static double arcade_x = 0.0;
  private static double arcade_y = 0.0;
  private static Timer timer = new Timer();
  private static double CAM_HEIGHT = 0.67;
  private static double TARGET_HEIGHT = 2.75;
  private static double CAM_PITCH = 53.0;
  private static double TARGET_DISTANCE = 2.0;
  private double atis_devir = 0.9;
  private double direction_boolean = 1.0;
  private static boolean isAligned = false;
  
  @Override
  public void robotInit() {
    photon.startClient("10.76.11.102");
    UsbCamera camera = CameraServer.startAutomaticCapture(0);
    camera.setResolution(640, 480);
  }

  @Override
  public void robotPeriodic() {
    if(hasTarget()){
      target_led.set(1);
    }
    else{
      target_led.set(0);
    }
  }

  @Override
  public void disabledInit() {
    timer.reset();
    atis.set(0);
    palet.set(0);
  }
  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
  }

  @Override
  public void autonomousPeriodic() {
    robot_drive.arcadeDrive(arcade_x, arcade_y);
    double range = olimpiyat_zubeyir_teoremi(CAM_HEIGHT, TARGET_HEIGHT, CAM_PITCH);
    System.out.println(range);
    atis.set(atis_devir);
    arcade_y = -0.7;
    if(hasTarget()){
      arcade_y = 0;
    }
    align_robot();
    if(isAligned)
      distance_set_auto(range, TARGET_DISTANCE);
  }
  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    robot_drive.arcadeDrive(arcade_x, arcade_y);
    //atis.set(atis_double);
    intake.set(intake_double);
    palet.set(palet_double);
    actuator.set(actuator_double);
    samet_actuator.set(samet_actuator_double);
    System.out.println(joystick.getPOV());
    if(joystick.getRawButtonReleased(8)){
      if(actuator_double == 0){
        actuator_double = 1.0;
      }
      else if(actuator_double == 1.0){
        actuator_double = 0;
      }
    }
    if(joystick.getRawButtonReleased(7)){
      if(samet_actuator_double == 1.0){
        samet_actuator_double = -1.0;
      }
      else if(samet_actuator_double == -1.0){
        samet_actuator_double = 1.0;
      }
    }

    if(joystick.getRawButton(12)){
      align_robot();
    }
    else if(joystick.getPOV() <= 315 && joystick.getPOV() >= 215){
      arcade_x = -0.45;
      arcade_y = 0.3;
    }
    else if(joystick.getPOV() <= 135 && joystick.getPOV() >= 45){
      arcade_x = 0.45;
      arcade_y = 0.3;
    }
    else{
      if(slider_position() >= 0.5){
      arcade_y = joystick.getY() * -1 * direction_boolean;
      arcade_x = joystick.getZ() * 0.7 * direction_boolean;
      }
      else{
      arcade_y = joystick.getY() * -1 * 0.5 * direction_boolean;
      arcade_x = joystick.getZ() * 0.5;
      }
    }
    if(joystick.getRawButtonReleased(9)){
      if(direction_boolean == 1.0)
        direction_boolean = -1.0;
      else if(direction_boolean == -1.0)
        direction_boolean = 1.0;
      else
        direction_boolean = 1.0;
    }
    
    if(joystick.getRawButton(11)){
      atis.set(-1 * atis_devir);
    }

    else if(joystick.getRawButton(1)){
      atis.set(atis_devir);
    }
    else{
      atis.set(0);
    }
    /*if(joystick.getRawButtonReleased(1)){
      if(atis_double == 0){
        atis_double = atis_devir;
      }
      else if(atis_double == atis_devir){
        atis_double = 0;
      }
      else{
        atis_double = 0;
      }
    }*/
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
        if(timer.get() > 3.0){
          palet.set(-1);
        }

         
      }
      
    }
  }


  public static void align_robot(){
    if(hasTarget()){
    double yaw_teleop = getYaw();
    if(yaw_teleop > 1){
      isAligned = false;
      arcade_x = 0.45;
      arcade_y = 0.3;
    }
    else if(yaw_teleop < -1){
      isAligned = false;
      arcade_x = -0.45;
      arcade_y = 0.3;
    }
    else{
      isAligned = true;
      arcade_x = 0;
      arcade_y = 0;
    }
  }
  }
  public static double slider_position(){
    return joystick.getRawAxis(3) * -1;
  }

}
