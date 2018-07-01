package com.denma.goforlunch.Utils;

import com.denma.goforlunch.Models.Firebase.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String placeId, int ranking, List<String> luncherName){
        Restaurant restaurantToCreate = new Restaurant(placeId, ranking, luncherName);
        return RestaurantHelper.getRestaurantsCollection().document(placeId).set(restaurantToCreate);
    }

    // --- READ ---
    public static Task<DocumentSnapshot> getRestaurant(String placeId){
        return  RestaurantHelper.getRestaurantsCollection().document(placeId).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateRanking(String placeId, int ranking){
        return RestaurantHelper.getRestaurantsCollection().document(placeId).update("ranking", ranking);
    }

    public static Task<Void> updateLuncherName(String placeId, List<String> luncherName){
        return RestaurantHelper.getRestaurantsCollection().document(placeId).update("luncherName", luncherName);
    }

    // --- DELETE ---
    public static Task<Void> deleteRestaurant(String placeId){
        return RestaurantHelper.getRestaurantsCollection().document(placeId).delete();
    }
}
