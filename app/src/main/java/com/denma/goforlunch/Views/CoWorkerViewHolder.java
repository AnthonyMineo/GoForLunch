package com.denma.goforlunch.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.denma.goforlunch.Models.Firebase.User;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

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

    public void updateWithCoWorker(final User user, RequestManager glide, Context context, boolean isFromDetail) {

        // - Set User Image
        try{
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(userImage);
        } catch (Exception e){
            e.printStackTrace();
        }

        // - Set User Choice
        if(isFromDetail == true){
            this.userChoice.setText(user.getUsername() + " is joining !" );
        } else {
            UserHelper.getUser(user.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User fireUser = task.getResult().toObject(User.class);
                    userChoice.setText(fireUser.getUsername() + " is eating at " + fireUser.getLunchRestaurantName());
                }
            });
        }


    }
}
