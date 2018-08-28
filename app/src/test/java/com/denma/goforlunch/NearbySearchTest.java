package com.denma.goforlunch;

import com.denma.goforlunch.Models.GoogleAPI.Nearby.ResponseN;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.Utils.GoogleMapsService;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;


import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;


public class NearbySearchTest {

    public String loadJSONFromResources(String fileName) {
        String json = null;
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Test
    public void NearbySearchTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        Disposable disposable;

        final String[] name = new String[1];
        final String[] placeId = new String[1];
        final String[] vicinity = new String[1];
        final Double[] lat = new Double[1];
        final Double[] lng = new Double[1];
        final String[] photos = new String[1];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mockWebServer.enqueue(new MockResponse().setBody(loadJSONFromResources("NearbySearch.json")));

        GoogleMapsService service = retrofit.create(GoogleMapsService.class);

        Observable<ResponseN> fecthSearch = service.getNearbyPlaces("restaurant", 0 + "," + 0, 1000);

        disposable = fecthSearch.subscribeWith(new DisposableObserver<ResponseN>() {
            @Override
            public void onNext(ResponseN SearchResponse) {
                Result restaurant = SearchResponse.getResults().get(0);
                name[0] = restaurant.getName();
                placeId[0] = restaurant.getPlaceId();
                vicinity[0] = restaurant.getVicinity();
                lat[0] = restaurant.getGeometry().getLocation().getLat();
                lng[0] = restaurant.getGeometry().getLocation().getLng();
                photos[0] = restaurant.getPhotos().get(0).getPhotoReference();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        Double expectLat = -33.870775;
        Double expectLng = 151.199025;

        assertEquals("Rhythmboat Cruises", name[0]);
        assertEquals("ChIJyWEHuEmuEmsRm9hTkapTCrk", placeId[0]);
        assertEquals("Pyrmont Bay Wharf Darling Dr, Sydney", vicinity[0]);
        assertEquals(expectLat, lat[0]);
        assertEquals(expectLng, lng[0]);
        assertEquals("CnRnAAAAF-LjFR1ZV93eawe1cU_3QNMCNmaGkowY7CnOf-kcNmPhNnPEG9W979jOuJJ1sGr75rhD5hqKzjD8vbMbSsRnq_Ni3ZIGfY6hKWmsOf3qHKJInkm4h55lzv" +
                "LAXJVc-Rr4kI9O1tmIblblUpg2oqoq8RIQRMQJhFsTr5s9haxQ07EQHxoUO0ICubVFGYfJiMUPor1GnIWb5i8", photos[0]);

        disposable.dispose();
        mockWebServer.shutdown();

    }
}
