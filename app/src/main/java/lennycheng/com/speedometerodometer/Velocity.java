package lennycheng.com.speedometerodometer;

import android.util.Log;

/**
 * Contains velocity-related fields and methods
 * Equation : v2 = v1 + at
 */
public class Velocity {

    private double currentTotalVelocity;

    private double currentXVelocity;
    private double currentYVelocity;



    public double getCurrentXVelocity() {
        return currentXVelocity;
    }
    public double getCurrentYVelocity() { return currentYVelocity; }


    //updates and returns current velocity along x-axis with the equation v2 = v1 + at, where acceleration is along x axis
    public double computeCurrentXVelocity(double averageXAcceleration, double deltaTime) {

        //convert deltaTime from milliseconds to seconds because our acceleration unit is per s^2
        deltaTime /= 1000;

        currentXVelocity = currentXVelocity + averageXAcceleration * deltaTime;

        Log.d("C", "averageXAcceleration: " + averageXAcceleration + ", currentXVelocity: " + currentXVelocity);


        return currentXVelocity;
    }

    //updates and returns current velocity along y-axis with the equation v2 = v1 + at, where acceleration is along y axis
    public double computeCurrentYVelocity(double averageYAcceleration, double deltaTime) {

        //convert deltaTime from milliseconds to seconds because our acceleration unit is per s^2
        deltaTime /= 1000;

        currentYVelocity = currentYVelocity + averageYAcceleration * deltaTime;
        Log.d("C", "averageYAcceleration: " + averageYAcceleration + ", currentYVelocity: " + currentYVelocity);

        return currentXVelocity;
    }


    private double computeCurrentTotalVelocity() {
        Log.d("H", Double.toString(currentTotalVelocity));
        currentTotalVelocity = Math.sqrt( Math.pow(currentXVelocity,2) + Math.pow(currentYVelocity,2));
        return currentTotalVelocity;
    }
}
