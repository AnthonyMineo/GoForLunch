package com.denma.goforlunch.Models.Firebase;

import android.support.annotation.Nullable;

import java.io.Serializable;


public class User implements Serializable{
    private String uid;
    private String username;
    private String mail;
    @Nullable private String urlPicture;
    private String lunchRestaurantId;
    private String lunchRestaurantName;


    public User(){}

    public User(String uid, String username, String mail, String urlPicture, String lunchRestaurantId, String lunchRestaurantName){
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.mail = mail;
        this.lunchRestaurantId = lunchRestaurantId;
        this.lunchRestaurantName = lunchRestaurantName;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getMail() { return mail; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public String getLunchRestaurantId() { return lunchRestaurantId; }
    public String getLunchRestaurantName() {
        return lunchRestaurantName;
    }

    // --- SETTERS ---

    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
    public void setLunchRestaurantId(String lunchRestaurant) {
        this.lunchRestaurantId = lunchRestaurant;
    }
    public void setLunchRestaurantName(String lunchRestaurantName) {
        this.lunchRestaurantName = lunchRestaurantName;
    }
}
