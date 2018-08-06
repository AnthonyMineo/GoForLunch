package com.denma.goforlunch.Controllers.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.denma.goforlunch.Models.Firebase.User;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    // FOR DESIGN
    @BindView(R.id.user_picture)
    ImageView user_picture;
    @BindView(R.id.user_name_edit_text)
    EditText user_name;
    @BindView(R.id.user_mail_edit_text)
    EditText user_mail;
    @BindView(R.id.notications_switch)
    Switch notif_switch;
    @BindView(R.id.settings_activity_save_changes)
    Button save_changes;

    // FOR PERMISSIONS

    // FOR DATA
    private static final String TAG = "Settings_Activity"; // - Activity ID for log
    private User currentUser;

    // --------------------
    // CREATION
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureUI();
        //this.configureListener();

        Log.e(TAG, "onCreate");
    }

    // --------------------
    // GETTERS
    // --------------------

    @Override
    public int getActivityLayout() { return R.layout.activity_settings; }

    // --------------------
    // SETTERS
    // --------------------

    // --------------------
    // MENU
    // --------------------

    // --------------------
    // ACTIONS
    // --------------------

    private void configureUI() {
        // - Get the current restaurant from the intent's extra
        this.currentUser = (User) getIntent().getSerializableExtra("currentUser");
        user_name.setText(currentUser.getUsername());
        user_mail.setText(currentUser.getMail());
        // - Set User Image
        if(currentUser.getUrlPicture() != null){
            try{
                Glide.with(this).load(currentUser.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(user_picture);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try{
                Glide.with(this).load("https://abs.twimg.com/sticky/default_profile_images/default_profile_normal.png").apply(RequestOptions.circleCropTransform()).into(user_picture);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /*
    private void configureListener(){
        notif_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {

                }
                    if(queryTerm.isEmpty()){
                        Toast.makeText(getContext(), R.string.error_query, Toast.LENGTH_SHORT).show();
                        mySwitch.setChecked(false);
                    }
                    else if(newsDesk == "") {
                        Toast.makeText(getContext(), R.string.error_category, Toast.LENGTH_SHORT).show();
                        mySwitch.setChecked(false);
                    }
                    else {
                        startAlarm();
                        mPreferences.edit().putBoolean("switchChecked", true).apply();
                    }
                }
                else {
                    stopAlarm();
                    mPreferences.edit().putBoolean("switchChecked", false).apply();
                }

    }*/

    @OnClick(R.id.settings_activity_save_changes)
    public void saveChanges(){
        UserHelper.updateUsername(user_name.getText().toString(), currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                UserHelper.updateMail(user_mail.getText().toString(), currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateDone();
                    }
                });
            }
        }).addOnFailureListener(this.onFailureListener());
    }

    private void updateDone(){
        Toast.makeText(this, "update done !", Toast.LENGTH_SHORT).show();
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
