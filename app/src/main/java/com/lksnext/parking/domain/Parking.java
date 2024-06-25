package com.lksnext.parking.domain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parking {
    private static Parking instance;
    private List<Reserva> reservas;
    private List<ReservaCompuesta> reservasCompuestas;
    private MutableLiveData<List<Plaza>> plazas = new MutableLiveData<>();
    private Usuario usuario;

    private Parking() {
        reservas = new ArrayList<>();
        reservasCompuestas = new ArrayList<>();
    }

    public static Parking getInstance() {
        if (instance == null) {
            instance = new Parking();
        }
        return instance;
    }
    public LiveData<List<Plaza>> getPlazasLiveData(){
        return plazas;
    }
    public List<Plaza> getPlazas(){
        return plazas.getValue();
    }
    public List<Long> getPlazasIDOfType(TipoPlaza tipo){
       return getPlazas().stream()
               .filter(plaza -> plaza.getTipoPlaza() == tipo)
               .map(Plaza::getId)
               .collect(Collectors.toList());
    }
    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }
    public void setPlazas(List<Plaza> plazas){
       this.plazas.setValue(plazas);
    }
    public void setReservas(List<Reserva> reservas){
        this.reservas = reservas;
    }
    public void setReservasCompuestas(List<ReservaCompuesta> compuestas){
        reservasCompuestas.addAll(compuestas);
    }
    public List<Reserva> getReservas(){
        return reservas;
    }
    public Reserva getReserva(String reservaID){
        return reservas.stream()
                .filter(reserva -> reserva.getId().equals(reservaID))
                .findFirst()
                .orElse(null);
    }
    public List<ReservaCompuesta> getReservasCompuestas(){
        return reservasCompuestas;
    }
    public TipoPlaza getTipoPlazaReserva(Long plazaID){
        return getPlazas().stream()
                .filter(plaza -> plaza.getId() == plazaID)
                .findFirst()
                .map(Plaza::getTipoPlaza)
                .orElse(null);
    }

    public int getNumPlazasCoche() {
        return (int) getPlazas().stream()
                .filter(plaza -> plaza.getTipoPlaza() == TipoPlaza.COCHE)
                .count();
    }
    public int getNumPlazasMoto() {
        return (int) getPlazas().stream()
                .filter(plaza -> plaza.getTipoPlaza() == TipoPlaza.MOTO)
                .count();
    }
    public int getNumPlazasElectrico() {
        return (int) getPlazas().stream()
                .filter(plaza -> plaza.getTipoPlaza() == TipoPlaza.ELECTRICO)
                .count();
    }
    public int getNumPlazasEspecial() {
        return (int) getPlazas().stream()
                .filter(plaza -> plaza.getTipoPlaza() == TipoPlaza.DISCAPACITADO)
                .count();
    }
}
