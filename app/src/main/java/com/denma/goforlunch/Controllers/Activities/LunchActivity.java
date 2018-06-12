package com.denma.goforlunch.Controllers.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.denma.goforlunch.R;
import com.denma.goforlunch.Views.PageAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.denma.goforlunch.Controllers.Fragments.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import pub.devrel.easypermissions.EasyPermissions;


public class LunchActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    // FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabs;
    private ViewPager pager;
    private MapFragment mMapFragment;

    // FOR PERMISSIONS

    // FOR DATA
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private double currentLat;
    private double currentLng;
    private LatLng focusPos;
    private boolean googleState;

    // --------------------
    // CREATION
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureViewPagerAndTabs();
        this.configurePosition();
        this.googleState = this.checkGooglePlayServices();
        this.showFirstFragment();
        Log.e("ACTIVITY", "onCreateOK");
    }

    // --------------------
    // GETTERS
    // --------------------

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_lunch;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public LatLng getFocusPos() {
        return focusPos;
    }

    // --------------------
    // SETTERS
    // --------------------

    // --------------------
    // MENU
    // --------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // - Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.activity_lunch_menu_tools, menu);
        return true;
    }

    // - Handle actions on menu items
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_lunch_menu_search:
                // - Launch Search activity/dialog
                this.chooseSearchEffect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // - Handle back click to close menu
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // - Handle Navigation Item Click in the Navigation Drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_drawer_item_lunch :
                //Do something about your lunch
                break;
            case R.id.menu_drawer_item_settings:
                //Do something about settings
                break;
            case R.id.menu_drawer_item_log_out:
                //Do something about log out
                this.disconnectUser();
                finish();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // --------------------
    // ACTIONS
    // --------------------

    // - Configure Toolbar
    private void configureToolBar() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // - Configure Drawer Layout
    private void configureDrawerLayout() {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_lunch_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // - Configure NavigationView
    private void configureNavigationView() {
        this.navigationView = (NavigationView) findViewById(R.id.activity_lunch_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // - Configure ViewPager and TabLayout
    private void configureViewPagerAndTabs() {
        // - Get ViewPager from layout
        pager = (ViewPager) findViewById(R.id.activity_lunch_viewpager);
        // - Set Adapter PageAdapter and glue it together
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), this));
        // - Get TabLayout from layout
        tabs = (TabLayout) findViewById(R.id.activity_lunch_tabs);
        // - Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager);
        // - Design purpose. Tabs have the same width
    }

    // - Show first fragment
    private void showFirstFragment(){
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.activity_lunch_viewpager);
        if (visibleFragment == null){
            // - Show MapFragment
            this.showMapFragment();
        }
    }

    // - Decide how the search should work depending on viewpager's current fragment display
    private void chooseSearchEffect(){
        int position = pager.getCurrentItem();
        switch (position) {
            case 0:
                this.searchForMaps();
                break;
            case 1:
                this.searchForRestaurants();
                break;
            case 2:
                this.searchForCoWorker();
                break;
        }
    }

    // - Search functionality for MapFragment
    private void searchForMaps(){
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(this.currentLat, this.currentLng),
                new LatLng(this.currentLat, this.currentLng));

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(latLngBounds)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // - Handle the error
            Toast.makeText(this, "GooglePlayServicesRepairableException", Toast.LENGTH_SHORT);
        } catch (GooglePlayServicesNotAvailableException e) {
            // - Handle the error.
            Toast.makeText(this, "GooglePlayServicesNotAvailableException", Toast.LENGTH_SHORT);
        }
    }

    // - Search functionality for RestaurantsListFragment
    private void searchForRestaurants(){
        Toast.makeText(this, "Restaurants", Toast.LENGTH_SHORT);
    }

    // - Search functionality for CoWorkerListFragment
    private void searchForCoWorker(){
        Toast.makeText(this, "CoWorker", Toast.LENGTH_SHORT);
    }

    // --------------------
    // UTILS
    // --------------------

    // - Configure Position
    @SuppressLint("MissingPermission")
    private void configurePosition() {

        // - Check for permission
        if (EasyPermissions.hasPermissions(this, PERMS2)) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                    }
                }
            });
        }
    }

    // - Test GooglePlayServices availability
    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    // - Configure API
    public synchronized void buildGoogleApiClient(){
        this.mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        this.mGoogleApiClient.connect();
    }

    // --------------------
    // NAVIGATION
    // --------------------

    private void showMapFragment(){
        if (this.mMapFragment == null) this.mMapFragment = mMapFragment.newInstance();
        pager.setCurrentItem(0);
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Impossible de se connecter aus APIs Google", Toast.LENGTH_SHORT);
    }

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("Info", "Place: " + place.getName() + " " + place.getLatLng().toString());
                this.focusPos = place.getLatLng();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // - Handle the error.
                Log.i("Info", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ACTIVITY", "onStartOK");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ACTIVITY", "onStopOK");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ACTIVITY", "onPauseOK");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ACTIVITY", "onResumeOK");
        if(focusPos !=null){
            Log.e("CCCCCCCCCC", focusPos.toString());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("ACTIVITY", "onRestartOK");
    }
}
