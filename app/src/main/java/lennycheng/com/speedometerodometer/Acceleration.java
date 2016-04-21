package lennycheng.com.speedometerodometer;

/**
 * Helper methods for acceleration-related items
 */
public class Acceleration {

    private static double offset;  //has the value user indicated in shared preference. Used for sanitation

    public static void setOffset(double ofset) {
        offset = ofset;
    }

    public static double[] sanitizePreviousAccelerations(float[] sensorAccelerations) {
        //set offset acceleration values, which are on average between -0.3 to 0.3 to be 0.

        double[] previousAccelerations = new double[3];

        double lowOffset = offset * -1;
        double highOffset = offset;

        for (int i = 0; i < 3; i++) {

            if ((lowOffset <= sensorAccelerations[i]) && (sensorAccelerations[i] <= highOffset)) {
                previousAccelerations[i] = 0;
            } else {
                previousAccelerations[i] = sensorAccelerations[i];
            }
        }

        return previousAccelerations;
    }


    public static double[] calculateAverageAccelerations(float[] sensorAccelerations, double[] previousAccelerations) {

        double[] averageAccelerations = new double[3];

        double lowOffset = offset * -1;
        double highOffset = offset;

        for (int i = 0; i < 3; i++) {
            if ((lowOffset <= sensorAccelerations[i]) && (sensorAccelerations[i] <= highOffset)) {
                averageAccelerations[i] = 0;
            } else {
                averageAccelerations[i] =
                        (sensorAccelerations[i] + previousAccelerations[i]) / 2;
            }
        }

        return averageAccelerations;
    }
}

