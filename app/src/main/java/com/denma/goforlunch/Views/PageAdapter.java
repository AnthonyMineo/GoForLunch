package com.denma.goforlunch.Views;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.denma.goforlunch.Controllers.Fragments.CoWorkerListFragment;
import com.denma.goforlunch.Controllers.Fragments.MapFragment;
import com.denma.goforlunch.Controllers.Fragments.RestaurantsListFragment;


public class PageAdapter extends FragmentStatePagerAdapter {
    // - A SparseArray that will contain our fragment
    private final SparseArray<Fragment> instantiatedFragments = new SparseArray<>();

    // - Constructor
    public PageAdapter(FragmentManager mgr) {
        super(mgr);
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
                return "Map View";
            case 1: //Page number 2
                return "List View";
            case 2: //Page number 3
                return "WorkMates";
            default:
                return null;
        }
    }

    // - Store the fragment instance to the SparseArray
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        instantiatedFragments.put(position, fragment);
        return fragment;
    }

    // - Suppress a fragment from the SparseArray
    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        instantiatedFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // - Fragment Getter
    // - It will allow us to perform method's call from the fragment it return directly into our activity
    @Nullable
    public Fragment getFragment(final int position) {
        return instantiatedFragments.get(position);
    }
}
