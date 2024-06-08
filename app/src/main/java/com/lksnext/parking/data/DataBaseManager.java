package com.lksnext.parking.data;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.domain.Reserva;
import com.google.android.gms.tasks.Tasks;

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
    public Task<String> addBookingToDB(Reserva reserva){
        return db.collection("reserva").add(reserva).continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().getId();
            } else {
                throw task.getException();
            }
        });
    }
    public void addReservaCompuestaToDB(ReservaCompuesta reservaCompuesta){
        db.collection("reservaCompuesta").add(reservaCompuesta);
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
    public void getParkingSpots(PlazaCallback callback){
        db.collection("plaza")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Plaza> plazas = task.getResult().toObjects(Plaza.class);
                        callback.onCallback(plazas);
                    }
                });
    }

    public void getCurrentUserBookings(ReservaCallback callback){
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("reserva")
            .whereEqualTo("usuarioID", uid)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Reserva> reservas = task.getResult().toObjects(Reserva.class);
                    db.collection("reservaCompuesta")
                        .whereEqualTo("usuarioID", uid)
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                List<ReservaCompuesta> reservasCompuestas = task2.getResult().toObjects(ReservaCompuesta.class);
                                callback.onCallback(reservas, reservasCompuestas);
                            }
                        });
                }
            });
}

}