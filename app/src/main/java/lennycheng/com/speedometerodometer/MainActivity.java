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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


/* Kinematics equations
d2 = d1 + v1t + (at^2)/2        (calculating distance)
v2 = v1 + at                    (calculating velocity)
 */


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor accelerometer;

    //These textviews include the accelerometer's readings
    TextView tv_x;
    TextView tv_y;

    TextView tv_distanceX;
    TextView tv_distanceY;

    TextView tv_time;

    Velocity velocity;
    Distance distance;

    long startTime;

    double previousXAcceleration;    //we use this because when the sensor is read, it gives the current acceleration and the
    //time elapsed since previous reading but to be more accurate to use the previous value acceleration value. Kind of like
    //Euler's method. It would be better to average the two accelerations together for even more accuracy.
    double previousYAcceleration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        tv_x = (TextView) findViewById(R.id.tv_velocityX);
        tv_y = (TextView) findViewById(R.id.tv_velocityY);
        tv_distanceX = (TextView) findViewById(R.id.tv_distanceX);
        tv_distanceY = (TextView) findViewById(R.id.tv_distanceY);

        tv_time = (TextView) findViewById(R.id.tv_time);

        velocity = new Velocity();
        distance = new Distance();
        startTime = SystemClock.elapsedRealtime();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        if (id == R.id.action_settings) {
            return true;
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

        //compute velocity along the y axis
        double currentXVelocity = velocity.computeCurrentXVelocity(previousXAcceleration, deltaTime);
        double currentYVelocity = velocity.computeCurrentXVelocity(previousYAcceleration, deltaTime);

        double currentXDistance = distance.computeCurrentXDistance(previousXAcceleration, currentXVelocity, deltaTime);
        double currentYDistance = distance.computeCurrentXDistance(previousYAcceleration, currentYVelocity, deltaTime);


        //set offset acceleration values, which are on average between -0.05 to 0.05 to be 0.
        if ((-0.05 <= event.values[0]) && (event.values[0] <= 0.05)) {
            previousXAcceleration = 0;
        } else {
            previousXAcceleration = event.values[0];
        }

        if ((-0.05 <= event.values[1]) && (event.values[1] <= 0.05)) {
            previousYAcceleration = 0;
        } else {
            previousYAcceleration = event.values[1];
        }

        startTime = SystemClock.elapsedRealtime();

        tv_distanceX.setText(Double.toString(currentXDistance));
        tv_distanceY.setText(Double.toString(currentYDistance));

//        tv_time.setText(Double.toString(currentTotalVelocity));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
