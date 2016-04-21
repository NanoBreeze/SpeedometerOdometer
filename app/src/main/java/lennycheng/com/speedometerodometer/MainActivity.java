package lennycheng.com.speedometerodometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/* Kinematics equations
d2 = d1 + v1t + (at^2)/2        (calculating distance)
v2 = v1 + at                    (calculating velocity)
 */


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor accelerometer;

    TextView tv_totalDistance;
    TextView tv_currentTotalVelocity;

    TextView tv_xVelocity;
    TextView tv_yVelocity;
    TextView tv_zVelocity;

    TextView tv_xDistance;
    TextView tv_yDistance;
    TextView tv_zDistance;

    ImageView iv_needle;


    long startTime;

    double[] previousAccelerations;
    double[] averageAccelerations;

    double[] currentVelocities;
    double[] distances;

    String FILE_TYPE;
    String VELOCITY_TIME_GAP;
     String DISTANCE_TIME_GAP;
    String ACCELERATION_OFFSET;
    String VELOCITY_OFFSET;
    String PREFERENCES;

    String fileType; //the type of file to export to

    //number of times the sensor is hit . Since sensor is handled
    //every 200ms, 5 hits equals 1s
    int velocityTimeGapCount;
    int distanceTimeGapCount;

    int velocityTimeGapConstant;    //This is the number of times the sensor must be hit to add velocity to List
    int distanceTimeGapConstant;

    List averageVelocityTimeGapValues;  //for storing the velocities per time gap time
    List totalDistanceTimeGapValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //find the instance of the textViews
        tv_totalDistance = (TextView) findViewById(R.id.tv_totalDistance);
        tv_currentTotalVelocity = (TextView) findViewById(R.id.tv_currentTotalVelocity);

        tv_xVelocity = (TextView) findViewById(R.id.tv_xVelocity);
        tv_yVelocity = (TextView) findViewById(R.id.tv_yVelocity);
        tv_zVelocity = (TextView) findViewById(R.id.tv_zVelocity);

        tv_xDistance = (TextView) findViewById(R.id.tv_xDistance);
        tv_yDistance = (TextView) findViewById(R.id.tv_yDistance);
        tv_zDistance = (TextView) findViewById(R.id.tv_zDistance);

        iv_needle = (ImageView) findViewById(R.id.iv_needle);

        previousAccelerations = new double[3];  //x, y, z
        averageAccelerations = new double[3];

        currentVelocities = new double[3];
        distances = new double[3];

        averageVelocityTimeGapValues = new ArrayList<Double>();
        totalDistanceTimeGapValues = new ArrayList<Double>();

        startTime = SystemClock.elapsedRealtime();
        Log.d("AHHHH", "onCreate() called");


         FILE_TYPE = getResources().getString(R.string.sp_FileType);
         VELOCITY_TIME_GAP = getResources().getString(R.string.sp_VelocityTimeGap);
         DISTANCE_TIME_GAP = getResources().getString(R.string.sp_DistanceTimeGap);
         ACCELERATION_OFFSET = getResources().getString(R.string.sp_AccelerationOffset);
         VELOCITY_OFFSET = getResources().getString(R.string.sp_VelocityOffset);
         PREFERENCES = getResources().getString(R.string.sp_Preferences);

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("AHHHH", "onStart() called");

    }

    @Override
    //register sensor
    //set the offset in Acceleration and Velocity, comes from shared preferences (which return default value if none is availalbe)
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        float accelerationOffset = sharedPref.getFloat(ACCELERATION_OFFSET, (float) 0.1);
        float velocityOffset = sharedPref.getFloat(VELOCITY_OFFSET, (float) 0.1);

        //change the acceleration and velocity filter to match this float value
        Acceleration.setOffset(accelerationOffset);
        Velocity.setOffset(velocityOffset);

        fileType = sharedPref.getString(FILE_TYPE, "xlsx");
        velocityTimeGapConstant = adjustForTimeGapCount(sharedPref.getString(VELOCITY_TIME_GAP, "10s"));
        distanceTimeGapConstant = adjustForTimeGapCount(sharedPref.getString(DISTANCE_TIME_GAP, "10s"));
    }

    //indicates the number of times the sensor, which is handled every 200ms, must be handled to record the time
    int adjustForTimeGapCount(String timeGap) {
        switch (timeGap) {
            case "1s":
                return 5;
            case "10s":
                return 50;
            case "30s":
                return 150;
            case "1min":
                return 300;
            case "2mins":
                return 600;
            case "5mins":
                return 1500;
            case "10mins":
                return 3000;
            case "15mins":
                return 4500;
            case "30mins":
                return 9000;
            default:
                throw new RuntimeException("adjustForTimeGapCount received an invalid value");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //go to settings activity
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        //upload data to Drive
        else {
            Intent intent = new Intent(this, Upload.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override

    //values[0] is x axis, values[1] is y axis, values[2] is z axis.
    //Acceleration is in m/s^2
    // we must remove offset before using value
    public void onSensorChanged(SensorEvent event) {

        //determines the time elapsed since the previous onSensorChanged was called and sets the startTime
        double deltaTime = SystemClock.elapsedRealtime() - startTime;

        averageAccelerations = Acceleration.calculateAverageAccelerations(event.values, previousAccelerations);
        previousAccelerations = Acceleration.sanitizePreviousAccelerations(event.values);


        currentVelocities = Velocity.computeCurrentVelocities(currentVelocities, averageAccelerations, deltaTime);
        currentVelocities = Velocity.sanitizeCurrentVelocities(currentVelocities);


        distances = Distance.computeDistances(distances, averageAccelerations, currentVelocities, deltaTime);

        startTime = SystemClock.elapsedRealtime();

        double currentTotalVelocity = Velocity.computeTotalVelocity(currentVelocities);
        displayCurrentVelocities(currentTotalVelocity, currentVelocities);

        double totalDistance = Distance.computeTotalDistance(distances);
        displayTotalDistances(totalDistance, distances);

        //add velocity and distance to data strucutre, later for exporting to Drive. If reached, add and set to 0, otherwise, increment
        if (velocityTimeGapCount == velocityTimeGapConstant) {
            averageVelocityTimeGapValues.add(currentTotalVelocity);
            velocityTimeGapCount = 0;
        } else {
            velocityTimeGapCount++;
        }

        if (distanceTimeGapCount == distanceTimeGapConstant) {
            totalDistanceTimeGapValues.add(totalDistance);
            distanceTimeGapCount = 0;
        } else {
            distanceTimeGapCount++;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //change accerlerations and current velocities to 0
    public void stopButton(View view) {
        for (int i = 0; i < 3; i++) {
            previousAccelerations[i] = 0;
            averageAccelerations[i] = 0;
            currentVelocities[i] = 0;
        }
    }


    //displays current velocity on activity
    private void displayCurrentVelocities(double currentTotalVelocity, double[] currentVelocities) {

        Log.d("Main", Double.toString(currentTotalVelocity));

        tv_currentTotalVelocity.setText(String.format("%.0f", currentTotalVelocity * 3.6));

        //if over 180, then point to 180
        if (currentTotalVelocity * 3.6 > 180) {
            iv_needle.setRotation(90);
        }
        //must be lower than 180
        else {
            iv_needle.setRotation((float) (currentTotalVelocity * 3.6 - 90));
        }

        tv_xVelocity.setText("x: " + String.format("%.1f", currentVelocities[0] * 3.6) + " km/h");
        tv_yVelocity.setText("y: " + String.format("%.1f", currentVelocities[1] * 3.6) + " km/h");
        tv_zVelocity.setText("z: " + String.format("%.1f", currentVelocities[2] * 3.6) + " km/h");
    }

    //displays the distance travelled on activity
    private void displayTotalDistances(double totalDistance, double[] distances) {
        tv_totalDistance.setText(String.format("%.0f", totalDistance));

        tv_xDistance.setText("x: " + String.format("%.1f", distances[0]) + " m");
        tv_yDistance.setText("y: " + String.format("%.1f", distances[1]) + " m");
        tv_zDistance.setText("z: " + String.format("%.1f", distances[2]) + " m");
    }
}

