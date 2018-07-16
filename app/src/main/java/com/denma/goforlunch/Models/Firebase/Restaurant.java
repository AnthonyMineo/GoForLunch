package com.denma.goforlunch.Models.Firebase;


public class Restaurant {
    private String placeId;
    private int ranking;

    public Restaurant(){}

    public Restaurant(String placeid){
        this.placeId = placeid;
    }

    public Restaurant(String placeId, int ranking){
        this.placeId = placeId;
        this.ranking = ranking;
    }

    // --- GETTERS ---
    public String getPlaceId() {
        return placeId;
    }

    public int getRanking() {
        return ranking;
    }


    // --- SETTERS ---
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

}
