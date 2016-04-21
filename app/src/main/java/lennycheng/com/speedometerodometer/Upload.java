package lennycheng.com.speedometerodometer;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import lennycheng.com.speedometerodometer.R;


//this class calls Drive
public class Upload extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
    GoogleApiClient mGoogleApiClient;


    ArrayList<Double> averageVelocityTimeGapValues;
    ArrayList<Double> totalDistanceTimeGapValues;

    String fileType;    //stores the mime to support different applications
    int velocityTimeGap;     //represents the time difference for displaying velocity
    int distanceTimeGap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
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

        averageVelocityTimeGapValues = (ArrayList<Double>) getIntent().getSerializableExtra("averageVelocityTimeGapValues");
        totalDistanceTimeGapValues = (ArrayList<Double>) getIntent().getSerializableExtra("totalDistanceTimeGapValues");

        Log.d("SIZE", Integer.toString(averageVelocityTimeGapValues.size()));

        fileType = getIntent().getExtras().getString("fileType");
        velocityTimeGap = getIntent().getExtras().getInt("velocityTimeGapConstant");
        distanceTimeGap = getIntent().getExtras().getInt("distanceTimeGapConstant");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private String getMime(String fileType) {
        switch (fileType) {
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "csv":
                return "text/plain";
            default:
                throw new RuntimeException("in getMime(). Default case thrown");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();


        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d("onActivityResult", "Inside onActivityResult");
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                }
                finish();
                break;
        }
    }

    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    Log.d("CallbackonResult", "Inside onResult. THis is the callback");
                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setMimeType(getMime(fileType)).setTitle("Velocity and Distance Data").build();

                    DriveContents driveContents = result.getDriveContents();
                    OutputStream outputStream = driveContents.getOutputStream();
                    Writer writer = new OutputStreamWriter(outputStream);

                        if (fileType.equals("xls") ||  fileType.equals("xlsx")) {
                            writeExcel(writer);
                        } else {
                            writeCsv(writer);
                        }


                    ;
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            // .setInitialDriveContents(driveContents)
                            .setInitialDriveContents(result.getDriveContents())
                            .build(mGoogleApiClient);
                    try {
                        startIntentSenderForResult(
                                intentSender, 1, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.w("Unable to send intent", e);
                    }
                }
            };

    private void writeCsv(Writer writer) {
        try {
            writer.write("Current Velocity Data (m/s) - Time between each data is " + Integer.toString(velocityTimeGap/5) + "s");
            writer.write("\n");

            for (double data: averageVelocityTimeGapValues) {
                writer.write(Double.toString(data));
                writer.write(",");
            }

            writer.write("\n\n");
            writer.write("Total Distance Data (m) - Time between each data is " + Integer.toString(distanceTimeGap/5) + "s");
            writer.write("\n");

            for (double data: totalDistanceTimeGapValues) {
                writer.write(Double.toString(data));
                writer.write(",");
            }

            writer.close();

        } catch (IOException e) {
            Log.e("onResult", "IO Exception when writing");
        }
    }

    private void writeExcel(Writer writer) {
        try {
            writer.write("Velocity Data - Time between each data is " + Integer.toString(velocityTimeGap/5) + "s");
            writer.write("\n");

            for (double data: averageVelocityTimeGapValues) {
                writer.write(Double.toString(data));
                writer.write("\n");
            }

            writer.write("Distance Data - Time between each data is " + Integer.toString(distanceTimeGap/5) + "s");
            writer.write("\n");

            for (double data: totalDistanceTimeGapValues) {
                writer.write(Double.toString(data));
                writer.write("\n");
            }

            writer.close();

        } catch (IOException e) {
            Log.e("onResult", "IO Exception when writing");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("onConnected", "Connection made");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("onConnectionSuspended", "Connection suspended");
    }
}
