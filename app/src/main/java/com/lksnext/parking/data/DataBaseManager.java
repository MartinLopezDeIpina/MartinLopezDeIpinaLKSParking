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

    public Usuario getCurrenUser(){
        String uid = mAuth.getCurrentUser().getUid();
        return getUserFromDB(uid);
    }
    private Usuario getUserFromDB(String uid){
        return null;
    }

}