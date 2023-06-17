package com.gubatron.helloAR;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationUpdatesComponent {
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationCallback locationCallback;

    private final LocationRequest locationRequest;

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);
    }

    private LocationUpdateListener listener;

    public LocationUpdatesComponent(Context context, LocationUpdateListener listener) {
        this.listener = listener;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = initLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    listener.onLocationUpdate(location);
                }
            }
        };
    }

    public void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private LocationRequest initLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update location every 10 seconds
        locationRequest.setFastestInterval(5000); // Fastest rate in millisecond at which your app can handle location updates.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
