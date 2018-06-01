package com.denma.goforlunch.Controllers.Fragments;


import android.os.Bundle;

import com.denma.goforlunch.R;


public class RestaurantsListFragment extends BaseFragment {


    public RestaurantsListFragment() { }

    public static RestaurantsListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        RestaurantsListFragment fragment = new RestaurantsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getFragmentLayout() { return R.layout.fragment_restaurants_list; }
}
