package com.denma.goforlunch.Controllers.Activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.denma.goforlunch.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    // FOR DESIGN

    // FOR PERMISSIONS
    protected static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    protected static final int RC_LOCATION_PERMS = 100;
    protected static final String PERMS2 = Manifest.permission.ACCESS_COARSE_LOCATION;
    protected static final int RC_LOCATION_PERMS2 = 200;

    // FOR DATA

    // --------------------
    // CREATION
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getActivityLayout());
        ButterKnife.bind(this); //Configure Butterknife
    }

    // --------------------
    // GETTERS
    // --------------------

    protected abstract int getActivityLayout();

    // --------------------
    // SETTERS
    // --------------------

    // --------------------
    // MENU
    // --------------------

    // --------------------
    // ACTIONS
    // --------------------

    // --------------------
    // UTILS
    // --------------------

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    protected void disconnectUser(){ FirebaseAuth.getInstance().signOut(); }


    // --------------------
    // NAVIGATION
    // --------------------

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

    // --------------------
    // LIFE CYCLE
    // --------------------

}