package lennycheng.com.speedometerodometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Settings extends AppCompatActivity {

    Spinner spinner_fileType;
    Spinner spinner_velocityTimeGap;
    Spinner spinner_distanceTimeGap;
    Button btn_save;
    EditText et_accelerationFilter;
    EditText et_velocityFilter;

    String FILE_TYPE;
    String VELOCITY_TIME_GAP;
    String DISTANCE_TIME_GAP;
    String ACCELERATION_OFFSET;
    String VELOCITY_OFFSET;
    String PREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FILE_TYPE = getResources().getString(R.string.sp_FileType);
        VELOCITY_TIME_GAP = getResources().getString(R.string.sp_VelocityTimeGap);
        DISTANCE_TIME_GAP = getResources().getString(R.string.sp_DistanceTimeGap);
        ACCELERATION_OFFSET = getResources().getString(R.string.sp_AccelerationOffset);
        VELOCITY_OFFSET = getResources().getString(R.string.sp_VelocityOffset);
        PREFERENCES = getResources().getString(R.string.sp_Preferences);

        instantiateWidgets();
        setWidgetValues(); //uses values from Shared preferences
    }

    private void instantiateWidgets() {
        et_accelerationFilter = (EditText) findViewById(R.id.et_accelerationFilter);

        et_velocityFilter = (EditText) findViewById(R.id.et_velocityFilter);


        spinner_fileType = (Spinner) findViewById(R.id.spinner_fileType);
        spinner_velocityTimeGap = (Spinner) findViewById(R.id.spinner_velocityTimeGap);
        spinner_distanceTimeGap = (Spinner) findViewById(R.id.spinner_distanceTimeGap);

        populateSpinner(spinner_fileType, R.array.exportFileTypes);
        populateSpinner(spinner_velocityTimeGap, R.array.timeGapVelocity);
        populateSpinner(spinner_distanceTimeGap, R.array.timeGapDistance);

        btn_save = (Button) findViewById(R.id.btn_save);
    }


    private void setWidgetValues() {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
        float accelerationFilter = sharedPref.getFloat(ACCELERATION_OFFSET, (float)0.1);
        float velocityFilter = sharedPref.getFloat(VELOCITY_OFFSET, (float)0.1);
        String fileType = sharedPref.getString(FILE_TYPE, "xlsx");
        String velocityTimeGap = sharedPref.getString(VELOCITY_TIME_GAP,"10s");
        String distanceTimeGap = sharedPref.getString(DISTANCE_TIME_GAP,"10s");

        et_accelerationFilter.setText(Float.toString(accelerationFilter));
        et_velocityFilter.setText(Float.toString(velocityFilter));


        ArrayAdapter myAdap = (ArrayAdapter) spinner_fileType.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition = myAdap.getPosition(fileType);
        //set the default according to value
        spinner_fileType.setSelection(spinnerPosition);

        ArrayAdapter myAdap2 = (ArrayAdapter) spinner_velocityTimeGap.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition2 = myAdap2.getPosition(velocityTimeGap);
        //set the default according to value
        spinner_velocityTimeGap.setSelection(spinnerPosition2);

        ArrayAdapter myAdap3 = (ArrayAdapter) spinner_distanceTimeGap.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition3 = myAdap3.getPosition(distanceTimeGap);
        //set the default according to value
        spinner_distanceTimeGap.setSelection(spinnerPosition3);

    }

    private void populateSpinner(Spinner spinnerInstance, @ArrayRes int arrayResource) {

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResource, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerInstance.setAdapter(adapter);
    }


    //saves the user's settings and activates them
    public void save(View view) {

        //get spinner values
        String text_fileType = spinner_fileType.getSelectedItem().toString();
        String text_velocityTimeGap = spinner_velocityTimeGap.getSelectedItem().toString();
        String text_distanceTimeGap = spinner_distanceTimeGap.getSelectedItem().toString();

        //get editText values
        String text_accelerationFilter = et_accelerationFilter.getText().toString();
        String text_velocityFilter = et_velocityFilter.getText().toString();


        //save the setting values to shared preferencvse
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FILE_TYPE, text_fileType);
        editor.putString(VELOCITY_TIME_GAP, text_velocityTimeGap);
        editor.putString(DISTANCE_TIME_GAP, text_distanceTimeGap);
        editor.putFloat(ACCELERATION_OFFSET, Float.parseFloat(text_accelerationFilter));
        editor.putFloat(VELOCITY_OFFSET, Float.parseFloat(text_velocityFilter));

        editor.commit();
    }
}