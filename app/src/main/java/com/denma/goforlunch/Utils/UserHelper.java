package com.denma.goforlunch.Utils;

import com.google.android.gms.tasks.Task;
import com.denma.goforlunch.Models.Firebase.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username, String mail, String urlPicture){
        User userToCreate = new User(uid, username, mail, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- READ ---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid){
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateMail(String uid, String mail){
        return UserHelper.getUsersCollection().document(uid).update("mail", mail);
    }

    // --- DELETE ---
    public static Task<Void> deleteUser(String uid){
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
