package com.denma.goforlunch.Views;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.denma.goforlunch.Controllers.Fragments.CoWorkerListFragment;
import com.denma.goforlunch.Controllers.Fragments.MapFragment;
import com.denma.goforlunch.Controllers.Fragments.RestaurantsListFragment;

public class PageAdapter extends FragmentPagerAdapter {

    Context context;

    //Constructor
    public PageAdapter(FragmentManager mgr, Context mContext) {
        super(mgr);
        this.context = mContext;
    }

    @Override
    public int getCount() {
        return(3);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: //Page number 1
                return MapFragment.newInstance();
            case 1: //Page number 2
                return RestaurantsListFragment.newInstance();
            case 2: //Page number 3
                return CoWorkerListFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: //Page number 1
                return "map";
            case 1: //Page number 2
                return "rest";
            case 2: //Page number 3
                return "cowork";
            default:
                return null;
        }
    }
}
