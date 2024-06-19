package com.lksnext.parking.data;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lksnext.parking.domain.Hora;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.domain.Reserva;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


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
        db.collection("reserva").document(reserva.getId()).set(reserva);
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

    public void getBookings(ReservasCallback callback){
        db.collection("reserva").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Reserva> reservas = task.getResult().toObjects(Reserva.class);
                callback.onCallback(reservas);
            } else {
                callback.onCallback(null);
            }
        });
    }

    public List<Reserva> getBookingsSpotNotExpired(long plazaID) {
        try {
            Task<List<DocumentSnapshot>> task = db.collection("reserva")
                    .whereEqualTo("plazaID", plazaID)
                    .get()
                    .continueWith(task1 -> task1.getResult().getDocuments());

            List<DocumentSnapshot> documents = Tasks.await(task);

            List<Reserva> reservas = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                String fecha = document.getString("fecha");
                Hora hora = document.toObject(Hora.class);
                Reserva reserva = new Reserva(fecha, hora);
                if(!reserva.isCaducada()){
                    reservas.add(reserva);
                }
            }

            return reservas;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    //todo: hacer que las reservas solo tengan dos valores para ver si es más eficiente así
    public LiveData<List<Reserva>> getBookingsSpotDay(String dia, TipoPlaza tipoPlaza) {
        return new LiveData<List<Reserva>>() {
            @Override
            protected void onActive() {
                List<Integer> plazasID = Parking.getInstance().getPlazas().stream().filter(plaza -> plaza.getTipo().equals(tipoPlaza)).map(Plaza::getId).map(Long::intValue).collect(Collectors.toList());
                db.collection("reserva")
                        .whereEqualTo("fecha", dia)
                        .whereIn("plazaID", plazasID)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<Reserva> reservas = task.getResult().toObjects(Reserva.class);
                                setValue(reservas);
                            } else {
                                setValue(null);
                            }
                        });
            }
        };
    }

    public LiveData<Integer> getCountBookingsConflictingSpotTypeDayHour(String dia, String hora, TipoPlaza tipoPlaza){
        return new LiveData<Integer>() {
            @Override
            protected void onActive() {
                Task<QuerySnapshot> task1 = db.collection("reserva")
                    .whereEqualTo("fecha", dia)
                    .whereEqualTo("tipoPlaza", tipoPlaza)
                    .whereLessThanOrEqualTo("hora.horaInicio", hora)
                    .get();

                Task<QuerySnapshot> task2 = db.collection("reserva")
                    .whereEqualTo("fecha", dia)
                    .whereEqualTo("tipoPlaza", tipoPlaza)
                    .whereGreaterThan("hora.horaFin", hora)
                    .get();

                Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(task1, task2);

                combinedTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> uniqueIds = new HashSet<>();
                        for (QuerySnapshot querySnapshot : task.getResult()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                uniqueIds.add(document.getId());
                            }
                        }
                        setValue(uniqueIds.size());
                    } else {
                        setValue(0);
                    }
                });
            }
        };
    }
}