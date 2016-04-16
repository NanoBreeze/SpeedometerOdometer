package lennycheng.com.speedometerodometer;

/**
 * Contains distance-related fields and methods
 * Equations: d2 = d1 + v1t + (at^2)/2 OR d2 = d1 + v2t - (at^2)/2   (we use the latter)
 */
public class Distance {

    public static double[] computeDistances(double[] distances, double[] averageAccelerations, double[] currentVelocities, double deltaTime) {

        //convert deltaTime from milliseconds to seconds because our acceleration unit is per s^2
        deltaTime /= 1000;

        double[] newDistances = new double[3];

        for (int i = 0; i < 3; i++) {
            newDistances[i] = compute_Distance(distances[i], averageAccelerations[i], currentVelocities[i], deltaTime);
        }

        return newDistances;
    }

    public static double computeTotalDistance(double[] distances) {
        double totalDistance = 0;

        for (double distance : distances) {
            totalDistance += Math.pow(distance,2);
        }

        totalDistance = Math.sqrt(totalDistance);

        return totalDistance;
    }

    //updates and returns current distance along x-axis with the equation  d2 = d1 + v2t -  (at^2)/2, where acceleration and velocity are along the x-axis
    //_ represents x, y, or z. Borrowed form Haskell
    private static double compute_Distance(double _distance, double average_Acceleration, double current_Velocity, double deltaTime) {

        return _distance + Math.abs(current_Velocity * deltaTime - (average_Acceleration * Math.pow(deltaTime,2)) /2);
    }

}