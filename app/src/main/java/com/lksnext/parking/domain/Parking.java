package com.lksnext.parking.domain;

import java.util.ArrayList;
import java.util.List;

public class Parking {
    private static Parking instance;

    private List<Reserva> reservas;
    private List<Plaza> plazas;
    private Usuario usuario;

    private Parking() {
        reservas = new ArrayList<>();
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
    public List<Reserva> getReservas(){
        return reservas;
    }
}
