package lennycheng.com.speedometerodometer;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import lennycheng.com.speedometerodometer.R;


//this class calls Drive
public class Upload extends AppCompatActivity {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
//    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Drive.DriveApi.newDriveContents(mGoogleApiClient)
//                .setResultCallback(driveContentsCallback);
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        if (connectionResult.hasResolution()) {
//            try {
//                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
//            } catch (IntentSender.SendIntentException e) {
//                // Unable to resolve, message user appropriately
//            }
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        Log.d("onActivityResult", "Inside onActivityResult");
//        switch (requestCode) {
//            case RESOLVE_CONNECTION_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    mGoogleApiClient.connect();
//                }
//                break;
//            case 1:
//                if (resultCode == RESULT_OK) {
//                    DriveId driveId = (DriveId) data.getParcelableExtra(
//                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//                }
//                finish();
//                break;
//        }
//    }
//
//
//    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
//            new ResultCallback<DriveApi.DriveContentsResult>() {
//                @Override
//                public void onResult(DriveApi.DriveContentsResult result) {
//                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
//                            .setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").setTitle("Coolios").build();
//
//                    DriveContents driveContents = result.getDriveContents();
//                    OutputStream outputStream = driveContents.getOutputStream();
//                    Writer writer = new OutputStreamWriter(outputStream);
//                    try {
//                        writer.write("Hello World!\t");
//                        writer.write("Once\n");
//                        writer.write("Wow");
//
//                        writer.close();
//                    } catch (IOException e) {
//                        Log.e("onResult", "IO Exception when writing");
//                    }
//
//                    ;
//                    IntentSender intentSender = Drive.DriveApi
//                            .newCreateFileActivityBuilder()
//                            .setInitialMetadata(metadataChangeSet)
//                            // .setInitialDriveContents(driveContents)
//                            .setInitialDriveContents(result.getDriveContents())
//                            .build(mGoogleApiClient);
//                    try {
//                        startIntentSenderForResult(
//                                intentSender, 1, null, 0, 0, 0);
//                    } catch (IntentSender.SendIntentException e) {
//                        Log.w("Unable to send intent", e);
//                    }
//                }
//            };
//
//    public void hey(View view) {
//
//        Drive.DriveApi.newDriveContents(mGoogleApiClient)
//                .setResultCallback(driveContentsCallback);
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Log.i("onConnected", "Connection made");
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.i("onConnectionSuspended", "Connection suspended");
//    }

}
