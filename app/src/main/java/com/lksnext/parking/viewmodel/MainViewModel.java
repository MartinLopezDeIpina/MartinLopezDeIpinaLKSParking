package com.lksnext.parking.viewmodel;

import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainViewModel extends ViewModel {

    private Parking parking = Parking.getInstance();
    private final MutableLiveData<Usuario> user = new MutableLiveData<>(null);

    private final MutableLiveData<List<Reserva>> reservasActivas = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Reserva>> getReservasActivas() {
        return reservasActivas;
    }
    private final MutableLiveData<List<ReservaCompuesta>> reservasCompuestas = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<ReservaCompuesta>> getReservasCompuestas() {
        return reservasCompuestas;
    }

    public LiveData<Pair<List<Reserva>, List<ReservaCompuesta>>> getCombinedReservas() {
        return combinedReservas;
    }
    private final MediatorLiveData<Pair<List<Reserva>, List<ReservaCompuesta>>> combinedReservas = new MediatorLiveData<>();
    private final MutableLiveData<List<Reserva>> reservasPasadas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> navigateToBookingFragment = new MutableLiveData<>();
    private  Integer bookingNavigationPosition = 0;

    private final MutableLiveData<Boolean> navigateToMainFragment = new MutableLiveData<>();

    public MainViewModel() {
        combinedReservas.addSource(reservasActivas, new Observer<List<Reserva>>() {
            @Override
            public void onChanged(@Nullable List<Reserva> reservas) {
                combinedReservas.setValue(new Pair<>(reservas, reservasCompuestas.getValue()));
            }
        });

        combinedReservas.addSource(reservasCompuestas, new Observer<List<ReservaCompuesta>>() {
            @Override
            public void onChanged(@Nullable List<ReservaCompuesta> reservasComp) {
                combinedReservas.setValue(new Pair<>(reservasActivas.getValue(), reservasComp));
            }
        });
    }

    public LiveData<Usuario> getUser() {
        return user;
    }
    public void setUser(Usuario usuario){
        user.setValue(usuario);
    }

    public void setListaPlazas(List<Plaza> plazas){
        parking.setPlazas(plazas);
    }
    public void setListaReservas(List<Reserva> reservas, List<ReservaCompuesta> compuestas){
        parking.setReservas(reservas);
        parking.setReservasCompuestas(compuestas);
        updateReservas();
    }
    public void updateReservas(){
        LiveData<Object[]> reservasLiveData = DataBaseManager.getInstance().getCurrentUserBookings();

        reservasLiveData.observeForever(result -> {
            List<Reserva> reservas = (List<Reserva>) result[0];
            List<ReservaCompuesta> reservasCompuestasActivas = (List<ReservaCompuesta>) result[1];

            Parking.getInstance().setReservas(reservas);
            Parking.getInstance().setReservasCompuestas(reservasCompuestasActivas);

            List<Reserva> activas = new ArrayList<>();
            List<Reserva> pasadas = new ArrayList<>();
            List<ReservaCompuesta> reservasCompActivas = new ArrayList<>();
            for (Reserva reserva : reservas) {
                if (reserva.isCaducada()) {
                    pasadas.add(reserva);
                } else {
                    activas.add(reserva);
                }

            }
            for(ReservaCompuesta reservaCompuesta : reservasCompuestasActivas){
                for(String reservaID : reservaCompuesta.getReservasID()){
                    List<String> reservasActivasID = activas.stream().map(Reserva::getId).collect(Collectors.toList());
                    //En caso de que la reserva haya caducado o haya sido eliminada no añadir la reserva compuesta
                    if(reservasActivasID.contains(reservaID)){
                        reservasCompActivas.add(reservaCompuesta);
                        break;
                    }
                    //En caso de que la reserva haya caducado o haya sido eliminada eliminar la reserva compuesta
                    DataBaseManager.getInstance().deleteReservaCompuesta(reservaCompuesta.getId());
                }
            }

            reservasActivas.setValue(activas);
            reservasCompuestas.setValue(reservasCompActivas);
            reservasPasadas.setValue(pasadas);
        });


    }


    public void navigateToBookingFragment(boolean shouldNavigate, Integer position) {
        setBookingNavigationPosition(position);
        navigateToBookingFragment.setValue(shouldNavigate);
    }
    public LiveData<Boolean> getNavigateToBookingFragment() {
        return navigateToBookingFragment;
    }

    public Integer getBookingNavigationPosition() {
        return bookingNavigationPosition;
    }

    public void setBookingNavigationPosition(Integer position) {
        bookingNavigationPosition = position;
    }

    public void navigateToMainFragment(boolean shouldNavigate) {
        navigateToMainFragment.setValue(shouldNavigate);
    }
    public LiveData<Boolean> getNavigateToMainFragment() {
        return navigateToMainFragment;
    }
    public LiveData<Boolean> deleteBooking(String reservationID) {
        return DataBaseManager.getInstance().deleteBooking(reservationID);
    }
}
