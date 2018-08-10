package com.wirasuta.karya2sparta;

import android.app.IntentService;
import android.app.PendingIntent;
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
            Log.e("GF", "Geofence Error");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Send notification
            sendNotification();
            Log.d("GF","Dwell event recorded");
        } else {
            // Log the error.
            Log.e("GF", "Invalid Geofence Event Recorded");
        }
    }

    private void sendNotification() {
        Intent tapNotifIntent = new Intent(this, MainActivity.class);
        tapNotifIntent.putExtra("emergencyCall",true);
        PendingIntent tapNotifPI = PendingIntent.getActivity(this,300,tapNotifIntent,0);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this,MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("EmergencITB")
                .setContentText("Tap untuk mengirimkan sinyal bantuan")
                .setContentIntent(tapNotifPI)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notifier = NotificationManagerCompat.from(this);
        notifier.notify(100,notifBuilder.build());
    }

}
