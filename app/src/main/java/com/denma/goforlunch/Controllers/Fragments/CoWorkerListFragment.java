package com.denma.goforlunch.Controllers.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denma.goforlunch.Models.GoogleAPI.Response;
import com.denma.goforlunch.R;


public class CoWorkerListFragment extends BaseFragment {

    // FOR DESIGN

    // FOR PERMISSIONS

    // FOR DATA

    // --------------------
    // CREATION
    // --------------------

    public CoWorkerListFragment() { }

    public static CoWorkerListFragment newInstance() { return new CoWorkerListFragment(); }

    // --------------------
    // GETTERS
    // --------------------

    @Override
    public int getFragmentLayout() { return R.layout.fragment_co_worker_list; }

    // --------------------
    // SETTERS
    // --------------------

    // --------------------
    // MENU
    // --------------------

    // --------------------
    // ACTIONS
    // --------------------

    @Override
    protected void updateUI(Response response) {

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
    public void onDestroy() {
        super.onDestroy();
        Log.e("COWORKER", "onCreateOK");
    }

    // Docs suggest to override them
    @Override
    public void onResume() {
        super.onResume();
        Log.e("COWORKER", "onCreateOK");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("COWORKER", "onCreateOK");
    }

}
