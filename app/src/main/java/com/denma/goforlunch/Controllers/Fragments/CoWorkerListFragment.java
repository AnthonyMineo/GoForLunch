package com.denma.goforlunch.Controllers.Fragments;


import android.util.Log;

import com.denma.goforlunch.Models.GoogleAPI.Response;
import com.denma.goforlunch.R;


public class CoWorkerListFragment extends BaseFragment {

    // FOR DESIGN

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "CoWorker_Fragment"; // - CoWorker Fragment ID for log

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
    public void updateUI(Response response) {

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
        Log.e(TAG, "onDestroy");
    }

    // Docs suggest to override them
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

}
