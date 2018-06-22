package com.denma.goforlunch.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.denma.goforlunch.Models.Firebase.User;
import com.denma.goforlunch.R;

import java.util.List;

public class CoWorkerAdapter extends RecyclerView.Adapter<CoWorkerViewHolder>  {

    // FOR DATA
    private List<User> users;
    private RequestManager glide;
    private Context mContext;

    // CONSTRUCTOR
    public CoWorkerAdapter(List<User> users, RequestManager glide) {
        this.users = users;
        this.glide = glide;
    }

    @Override
    public CoWorkerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        this.mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.coworker_recycle_item, parent, false);

        return new CoWorkerViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A COWORKER
    @Override
    public void onBindViewHolder(CoWorkerViewHolder viewHolder, int position) {
        viewHolder.updateWithCoWorker(this.users.get(position), this.glide, this.mContext);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {
        return this.users.size();
    }

    public User getCoWorker(int position){
        return this.users.get(position);
    }
}
