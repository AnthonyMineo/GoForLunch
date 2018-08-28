package com.denma.goforlunch;

import com.denma.goforlunch.Models.GoogleAPI.Details.ResponseD;
import com.denma.goforlunch.Models.GoogleAPI.Details.Result;
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

public class DetailSearchTest {

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
    public void DetailSearchTest() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        Disposable disposable;

        final String[] phone = new String[1];
        final String[] webSite = new String[1];
        final String[] openTime = new String[1];
        final String[] closeTime = new String[1];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mockWebServer.enqueue(new MockResponse().setBody(loadJSONFromResources("DetailSearch.json")));

        GoogleMapsService service = retrofit.create(GoogleMapsService.class);

        Observable<ResponseD> fecthSearch = service.getDetailPlaces("ChIJyWEHuEmuEmsRm9hTkapTCrk");

        disposable = fecthSearch.subscribeWith(new DisposableObserver<ResponseD>() {
            @Override
            public void onNext(ResponseD SearchResponse) {
                Result restaurant = SearchResponse.getResult();
                phone[0] = restaurant.getPhoneNumber();
                webSite[0] = restaurant.getWebsite();
                openTime[0] = restaurant.getOpeningHours().getPeriods().get(0).getOpen().getTime();
                closeTime[0] = restaurant.getOpeningHours().getPeriods().get(0).getClose().getTime();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        assertEquals("(02) 9374 4000", phone[0]);
        assertEquals("https://www.google.com.au/about/careers/locations/sydney/", webSite[0]);
        assertEquals("2230", closeTime[0]);
        assertEquals("1200", openTime[0]);

        disposable.dispose();
        mockWebServer.shutdown();
    }
}
