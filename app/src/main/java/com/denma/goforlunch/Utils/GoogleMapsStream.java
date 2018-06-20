package com.denma.goforlunch.Utils;


import com.denma.goforlunch.Models.GoogleAPI.Details.ResponseD;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleMapsStream {

    // - Create a stream that will get NearbyPlaces from Place API
    public static Observable<ResponseN> streamFetchNearbyPlaces(String type, String location, int radius){
        GoogleMapsService googleMapsService = GoogleMapsService.retrofit.create(GoogleMapsService.class);
        return googleMapsService.getNearbyPlaces(type, location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // - Create a stream that will get Detail from Place API
    public static Observable<ResponseD> streamFetchDetailPlaces(String placeid){
        GoogleMapsService googleMapsService = GoogleMapsService.retrofit.create(GoogleMapsService.class);
        return googleMapsService.getDetailPlaces(placeid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // Not use for the moment but may be useful in the futur
    // - Create a stream that will get main image of a place using his photo reference from Place API
    public static Observable<String> streamFetchPlaceMainImage(int maxHeight, int maxWidth, String photoReference){
        GoogleMapsService googleMapsService = GoogleMapsService.retrofit.create(GoogleMapsService.class);
        return googleMapsService.getImageByReference(maxHeight, maxWidth, photoReference)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }


}
