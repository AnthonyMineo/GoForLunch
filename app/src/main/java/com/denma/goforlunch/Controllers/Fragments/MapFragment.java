package com.denma.goforlunch.Controllers.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.denma.goforlunch.Controllers.Activities.LunchActivity;
import com.denma.goforlunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    // FOR DESIGN
    private GoogleMap mMap;
    @BindView(R.id.map)
    MapView mMapView;

    // FOR DATA
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng currentPosition;
    private LunchActivity mLunchActivity;

    // FOR DATA
    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view); //Configure Butterknife
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return view;
    }

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_map;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

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
            mMap.setMyLocationEnabled(true);
        }

        mLunchActivity = (LunchActivity) getActivity();
        currentPosition = new LatLng(mLunchActivity.getCurrentLat(), mLunchActivity.getCurrentLng());
        changeFocusPosition(currentPosition);
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

    // - Change the Camera position on the map to the given LatLng
    public void changeFocusPosition(LatLng latLng){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,
                16);
        mMap.moveCamera(update);
    }

    // ---------------
    // LIFE CYCLE
    // ---------------

    // Docs suggest to override them
    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
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
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

}
