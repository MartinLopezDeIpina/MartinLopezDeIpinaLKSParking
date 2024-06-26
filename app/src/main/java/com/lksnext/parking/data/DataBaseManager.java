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
import com.lksnext.parking.domain.Notificacion;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.domain.Reserva;
import com.google.android.gms.tasks.Tasks;
import com.lksnext.parking.util.DateUtils;
import com.lksnext.parking.util.notifications.NotificationsManager;

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


    public LiveData<Boolean> addUserToDB(Usuario usuario){
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        db.collection("usuario").document(usuario.getID()).set(usuario)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
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

    public LiveData<String> addBookingWithIDToDB(Reserva reserva){
        MutableLiveData<String> result = new MutableLiveData<>();
        DocumentReference docRef = db.collection("reserva").document(reserva.getId());
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

    public static void addNotificationToDB(String id, int requestCode) {
        db.collection("notification").document().set(new Notificacion(requestCode, id));
    }

    //Para cuando se cancele una reserva
    public static LiveData<List<Notificacion>> getAndDeleteNotificationsFromDB(String reservaID){
        MutableLiveData<List<Notificacion>> result = new MutableLiveData<>();
        db.collection("notification")
            .whereEqualTo("reservaID", reservaID)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Notificacion> notifications = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Notificacion notificacion = document.toObject(Notificacion.class);
                        notifications.add(notificacion);
                        db.collection("notification").document(document.getId()).delete();
                    }
                    result.postValue(notifications);
                } else {
                    Log.d("DataBaseManager", "Error getting documents: ", task.getException());
                }
            });
        return result;
    }

    public LiveData<Usuario> getCurrenUser(){
        MutableLiveData<Usuario> result = new MutableLiveData<>();
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("usuario").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Usuario usuario = task.getResult().toObject(Usuario.class);
                result.setValue(usuario);
            }else{
                result.setValue(null);
            }
        });
        return result;
    }
    public LiveData<List<Plaza>> getParkingSpots(){
        MutableLiveData<List<Plaza>> result = new MutableLiveData<>();
        db.collection("plaza")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Plaza> plazas = task.getResult().toObjects(Plaza.class);
                        result.setValue(plazas);
                    }else{
                        result.setValue(new ArrayList<>());
                    }
                });
        return result;
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

    public LiveData<Boolean> deleteBooking(String reservationID) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        Task<Void> task1 = db.collection("reserva").document(reservationID).delete();

        Reserva reserva = Parking.getInstance().getReserva(reservationID);
        Task<Void> task2 = null;
        //eliminar la reserva de su reserva múltiple
        if(reserva.isInsideReservaMultiple()){
            ReservaCompuesta reservaCompuesta = Parking.getInstance().getReservaCompuestaThatContains(reservationID);
            reservaCompuesta.getReservasID().remove(reservationID);
            if(!reservaCompuesta.getReservasID().isEmpty()){
                task2 = db.collection("reservaCompuesta").document(reservaCompuesta.getId()).set(reservaCompuesta);
            }else{
                task2 = db.collection("reservaCompuesta").document(reservaCompuesta.getId()).delete();
            }
        }

        if (task2 != null) {
            Task combinedTask = Tasks.whenAllSuccess(task1, task2);
            combinedTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    result.setValue(true);
                } else {
                    result.setValue(false);
                }
            });
        } else {
            task1.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    result.setValue(true);
                } else {
                    result.setValue(false);
                }
            });
        }

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

    public LiveData<Integer[]> getPlazasOcupadas() {
        MutableLiveData<Integer[]> result = new MutableLiveData<>();
        String dia = DateUtils.getTodayString();
        String hora = DateUtils.getNowHourString();
        Task<QuerySnapshot> taskCoches = db.collection("reserva")
                .whereEqualTo("fecha", dia)
                .whereEqualTo("tipoPlaza", TipoPlaza.COCHE)
                .whereLessThan("hora.horaInicio", hora)
                .whereGreaterThan("hora.horaFin", hora)
                .get();

        Task<QuerySnapshot> taskMotos = db.collection("reserva")
                .whereEqualTo("fecha", dia)
                .whereEqualTo("tipoPlaza", TipoPlaza.MOTO)
                .whereLessThan("hora.horaInicio", hora)
                .whereGreaterThan("hora.horaFin", hora)
                .get();

        Task<QuerySnapshot> taskElectricos = db.collection("reserva")
                .whereEqualTo("fecha", dia)
                .whereEqualTo("tipoPlaza", TipoPlaza.ELECTRICO)
                .whereLessThan("hora.horaInicio", hora)
                .whereGreaterThan("hora.horaFin", hora)
                .get();

        Task<QuerySnapshot> taskEspeciales = db.collection("reserva")
                .whereEqualTo("fecha", dia)
                .whereEqualTo("tipoPlaza", TipoPlaza.DISCAPACITADO)
                .whereLessThan("hora.horaInicio", hora)
                .whereGreaterThan("hora.horaFin", hora)
                .get();

        Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(taskCoches, taskMotos, taskElectricos, taskEspeciales);

        combinedTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<QuerySnapshot> results = task.getResult();
                Integer[] counts = new Integer[4];
                counts[0] = results.get(0).size();
                counts[1] = results.get(1).size();
                counts[2] = results.get(2).size();
                counts[3] = results.get(3).size();
                result.setValue(counts);
            } else {
                Log.d("DataBaseManager", "Error getting documents: ", task.getException());
                result.setValue(new Integer[]{0, 0, 0, 0});
            }
        });

        return result;
    }

    public LiveData<Boolean> getUserExists(String uuid) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        db.collection("usuario")
                .whereEqualTo("id", uuid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        result.setValue(!documents.isEmpty());
                    } else {
                        result.setValue(false);
                    }
                });
        return result;
    }


}