package com.denma.goforlunch.Controllers.Fragments;


import android.os.Bundle;

import com.denma.goforlunch.R;


public class RestaurantsListFragment extends BaseFragment {

    public RestaurantsListFragment() { }

    public static RestaurantsListFragment newInstance() { return new RestaurantsListFragment(); }

    @Override
    public int getFragmentLayout() { return R.layout.fragment_restaurants_list; }
}
