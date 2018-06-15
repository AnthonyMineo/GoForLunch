package com.denma.goforlunch.Controllers.Fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.denma.goforlunch.Controllers.Activities.LunchActivity;
import com.denma.goforlunch.Models.GoogleAPI.Response;
import com.denma.goforlunch.Utils.GoogleMapsStream;

import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public abstract class BaseFragment extends Fragment {

    // FOR DESIGN

    // FOR PERMISSIONS
    protected static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;

    // FOR DATA

    protected static LunchActivity mLunchActivity;
    protected double currentLat;
    protected double currentLng;
    protected Response mResponse;

    // --------------------
    // CREATION
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view); //Configure Butterknife
        this.mLunchActivity = (LunchActivity) getActivity();
        this.currentLat = mLunchActivity.getCurrentLat();
        this.currentLng = mLunchActivity.getCurrentLng();
        this.mResponse = mLunchActivity.getResponse();
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

    protected abstract void updateUI(Response response);

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
    public void onDestroy() {
        super.onDestroy();
    }
}
