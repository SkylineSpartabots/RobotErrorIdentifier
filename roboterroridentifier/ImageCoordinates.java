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
        case "Top View":
            aCoords.put("Left Master", Arrays.asList(179, 294));
            aCoords.put("Left Slave", Arrays.asList(179, 352));
            aCoords.put("Right Master", Arrays.asList(459, 295));
            aCoords.put("Right Slave", Arrays.asList(456, 358));
            aCoords.put("Limelight Problem", Arrays.asList(317, 96));
            aCoords.put("Intake Master", Arrays.asList(126, 648));
            aCoords.put("Intake Slave", Arrays.asList(515, 648));
            aCoords.put("Shooter Master", Arrays.asList(227, 100));
            aCoords.put("Shooter Slave", Arrays.asList(410, 100));
            break;
        case "Angle View":
            aCoords.put("Left Master", Arrays.asList(189, 444));
            aCoords.put("Left Slave", Arrays.asList(189, 487));
            aCoords.put("Right Master", Arrays.asList(452, 445));
            aCoords.put("Right Slave", Arrays.asList(452, 485));
            aCoords.put("Intake Master", Arrays.asList(145, 588));
            aCoords.put("Intake Slave", Arrays.asList(503, 588));
            aCoords.put("Shooter Master", Arrays.asList(228, 135));
            aCoords.put("Shooter Slave", Arrays.asList(403, 135));
            aCoords.put("Top Piston", Arrays.asList(115, 281));
            aCoords.put("Side Left Piston", Arrays.asList(90, 461));
            aCoords.put("Side Right Piston", Arrays.asList(555, 461));
            break;
        case "Isometric View":
            aCoords.put("Left Master", Arrays.asList(182, 344));
            aCoords.put("Left Slave", Arrays.asList(186, 386));
            aCoords.put("Right Master", Arrays.asList(336, 416));
            aCoords.put("Right Slave", Arrays.asList(338, 456));
            aCoords.put("Intake Master", Arrays.asList(294, 542));
            aCoords.put("Intake Slave", Arrays.asList(503, 636));
            aCoords.put("Shooter Master", Arrays.asList(355, 117));
            aCoords.put("Limelight Problem", Arrays.asList(428, 114));
            break;
        }
    }
}