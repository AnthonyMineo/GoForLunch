package com.denma.goforlunch.Models.Firebase;




public class Restaurant {
    private String placeId;
    private String placeName;
    private int ranking;

    public Restaurant(){}

    public Restaurant(String placeId, int ranking, String placeName){
        this.placeId = placeId;
        this.ranking = ranking;
        this.placeName = placeName;
    }


    // --- GETTERS ---
    public String getPlaceId() {
        return placeId;
    }

    public int getRanking() {
        return ranking;
    }

    public String getPlaceName() {
        return placeName;
    }

    // --- SETTERS ---
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
