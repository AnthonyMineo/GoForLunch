package com.denma.goforlunch.Controllers.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denma.goforlunch.BuildConfig;
import com.denma.goforlunch.Models.Firebase.User;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.ItemClickSupport;
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
    @BindView(R.id.restaurant_name)
    TextView restName;
    @BindView(R.id.restaurant_ranking)
    ImageView restRanking;
    @BindView(R.id.restaurant_location)
    TextView restLocation;
    @BindView(R.id.activity_restaurant_detail_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_restaurant_detail_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "RestDetail_Activity"; // - RestaurantDetail Activity ID for log
    private Result currentRest;
    private List<User> users;
    private User currentUser;
    public CoWorkerAdapter mCoworkerAdapter;
    private boolean imIn;

    // --------------------
    // CREATION
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureSwipeRefreshLayout();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
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

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUser();
            }
        });
    }

    // - Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(){
        // - Reset list
        this.users = new ArrayList<>();
        // - Create adapter passing the list of Restaurants
        this.mCoworkerAdapter = new CoWorkerAdapter(this.users, Glide.with(this));
        // - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.mCoworkerAdapter);
        // - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantDetailActivity.this));
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.coworker_recycle_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // 1 - Get user from adapter
                        User user = mCoworkerAdapter.getCoWorker(position);
                        // 2 - Do something
                        Toast.makeText(RestaurantDetailActivity.this, "You click on : " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void configureUI(){
        // - Get the current restaurant from the intent's extra
        this.currentRest = (Result) getIntent().getSerializableExtra("restaurant");
        UserHelper.getUser(getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                currentUser = task.getResult().toObject(User.class);

                // - Style for floating button
                if(currentRest.getPlaceId().equals(currentUser.getLunchRestaurant())){
                    imIn = true;
                    floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_green_48));
                }
                else {
                    imIn = false;
                    floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_grey_48));
                }
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
            /* soon */

        // - Set the restaurant address
        restLocation.setText(currentRest.getVicinity());
    }

    // Set the user that will eat to this restaurant
    public void updateUser() {
        users.clear();

        // for the moment it just show all user
        UserHelper.getUsersCollection().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot user : task.getResult()) {
                            // - Need a test to avoid to display currentUser and double account
                            users.add(user.toObject(User.class));
                            Log.e(TAG, "update user");
                        }
                        mCoworkerAdapter.notifyDataSetChanged();
                        Log.e(TAG, "notif done");
                    }
                });
    }

    @OnClick(R.id.floating_button_go)
    public void floatingButtonClick(){
        // - Style for floating button
        if(imIn){
            floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_green_48));
            UserHelper.updateLunch(getCurrentUser().getUid(), currentRest.getPlaceId()).addOnFailureListener(this.onFailureListener());
            imIn = false;
        } else {
            floatingButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_check_circle_grey_48));
            UserHelper.updateLunch(getCurrentUser().getUid(), null).addOnFailureListener(this.onFailureListener());
            imIn = true;
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
        // soon !
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
