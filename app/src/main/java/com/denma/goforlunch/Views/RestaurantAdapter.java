package com.denma.goforlunch.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    // FOR DATA
    private List<Result> mRestaurants;
    private RequestManager glide;
    private Context mContext;
    private double currentLat;
    private double currentLng;

    // CONSTRUCTOR
    public RestaurantAdapter(List<Result> restaurants, RequestManager glide) {
        this.mRestaurants = restaurants;
        this.glide = glide;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        this.mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.fragment_recycle_item, parent, false);

        return new RestaurantViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A RESTAURANTS
    @Override
    public void onBindViewHolder(RestaurantViewHolder viewHolder, int position) {
        viewHolder.updateWithRestaurant(this.mRestaurants.get(position), this.glide, mContext, this.currentLat, this.currentLng);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {
        return this.mRestaurants.size();
    }

    public Result getRestaurant(int position){
        return this.mRestaurants.get(position);
    }

    public void updateCurrentData(double currentLat, double currentLng){
        this.currentLat = currentLat;
        this.currentLng = currentLng;
    }
}