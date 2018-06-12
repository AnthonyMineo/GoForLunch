package com.denma.goforlunch.Controllers.Fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    // FOR DESIGN

    // FOR PERMISSIONS
    protected static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;

    // FOR DATA

    // --------------------
    // CREATION
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view); //Configure Butterknife
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

}
