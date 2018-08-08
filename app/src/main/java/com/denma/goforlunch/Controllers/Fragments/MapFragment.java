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

import com.denma.goforlunch.Utils.RestaurantHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

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
    private MarkerOptions markerOptions;

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
        // - Allow us to handle user's selection from Place autocomplete
        if(mLunchActivity.getFocusPos() != null){
            changeFocusPosition(mLunchActivity.getFocusPos(), mLunchActivity.getFocusPlaceId());
        } else {
            changeFocusPosition(currentPosition, mLunchActivity.getFocusPlaceId());
        }

        if (mResponseN != null){
            // - Display nearby restaurant marker
            updateUI(mResponseN);
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // The camera animates to the user's current position.
        mLunchActivity.setFocusPos(currentPosition);
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        // Maybe useful later
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e(TAG, "markerOnClick");
        mLunchActivity.setFocusPos(marker.getPosition());
        for(int i = 0; i < mResponseN.getResults().size(); i++) {
            if (marker.getSnippet().equals(mResponseN.getResults().get(i).getPlaceId())) {
                Result restaurant = mResponseN.getResults().get(i);
                restaurantExist(restaurant);
                // - Launch Detail activity
                Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        }
        return false;
    }

    // - Change the Camera position on the map to the given LatLng
    public void changeFocusPosition(LatLng latLng, String placeId){
        Log.e(TAG, "CameraUpdate");
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,16);

        if(placeId != ""){
            MarkerOptions markerOptions = new MarkerOptions();
            // Position of Marker on Map
            markerOptions.position(latLng);
            markerOptions.snippet(placeId);
            // Adding Marker to the Camera.
            Marker m = mMap.addMarker(markerOptions);
            // Adding colour to the marker
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        this.mMap.animateCamera(update);
    }

    @Override
    public void updateUI(ResponseN response) {
        mMap.clear();
        // This loop will go through all the results and add marker on each location.
        for (int i = 0; i < response.getResults().size(); i++) {
            final Double lat = response.getResults().get(i).getGeometry().getLocation().getLat();
            final Double lng = response.getResults().get(i).getGeometry().getLocation().getLng();
            final String placeId =  response.getResults().get(i).getPlaceId();
            // Adding colour to the marker
            RestaurantHelper.getCollectionFromARestaurant(response.getResults().get(i).getPlaceId(), "luncherId").addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().isEmpty()){
                        markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);
                        // Position of Marker on Map
                        markerOptions.position(latLng);
                        markerOptions.snippet(placeId);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_marker_red));
                        // Adding Marker to the Camera.
                        Marker m = mMap.addMarker(markerOptions);
                    } else {
                        markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);
                        // Position of Marker on Map
                        markerOptions.position(latLng);
                        markerOptions.snippet(placeId);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_marker_green));
                        // Adding Marker to the Camera.
                        Marker m = mMap.addMarker(markerOptions);
                    }
                }
            });
        }
        Log.e(TAG, "Update done ! " + String.valueOf(response.getResults().size()));
    }

    private void checkForMarkerStyle(){

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
        if(mResponseN != null && mMap != null){
            updateUI(mResponseN);
        }
        // - Allow us to handle user's selection from Place autocomplete
        if(mLunchActivity.getFocusPos() != null && mMap != null)
            changeFocusPosition(mLunchActivity.getFocusPos(), mLunchActivity.getFocusPlaceId());
        Log.e(TAG, "onResume");
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
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e("Error","Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        Log.e(TAG, "onDestroy");
        super.onDestroy();
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
