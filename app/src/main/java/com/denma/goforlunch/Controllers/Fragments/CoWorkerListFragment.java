package com.denma.goforlunch.Controllers.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denma.goforlunch.Models.Firebase.User;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.ItemClickSupport;
import com.denma.goforlunch.Utils.UserHelper;
import com.denma.goforlunch.Views.CoWorkerAdapter;
import com.denma.goforlunch.Views.RestaurantAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CoWorkerListFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.fragment_co_worker_list_recycler_view)
    RecyclerView recyclerView;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "CoWorker_Fragment"; // - CoWorker Fragment ID for log
    private List<User> users;
    public CoWorkerAdapter mCoworkerAdapter;

    // --------------------
    // CREATION
    // --------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view); //Configure Butterknife
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        this.updateUI();
        Log.e(TAG, "onCreate");
        return view;
    }

    public CoWorkerListFragment() { }

    public static CoWorkerListFragment newInstance() { return new CoWorkerListFragment(); }

    // --------------------
    // GETTERS
    // --------------------

    @Override
    public int getFragmentLayout() { return R.layout.fragment_co_worker_list; }

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
        this.mCoworkerAdapter = new CoWorkerAdapter(this.users, Glide.with(this));
        // - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.mCoworkerAdapter);
        // - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.coworker_recycle_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // 1 - Get user from adapter
                        User user = mCoworkerAdapter.getCoWorker(position);
                        // 2 - Do something
                        Toast.makeText(getContext(), "You click on : " +user.getUsername(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateUI() {
        users.clear();
        UserHelper.getUsersCollection().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           for (DocumentSnapshot user : task.getResult()){
                               // - Need a test to avoid to display currentUser and double account
                               users.add(user.toObject(User.class));
                               Log.e(TAG, "update user");
                           }
                           mCoworkerAdapter.notifyDataSetChanged();
                           Log.e(TAG, "notif done");
                       }
                });
    }

    @Override
    public void updateUI(ResponseN response) { }

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

    // Docs suggest to override them
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
