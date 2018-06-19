package com.denma.goforlunch.Controllers.Fragments;



import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.denma.goforlunch.Models.GoogleAPI.Response;
import com.denma.goforlunch.Models.GoogleAPI.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.ItemClickSupport;
import com.denma.goforlunch.Views.RestaurantAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RestaurantsListFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.fragment_restaurants_list_recycler_view)
    RecyclerView recyclerView;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "RestaurantList_Fragment"; // - RestaurantsList Fragment ID for log
    private List<Result> mRestaurants;
    public static RestaurantAdapter mRestaurantAdapter;

    // --------------------
    // CREATION
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view); //Configure Butterknife
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        updateUI(mResponse);
        Log.e(TAG, "onCreate");
        return view;
    }

    public RestaurantsListFragment() { }

    public static RestaurantsListFragment newInstance() { return new RestaurantsListFragment(); }

    // --------------------
    // GETTERS
    // --------------------

    @Override
    public int getFragmentLayout() { return R.layout.fragment_restaurants_list; }

    // --------------------
    // SETTERS
    // --------------------

    // --------------------
    // MENU
    // --------------------

    // --------------------
    // ACTIONS
    // --------------------

    // - Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(){
        // - Reset list
        this.mRestaurants= new ArrayList<>();
        // - Create adapter passing the list of Restaurants
        this.mRestaurantAdapter = new RestaurantAdapter(this.mRestaurants, Glide.with(this));
        // - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.mRestaurantAdapter);
        // - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_recycle_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // 1 - Get user from adapter
                        Result restaurants = mRestaurantAdapter.getRestaurant(position);
                        // 2 - Do something
                        Toast.makeText(getContext(), "You click on : " + restaurants.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void updateUI(Response response) {
        // This loop will go through all the results and add marker on each location.
        for (int i = 0; i < response.getResults().size(); i++) { }
        mRestaurants.clear();
        mRestaurants.addAll(response.getResults());
        mRestaurantAdapter.notifyDataSetChanged();
        Log.e(TAG, "Update done ! " + String.valueOf(response.getResults().size()));
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
