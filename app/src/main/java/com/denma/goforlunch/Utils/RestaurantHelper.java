package com.denma.goforlunch.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String placeId, int ranking, String placeName, String vicinity){
        Result restaurantToCreate = new Result();
        restaurantToCreate.setPlaceId(placeId);
        restaurantToCreate.setRanking(ranking);
        restaurantToCreate.setName(placeName);
        restaurantToCreate.setVicinity(vicinity);
        return RestaurantHelper.getRestaurantsCollection().document(placeId).set(restaurantToCreate);
    }

    // --- READ ---
    public static Task<DocumentSnapshot> getRestaurant(String placeId){
        return  RestaurantHelper.getRestaurantsCollection().document(placeId).get();
    }

    public static Task<QuerySnapshot> getCollectionFromARestaurant(String placeId, String collection){
        return RestaurantHelper.getRestaurantsCollection().document(placeId).collection(collection).get();
    }

    // --- UPDATE ---
    public static Task<Void> addLuncherId(String placeId, String luncherId){
        Map<String, Object> mock = new HashMap<>();
        return RestaurantHelper.getRestaurantsCollection().document(placeId).collection("luncherId").document(luncherId).set(mock);
    }

    public static void incRanking(final String placeId){
        Log.e("incRanking", "start");
        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>(){
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot docSnap = transaction.get(RestaurantHelper.getRestaurantsCollection().document(placeId));
                Result rest = docSnap.toObject(Result.class);
                int rank =  rest.getRanking() + 1;
                transaction.update(RestaurantHelper.getRestaurantsCollection().document(placeId), "ranking", rank);
                Log.e("incRanking", "+1");
                return null;
            }
        });
    }

    public static void decRanking(final String placeId){
        Log.e("decRanking", "start");

        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>(){
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot docSnap = transaction.get(RestaurantHelper.getRestaurantsCollection().document(placeId));
                Result rest = docSnap.toObject(Result.class);
                int rank = rest.getRanking() - 1;
                transaction.update(RestaurantHelper.getRestaurantsCollection().document(placeId), "ranking", rank);
                Log.e("decRanking", "-1");
                return null;
            }
        });
    }

    // --- DELETE ---
    public static Task<Void> deleteRestaurant(String placeId){
        return RestaurantHelper.getRestaurantsCollection().document(placeId).delete();
    }

    public static  Task<Void> deleteLuncherId(String placeId, String luncherId){
        return RestaurantHelper.getRestaurantsCollection().document(placeId).collection("luncherId").document(luncherId).delete();
    }
}
