package com.denma.goforlunch.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.denma.goforlunch.Models.Firebase.User;
import com.denma.goforlunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoWorkerViewHolder extends RecyclerView.ViewHolder {

    // FOR DESIGN
    @BindView(R.id.coworker_recycle_item_user_image) ImageView userImage;
    @BindView(R.id.coworker_recycle_item_user_choice) TextView userChoice;

    public CoWorkerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithCoWorker(User user, RequestManager glide, Context context) {

        // - Set User Image
        try{
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(userImage);
        } catch (Exception e){
            e.printStackTrace();
        }


        // - Set User Choice
        this.userChoice.setText("this will change soon, thx to firebase");
    }
}
