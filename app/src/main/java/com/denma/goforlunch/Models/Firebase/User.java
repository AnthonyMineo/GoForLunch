package com.denma.goforlunch.Models.Firebase;

import android.support.annotation.Nullable;

public class User {
    private String uid;
    private String username;
    private String mail;
    @Nullable private String urlPicture;
    private String lunchRestaurant;

    public User(){}

    public User(String uid, String username,String mail, String urlPicture, String lunchRestaurant){
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.mail = mail;
        this.lunchRestaurant = lunchRestaurant;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getMail() { return mail; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public String getLunchRestaurant() { return lunchRestaurant; }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setMail(String mail) { this.mail = mail; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setLunchRestaurant(String lunchRestaurant) { this.lunchRestaurant = lunchRestaurant; }
}
