package com.denma.goforlunch.Controllers.Fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.denma.goforlunch.Controllers.Activities.LunchActivity;

import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.RestaurantHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;


public abstract class BaseFragment extends Fragment {

    // FOR DESIGN

    // FOR PERMISSIONS
    protected static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;

    // FOR DATA
    protected LunchActivity mLunchActivity;
    protected static double currentLat;
    protected static double currentLng;
    protected static ResponseN mResponseN;

    // --------------------
    // CREATION
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view); //Configure Butterknife
        this.mLunchActivity = (LunchActivity) getActivity();
        currentLat = mLunchActivity.getCurrentLat();
        currentLng = mLunchActivity.getCurrentLng();
        mResponseN = mLunchActivity.getResponse();
        return view;
    }

    // --------------------
    // GETTERS
    // --------------------

    public abstract int getFragmentLayout();

    // --------------------
    // SETTERS
    // --------------------

    // --------------------
    // MENU
    // --------------------

    // --------------------
    // ACTIONS
    // --------------------

    public abstract void updateUI(ResponseN response);

    public static void updateFragmentData(double lat, double lng, ResponseN rep){
        currentLat = lat;
        currentLng = lng;
        mResponseN = rep;
    }

    // --------------------
    // UTILS
    // --------------------

    // - Http request that create restaurant in firestore
    protected void createRestaurantInFireStore(Result result){
        String placeId = result.getPlaceId();
        int ranking = 0;
        List<String> luncherName = null;

        RestaurantHelper.createRestaurant(placeId, ranking, luncherName).addOnFailureListener(this.onFailureListener());
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

    // --------------------
    // NAVIGATION
    // --------------------

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

    // --------------------
    // LIFE CYCLE
    // --------------------
}
