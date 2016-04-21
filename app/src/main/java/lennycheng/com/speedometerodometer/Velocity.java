package lennycheng.com.speedometerodometer;

import android.util.Log;

/**
 * Contains velocity-related fields and methods
 * Equation : v2 = v1 + at
 */
public class Velocity {

    private static double offset;  //has the value user indicated in shared preference. Used for sanitation

    public static void setOffset(double ofset) {
        offset = ofset;
    }

    public static double[] computeCurrentVelocities(double[] currentVelocities, double[] averageAccelerations, double deltaTime) {

        //convert deltaTime from milliseconds to seconds because our acceleration unit is per s^2
        deltaTime /= 1000;

        double[] newCurrentVelocities = new double[3];

        for (int i = 0; i < 3; i++) {
            newCurrentVelocities[i] = computeCurrent_Velocity(currentVelocities[i], averageAccelerations[i], deltaTime);
        }

        return newCurrentVelocities;
    }

    public static double[] sanitizeCurrentVelocities(double[] currentVelocities) {

        double[] sanitizedVelocities = new double[3];

        double lowOffset = offset * -1;
        double highOffset = offset;

        for (int i = 0; i < 3; i++) {
            if ((lowOffset <= currentVelocities[i]) && (currentVelocities[i] <= highOffset)) {
                sanitizedVelocities[i] = 0;
            }
            else {
                sanitizedVelocities[i] = currentVelocities[i];
            }
        }

        return sanitizedVelocities;
    }

    //technically, since Distance class already contains the current x, y, z velocities, we don't need these parameters
    //but I feel it is more maintainable if we use local variables
    public static double computeTotalVelocity(double[] currentVelocities) {

        double currentTotalVelocity = 0;

        for (double velocity : currentVelocities) {
            currentTotalVelocity += Math.pow(velocity,2);
        }

        currentTotalVelocity = Math.sqrt(currentTotalVelocity);

        return currentTotalVelocity;
    }

    //updates and returns current velocity along x-axis with the equation v2 = v1 + at, where acceleration is along x axis
    private static double computeCurrent_Velocity(double current_Velocity, double average_Acceleration, double deltaTime) {
        return  current_Velocity + average_Acceleration * deltaTime;
    }
}
