package org.brickbot.ftc.examplecode;
import com.bylazar.configurables.annotations.Configurable;

@Configurable
public final class Constants {
    private Constants() {}
    @Configurable
    public static class MecanumDriveConstants {
        private MecanumDriveConstants() {}
        public static double kP = 0;
        public static double kD  = 0;
        public static double kF = 0.085;
        public static double kS = 0;
        public static double kStatic = 0.067;
    }
    @Configurable
    public static class TurretConstants {
        private TurretConstants() {}
        //Servo Limits
        public static double turretMinLimit = 0;
        public static double constantServoPosition = 0.5;
        public static double turretMaxLimit = 1;
    }
}