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

import com.denma.goforlunch.Models.GoogleAPI.Details.Periods;
import com.denma.goforlunch.Models.GoogleAPI.Details.ResponseD;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.GoogleMapsStream;
import com.denma.goforlunch.Utils.ItemClickSupport;
import com.denma.goforlunch.Views.RestaurantAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class RestaurantsListFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.fragment_restaurants_list_recycler_view)
    RecyclerView recyclerView;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "RestaurantList_Fragment"; // - RestaurantsList Fragment ID for log
    private List<Result> mRestaurants;
    public RestaurantAdapter mRestaurantAdapter;
    private Disposable disposable;
    private Calendar calendar ;

    // --------------------
    // CREATION
    // --------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view); //Configure Butterknife
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        calendar = Calendar.getInstance();
        updateUI(mResponseN);
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
    public void updateUI(ResponseN response) {
        // This loop will go through all the results
        for (int i = 0; i < response.getResults().size(); i++) {
            executeHttpRequestWithRetrofit_DetailPlaces(response.getResults().get(i).getPlaceId(), i);
        }
        mRestaurants.clear();
        mRestaurants.addAll(response.getResults());
        mRestaurantAdapter.updateCurrentData(currentLat, currentLng);
        mRestaurantAdapter.notifyDataSetChanged();
        Log.e(TAG, "Update done ! " + String.valueOf(response.getResults().size()));
    }

    // --------------------
    // UTILS
    // --------------------

    // - Set Opening hours for each restaurant in the mResponseN
    private void executeHttpRequestWithRetrofit_DetailPlaces(String placeid, final int i) {
        this.disposable = GoogleMapsStream.streamFetchDetailPlaces(placeid).subscribeWith(new DisposableObserver<ResponseD>() {

            @Override
            public void onNext(ResponseD responseD) {
                Log.e(TAG, "DetailPlaces On Next");
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                List<Periods> temp = responseD.getResult().getOpeningHours().getPeriods();

                for (Periods p : temp){
                    if((p.getOpen().getDay() + 1) == day){
                        String open = p.getOpen().getTime();
                        String close = p.getClose().getTime();

                        // - Format Open hour
                        StringBuilder str = new StringBuilder(open);
                        str.insert(2, ":");
                        open = str.toString();

                        // - Format Close hour
                        str = new StringBuilder(close);
                        str.insert(2, ":");
                        close = str.toString();

                        // - Fusion !!
                        String opening_hours = open + " - " + close;
                        mResponseN.getResults().get(i).setOpening_hours(opening_hours);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "DetailPlaces On Error " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "DetailPlaces On Complete");
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

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
        this.disposeWhenDestroy();
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
