package lennycheng.com.speedometerodometer;

/**
 * Contains distance-related fields and methods
 * Equations: d2 = d1 + v1t + (at^2)/2 OR d2 = d1 + v2t - (at^2)/2   (we use the latter)
 */
public class Distance {

    private double currentTotalDistance;

    private double xDistance;
    private double yDistance;

    //updates and returns current distance along x-axis with the equation  d2 = d1 + v2t -  (at^2)/2, where acceleration and velocity are along the x-axis
    public double computeCurrentXDistance(double previousXAcceleration, double currentXVelocity, double deltaTime) {

        //convert deltaTime from milliseconds to seconds because our acceleration unit is per s^2
        deltaTime /= 1000;

//        Log.d("A", "previousXAcceleration: " + previousXAcceleration + ", currentXVelocity: " + currentXVelocity);

        xDistance = xDistance + currentXVelocity * deltaTime - (previousXAcceleration * Math.pow(deltaTime,2)) /2;

        return xDistance;
    }

    //updates and returns current distance along y-axis with the equation  d2 = d1 + v2t - (at^2)/2, where acceleration and velocity are along the y-axis
    public double computeCurrentYDistance(double previousYAcceleration, double currentYVelocity, double deltaTime) {

        //convert deltaTime from milliseconds to seconds because our acceleration unit is per s^2
        deltaTime /= 1000;

        yDistance = yDistance + currentYVelocity * deltaTime - (previousYAcceleration * Math.pow(deltaTime,2)) /2;

        return yDistance;
    }

    public double computeTotalDistance(double currentXDistance, double currentYDistance) {
        return currentTotalDistance = Math.sqrt( Math.pow(currentXDistance,2) + Math.pow(currentYDistance,2));
    }

}