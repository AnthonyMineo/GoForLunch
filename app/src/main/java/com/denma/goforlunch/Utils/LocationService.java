package com.denma.goforlunch.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Places;

import pub.devrel.easypermissions.EasyPermissions;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // FOR PERMISSIONS
    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMS2 = Manifest.permission.ACCESS_COARSE_LOCATION;

    // FOR DATA
    private static final String TAG = "G4L_LocationService"; // - Service ID for log
    public static final String ACTION_LOCATION_BROADCAST = "G4L_LocationService_LocationBroadcast"; // - The broadcast ID
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final int INTERVAL = 20000;
    public static final int FASTEST_INTERVAL = 10000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    private double currentLat;
    private double currentLng;

    public LocationService() { }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();

        // - Location Request will define the call's frequency on LocationServices API
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient.connect();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // - Set location data into local variables
                    setLocationData(location);
                    // - Send them through a broadcast (actually only LunchActivity can get it)
                    sendBroadcastToActivity(currentLat, currentLng);
                }
            }
        };

        // - Allow the service to be restart if the OS suppress it due to a lack of memory
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // - Permissions check
        if (EasyPermissions.hasPermissions(this, PERMS)){
            if (EasyPermissions.hasPermissions(this, PERMS2)){
                // - Request location with a specified frequency to update current location
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

                Log.e(TAG, "Connected to Google API");
            }
        }
    }

    // - Set current latitude + current longitude + current location on local variables
    private void setLocationData(Location location){
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended");
    }

    // - Trigger when the location is updated
    @Override
    public void onLocationChanged(Location location) {
        // - Update local variables
        setLocationData(location);

        if (location != null) {
            Log.e(TAG, "== location != null");
            // - Send result through a broadcast
            sendBroadcastToActivity(currentLat, currentLng);
        }
    }

    // - Broadcast for location data
    private void sendBroadcastToActivity(double Lat, double Lng) {
        Log.e(TAG, "Sending info...");
        // - Intent filter that will help to recognize it from the activity
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, Lat);
        intent.putExtra(EXTRA_LONGITUDE, Lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect to Google API");

    }

    @Override
    public void onDestroy() {
        stopServiceLocation();
        super.onDestroy();
        Log.e(TAG, "On Destroy");
    }

    @SuppressLint("MissingPermission")
    public void stopServiceLocation(){
        // - Permissions check
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            if (EasyPermissions.hasPermissions(this, PERMS2)) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                mGoogleApiClient.disconnect();
            }
        }
    }
}
