package com.lksnext.parking.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private Parking parking = Parking.getInstance();
    private final MutableLiveData<Usuario> user = new MutableLiveData<>(null);
    private MutableLiveData<List<Reserva>> reservasActivas = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Reserva>> reservasPasadas = new MutableLiveData<>(new ArrayList<>());


    public LiveData<Usuario> getUser() {
        return user;
    }
    public void setUser(Usuario usuario){
        user.setValue(usuario);
    }
    public void setListaReservas(List<Reserva> reservas){
        reservas.forEach(reserva -> {
            parking.addReserva(reserva);
            updateReservas();
        });
    }
    private void updateReservas(){
        List<Reserva> reservas = parking.getReservas();
        List<Reserva> activas = new ArrayList<>();
        List<Reserva> pasadas = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.isCaducada()) {
                pasadas.add(reserva);
            } else {
                activas.add(reserva);
            }
        }
        reservasActivas.setValue(activas);
        reservasPasadas.setValue(pasadas);
    }
}
