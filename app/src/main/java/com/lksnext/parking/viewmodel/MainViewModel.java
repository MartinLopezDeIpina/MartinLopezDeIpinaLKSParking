package com.lksnext.parking.viewmodel;

import static java.security.AccessController.getContext;

import android.content.Context;
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
import com.lksnext.parking.domain.TipoPlaza;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.util.notifications.NotificationsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    private final MutableLiveData<List<Reserva>> reservasPasadas = new MutableLiveData<>();
    private final MutableLiveData<String> bookingModified = new MutableLiveData<>();
    private AtomicBoolean isFirstTime;

    private final MutableLiveData<Pair<Integer, Boolean>> shouldNavigateTooBookingFragment = new MutableLiveData<>();


    public MainViewModel() {
        isFirstTime = new AtomicBoolean(true);
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

    public AtomicBoolean getIsFirstTime() {
        return isFirstTime;
    }
    public void setIsFirstTime(AtomicBoolean isFirstTime) {
        this.isFirstTime = isFirstTime;
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
    public LiveData<Boolean> updateReservas(){
        MutableLiveData<Boolean> reservasUpdated = new MutableLiveData<>();
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
                    int numReservasActivas = 0;
                    List<String> reservasActivasID = activas.stream().map(Reserva::getId).collect(Collectors.toList());
                    //En caso de que la reserva haya caducado o haya sido eliminada no añadir la reserva compuesta
                    if(reservasActivasID.contains(reservaID)){
                        numReservasActivas++;
                        reservasCompActivas.add(reservaCompuesta);
                        break;
                    }
                    //En caso de que todas las reservas de la reserva compuesta hayan caducado o hayan sido eliminadas eliminar la reserva compuesta de la bd
                    if(numReservasActivas == 0){
                        DataBaseManager.getInstance().deleteReservaCompuesta(reservaCompuesta.getId());
                    }
                }
            }

            reservasActivas.setValue(activas);
            reservasCompuestas.setValue(reservasCompActivas);
            reservasPasadas.setValue(pasadas);

            reservasUpdated.setValue(true);
        });

        return reservasUpdated;
    }

    public LiveData<String> getBookingModified() {
        return bookingModified;
    }
    public void setBookingModified(String reservationID) {
        bookingModified.setValue(reservationID);
    }

    public LiveData<Pair<Integer, Boolean>> getShouldNavigateTooBookingFragment() {
        return shouldNavigateTooBookingFragment;
    }
    public void setShouldNavigateTooBookingFragment(Pair<Integer, Boolean> position) {
        shouldNavigateTooBookingFragment.setValue(position);
    }


    public LiveData<Boolean> deleteBooking(String reservationID) {
        return DataBaseManager.getInstance().deleteBooking(reservationID);
    }

    public LiveData<Boolean> deleteReservaCompuestaAndChilds(String reservationID) {
        return DataBaseManager.getInstance().deleteReservaCompuestaAndChilds(reservationID);
    }

    public LiveData<List<Reserva>> getReservasPasadas() {
        return reservasPasadas;
    }
    public LiveData<Integer[]> getCantidadPlazas(){
        LiveData<List<Plaza>> plazas = parking.getPlazasLiveData();
        MutableLiveData<Integer[]> result = new MutableLiveData<>();
        plazas.observeForever(new Observer<List<Plaza>>() {
            @Override
            public void onChanged(List<Plaza> plazas) {
                result.setValue(new Integer[]{parking.getNumPlazasCoche(), parking.getNumPlazasMoto(), parking.getNumPlazasElectrico(), parking.getNumPlazasEspecial()});
            }
        });
        return result;
    }

    public LiveData<Integer[]> getCantidadPlazasOcupadas() {
        return DataBaseManager.getInstance().getPlazasOcupadas();
    }

    public Integer[] getEstadisticasReservas() {
        List<Reserva> reservasUsuario = parking.getReservas();
        int numCoche = reservasUsuario.stream().filter(reserva -> reserva.getTipoPlaza().equals(TipoPlaza.COCHE)).collect(Collectors.toList()).size();
        int numMoto = reservasUsuario.stream().filter(reserva -> reserva.getTipoPlaza().equals(TipoPlaza.MOTO)).collect(Collectors.toList()).size();
        int numElectrico = reservasUsuario.stream().filter(reserva -> reserva.getTipoPlaza().equals(TipoPlaza.ELECTRICO)).collect(Collectors.toList()).size();
        int numEspecial = reservasUsuario.stream().filter(reserva -> reserva.getTipoPlaza().equals(TipoPlaza.DISCAPACITADO)).collect(Collectors.toList()).size();

        return new Integer[]{numCoche, numMoto, numElectrico, numEspecial};
    }
}
