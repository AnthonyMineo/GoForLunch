package com.denma.goforlunch.Utils;

import com.denma.goforlunch.Models.GoogleAPI.Response;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleMapsStream {

    // - Create a stream that will get top stories article from NYT Top Stories API
    public static Observable<Response> streamFetchNearbyPlaces(String type, String location, int radius){
        GoogleMapsService googleMapsService = GoogleMapsService.retrofit.create(GoogleMapsService.class);
        return googleMapsService.getNearbyPlaces(type, location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
