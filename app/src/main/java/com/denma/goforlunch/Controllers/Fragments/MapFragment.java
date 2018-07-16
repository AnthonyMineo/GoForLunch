package com.denma.goforlunch.Controllers.Fragments;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.denma.goforlunch.Controllers.Activities.RestaurantDetailActivity;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {

    // FOR DESIGN
    private GoogleMap mMap;
    @BindView(R.id.map)
    MapView mMapView;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "Map_Fragment"; // - Map Fragment ID for log
    private LatLng currentPosition;

    // --------------------
    // CREATION
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view); //Configure Butterknife
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        Log.e(TAG, "onCreate");
        return view;
    }

    public MapFragment() { }

    public static MapFragment newInstance() { return new MapFragment(); }

    // --------------------
    // GETTERS
    // --------------------

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_map;
    }

    // --------------------
    // SETTERS
    // --------------------

    // --------------------
    // MENU
    // --------------------

    // --------------------
    // ACTIONS
    // --------------------

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        Log.e(TAG, "onMapReady");
        this.mMap.setOnMyLocationButtonClickListener(this);
        this.mMap.setOnMyLocationClickListener(this);
        this.mMap.setOnMarkerClickListener(this);

        // - Adjust MyLocation Button position on screen
        View locationButton = (View) mMapView.findViewWithTag("GoogleMapMyLocationButton");
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // - Position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 30);

        // - Check for permission
        if (EasyPermissions.hasPermissions(getContext(), PERMS)) {
            this.mMap.setMyLocationEnabled(true);
        }

        // - Change camera to currentPosition
        this.currentPosition = new LatLng(currentLat, currentLng);
        changeFocusPosition(currentPosition);

        if (mResponseN != null){
            // - Display nearby restaurant marker
            updateUI(mResponseN);
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // The camera animates to the user's current position.
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        // Maybe useful later
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e(TAG, "markerOnClick");
        for(int i = 0; i < mResponseN.getResults().size(); i++) {
            if (marker.getSnippet().equals(mResponseN.getResults().get(i).getPlaceId())) {
                Result restaurant = mResponseN.getResults().get(i);
                // - Launch Detail activity
                Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        }
        return false;
    }

    // - Change the Camera position on the map to the given LatLng
    public void changeFocusPosition(LatLng latLng){
        Log.e(TAG, "CameraUpdate");
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,16);
        this.mMap.animateCamera(update);
    }

    @Override
    public void updateUI(ResponseN response) {
        mMap.clear();
        // This loop will go through all the results and add marker on each location.
        for (int i = 0; i < response.getResults().size(); i++) {
            Double lat = response.getResults().get(i).getGeometry().getLocation().getLat();
            Double lng = response.getResults().get(i).getGeometry().getLocation().getLng();
            String placeId =  response.getResults().get(i).getPlaceId();

            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            // Position of Marker on Map
            markerOptions.position(latLng);
            markerOptions.snippet(placeId);
            // Adding Marker to the Camera.
            Marker m = mMap.addMarker(markerOptions);
            // Adding colour to the marker
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        Log.e(TAG, "Update done ! " + String.valueOf(response.getResults().size()));
    }

    // --------------------
    // UTILS
    // --------------------

    // --------------------
    // NAVIGATION
    // --------------------

    // --------------------
    // ERROR HANDLER
    // --------------------

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null)
            mMapView.onSaveInstanceState(outState);
    }

    // Docs suggest to override them
    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
        Log.e(TAG, "onResume");
        // - Allow us to handle user's selection from Place autocomplete
       if(mLunchActivity.getFocusPos() != null)
            changeFocusPosition(mLunchActivity.getFocusPos());
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        Log.e(TAG, "onPause");
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e("Error","Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
        Log.e(TAG, "onLowMemory");
    }
}
