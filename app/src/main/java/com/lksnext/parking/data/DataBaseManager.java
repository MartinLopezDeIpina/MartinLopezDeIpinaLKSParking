package com.lksnext.parking.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.lksnext.parking.domain.Usuario;

import java.util.HashMap;
import java.util.Map;

public class DataBaseManager {
    private static DataBaseManager instance;
    private static FirebaseFirestore db;
    private static FirebaseAuth mAuth;

    private DataBaseManager() {
    }

    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
        }
        return instance;
    }

    public void addUserToDB(Usuario usuario){
        db.collection("usuario").document(usuario.getID()).set(usuario);
    }

    public void setCurrenUser(UserCallback callback){
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("usuario").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Usuario usuario = task.getResult().toObject(Usuario.class);
                callback.onCallback(usuario);
            }
        });
    }
}