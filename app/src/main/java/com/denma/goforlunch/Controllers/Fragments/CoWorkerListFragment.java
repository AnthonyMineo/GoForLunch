package com.denma.goforlunch.Controllers.Fragments;


import android.os.Bundle;

import com.denma.goforlunch.R;


public class CoWorkerListFragment extends BaseFragment {


    public CoWorkerListFragment() { }

    public static CoWorkerListFragment newInstance() { return new CoWorkerListFragment(); }

    @Override
    public int getFragmentLayout() { return R.layout.fragment_co_worker_list; }
}
