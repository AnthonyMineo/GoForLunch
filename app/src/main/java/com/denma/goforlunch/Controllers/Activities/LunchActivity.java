package com.denma.goforlunch.Controllers.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.denma.goforlunch.Controllers.Fragments.BaseFragment;
import com.denma.goforlunch.Controllers.Fragments.CoWorkerListFragment;
import com.denma.goforlunch.Controllers.Fragments.RestaurantsListFragment;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.GoogleMapsStream;
import com.denma.goforlunch.Utils.LocationService;
import com.denma.goforlunch.Utils.RestaurantHelper;
import com.denma.goforlunch.Views.PageAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.places.Place;

import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.denma.goforlunch.Controllers.Fragments.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;



public class LunchActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    // FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabs;
    private PageAdapter myPagerAdapter;
    private ViewPager pager;
    private MapFragment mMapFragment;
    private RestaurantsListFragment mRestaurantsListFragment;
    private CoWorkerListFragment mCoWorkerListFragment;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "Lunch_Activity"; // - Activity ID for log
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PROXIMITY_RADIUS = 1000;
    private double currentLat;
    private double currentLng;
    private LatLng focusPos;
    private boolean mServiceState;
    private boolean mInitUI;
    private Disposable disposable;
    private ResponseN mResponseN;

    // --------------------
    // CREATION
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mInitUI = false;

        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBroadcastReceiver();
        this.configureLocationService();
        Log.e(TAG, "onCreate");
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

    public ResponseN getResponse(){
        return mResponseN;
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
                this.finish();
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
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // - Configure Drawer Layout
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_lunch_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // - Configure NavigationView
    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.activity_lunch_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // - Configure ViewPager and TabLayout
    private void configureViewPagerAndTabs() {
        // - Get ViewPager from layout
        pager = findViewById(R.id.activity_lunch_viewpager);
        myPagerAdapter = new PageAdapter(getSupportFragmentManager());
        // - Set Adapter PageAdapter and glue it together
        pager.setAdapter(myPagerAdapter);
        // - Get TabLayout from layout
        tabs = findViewById(R.id.activity_lunch_tabs);
        // - Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager);
        // - Design purpose. Tabs have the same width
        tabs.setTabMode(TabLayout.MODE_FIXED);

        // - Fragment init
        mMapFragment = (MapFragment) myPagerAdapter.getFragment(0);
        mRestaurantsListFragment = (RestaurantsListFragment) myPagerAdapter.getFragment(1);
        mCoWorkerListFragment = (CoWorkerListFragment) myPagerAdapter.getFragment(2);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    // - We need to do something like this because the middle fragment is not re-created when switching between 3 views
                    if(mResponseN != null)
                        mRestaurantsListFragment.updateUI(mResponseN);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    // - Show first fragment
    private void showFirstFragment(){
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.activity_lunch_viewpager);
        if (visibleFragment == null){
            // - Show MapFragment
            this.showMapFragment();
            pager.setCurrentItem(0);
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
        // - Set Bounds to suggest relevant place in the overlay
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(this.currentLat, this.currentLng),
                new LatLng(this.currentLat, this.currentLng));

        // - Place Auto Complete Activity
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
        Toast.makeText(this, "Restaurants", Toast.LENGTH_SHORT).show();
    }

    // - Search functionality for CoWorkerListFragment
    private void searchForCoWorker(){
        Toast.makeText(this, "CoWorker", Toast.LENGTH_SHORT).show();
    }

    // --------------------
    // UTILS
    // --------------------

    // - Configure BroadcastReceiver
    private void configureBroadcastReceiver(){
        // - Identify the service broadcast with the intentFilter
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // - Update local variables relation to current location
                        currentLat = intent.getDoubleExtra(LocationService.EXTRA_LATITUDE, 0);
                        currentLng = intent.getDoubleExtra(LocationService.EXTRA_LONGITUDE, 0);
                        // - Call our search for NearbyPlaces
                        executeHttpRequestWithRetrofit_NearbyPlaces();
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST));
    }

    private void configureLocationService(){
        if(checkGooglePlayServices() && !mServiceState){
            // - Start location Service
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            // - Service is active now !
            mServiceState = true;
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

    //- Execute our Stream
    private void executeHttpRequestWithRetrofit_NearbyPlaces(){
        // - Execute the stream subscribing to Observable defined inside GoogleMapsStream
        this.disposable = GoogleMapsStream.streamFetchNearbyPlaces("restaurant", currentLat + "," + currentLng, PROXIMITY_RADIUS).subscribeWith(new DisposableObserver<ResponseN>() {
            @Override
            public void onNext(ResponseN response) {
                Log.e(TAG,"NearbyPlaces On Next");
                // - Update local Response
                mResponseN = response;
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,"NearbyPlaces On Error " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG,"NearbyPlaces On Complete !!");
                // - If UI method are not already call, then call them
                for(Result rest : mResponseN.getResults()){
                    restaurantExist(rest);
                }
                if (mInitUI == false){
                    ConfigureActivityUI();
                } else {
                    // - Else update the UI of fragment using Response from Google Place API
                    BaseFragment.updateFragmentData(currentLat, currentLng, mResponseN);
                    mMapFragment.updateUI(mResponseN);
                }
            }
        });
    }

    // - Http request that create restaurant in firestore
    protected void createRestaurantInFireStore(Result result){
        String placeId = result.getPlaceId();
        int ranking = 0;
        String placeName = result.getName();

        RestaurantHelper.createRestaurant(placeId, ranking, placeName).addOnFailureListener(this.onFailureListener());
        Log.e("AAAAAAAAAAA", "restaurant create");
    }

    // - Test if restaurant already exist in firebase, if not create it
    protected void restaurantExist(final Result result){
        RestaurantHelper.getRestaurantsCollection().document(result.getPlaceId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()) {
                    createRestaurantInFireStore(result);
                }
            }
        });
    }

    // - Configure Activity UI and show the mapFragment first
    private void ConfigureActivityUI(){
        this.configureViewPagerAndTabs();
        this.showFirstFragment();
        // - UI is configure now
        this.mInitUI = true;
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // --------------------
    // NAVIGATION
    // --------------------

    private void showMapFragment(){
        if(this.mMapFragment == null) this.mMapFragment = MapFragment.newInstance();
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    // --------------------
    // LIFE CYCLE
    // --------------------

    // - Handle the result of Place auto complete activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // - Get the LatLng from the user's selected place and update the focusPos variable
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.focusPos = place.getLatLng();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // - Handle the error.
                Log.i("Info", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // - The user canceled the operation.
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        this.disposeWhenDestroy();
        this.disconnectUser();
        // - Stop the location service
        stopService(new Intent(this, LocationService.class));
        // - Service is off now
        mServiceState = false;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
    }
}
