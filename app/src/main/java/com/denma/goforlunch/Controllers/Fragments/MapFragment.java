package com.denma.goforlunch.Controllers.Fragments;


import android.os.Bundle;
import com.denma.goforlunch.R;


public class MapFragment extends BaseFragment {


    public MapFragment() { }

    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getFragmentLayout() { return R.layout.fragment_map; }
}
