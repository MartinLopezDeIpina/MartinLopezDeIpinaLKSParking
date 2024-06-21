package com.lksnext.parking.viewmodel;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
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

public class MainViewModel extends ViewModel {

    private Parking parking = Parking.getInstance();
    private final MutableLiveData<Usuario> user = new MutableLiveData<>(null);

    private final MediatorLiveData<Pair<List<Reserva>, List<ReservaCompuesta>>> reservasActivas = new MediatorLiveData<>();
    private MutableLiveData<List<Reserva>> reservasPasadas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> navigateToBookingFragment = new MutableLiveData<>();
    private  Integer bookingNavigationPosition = 0;

    private final MutableLiveData<Boolean> navigateToMainFragment = new MutableLiveData<>();

    public LiveData<Usuario> getUser() {
        return user;
    }
    public void setUser(Usuario usuario){
        user.setValue(usuario);
    }

    public LiveData<Pair<List<Reserva>, List<ReservaCompuesta>>> getReservasActivas() {
        return reservasActivas;
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
            List<ReservaCompuesta> reservasCompuestas = (List<ReservaCompuesta>) result[1];

            Parking.getInstance().setReservas(reservas);
            Parking.getInstance().setReservasCompuestas(reservasCompuestas);

            List<Reserva> activas = new ArrayList<>();
            List<Reserva> pasadas = new ArrayList<>();
            for (Reserva reserva : reservas) {
                if (reserva.isCaducada()) {
                    pasadas.add(reserva);
                } else {
                    activas.add(reserva);
                }
            }

            reservasActivas.setValue(new Pair<>(activas, reservasCompuestas));
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
}
