package lennycheng.com.speedometerodometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


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

//    double previousXAcceleration;    //we use this because when the sensor is read, it gives the current acceleration and the
//    //time elapsed since previous reading but to be more accurate to use the previous value acceleration value. Kind of like
//    //Euler's method. It would be better to average the two accelerations together for even more accuracy.
//    double previousYAcceleration;
//    double previousZAcceleration;


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

        startTime = SystemClock.elapsedRealtime();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

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

