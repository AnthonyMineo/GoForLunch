package com.denma.goforlunch.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.denma.goforlunch.Models.GoogleAPI.Result;
import com.denma.goforlunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    // FOR DESIGN
    @BindView(R.id.fragment_recycle_item_name)
    TextView restName;
    @BindView(R.id.fragment_recycle_item_distance)
    TextView restDistance;
    @BindView(R.id.fragment_recycle_item_location)
    TextView restLocation;
    @BindView(R.id.fragment_recycle_item_mate_number)
    TextView restMateNumber;
    @BindView(R.id.fragment_recycle_item_openning_hours)
    TextView restOpenningHours;
    @BindView(R.id.fragment_recycle_item_rank)
    ImageView restRankImage;
    @BindView(R.id.fragment_recycle_item_restaurant_image)
    ImageView restImage;

    // FOR DATA
    private String photoRef;

    public RestaurantViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithRestaurant(Result restaurant, RequestManager glide, Context context){
        this.restName.setText(restaurant.getName());
        this.restDistance.setText("100m"); // will change
        this.restLocation.setText(restaurant.getVicinity());
        // need a test from firebase data
        this.restMateNumber.setText("3"); // will change
        this.restOpenningHours.setText(restaurant.getOpeningHours().getOpenNow().toString()); // will change
        // need a test from firebase data
        this.restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_black_24dp)); // will change
        if(restaurant.getPhotos().size() > 0){
            photoRef = restaurant.getPhotos().get(0).getPhotoReference();
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=500&maxwidth=500&photoreference=" + photoRef + "&key=" + context.getResources().getString(R.string.key_place_api);
            glide.load(url).into(restImage);
        }
        else {
            restImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_place_image_not_found));
        }
    }
}
