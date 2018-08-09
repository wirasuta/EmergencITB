package com.wirasuta.karya2sparta;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "KaryaSPARTA2";

    private static final int PERM_REQ_CODE = 100;

    //TODO:Change geofence area to ITB
    private static final Double bcLat = -6.892491;
    private static final Double bcLon = 107.613031;
    private static final float radius = 50;

    private SharedPreferences shPref;
    private FusedLocationProviderClient locationProviderClient;
    private Location currLoc;
    private LocationRequest locationRequestClient;
    private LocationCallback locationRequestCallback;
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList;
    private PendingIntent geofencePI;

    Switch enableSw;
    TextView textView;
    FloatingActionButton emergencyButton;
    EditText phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);

        shPref = getPreferences(Context.MODE_PRIVATE);
        enableSw = findViewById(R.id.swGeofence);
        textView = findViewById(R.id.currLocText);
        emergencyButton = findViewById(R.id.sendEmergency);
        phoneNum = findViewById(R.id.phoneNum);
        geofenceList = new ArrayList<>();

        populateGeofenceList();
        createNotificationChannel();
        createLocRequestCallback();

        if (!allowedPermissions()) {
            askPermissions();
        } else {
            loadPref();
            createListener();
            createLocationRequest();
            startLocationService();
        }
    }

    private void createLocRequestCallback() {
        locationRequestCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currLoc = location;
                    updateText();
                }
            }
        };
    }

    private void populateGeofenceList() {
        geofenceList.add(new Geofence.Builder()
                .setRequestId("KaryaSPARTA2")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(100*60*2)
                .setCircularRegion(bcLat,bcLon,radius)
                .build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allowedPermissions()) {
            startLocationService();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM_REQ_CODE: {
                startLocationService();
            }
        }
    }

    private void askPermissions() {
        String[] Permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        ActivityCompat.requestPermissions(this, Permissions, PERM_REQ_CODE);
    }

    private boolean allowedPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return false;
        else return true;
    }

    @SuppressLint("MissingPermission")
    private void startLocationService() {
        locationProviderClient.requestLocationUpdates(locationRequestClient, locationRequestCallback, null);
    }

    private void stopLocationService() {
        locationProviderClient.removeLocationUpdates(locationRequestCallback);
    }

    protected void createLocationRequest() {
        locationRequestClient = new LocationRequest();
        locationRequestClient.setInterval(100 * 120);
        locationRequestClient.setFastestInterval(100 * 30);
        locationRequestClient.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(geofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePI!= null) {
            return geofencePI;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        geofencePI = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePI;
    }


    private void createListener() {
        enableSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shPref.edit().putBoolean("enableGeofence", true).apply();
                    loadPref();
                } else {
                    shPref.edit().putBoolean("enableGeofence", false).apply();
                    loadPref();
                }
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "KaryaSPARTA2";
            String description = "KaryaSPARTA2 Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadPref() {
        //TODO:Load settings from shared preferences
        boolean switchState = shPref.getBoolean("enableGeofence", true);
        enableSw.setChecked(switchState);
        if (switchState) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //do nothing
                }
            });
        }else{
            geofencingClient.removeGeofences(getGeofencePendingIntent());
        }
    }

    private void updateText() {
        String currLocString = currLoc.getLatitude() + " / " + currLoc.getLongitude();
        textView.setText(currLocString);
    }

    public void sendEmergency(View view) {
        //TODO:Send user info from shared preferences
        String smsTitle = "INCOMING EMERGENCY LEVEL "+"I";
        String fullName = "Nama: " + "John Doe";
        String NIM = "NIM: " + "16517999";
        String currLocURL = "Maps URL: " + "https://www.google.com/maps/search/?api=1&query=" + String.valueOf(currLoc.getLatitude()) + "," + String.valueOf(currLoc.getLongitude());
        String smsText = smsTitle + "\n" + fullName + "\n" + NIM + "\n" + currLocURL;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum.getText().toString(),null,smsText,null,null);
    }
}
