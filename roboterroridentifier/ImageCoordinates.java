package roboterroridentifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class that contains coordinates of all actuators for each angle of the robot.
 * You can find out what each coordiante should be by clicking on the actuator
 * and viewing the Last clicked point" coordinates after clicking on the center
 * of the actuator on the robot image. All coordinates should be in the format
 * Arrays.asList(X, Y).
 */
public class ImageCoordinates {
    /**
     * Method that populates a hashmap with keys being actuator names and values
     * representing coordinate pairs.
     * 
     * @param mName   -> The name of the viewing angle. Used in order to populate
     *                the hashmap with appropriate coordinates depending on the
     *                position of the actuator (X, Y) for the viewing angle.
     * @param aCoords -> The hashmap to populate.
     */
    public static void setupCoordinates(final String mName, final HashMap<String, List<Integer>> aCoords) {
        switch (mName) {
        case "Drivetrain":
            aCoords.put("Left Master Falcon", Arrays.asList(148, 304));
            aCoords.put("Right Master Falcon", Arrays.asList(353, 304));
            aCoords.put("Left Slave Falcon", Arrays.asList(148, 343));
            aCoords.put("Right Slave Falcon", Arrays.asList(353, 343));
            break;
        case "Hopper":
            aCoords.put("Left Hopper Motor", Arrays.asList(45, 278));
            aCoords.put("Right Hopper Motor", Arrays.asList(452, 278));
            break;
        case "Intake":
            aCoords.put("Left Intake Motor", Arrays.asList(77, 207));
            aCoords.put("Right Intake Motor", Arrays.asList(419, 189));
            aCoords.put("Left Deploy Piston", Arrays.asList(35, 412));
            aCoords.put("Right Deploy Piston", Arrays.asList(464, 412));
            break;
        case "Shooter/Climb":
            aCoords.put("Left Winch Motor", Arrays.asList(128, 116));
            aCoords.put("Right Winch Motor", Arrays.asList(431, 116));
            aCoords.put("Index Motor", Arrays.asList(351, 268));
            aCoords.put("Left Shooter Motor", Arrays.asList(210, 304));
            aCoords.put("Right Shooter Motor", Arrays.asList(355, 304));
            aCoords.put("Slide Motor", Arrays.asList(235, 472));
            aCoords.put("Limelight", Arrays.asList(280,55));
            break;
        }
    }
}