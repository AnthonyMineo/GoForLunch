package com.denma.goforlunch.Controllers.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denma.goforlunch.BuildConfig;
import com.denma.goforlunch.Models.Firebase.User;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.Notifications.NotificationAlarm;

import com.denma.goforlunch.Utils.RestaurantHelper;
import com.denma.goforlunch.Utils.UserHelper;
import com.denma.goforlunch.Views.CoWorkerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RestaurantDetailActivity extends BaseActivity {

    // FOR DESIGN
    @BindView(R.id.restaurant_main_image)
    ImageView restImage;
    @BindView(R.id.floating_button_go)
    FloatingActionButton floatingButton;
    @BindView(R.id.restaurant_like)
    ImageView restLike;
    @BindView(R.id.restaurant_name)
    TextView restName;
    @BindView(R.id.restaurant_ranking)
    ImageView restRanking;
    @BindView(R.id.restaurant_location)
    TextView restLocation;
    @BindView(R.id.activity_restaurant_detail_recycler_view)
    RecyclerView recyclerView;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "RestDetail_Activity"; // - RestaurantDetail Activity ID for log
    private Result currentRest;
    private List<User> users;
    private User currentUser;
    public CoWorkerAdapter mCoworkerAdapter;
    private boolean imIn;
    private boolean iLike;
    private Context mContext;
    private NotificationAlarm mNotificationAlarm;

    // --------------------
    // CREATION
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getApplicationContext();
        this.configureRecyclerView();
        this.configureUI();
        this.updateUser();
    }

    // --------------------
    // GETTERS
    // --------------------

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_restaurant_detail;
    }

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
        this.users = new ArrayList<>();
        // - Create adapter passing the list of Restaurants
        this.mCoworkerAdapter = new CoWorkerAdapter(this.users, Glide.with(this), true);
        // - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.mCoworkerAdapter);
        // - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantDetailActivity.this));
    }

    private void configureUI(){
        this.mNotificationAlarm = new NotificationAlarm(mContext);
        // - Get the current restaurant from the intent's extra
        this.currentRest = (Result) getIntent().getSerializableExtra("restaurant");
        UserHelper.getUser(getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                currentUser = task.getResult().toObject(User.class);
                // - Style for floating button
                if(currentRest.getPlaceId().equals(currentUser.getLunchRestaurantId())){
                    imIn = true;
                    floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_green_48));
                }
                else {
                    imIn = false;
                    floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_grey_48));
                }

                UserHelper.getCollectionFromAUser(currentUser.getUid(), "restLike").addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot docSnap : task.getResult()){
                            if (docSnap.getId().equals(currentRest.getPlaceId())) {
                                iLike = true;
                            }
                        }
                        if (iLike){
                            restLike.setBackgroundColor(getResources().getColor(R.color.colorGold));
                        } else {
                            iLike = false;
                            restLike.setBackgroundColor(0);
                        }
                    }
                });
            }
        });

        // - Set the restaurant main image
        if(currentRest.getPhotos().size() > 0){
            String photoRef = currentRest.getPhotos().get(0).getPhotoReference();
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=500&maxwidth=500&photoreference=" + photoRef + "&key=" + BuildConfig.PLACE_API_KEY;
            Glide.with(this).load(url).into(restImage);
        }
        else {
            restImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_place_image_not_found));
        }

        // - Set the restaurant name
        restName.setText(currentRest.getName());

        // Set the restaurant ranking
        // - Set Rank from friends opinion
        final int[] totalUsers = new int[1];
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                totalUsers[0] = task.getResult().size();

                RestaurantHelper.getRestaurant(currentRest.getPlaceId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Result temp = task.getResult().toObject(Result.class);
                        int rank = temp.getRanking();
                        if (rank == 0) {
                            restRanking.setImageDrawable(getResources().getDrawable(R.drawable.ic_0star_border_black_24dp)); // no Star
                        } else if (rank < totalUsers[0] * 0.33) {
                            restRanking.setImageDrawable(getResources().getDrawable(R.drawable.ic_1star_border_black_24dp)); // one Star
                        } else if (rank < totalUsers[0] * 0.66) {
                            restRanking.setImageDrawable(getResources().getDrawable(R.drawable.ic_2star_border_black_24dp)); // two Star
                        } else {
                            restRanking.setImageDrawable(getResources().getDrawable(R.drawable.ic_3star_border_black_24dp)); // three Star
                        }
                    }
                });
            }
        });

        // - Set the restaurant address
        restLocation.setText(currentRest.getVicinity());
    }

    // Set the user that will eat to this restaurant
    public void updateUser() {
        users.clear();
        RestaurantHelper.getCollectionFromARestaurant(currentRest.getPlaceId(), "luncherId").addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot docSnap : task.getResult()) {
                    UserHelper.getUser(docSnap.getId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(!task.getResult().getId().equals(currentUser.getUid())){
                                users.add(task.getResult().toObject(User.class));
                                mCoworkerAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(this.onFailureListener());
    }

    @OnClick(R.id.floating_button_go)
    public void floatingButtonClick(){
        // - Style for floating button
        if(imIn){
            floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_grey_48));
            UserHelper.updateLunchId(getCurrentUser().getUid(), "").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    UserHelper.updateLunchName(getCurrentUser().getUid(), "");
                    RestaurantHelper.deleteLuncherId(currentRest.getPlaceId(), getCurrentUser().getUid());
                }
            }).addOnFailureListener(this.onFailureListener());
            imIn = false;
            // - Update the lunching restaurant of current user and update his pending intent for notification with a valid Restaurant object
            mNotificationAlarm.updateLunchingRestaurant(null);

        } else {
            floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_green_48));
            UserHelper.updateLunchId(getCurrentUser().getUid(), currentRest.getPlaceId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    UserHelper.updateLunchName(getCurrentUser().getUid(), currentRest.getName());
                    RestaurantHelper.getRestaurantsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot docSnap : task.getResult()){
                                RestaurantHelper.deleteLuncherId(docSnap.getId(), getCurrentUser().getUid());
                            }
                            RestaurantHelper.addLuncherId(currentRest.getPlaceId() , getCurrentUser().getUid());
                        }
                    });
                }
            }).addOnFailureListener(this.onFailureListener());
            imIn = true;
            // - Update the lunching restaurant of current user and update his pending intent for notification with a valid Restaurant object
            mNotificationAlarm.updateLunchingRestaurant(currentRest);
        }
    }

    @OnClick(R.id.restaurant_phone_call)
    public void phoneCallClick(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + currentRest.getPhoneNumber()));
        startActivity(callIntent);
    }

    @OnClick(R.id.restaurant_like)
    public void likeClick(){
        if(iLike){
            restLike.setBackgroundColor(0);
            UserHelper.deleteLike(getCurrentUser().getUid(), currentRest.getPlaceId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    RestaurantHelper.decRanking(currentRest.getPlaceId());
                    iLike = false;
                }
            }).addOnFailureListener(this.onFailureListener());

        } else {
            restLike.setBackgroundColor(getResources().getColor(R.color.colorGold));
            UserHelper.addLike(getCurrentUser().getUid(), currentRest.getPlaceId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    RestaurantHelper.incRanking(currentRest.getPlaceId());
                    iLike = true;
                }
            });
        }
    }

    @OnClick(R.id.restaurant_website)
    public void websiteClick(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(currentRest.getWebsite()));
        startActivity(i);
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

}
