package com.lksnext.parking.data;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DataBaseManager {
    private static DataBaseManager instance;
    private static FirebaseFirestore db;

    private DataBaseManager() {
    }

    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
            db = FirebaseFirestore.getInstance();
        }
        return instance;
    }

    public void addUserToDB(String userUID, String name, String email, String phone){
        Map<String, Object> user = new HashMap<>();
        user.put("id", userUID);
        user.put("nombre", name);
        user.put("email", email);
        user.put("telefono", phone);

        db.collection("usuario").add(user);
    }

}