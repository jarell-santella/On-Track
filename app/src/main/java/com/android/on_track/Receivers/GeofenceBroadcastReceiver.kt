package com.android.on_track.Receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.on_track.sendGeofenceEnteredNotification
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("ERROR: ", errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
//            || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

//            // Get the geofences that were triggered. A single event can trigger multiple geofences.
//            val triggeringGeofences = geofencingEvent.triggeringGeofences
//
//            // Get the transition details as a String.
//            val geofenceTransitionDetails: String = getGeofenceTransitionDetails(
//                this,
//                geofenceTransition,
//                triggeringGeofences
//            )

            // Creating and sending Notification
            val notificationManager = ContextCompat.getSystemService(
                context, NotificationManager::class.java) as NotificationManager

            // Send notification and log the transition details.

            notificationManager.sendGeofenceEnteredNotification(context)
//            Log.d("DEBUG: ", geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e("ERROR: ", "Invalid type transition $geofenceTransition")
        }
    }
}