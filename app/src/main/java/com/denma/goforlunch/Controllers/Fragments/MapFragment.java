package com.denma.goforlunch.Controllers.Fragments;


import android.annotation.SuppressLint;

import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.denma.goforlunch.Controllers.Activities.LunchActivity;
import com.denma.goforlunch.Models.GoogleAPI.Response;
import com.denma.goforlunch.R;

import com.denma.goforlunch.Utils.GoogleMapsStream;
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

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    // FOR DESIGN
    private GoogleMap mMap;
    @BindView(R.id.map)
    MapView mMapView;

    // FOR PERMISSIONS

    // FOR DATA
    private LatLng currentPosition;
    private LatLng focusPosition;
    private double currentLat;
    private double currentLng;
    private LunchActivity mLunchActivity;
    private Disposable disposable;
    private static final int PROXIMITY_RADIUS = 1000;

    // --------------------
    // CREATION
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view); //Configure Butterknife
        this.mLunchActivity = (LunchActivity) getActivity();
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        Log.e("FRAGMENT", "onCreateOK");
        return view;
    }

    public MapFragment() { }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

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
        mLunchActivity.buildGoogleApiClient();
        Log.e("FRAGMENT", "onMapReadyOK");
        this.mMap.setOnMyLocationButtonClickListener(this);
        this.mMap.setOnMyLocationClickListener(this);

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
        this.currentLat = mLunchActivity.getCurrentLat();
        this.currentLng = mLunchActivity.getCurrentLng();
        this.currentPosition = new LatLng(currentLat, currentLng);
        changeFocusPosition(currentPosition);

        // - Display nearby restaurant marker
        executeHttpRequestWithRetrofit();
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
        Log.e("FRAGMENT", "CameraUpdate");
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,
                16);
        this.mMap.animateCamera(update);
    }

    // --------------------
    // UTILS
    // --------------------

    //- Execute our Stream
    private void executeHttpRequestWithRetrofit(){
        // - Execute the stream subscribing to Observable defined inside NYTStream
        this.disposable = GoogleMapsStream.streamFetchNearbyPlaces("restaurant", currentLat + "," + currentLng, PROXIMITY_RADIUS).subscribeWith(new DisposableObserver<Response>() {
            @Override
            public void onNext(Response response) {
                Log.e("TAG","On Next");
                // - Update UI with response
                updateMapUI(response);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error "+ e.getMessage());

            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    private void updateMapUI(Response response){
        mMap.clear();
        // This loop will go through all the results and add marker on each location.
        for (int i = 0; i < response.getResults().size(); i++) {
            Double lat = response.getResults().get(i).getGeometry().getLocation().getLat();
            Double lng = response.getResults().get(i).getGeometry().getLocation().getLng();
            String placeName = response.getResults().get(i).getName();
            String vicinity = response.getResults().get(i).getVicinity();
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            // Position of Marker on Map
            markerOptions.position(latLng);
            // Adding Title to the Marker
            markerOptions.title(placeName + " : " + vicinity);
            // Adding Marker to the Camera.
            Marker m = mMap.addMarker(markerOptions);
            // Adding colour to the marker
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            Log.e("TAG", "Update done !");
        }

        Log.e("TAG", String.valueOf(response.getResults().size()));
    }

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
        Log.e("FRAGMENT", "onResumeOK");
        // - Allow us to handle user's selection from Place autocomplete
        if(mLunchActivity.getFocusPos() != null)
            changeFocusPosition(mLunchActivity.getFocusPos());
        Log.e("BBBBBBBBBBBBB", "Focus OK");
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        Log.e("FRAGMENT", "onPauseOK");
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
        Log.e("FRAGMENT", "onDestroyOK");
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
        Log.e("FRAGMENT", "onLowMemoryOK");
    }

}
