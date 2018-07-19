package com.denma.goforlunch.Views;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.denma.goforlunch.BuildConfig;
import com.denma.goforlunch.Models.Firebase.Restaurant;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.RestaurantHelper;
import com.denma.goforlunch.Utils.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void updateWithRestaurant(final Result restaurant, RequestManager glide, final Context context, double currentLat, double currentLng){
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
        RestaurantHelper.getRestaurantsCollection().document(restaurant.getPlaceId()).collection("luncherId").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                restMateNumber.setText("("+ String.valueOf(task.getResult().size()) + ")");
            }
        });


        // - Set Opening hours
        if(restaurant.getOpening_hours() != null){
            this.restOpeningHours.setText(restaurant.getOpening_hours());
        } else {
            this.restOpeningHours.setTextColor(context.getResources().getColor(R.color.colorError));
            this.restOpeningHours.setText(R.string.opening_hours_status);
        }

        // - Set Rank from friends opinion
        final int[] totalUsers = new int[1];
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                totalUsers[0] = task.getResult().size();

                RestaurantHelper.getRestaurant(restaurant.getPlaceId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Restaurant rest = task.getResult().toObject(Restaurant.class);
                        int rank = rest.getRanking();
                        if(rank == 0){
                            restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_0star_border_black_24dp)); // no Star
                        } else if (rank < totalUsers[0] * 0.2){
                            restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_1star_border_black_24dp)); // one Star
                        }  else if (rank < totalUsers[0] * 0.4){
                            restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_2star_border_black_24dp)); // two Star
                        } else if (rank < totalUsers[0] * 0.6){
                            restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_3star_border_black_24dp)); // three Star
                        } else if (rank < totalUsers[0] * 0.8){
                            restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_4star_border_black_24dp)); // four Star
                        }  else {
                            restRankImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_5star_border_black_24dp)); // five Star
                        }
                    }
                });
            }
        });

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
