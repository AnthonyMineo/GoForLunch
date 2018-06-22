package com.denma.goforlunch.Utils;


import com.denma.goforlunch.BuildConfig;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;
import com.denma.goforlunch.Models.GoogleAPI.Details.ResponseD;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsService {

    String apiKeyPlace = BuildConfig.PLACE_API_KEY;

    @GET("api/place/nearbysearch/json?key="+ apiKeyPlace)
    Observable<ResponseN> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);

    @GET("api/place/details/json?key="+ apiKeyPlace)
    Observable<ResponseD> getDetailPlaces(@Query("placeid") String placeid);

    @GET("api/place/photo?key="+ apiKeyPlace)
    Observable<String> getImageByReference(@Query("maxheight") int maxHeight, @Query("maxwidth") int maxWidth, @Query("photoreference") String photoReference);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
