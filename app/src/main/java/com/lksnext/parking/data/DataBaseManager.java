package com.lksnext.parking.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.domain.Reserva;

import java.util.List;


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
    public void addSpotToDB(Plaza plaza){
        db.collection("plaza").document(Long.toString(plaza.getId())).set(plaza);
    }
    public void addBookingToDB(Reserva reserva){
        db.collection("reserva").document(reserva.getReservaID()).set(reserva);
    }

    public void getCurrenUser(UserCallback callback){
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("usuario").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Usuario usuario = task.getResult().toObject(Usuario.class);
                callback.onCallback(usuario);
            }
        });
    }
    public void getCurrentUserBookings(ReservaCallback callback){
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("reserva").whereEqualTo("userID", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Reserva> reservas = task.getResult().toObjects(Reserva.class);
                        callback.onCallback(reservas);
                    }
                });
    }

}