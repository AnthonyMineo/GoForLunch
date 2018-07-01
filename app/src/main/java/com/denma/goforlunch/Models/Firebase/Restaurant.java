package com.denma.goforlunch.Models.Firebase;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String placeId;
    private int ranking;
    @Nullable
    private List<String> luncherName = new ArrayList<String>();

    public Restaurant(){}

    public Restaurant(String placeId, int ranking, List<String> luncherName){
        this.placeId = placeId;
        this.ranking = ranking;
        this.luncherName = luncherName;
    }

    // --- GETTERS ---
    public String getPlaceId() {
        return placeId;
    }

    public int getRanking() {
        return ranking;
    }

    @Nullable
    public List<String> getLuncherName() {
        return luncherName;
    }

    // --- SETTERS ---
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void setLuncherName(@Nullable List<String> luncherName) {
        this.luncherName = luncherName;
    }
}
