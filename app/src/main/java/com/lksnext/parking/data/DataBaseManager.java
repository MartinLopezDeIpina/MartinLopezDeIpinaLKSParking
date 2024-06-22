package com.lksnext.parking.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import java.util.Objects;
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

    public LiveData<String> addBookingToDB(Reserva reserva){
        MutableLiveData<String> result = new MutableLiveData<>();
        DocumentReference docRef = db.collection("reserva").document();
        reserva.setId(docRef.getId());
        docRef.set(reserva)
                .addOnSuccessListener(aVoid -> result.setValue(docRef.getId()))
                .addOnFailureListener(e -> result.setValue(null));
        return result;
    }

    public LiveData<String> addReservaCompuestaToDB(String userUuid, List<String> reservasID, Long plazaID,Hora hora){
        MutableLiveData<String> result = new MutableLiveData<>();
        DocumentReference docRef = db.collection("reservaCompuesta").document();
        docRef.set(new ReservaCompuesta(userUuid, reservasID, plazaID, hora))
            .addOnSuccessListener(aVoid -> {
                String id = docRef.getId();
                docRef.update("id", id);
                result.setValue(id);
            })
            .addOnFailureListener(e -> result.setValue(null));
        return result;
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

    //primer valor: reservas, segundo valor: reservas compuestas
    public LiveData<Object[]> getCurrentUserBookings(){
        MutableLiveData<Object[]> result = new MutableLiveData<>();
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
                                result.setValue(new Object[]{reservas, reservasCompuestas});
                            }
                        });
                }
            });
        return result;
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

                db.collection("reserva")
                        .whereEqualTo("tipoPlaza", tipoPlaza)
                        .whereEqualTo("fecha", dia)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<Reserva> reservas = task.getResult().toObjects(Reserva.class);
                                setValue(reservas);
                            } else {
                                Log.d("DataBaseManager", "Error getting documents: ", task.getException());
                                setValue(null);
                            }
                        });
            }
        };
    }

    public LiveData<List<Reserva>> getBookingsSpotTypeDayAndHours(TipoPlaza tipoPlaza, List<String> dias, String horaInicio, String horaFin){
        List<String> horas = new ArrayList<>();
       return new LiveData<List<Reserva>>(){

           @Override
           protected void onActive(){
               //si la reserva empieza antes y acaba después de que empiece la hora especificada
               Task<QuerySnapshot> task1 = db.collection("reserva")
                       .whereEqualTo("tipoPlaza", tipoPlaza)
                       .whereIn("fecha", dias)
                       .whereLessThan("hora.horaInicio", horaInicio)
                       .whereGreaterThan("hora.horaFin", horaInicio)
                       .get();

               //si la reserva empieza después y acaba antes de que acabe la hora especificada
               Task<QuerySnapshot> task2 = db.collection("reserva")
                       .whereEqualTo("tipoPlaza", tipoPlaza)
                       .whereIn("fecha", dias)
                       .whereGreaterThan("hora.horaInicio", horaInicio)
                       .whereLessThan("hora.horaFin", horaFin)
                       .get();

               //si la reserva empieza antes y acaba después de que acabe la hora especificada
               Task<QuerySnapshot> task3 = db.collection("reserva")
                       .whereEqualTo("tipoPlaza", tipoPlaza)
                       .whereIn("fecha", dias)
                       .whereLessThanOrEqualTo("hora.horaInicio", horaFin)
                       .whereGreaterThanOrEqualTo("hora.horaFin", horaFin)
                       .get();

               //si la reserva empieza después y acaba antes de que empiece la hora especificada
               Task<QuerySnapshot> task4 = db.collection("reserva")
                       .whereEqualTo("tipoPlaza", tipoPlaza)
                       .whereIn("fecha", dias)
                       .whereGreaterThanOrEqualTo("hora.horaInicio", horaInicio)
                       .whereLessThanOrEqualTo("hora.horaFin", horaFin)
                       .get();

               Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(task1, task2, task3, task4);


               combinedTask.addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       List<Reserva> reservasTask1 = new ArrayList<>();
                       List<Reserva> reservasTask2 = new ArrayList<>();
                       List<Reserva> reservasTask3 = new ArrayList<>();
                       List<Reserva> reservasTask4 = new ArrayList<>();
                       List<QuerySnapshot> results = task.getResult();

                       for (DocumentSnapshot document : results.get(0).getDocuments()) {
                           reservasTask1.add(document.toObject(Reserva.class));
                       }

                       for (DocumentSnapshot document : results.get(1).getDocuments()) {
                           reservasTask2.add(document.toObject(Reserva.class));
                       }

                       for (DocumentSnapshot document : task3.getResult().getDocuments()) {
                           reservasTask3.add(document.toObject(Reserva.class));
                       }
                       for (DocumentSnapshot document : task4.getResult().getDocuments()) {
                           reservasTask4.add(document.toObject(Reserva.class));
                       }

                       Set<Reserva> reservasSet = new HashSet<>(reservasTask1);
                       reservasSet.addAll(reservasTask2);
                       reservasSet.addAll(reservasTask3);
                       reservasSet.addAll(reservasTask4);

                       setValue(new ArrayList<>(reservasSet));
                   } else {
                       Log.d("DataBaseManager", "Error getting documents: ", task.getException());
                       setValue(new ArrayList<>());
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

    public LiveData<Boolean> deleteBooking(String reservationID) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        Task task = db.collection("reserva").document(reservationID).delete();
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                result.setValue(true);
            } else {
                result.setValue(false);
            }
        });
        return result;
    }

    public void deleteReservaCompuesta(String id) {
        db.collection("reservaCompuesta").document(id).delete();
    }

    public LiveData<Boolean> deleteReservaCompuestaAndChilds(String reservationID) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        db.collection("reservaCompuesta").document(reservationID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReservaCompuesta reservaCompuesta = task.getResult().toObject(ReservaCompuesta.class);
                if (reservaCompuesta != null) {
                    List<String> reservasID = reservaCompuesta.getReservasID();
                    for (String reservaID : reservasID) {
                        db.collection("reserva").document(reservaID).delete();
                    }
                    db.collection("reservaCompuesta").document(reservationID).delete();
                    result.setValue(true);
                } else {
                    result.setValue(false);
                }
            } else {
                result.setValue(false);
            }
        });
        return result;
    }
}