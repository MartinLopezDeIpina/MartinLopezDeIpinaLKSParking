package com.lksnext.parking.data;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth firebaseAuth;

    private FirebaseManager() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}
