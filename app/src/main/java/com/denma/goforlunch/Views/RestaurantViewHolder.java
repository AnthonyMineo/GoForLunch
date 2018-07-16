package com.denma.goforlunch.Views;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.denma.goforlunch.BuildConfig;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
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
    @BindView(R.id.fragment_recycle_item_opening_hours)
    TextView restOpeningHours;
    @BindView(R.id.fragment_recycle_item_rank)
    ImageView restRankImage;
    @BindView(R.id.fragment_recycle_item_restaurant_image)
    ImageView restImage;

    // FOR DATA
    private String photoRef;
    private float[] distanceTo = new float[3];

    public RestaurantViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithRestaurant(Result restaurant, RequestManager glide, Context context, double currentLat, double currentLng){
        double targetLat = restaurant.getGeometry().getLocation().getLat();
        double targetLng = restaurant.getGeometry().getLocation().getLng();
        Location.distanceBetween(currentLat, currentLng, targetLat, targetLng, distanceTo);

        // - Set Name
        this.restName.setText(restaurant.getName());

        // - Set Distance between user and restaurant
        this.restDistance.setText(String.valueOf(Math.round(distanceTo[0]))+ "m");
        Log.e("TARGET_LAT", String.valueOf(targetLat));
        Log.e("TARGET_LNG", String.valueOf(targetLng));
        Log.e("CURRENT_LAT", String.valueOf(currentLat));
        Log.e("CURRENT_LNG", String.valueOf(currentLng));
        Log.e("DISTANCE_TO", String.valueOf(distanceTo[0])+ "m");

        // - Set Address
        this.restLocation.setText(restaurant.getVicinity());

        // - Set Mate number that already decide to eat at this restaurant
        this.restMateNumber.setText("3");  // need a test from firebase data

        // - Set Opening hours
        if(restaurant.getOpening_hours() != null){
            this.restOpeningHours.setText(restaurant.getOpening_hours());
        } else {
            this.restOpeningHours.setTextColor(context.getResources().getColor(R.color.colorError));
            this.restOpeningHours.setText(R.string.opening_hours_status);
        }


        // - Set Rank from friends opinion
        this.restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_black_24dp)); // need a test from firebase data

        // - Set Main Photo if it exist
        if(restaurant.getPhotos().size() > 0){
            photoRef = restaurant.getPhotos().get(0).getPhotoReference();
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=500&maxwidth=500&photoreference=" + photoRef + "&key=" + BuildConfig.PLACE_API_KEY;
            glide.load(url).into(restImage);
        }
        else {
            restImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_place_image_not_found));
        }
    }
}
