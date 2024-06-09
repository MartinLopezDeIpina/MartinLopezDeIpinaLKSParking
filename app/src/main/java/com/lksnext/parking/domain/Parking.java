package com.lksnext.parking.domain;

import java.util.ArrayList;
import java.util.List;

public class Parking {
    private static Parking instance;

    private List<Reserva> reservas;
    private List<ReservaCompuesta> reservasCompuestas;
    private List<Plaza> plazas;
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
    public void addReserva(Reserva reserva){
        reservas.add(reserva);
    }
    public void setPlazas(List<Plaza> plazas){
        this.plazas = plazas;
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
                .filter(reserva -> reserva.getReservaID().equals(reservaID))
                .findFirst()
                .orElse(null);
    }
    public List<ReservaCompuesta> getReservasCompuestas(){
        return reservasCompuestas;
    }
    public TipoPlaza getTipoPlazaReserva(int plazaID){
        return plazas.stream()
                .filter(plaza -> plaza.getId() == plazaID)
                .findFirst()
                .map(Plaza::getTipoPlaza)
                .orElse(null);
    }
}
