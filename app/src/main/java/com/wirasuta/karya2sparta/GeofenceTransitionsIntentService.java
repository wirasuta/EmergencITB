package com.wirasuta.karya2sparta;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceNotifier");
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("GF:", "Geofence Error");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Send notification and log the transition details.
            sendNotification();
            Log.i("GF:", "Geofence Dwell Event Recorded");
        } else {
            // Log the error.
            Log.e("GF:", "Invalid Geofence Event Recorded");
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this,MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Geofencing Notification")
                .setContentText("Geofence Dwell Event Recorded")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notifier = NotificationManagerCompat.from(this);
        notifier.notify(100,notifBuilder.build());
    }

}
